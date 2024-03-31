package com.forever.whatsappstatussaver;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;


import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ViewImages extends AppCompatActivity {
    ImageView imageView;
    FloatingActionButton btnDownload;
    FloatingActionButton btnShare;
    LinearLayout imgNext;
    LinearLayout imgPrev;
    File file1;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);
        btnShare = findViewById(R.id.btn_share);
        imgNext = findViewById(R.id.imgnext);
        imgPrev = findViewById(R.id.imgprev);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_" + new Random().nextInt(1000) + ".jpg";
        btnDownload = findViewById(R.id.btn_download);
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imageView = findViewById(R.id.img);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        final String[] imgUri = {intent.getStringExtra("seletedfile")};
        Uri imguri = Uri.parse(intent.getStringExtra("seletedfile"));
        ArrayList<String> arrayList = intent.getStringArrayListExtra("arrayofstring");
        Log.d(TAG, "Position: " + position);
        Log.d(TAG, "ArrayList: " + arrayList);
        Log.d(TAG, "onCreate: " + imgUri[0]);
        file1 = new File(imgUri[0]);
        final File[] file = {new File(imgUri[0])};
        imageView.setImageURI(imguri);
        if (!picturesDirectory.exists()) {
            picturesDirectory.mkdirs();
        }
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File destinationFile = new File(picturesDirectory, imageFileName);

                try {
                    Uri uri = Uri.parse(arrayList.get(position).toString());
                    FileInputStream fis = (FileInputStream) getApplicationContext().getContentResolver().openInputStream(uri);
                    FileOutputStream fos = new FileOutputStream(destinationFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fis.close();
                    fos.close();
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{destinationFile.getAbsolutePath()}, null, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("image/*");
                whatsappIntent.setPackage("com.whatsapp");
                Uri uri = Uri.parse(imgUri[0]);
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Check out this file!");
                startActivity(whatsappIntent);
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (arrayList.size() == position) {
                    position = 0;
                }

                Uri uri = Uri.parse(arrayList.get(position).toString());

                imageView.setImageURI(uri);
            }
        });

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if (position < 0) {
                    position = arrayList.size() - 1;
                }

                Uri uri = Uri.parse(arrayList.get(position).toString());

                imageView.setImageURI(uri);
            }
        });

    }


}