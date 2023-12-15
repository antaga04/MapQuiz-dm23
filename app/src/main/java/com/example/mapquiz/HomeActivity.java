package com.example.mapquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    ImageButton profile_btn, playGame4Btn;
    Button study_btn;
    TextView helloText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        profile_btn = findViewById(R.id.profile_btn);
        study_btn = findViewById(R.id.study_btn);
        playGame4Btn = findViewById(R.id.playGame4Btn);

        user = auth.getCurrentUser();
        helloText = findViewById(R.id.textHello);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            helloText.setText("Hello " + (user.getDisplayName()));
        }

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        study_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CountryListActivity.class);
                startActivity(intent);
            }
        });

        playGame4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.info_dialog);

                dialog.findViewById(R.id.start_quiz).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });
    }
}