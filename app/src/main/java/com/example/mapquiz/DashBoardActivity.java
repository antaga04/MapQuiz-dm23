package com.example.mapquiz;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;

import java.util.ArrayList;
import java.util.Collections;

public class DashBoardActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;
    int timervalue = 20;
    IconRoundCornerProgressBar progressBar;
    ArrayList<Modelclass> allQuestionList;
    Modelclass modelclass;
    TextView card_question, optionA, optionB, optionC, optionD;
    CardView cardOA, cardOB, cardOC, cardOD;
    int index = 0;
    int correctCount = 0;
    int wrongCount = 0;
    LinearLayout nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        allQuestionList = new ArrayList<>();
        allQuestionList.add(new Modelclass("España", "Italia", "España", "Alemania", "Portugal", "España"));
        allQuestionList.add(new Modelclass("Francia", "España", "Italia", "Francia", "Reino Unido", "Francia"));
        allQuestionList.add(new Modelclass("Alemania", "España", "Alemania", "Francia", "Polonia", "Alemania"));
        allQuestionList.add(new Modelclass("Italia", "Portugal", "Italia", "Francia", "Suiza", "Italia"));
        allQuestionList.add(new Modelclass("Portugal", "España", "Italia", "Portugal", "Irlanda", "Portugal"));

        findIds();

        Collections.shuffle(allQuestionList);
        modelclass = allQuestionList.get(index);

        nextBtn.setClickable(false);

        setAllData();

        countDownTimer = new CountDownTimer(20000, 1000) {
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
                        Intent intent = new Intent(DashBoardActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        }.start();

    }

    private void setAllData() {
        card_question.setText(modelclass.getQuestion());
        optionA.setText(modelclass.getOpA());
        optionB.setText(modelclass.getOpB());
        optionC.setText(modelclass.getOpC());
        optionD.setText(modelclass.getOpD());
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

    public void Correct(CardView c) {
        correctCount++;
        c.setBackgroundColor(ContextCompat.getColor(this, R.color.green));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < allQuestionList.size() - 1) {
                    index++;
                    modelclass = allQuestionList.get(index);
                    resetColor();
                    setAllData();
                    enableButton();
                    nextBtn.setClickable(false);
                } else {
                    GameWon();
                }
            }
        });
    }

    public void Wrong(CardView c) {
        wrongCount++;
        c.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < allQuestionList.size() - 1) {
                    index++;
                    modelclass = allQuestionList.get(index);
                    resetColor();
                    setAllData();
                    enableButton();
                    nextBtn.setClickable(false);
                } else {
                    GameWon();
                }
            }
        });
    }

    private void GameWon() {
        Intent intent = new Intent(DashBoardActivity.this, WonActivity.class);
        intent.putExtra("Correct", correctCount);
        intent.putExtra("Wrong", wrongCount);
        startActivity(intent);
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

    public void OptionAClick(View view) {
        disableButton();
        nextBtn.setClickable(true);
        if (modelclass.getOpA().equals(modelclass.getCorrect())) {
            cardOA.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            Correct(cardOA);
        } else {
            Wrong(cardOA);
        }
    }

    public void OptionBClick(View view) {
        disableButton();
        nextBtn.setClickable(true);
        if (modelclass.getOpB().equals(modelclass.getCorrect())) {
            cardOB.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            Correct(cardOB);
        } else {
            Wrong(cardOB);
        }

    }

    public void OptionCClick(View view) {
        disableButton();
        nextBtn.setClickable(true);
        if (modelclass.getOpC().equals(modelclass.getCorrect())) {
            Correct(cardOC);
        } else {
            Wrong(cardOC);
        }
    }

    public void OptionDClick(View view) {
        disableButton();
        nextBtn.setClickable(true);
        if (modelclass.getOpD().equals(modelclass.getCorrect())) {
            Correct(cardOD);
        } else {
            Wrong(cardOD);
        }
    }
}