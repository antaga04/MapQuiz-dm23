package com.example.mapquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button logoutBtn, confirmChangesBtn;

    FirebaseUser user;
    ImageButton editNameBtn, editEmailBtn, editPasswordBtn;
    EditText editTextName, editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logout);

        editNameBtn = findViewById(R.id.imageButtonEditName);
//        editEmailBtn = findViewById(R.id.imageButtonEditEmail);
        editPasswordBtn = findViewById(R.id.imageButtonEditPassword);
        confirmChangesBtn = findViewById(R.id.buttonConfirmChanges);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            editTextEmail.setHint((user.getEmail()));
            editTextName.setHint((user.getDisplayName()));
        }

        // En el método onCreate después de inicializar los EditText
        editTextName.setBackgroundResource(R.drawable.edit_text_border);
        editTextPassword.setBackgroundResource(R.drawable.edit_text_border);

        // Set initial state
        editTextEmail.setEnabled(false);
        editTextName.setEnabled(false);
        editTextPassword.setEnabled(false);

        editNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditText(editTextName);
            }
        });

//        editEmailBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                enableEditText(editTextEmail);
//            }
//        });

        editPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditText(editTextPassword);
            }
        });

        // Add onFocusChangeListeners to disable EditTexts when they lose focus
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    disableEditText(editTextEmail);
                }
            }
        });

        editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                String newName = String.valueOf(editTextPassword.getText());
//
//                if (TextUtils.isEmpty(newName)) {
//                    Toast.makeText(ProfileActivity.this, "Write a Name", Toast.LENGTH_SHORT).show();
//                }

                if (!hasFocus) {
                    disableEditText(editTextName);
                }
            }
        });

        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                String newPassword = String.valueOf(editTextPassword.getText());
//
//                if (TextUtils.isEmpty(newPassword)) {
//                    Toast.makeText(ProfileActivity.this, "Write a Password", Toast.LENGTH_SHORT).show();
//                }

                if (!hasFocus) {
                    disableEditText(editTextPassword);
                }
            }
        });

        confirmChangesBtn.setOnClickListener(new View.OnClickListener() {
            String originalName = user.getDisplayName();
            String originalPassword = "la contraseña original";

            @Override
            public void onClick(View v) {
                String name;
                name = String.valueOf(editTextName.getText());
                String newPassword = String.valueOf(editTextPassword.getText());

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                if (!TextUtils.isEmpty(name) && !name.equals(originalName)) {
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        originalName = name;
                                        Toast.makeText(ProfileActivity.this, "Name successfully updated!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                if (!TextUtils.isEmpty(newPassword) && !newPassword.equals(originalPassword)) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Password successfully updated!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FirebaseAuth.getInstance().signOut();*/
                auth.signOut();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

//        findViewById(R.id.mainLayout).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.requestFocus();
//                return false;
//            }
//        });

    }

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
    }
}
