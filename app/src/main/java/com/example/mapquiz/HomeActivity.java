package com.example.mapquiz;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageButton profileBtn, playGame4Btn, rankingBtn;
    private Button studyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize views
        profileBtn = findViewById(R.id.profile_btn);
        studyBtn = findViewById(R.id.study_btn);
        rankingBtn = findViewById(R.id.ranking_btn);
        playGame4Btn = findViewById(R.id.playGame4Btn);

        // Set onClickListeners
        profileBtn.setOnClickListener(v -> openActivity(ProfileActivity.class));
        rankingBtn.setOnClickListener(v -> openActivity(RankingActivity.class));
        studyBtn.setOnClickListener(v -> openActivity(CountryListActivity.class));
        playGame4Btn.setOnClickListener(v -> openGameDialog());
    }

    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(HomeActivity.this, cls);
        startActivity(intent);
    }

    private void openGameDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.info_dialog);

        dialog.findViewById(R.id.start_quiz).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DashBoardActivity.class);
            startActivity(intent);
        });

        dialog.show();
    }
}
