package com.example.mapquiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private TextInputEditText editTextName, editTextEmail, editTextPassword, editTextCountry;
    private Button buttonReg;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.name_register);
        editTextCountry = findViewById(R.id.country_register);
        editTextEmail = findViewById(R.id.email_register);
        editTextPassword = findViewById(R.id.password_register);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        buttonReg.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateProfile(user, name, email);
                        Toast.makeText(RegisterActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error creating user: " + task.getException().getMessage()); // Agrega este log para ver detalles del error
                    }
                });
    }

    private void updateProfile(FirebaseUser user, String name, String email) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Escribe los datos del usuario en la base de datos
                        writeUserDataToDatabase(user.getUid(), name, email);
                        Toast.makeText(RegisterActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to create user profile.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating profile: " + task.getException().getMessage());
                    }
                });
    }

    private void writeUserDataToDatabase(String userId, String name, String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mapquiz-dm23-default-rtdb.firebaseio.com/");
        DatabaseReference usersRef = database.getReference("users");

        Log.d("USER_UID", userId);
        Log.d("USER_NAME", name);
        Log.d("USER_EMAIL", email);

        // Crea un nuevo nodo con la ID de usuario y escribe los datos
        DatabaseReference userNodeRef = usersRef.child(userId);
        userNodeRef.child("name").setValue(name);
        userNodeRef.child("email").setValue(email);
    }

}
