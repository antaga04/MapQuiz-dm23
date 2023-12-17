package com.example.mapquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button logoutBtn, confirmChangesBtn;
    private FirebaseUser user;
    private ImageButton editNameBtn, editPasswordBtn;
    private EditText editTextName, editTextPassword, editTextEmail;

    private String originalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        auth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logout);
        editNameBtn = findViewById(R.id.imageButtonEditName);
        editPasswordBtn = findViewById(R.id.imageButtonEditPassword);
        confirmChangesBtn = findViewById(R.id.buttonConfirmChanges);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            editTextEmail.setHint(user.getEmail());
            originalName = user.getDisplayName();
            editTextName.setHint(originalName);
        }

        editTextName.setBackgroundResource(R.drawable.edit_text_border);
        editTextPassword.setBackgroundResource(R.drawable.edit_text_border);

//        editTextEmail.setEnabled(false);
        editTextName.setEnabled(false);
        editTextPassword.setEnabled(false);

        editNameBtn.setOnClickListener(v -> enableEditText(editTextName));

        editPasswordBtn.setOnClickListener(v -> enableEditText(editTextPassword));

//        setOnFocusChangeListener(editTextEmail);
        setOnFocusChangeListener(editTextName);
        setOnFocusChangeListener(editTextPassword);

        confirmChangesBtn.setOnClickListener(v -> confirmChanges());

        logoutBtn.setOnClickListener(v -> logout());
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
            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    originalName = name;
                    Toast.makeText(ProfileActivity.this, "Name successfully updated!", Toast.LENGTH_SHORT).show();
                    checkUserSession(); // Verifica la sesión después de actualizar el nombre
                }
            });
        }

        if (!TextUtils.isEmpty(newPassword)) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Password successfully updated!", Toast.LENGTH_SHORT).show();
                    checkUserSession(); // Verifica la sesión después de actualizar la contraseña
                }
            });
        }
    }

    private void checkUserSession() {
        // Verifica si el usuario aún está autenticado
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Si no está autenticado, redirige a la pantalla de inicio de sesión
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

// ... (código posterior)


    private void logout() {
        auth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
