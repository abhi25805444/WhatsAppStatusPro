package com.forever.whatsappstatussaver;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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

    File file1;
    int position;
    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    ArrayList<String> arrayList;

    File[] file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);
        btnShare = findViewById(R.id.btn_share);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_" + new Random().nextInt(1000) + ".jpg";
        btnDownload = findViewById(R.id.btn_download);
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imageView = findViewById(R.id.img);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        final String[] imgUri = {intent.getStringExtra("seletedfile")};
        Uri imguri = Uri.parse(intent.getStringExtra("seletedfile"));
        arrayList = intent.getStringArrayListExtra("arrayofstring");
        Log.d(TAG, "Position: " + position);
        Log.d(TAG, "ArrayList: " + arrayList);
        Log.d(TAG, "onCreate: " + imgUri[0]);
        file1 = new File(imgUri[0]);
        file = new File[]{new File(imgUri[0])};
        imageView.setImageURI(imguri);
        if (!picturesDirectory.exists()) {
            picturesDirectory.mkdirs();
        }
        Drawable icon = btnShare.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnShare.setImageDrawable(icon);
        icon = btnDownload.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnDownload.setImageDrawable(icon);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
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
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(whatsappIntent);
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }


    public void nextImg() {
        position++;
        if (arrayList.size() == position) {
            position = 0;
        }

        Uri uri = Uri.parse(arrayList.get(position).toString());

        imageView.setImageURI(uri);
    }

    public void prevImg() {
        position--;
        if (position < 0) {
            position = arrayList.size() - 1;
        }

        Uri uri = Uri.parse(arrayList.get(position).toString());

        imageView.setImageURI(uri);
    }


    private void onSwipeLeft() {
        nextImg();
    }

    private void onSwipeRight() {
        prevImg();
    }

}