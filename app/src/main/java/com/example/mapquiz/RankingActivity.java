package com.example.mapquiz;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> rankingList;
    private Map<String, String> userIdToNameMap; // Mapa para almacenar UID y nombres de usuarios

    String instance = "https://mapquiz-dm23-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

        listView = findViewById(R.id.rankingListView);
        rankingList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rankingList);
        listView.setAdapter(adapter);

        userIdToNameMap = new HashMap<>(); // Inicializar el mapa

        // Llamamos a startUserIdToNameMapListener en onCreate
        startUserIdToNameMapListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Este método ahora está vacío porque la lógica se ha movido a startUserIdToNameMapListener
    }

    private void startUserIdToNameMapListener() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance(instance).getReference("users");

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIdToNameMap.clear(); // Limpiar el mapa antes de agregar nuevos datos
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String userName = userSnapshot.child("name").getValue(String.class);
                    userIdToNameMap.put(userId, userName);
                    Log.e("USERNAME_UID", userName);
                }
                Log.e("MAP_USERNAME_UID", userIdToNameMap.toString());

                // Actualizamos el ranking después de obtener los nombres de usuario
                updateRanking();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RankingActivity", "Error al obtener datos de usuarios de Firebase", databaseError.toException());
            }
        });
    }

    private void updateRanking() {
        DatabaseReference rankingReference = FirebaseDatabase.getInstance(instance).getReference("ranking");

        rankingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar la lista antes de agregar nuevos elementos
                rankingList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    Log.e("USER_ID_KEY", userId);

                    String userName = userIdToNameMap.get(userId);
                    String date = snapshot.child("date").getValue(String.class);
                    String game = snapshot.child("game").getValue(String.class);
                    int score = snapshot.child("score").getValue(Integer.class);

                    // Crear una cadena para mostrar en el ListView
                    String item = "User: " + userName + "\nDate: " + date + "\nGame: " + game + "\nScore: " + score;

                    rankingList.add(item);
                }

                // Ordenar la lista en orden descendente (mayor a menor)
                Collections.sort(rankingList, (s1, s2) -> {
                    // Obtener el score de cada cadena
                    int score1 = Integer.parseInt(s1.substring(s1.lastIndexOf("Score: ") + 7));
                    int score2 = Integer.parseInt(s2.substring(s2.lastIndexOf("Score: ") + 7));

                    // Comparar en orden descendente
                    return Integer.compare(score2, score1);
                });

                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RankingActivity", "Error al obtener datos de ranking de Firebase", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Presionaste el botón de retroceso en la barra de herramientas
                finish(); // Finaliza la actividad actual
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
