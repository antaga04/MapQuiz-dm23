package com.example.mapquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class WonActivity extends AppCompatActivity {

    TextView resultText;
    int correct,wrong;
    LinearLayout btnShare;
    public static final String LIBRARY_PACKAGE_NAME = "com.example.prueba2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_won);

        correct=getIntent().getIntExtra("Correct",0);
        wrong=getIntent().getIntExtra("Wrong",0);

        resultText = findViewById(R.id.resultText);

        CircularProgressBar circularProgressBar = findViewById(R.id.mycircularProgressBar);
        circularProgressBar.setProgressWithAnimation((float) correct, 1000L);

        resultText.setText(correct+"/5");

        btnShare=findViewById(R.id.btnShare);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MapQuiz");
                    String shareMessage= "\nI got "+correct+" of 20 You can also try!!!\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + LIBRARY_PACKAGE_NAME +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

    }
}