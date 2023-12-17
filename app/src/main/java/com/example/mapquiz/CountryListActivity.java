package com.example.mapquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CountryListActivity extends AppCompatActivity {

    private ListView countryListView;
    private Spinner regionSpinner;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private List<Country> countryList;
    private List<Country> filteredCountryList;
    private ArrayAdapter<String> countryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(CountryListActivity.this, LoginActivity.class));
            finish();
        }

        loadCountries();
        setupRegionFilter();
        setupCountryListView();
    }

    private void loadCountries() {
        countryList = new ArrayList<>();
        filteredCountryList = new ArrayList<>();

        try {
            InputStream inputStream = getAssets().open("countries.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer);
            Log.d("JSON_CONTENT", json);

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Country country = new Country(jsonObject);
                countryList.add(country);
            }

            filteredCountryList.addAll(countryList);
            Log.d("COUNTRY_COUNT", "Number of countries loaded: " + countryList.size());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupRegionFilter() {
        regionSpinner = findViewById(R.id.regionSpinner);

        List<String> regionList = new ArrayList<>();
        regionList.add("All");

        for (Country country : countryList) {
            if (!regionList.contains(country.getRegion()))
                regionList.add(country.getRegion());
        }

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regionList);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(regionAdapter);

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

    private void setupCountryListView() {
        countryListView = findViewById(R.id.countryListView);

        countryAdapter = new ArrayAdapter<String>(this, R.layout.list_item_country, R.id.countryNameTextView) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView flagImageView = view.findViewById(R.id.flagImageView);
                TextView countryNameTextView = view.findViewById(R.id.countryNameTextView);

                Country currentCountry = filteredCountryList.get(position);

                Glide.with(CountryListActivity.this)
                        .load(currentCountry.getFlagUrl())
                        .into(flagImageView);

                countryNameTextView.setText(currentCountry.getName());

                return view;
            }
        };

        countryListView.setAdapter(countryAdapter);

        countryListView.setOnItemClickListener((parent, view, position, id) -> showCountryDetails(position));
    }

    private void filterCountriesByRegion(String selectedRegion) {
        filteredCountryList.clear();

        if ("All".equals(selectedRegion)) {
            filteredCountryList.addAll(countryList);
        } else {
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

    private List<String> getCountryNames() {
        List<String> countryNames = new ArrayList<>();
        for (Country country : filteredCountryList) {
            countryNames.add(country.getName());
        }
        return countryNames;
    }

    private void showCountryDetails(int position) {
        Intent intent = new Intent(this, CountryDetailsActivity.class);
        intent.putExtra("country", filteredCountryList.get(position));
        startActivity(intent);
    }
}
