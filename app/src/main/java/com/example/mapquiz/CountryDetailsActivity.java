package com.example.mapquiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class CountryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);

        // Get country details from intent
        Country country = getIntent().getParcelableExtra("country");

        // Display country details
        if (country != null) {
            setTitle(country.getName());
            TextView countryTextView = findViewById(R.id.countryTextView);
            TextView capitalTextView = findViewById(R.id.capitalTextView);
            TextView regionTextView = findViewById(R.id.regionTextView);
            ImageView flagImageView = findViewById(R.id.flagImageView);
            TextView altTextView = findViewById(R.id.altTextView);

            countryTextView.setText("Country: " + country.getName());
            capitalTextView.setText("Capital: " + country.getCapital());
            regionTextView.setText("Region: " + country.getRegion());
            altTextView.setText("Description: " + country.getAlt());

            Picasso.get().load(country.getFlagUrl()).into(flagImageView);

        }
    }
}
