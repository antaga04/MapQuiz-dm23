package com.example.mapquiz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class WonActivity extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String actualDate = dateFormat.format(calendar.getTime());
    TextView resultText;
    int correct, wrong;
    Button btnShare, scoreButton;
    public static final String LIBRARY_PACKAGE_NAME = "com.example.mapquiz";
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_won);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        correct = getIntent().getIntExtra("Correct", 0);
        wrong = getIntent().getIntExtra("Wrong", 0);
        int max = correct + wrong;

        resultText = findViewById(R.id.resultText);

        CircularProgressBar circularProgressBar = findViewById(R.id.mycircularProgressBar);
        circularProgressBar.setProgressMax(max);
        circularProgressBar.setProgressWithAnimation((float) correct, 1000L);

        resultText.setText(correct + "/" + (max));

        btnShare = findViewById(R.id.btnShare);

        // score database
        scoreButton = findViewById(R.id.btnAddScore);

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                insertData();

                FirebaseDatabase database = FirebaseDatabase.getInstance("https://mapquiz-dm23-default-rtdb.firebaseio.com/");
                DatabaseReference myRef = database.getReference("ranking");

                String game = "FlagCountry";
                int score = correct;
                String uId = user.getUid();
                String date = actualDate;

                Score scoreObj = new Score(game, score, date);
                myRef.child(uId).setValue(scoreObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(WonActivity.this, "Score uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void insertData() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Justin");
        map.put("email", "justin@gmail.com");
        FirebaseDatabase.getInstance().getReference().child("students").push()
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(WonActivity.this, "Inserted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WonActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
