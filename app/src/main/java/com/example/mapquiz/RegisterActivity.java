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
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(country)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        User userObj = new User(name, country, email, 0);
                        updateProfile(user, userObj);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error creating user: " + task.getException().getMessage());
                    }
                });
    }

    private void updateProfile(FirebaseUser user, User userObj) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userObj.getName())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        writeUserDataToDatabase(user.getUid(), userObj);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to create user profile.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating profile: " + task.getException().getMessage());
                    }
                });
    }

    private void writeUserDataToDatabase(String userId, User userObj) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mapquiz-dm23-default-rtdb.firebaseio.com/");
        DatabaseReference usersRef = database.getReference("users");
        DatabaseReference userNodeRef = usersRef.child(userId);
        userNodeRef.setValue(userObj);

        Toast.makeText(RegisterActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
