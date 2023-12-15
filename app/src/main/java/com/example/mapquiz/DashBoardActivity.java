package com.example.mapquiz;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DashBoardActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;
    int timervalue = 60;
    RoundCornerProgressBar progressBar;
    TextView card_question, optionA, optionB, optionC, optionD;
    CardView cardOA, cardOB, cardOC, cardOD;
    int index = 0;
    int correctCount = 0;
    int wrongCount = 0;
    LinearLayout nextBtn;
    List<Question> questions;
    List<Country> countries;
    Question Q1;
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        loadCountries();
        loadQuestions();

        findIds();

        Collections.shuffle(questions);
        Q1 = questions.get(index);

        nextBtn.setClickable(false);

        setAllData();

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timervalue = timervalue - 1;
                progressBar.setProgress(timervalue);
            }

            @Override
            public void onFinish() {
                Dialog dialog = new Dialog(DashBoardActivity.this);
                dialog.setContentView(R.layout.time_out_dialog);

                dialog.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DashBoardActivity.this, WonActivity.class);
                        intent.putExtra("Correct", correctCount);
                        intent.putExtra("Wrong", wrongCount);
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        }.start();
    }

    private void loadCountries() {
        countries = new ArrayList<>();
        try {
            // Load JSON data from file
            InputStream inputStream = getAssets().open("countries.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // Convert the buffer into a string
            String json = new String(buffer);

            // Print JSON content to log (for debugging purposes)
            Log.d("JSON_CONTENT", json);

            // Parse JSON array
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Country c = new Country(jsonObject);
                countries.add(c);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void loadQuestions() {
        questions = new ArrayList<>();
        random = new Random();

        for (int i = 0; i < countries.size(); i++) {
            Question question = new Question();
            question.setOpA(countries.get(random.nextInt(countries.size())));
            question.setOpB(countries.get(random.nextInt(countries.size())));
            question.setOpC(countries.get(random.nextInt(countries.size())));
            question.setOpD(countries.get(random.nextInt(countries.size())));

            questions.add(question);
        }
    }

    private void setAllData() {

        card_question.setText(Q1.getRandomOption().getFlagUrl());
        optionA.setText(Q1.getOpA().getName());
        optionB.setText(Q1.getOpB().getName());
        optionC.setText(Q1.getOpC().getName());
        optionD.setText(Q1.getOpD().getName());

/*      timervalue = 20;
        countDownTimer.cancel();
        countDownTimer.start();
 */
    }

    private void findIds() {
        progressBar = findViewById(R.id.quiz_timer);
        card_question = findViewById(R.id.card_question);

        optionA = findViewById(R.id.card_optiona);
        optionB = findViewById(R.id.card_optionb);
        optionC = findViewById(R.id.card_optionc);
        optionD = findViewById(R.id.card_optiond);

        cardOA = findViewById(R.id.cardA);
        cardOB = findViewById(R.id.cardB);
        cardOC = findViewById(R.id.cardC);
        cardOD = findViewById(R.id.cardD);

        nextBtn = findViewById(R.id.nextBtn);
    }
    public void setCorrect(){
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < questions.size()-1){
                    index++;
                    Q1 = questions.get(index);
                    resetColor();
                    setAllData();
                    enableButton();
                    nextBtn.setClickable(false);
                }
            }
        });
    }

    public void OptionClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.cardA) {
            handleOptionClick(Q1.getOpA(), cardOA);
        } else if (viewId == R.id.cardB) {
            handleOptionClick(Q1.getOpB(), cardOB);
        } else if (viewId == R.id.cardC) {
            handleOptionClick(Q1.getOpC(), cardOC);
        } else if (viewId == R.id.cardD) {
            handleOptionClick(Q1.getOpD(), cardOD);
        }
    }
    public void handleOptionClick(Country selectedOption, CardView cardView) {
        disableButton();
        nextBtn.setClickable(true);

        if (selectedOption.getFlagUrl().equals(card_question.getText().toString())) {
            cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            correctCount++;
        } else {
            cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            wrongCount++;
        }
        setCorrect();
    }


    public void enableButton() {
        cardOA.setClickable(true);
        cardOB.setClickable(true);
        cardOC.setClickable(true);
        cardOD.setClickable(true);
    }

    public void disableButton() {
        cardOA.setClickable(false);
        cardOB.setClickable(false);
        cardOC.setClickable(false);
        cardOD.setClickable(false);
    }

    public void resetColor() {
        cardOA.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        cardOB.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        cardOC.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        cardOD.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

}