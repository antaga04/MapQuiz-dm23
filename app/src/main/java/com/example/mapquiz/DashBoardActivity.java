package com.example.mapquiz;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.squareup.picasso.Picasso;

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
    TextView optionA, optionB, optionC, optionD;
    ImageView card_question;
    CardView cardOA, cardOB, cardOC, cardOD;
    int index = 0;
    int correctCount = 0;
    int wrongCount = 0;
    List<Question> questions;
    List<Country> countries;
    Question Q1;
    Random random;
    String urlflag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        loadCountries();
        loadQuestions();
        findIds();

        //Se barajan las preguntas para que el orden sea distinto en cada juego
        Collections.shuffle(questions);
        Q1 = questions.get(index);

        setAllData();

        //Se crea el contador de la duración del Quiz (60s)
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timervalue = timervalue - 1;
                progressBar.setProgress(timervalue);
            }

            @Override
            public void onFinish() {
                //Se muestra el diálogo de finalización del Quiz y saltamos a la WonActivity con los aciertos/fallos
                Dialog dialog = new Dialog(DashBoardActivity.this);
                dialog.setContentView(R.layout.time_out_dialog);
                dialog.setCancelable(false);
                dialog.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DashBoardActivity.this, WonActivity.class);
                        intent.putExtra("Correct", correctCount);
                        intent.putExtra("Wrong", wrongCount);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.show();
            }
        }.start();
    }

    /*
    Método para cargar el contenido del JSON y guardarlo en una Lista de países
    */
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

    /*
    Método para inicializar una lista de preguntas aleatorias para el juego
    */
    private void loadQuestions() {
        questions = new ArrayList<>();
        random = new Random();
        for (int i = 0; i < countries.size(); i++) {
            List<Integer> difQuestions = new ArrayList<>();
            //Aseguramos que las 4 opciones son diferentes
            while (difQuestions.size() < 4) {
                int newIndex = random.nextInt(countries.size());
                if (!difQuestions.contains(newIndex)) {
                    difQuestions.add(newIndex);
                }
            }
            //Añadimos las opciones a la lista de preguntas
            Question question = new Question();
            question.setOpA(countries.get(difQuestions.get(0)));
            question.setOpB(countries.get(difQuestions.get(1)));
            question.setOpC(countries.get(difQuestions.get(2)));
            question.setOpD(countries.get(difQuestions.get(3)));

            questions.add(question);
        }
    }

    /*
    Método para recoger los id's del layout
    */
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
    }

    /*
    Método para seleccionar y colocar las preguntas/respuestas en el layout
    */
    private void setAllData() {
        //Entre las 4 posibles respuestas, se selecciona una aleatoria y se coloca como pregunta
        urlflag = Q1.getRandomOption().getFlagUrl();
        Picasso.get().load(urlflag).into(card_question);

        optionA.setText(Q1.getOpA().getName());
        optionB.setText(Q1.getOpB().getName());
        optionC.setText(Q1.getOpC().getName());
        optionD.setText(Q1.getOpD().getName());
    }

    /*
    Método para controlar la respuesta que se selecciona
     */
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

    /*
    Método para comprobar si la respuesta es correcta/falsa mostrando el color verde/rojo
    */
    public void handleOptionClick(Country selectedOption, CardView cardView) {
        disableButton();

        if (selectedOption.getFlagUrl().equals(urlflag)) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green));
            correctCount++;
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red));
            wrongCount++;
        }
        setCorrect();
    }

    /*
    Método por el cual saltamos a la siguiente pregunta de la lista y actualizamos el layout
    */
    public void setCorrect() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < questions.size() - 1) {
                    index++;
                    Q1 = questions.get(index);
                    resetColor();
                    setAllData();
                    enableButton();
                }
            }
        }, 350);
    }

    /*
    Método para impedir elegir una respuesta
    */
    public void enableButton() {
        cardOA.setClickable(true);
        cardOB.setClickable(true);
        cardOC.setClickable(true);
        cardOD.setClickable(true);
    }

    /*
    Método para permitir elegir una respuesta
    */
    public void disableButton() {
        cardOA.setClickable(false);
        cardOB.setClickable(false);
        cardOC.setClickable(false);
        cardOD.setClickable(false);
    }

    /*
    Método para resetear los colores de las respuestas a elegir
     */
    public void resetColor() {
        cardOA.setCardBackgroundColor(ContextCompat.getColor(this, R.color.paleta5));
        cardOB.setCardBackgroundColor(ContextCompat.getColor(this, R.color.paleta5));
        cardOC.setCardBackgroundColor(ContextCompat.getColor(this, R.color.paleta5));
        cardOD.setCardBackgroundColor(ContextCompat.getColor(this, R.color.paleta5));
    }
}