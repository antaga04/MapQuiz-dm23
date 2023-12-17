package com.example.mapquiz;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
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
    Button btnShare, scoreButton, btnPlayAgain;
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
        scoreButton = findViewById(R.id.btnAddScore);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WonActivity.this, DashBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://mapquiz-dm23-default-rtdb.firebaseio.com/");
                DatabaseReference userRef = database.getReference("users").child(user.getUid());

                // Obtener el bestScore actual del usuario
                userRef.child("bestScore").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            int bestScore = task.getResult().getValue(Integer.class);

                            // Verificar si el nuevo score es mayor que el bestScore actual
                            if (correct > bestScore) {
                                // Actualizar el bestScore en la base de datos
                                userRef.child("bestScore").setValue(correct);

                                // Subir el nuevo score a la tabla de ranking
                                DatabaseReference rankingRef = database.getReference("ranking");
                                String game = "FlagCountry";
                                String uId = user.getUid();
                                String date = actualDate;

                                Score scoreObj = new Score(game, correct, date);
                                rankingRef.child(uId).setValue(scoreObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(WonActivity.this, "Score uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // El nuevo score no es mayor que el bestScore actual
                                Toast.makeText(WonActivity.this, "Score is not higher than bestScore", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle error
                            Toast.makeText(WonActivity.this, "Error getting bestScore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
