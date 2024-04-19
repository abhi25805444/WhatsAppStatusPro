package com.forever.whatsappstatussaver;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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
import android.widget.MediaController;
import android.widget.VideoView;

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

public class ViewVideos extends AppCompatActivity {
    FloatingActionButton btnDownload;
    FloatingActionButton btnShare;
    int position;
    String imgUri;
    ArrayList<String> stringArrayList;
    File[] file;
    VideoView videoView;

    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_videos);
        videoView = findViewById(R.id.videoView);
        btnDownload = findViewById(R.id.btn_download);
        btnShare = findViewById(R.id.btn_share);

        Drawable icon = btnShare.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnShare.setImageDrawable(icon);
        icon = btnDownload.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnDownload.setImageDrawable(icon);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "Video_" + timeStamp + "_" + new Random().nextInt(1000) + ".mp4";
        Intent intent = getIntent();
        imgUri = intent.getStringExtra("seletedfile");
        position = intent.getIntExtra("postionofvideo", 0);
        stringArrayList = intent.getStringArrayListExtra("arraylistofvideos");
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        file = new File[]{new File(imgUri)};


        videoView.setVideoURI(Uri.parse(imgUri));

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
                videoView.start();
            }
        });
        videoView.start();

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
                File destinationFile = new File(picturesDirectory, videoFileName);

                try {
                    Uri uri = Uri.parse(stringArrayList.get(position).toString());
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

                Uri uri = Uri.parse(String.valueOf(Uri.parse(imgUri)));
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
        if (position == stringArrayList.size()) {
            position = 0;
        }
        file[0] = new File(stringArrayList.get(position));
        imgUri = stringArrayList.get(position);
        videoView.setVideoURI(Uri.parse(stringArrayList.get(position)));
    }

    public void prevImg() {
        position--;
        if (position < 0) {
            position = stringArrayList.size() - 1;
        }
        file[0] = new File(stringArrayList.get(position));
        imgUri = stringArrayList.get(position);
        videoView.setVideoURI(Uri.parse(stringArrayList.get(position)));
    }


    private void onSwipeLeft() {
        nextImg();
    }

    private void onSwipeRight() {
        prevImg();
    }

}