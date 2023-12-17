package com.example.mapquiz;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CountryDetailsActivity extends AppCompatActivity {

    private ImageView flagImageView;

    private static final int MENU_DOWNLOAD = 1;
    private static final int MENU_SHARE = 2;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1;

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
            flagImageView = findViewById(R.id.flagImageView);
            TextView altTextView = findViewById(R.id.altTextView);

            countryTextView.setText("Country: " + country.getName());
            capitalTextView.setText("Capital: " + country.getCapital());
            regionTextView.setText("Region: " + country.getRegion());
            altTextView.setText("Description: " + country.getAlt());

            Picasso.get().load(country.getFlagUrl()).into(flagImageView);

            // Register the image view for the context menu
            registerForContextMenu(flagImageView);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Options");
        menu.add(0, MENU_DOWNLOAD, 0, "Download");
        menu.add(0, MENU_SHARE, 0, "Share");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DOWNLOAD:
                downloadImage();
                return true;
            case MENU_SHARE:
                shareImage();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void downloadImage() {
        Bitmap bitmap = getBitmapFromImageView(flagImageView);
        saveImageToGallery(bitmap);
        Toast.makeText(this, "Image downloaded", Toast.LENGTH_SHORT).show();
    }

    private void shareImage() {
        Bitmap bitmap = getBitmapFromImageView(flagImageView);

        if (bitmap != null) {
            Uri imageUri = saveImageToGallery(bitmap);

            if (imageUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(shareIntent, "Share image"));
            } else {
                Toast.makeText(this, "Error saving image. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error converting ImageView to Bitmap. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri saveImageToGallery(Bitmap bitmap) {
        // Verificar y solicitar permisos si es necesario
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
            return null;
        }


        // Directorio donde se almacenará la imagen
        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CountryFlags");

        if (!imagesFolder.exists()) {
            if (!imagesFolder.mkdirs()) {
                return null;
            }
        }

        // Nombre de archivo único
        String fileName = "flag_image_" + System.currentTimeMillis() + ".png";
        File imageFile = new File(imagesFolder, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            MediaScannerConnection.scanFile(
                    this,
                    new String[]{imageFile.getAbsolutePath()},
                    new String[]{"image/png"},
                    null
            );

            // Escanear el archivo para que aparezca en la galería
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CountryFlags");
                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                return imageUri;
            } else {
                // Actualizar la galería para que aparezca la imagen (para versiones anteriores a Android 10)
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
                return Uri.fromFile(imageFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getBitmapFromImageView(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);
        return bitmap;
    }
}

