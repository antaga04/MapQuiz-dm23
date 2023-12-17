package com.example.mapquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RankingActivity extends AppCompatActivity {

    Button upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mapquiz-dm23-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("casas");
        myRef.setValue("hola!");

        upload = findViewById(R.id.btn_addData);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RankingActivity.this, "Click score", Toast.LENGTH_SHORT).show();
            }
        });
    }
}