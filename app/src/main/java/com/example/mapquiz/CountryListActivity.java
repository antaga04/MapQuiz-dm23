package com.example.mapquiz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CountryListActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    private List<Country> countryList;
    private List<Country> filteredCountryList;
    private ArrayAdapter<String> countryAdapter;
    private Spinner regionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(CountryListActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Load countries from JSON file
        loadCountries();

        // Initialize UI components
        ListView countryListView = findViewById(R.id.countryListView);
        regionSpinner = findViewById(R.id.regionSpinner);

        // Set up region filter
        setupRegionFilter();

        // Set up country list
        countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getCountryNames());
        countryListView.setAdapter(countryAdapter);

        // Handle item click
        countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showCountryDetails(position);
            }
        });
    }

    private void loadCountries() {
        countryList = new ArrayList<>();
        filteredCountryList = new ArrayList<>();
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
                Country country = new Country(jsonObject);
                countryList.add(country);
            }

            // Inicializa la lista filtrada con la lista completa al principio
            filteredCountryList.addAll(countryList);

            // Print the number of countries loaded (for debugging purposes)
            Log.d("COUNTRY_COUNT", "Number of countries loaded: " + countryList.size());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getCountryNames() {
        List<String> countryNames = new ArrayList<>();
        for (Country country : filteredCountryList) {
            countryNames.add(country.getName());
        }
        return countryNames;
    }

    private void setupRegionFilter() {
        List<String> regionList = new ArrayList<>();
        regionList.add("All");
        for (Country country : countryList) {
            if (!regionList.contains(country.getRegion()))
                regionList.add(country.getRegion());
        }

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regionList);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(regionAdapter);

        // Handle region selection
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterCountriesByRegion(regionSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                filterCountriesByRegion("All");
            }
        });
    }

    private void filterCountriesByRegion(String selectedRegion) {
        filteredCountryList.clear();
        if ("All".equals(selectedRegion)) {
            // Si se selecciona "All", muestra la lista completa
            filteredCountryList.addAll(countryList);
        } else {
            // Filtra por región
            for (Country country : countryList) {
                if (country.getRegion().equals(selectedRegion)) {
                    filteredCountryList.add(country);
                }
            }
        }
        countryAdapter.clear();
        countryAdapter.addAll(getCountryNames());
        countryAdapter.notifyDataSetChanged();
    }

    private void showCountryDetails(int position) {
        Intent intent = new Intent(this, CountryDetailsActivity.class);
        intent.putExtra("country", filteredCountryList.get(position));
        startActivity(intent);
    }

}