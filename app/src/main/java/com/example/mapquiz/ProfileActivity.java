package com.example.mapquiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button logoutBtn, confirmChangesBtn;
    private ImageButton editNameBtn, editPasswordBtn, editCountryBtn;
    private EditText editTextName, editTextPassword, editTextEmail, editTextCountry;

    private String originalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setActionBar();
        setOnClickListeners();
        setUserDetails();
        disableEditTexts();
    }

    private void initializeViews() {
        auth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logout);
        editNameBtn = findViewById(R.id.imageButtonEditName);
        editPasswordBtn = findViewById(R.id.imageButtonEditPassword);
        editCountryBtn = findViewById(R.id.imageButtonEditCountry);
        confirmChangesBtn = findViewById(R.id.buttonConfirmChanges);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    private void setActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setOnClickListeners() {
        editNameBtn.setOnClickListener(v -> enableEditText(editTextName));
        editPasswordBtn.setOnClickListener(v -> enableEditText(editTextPassword));
        editCountryBtn.setOnClickListener(v -> enableEditText(editTextCountry));
        setOnFocusChangeListener(editTextName);
        setOnFocusChangeListener(editTextPassword);
        setOnFocusChangeListener(editTextCountry);
        confirmChangesBtn.setOnClickListener(v -> confirmChanges());
        logoutBtn.setOnClickListener(v -> logout());
    }

    private void setUserDetails() {
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            // Lógica para obtener y mostrar el country y bestScore desde la base de datos
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String country = dataSnapshot.child("country").getValue(String.class);
                        int bestScore = dataSnapshot.child("bestScore").getValue(Integer.class);

                        editTextCountry.setHint(country);
                        TextView textViewBestScore = findViewById(R.id.textViewBestScore);
                        textViewBestScore.setText(String.valueOf(bestScore));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ProfileActivity", "Error al obtener datos de usuario de Firebase", databaseError.toException());
                }
            });

            editTextEmail.setHint(user.getEmail());
            originalName = user.getDisplayName();
            editTextName.setHint(originalName);
        }
    }

    private void disableEditTexts() {
        disableEditText(editTextName);
        disableEditText(editTextPassword);
        disableEditText(editTextCountry);
    }

    private void setOnFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                disableEditText(editText);
            }
        });
    }

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
    }

    private void confirmChanges() {
        String name = editTextName.getText().toString().trim();
        String newPassword = editTextPassword.getText().toString().trim();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        if (!TextUtils.isEmpty(name) && !name.equals(originalName)) {
            user.updateProfile(profileUpdates);
        }

        if (!TextUtils.isEmpty(newPassword)) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Password successfully updated!");
                    checkUserSession();
                }
            });
        }

        updateUserDataInDatabase();
        checkUserSession();
    }

    private void updateUserDataInDatabase() {
        // Actualizar campos en la base de datos
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        String country = editTextCountry.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        if (!TextUtils.isEmpty(country)) {
            usersReference.child("country").setValue(country).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Country successfully updated!");
                }
            });
        }

        if (!TextUtils.isEmpty(name)) {
            usersReference.child("name").setValue(name).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    originalName = name;
                    showToast("Name successfully updated!");
                }
            });
        }
    }


    private void checkUserSession() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logout() {
        auth.signOut();
        navigateToLogin();
    }

    private void showToast(String message) {
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}