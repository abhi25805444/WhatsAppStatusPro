package com.forever.whatsappstatussaver;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
    ImageView imageView;
    LinearLayout imgPrev;
    LinearLayout imgNext;
    int position;
    String imgUri;

    @Override
    protected void onPause() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_circle_24));
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_videos);
        VideoView videoView = findViewById(R.id.videoView);
        imgNext=findViewById(R.id.imgnext1);
        imgPrev=findViewById(R.id.imgprev1);
        imageView=findViewById(R.id.imgconrol);
        btnDownload=findViewById(R.id.btn_download);
        btnShare=findViewById(R.id.btn_share);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "Video_" + timeStamp + "_" + new Random().nextInt(1000) + ".mp4";
        Intent intent=getIntent();
         imgUri= intent.getStringExtra("seletedfile");
        position=intent.getIntExtra("postionofvideo",0);
        ArrayList<String>stringArrayList=intent.getStringArrayListExtra("arraylistofvideos");
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        final File[] file = {new File(imgUri)};


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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying())
                {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_circle_24));
                    videoView.pause();
                }
                else {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pause_circle_24));
                    videoView.start();
                }
            }
        });
        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if(position<0)
                {
                    position=stringArrayList.size()-1;
                }
                file[0] =new File(stringArrayList.get(position));
                imgUri=stringArrayList.get(position);
                videoView.setVideoURI(Uri.parse(stringArrayList.get(position)));
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if(position==stringArrayList.size())
                {
                    position=0;
                }
                file[0] =new File(stringArrayList.get(position));
                imgUri=stringArrayList.get(position);
                videoView.setVideoURI(Uri.parse(stringArrayList.get(position)));
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File destinationFile = new File(picturesDirectory,videoFileName);

                try {
                    FileInputStream fis = new FileInputStream(file[0]);
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
                whatsappIntent.setType("video/*");
                whatsappIntent.setPackage("com.whatsapp");
                Uri uri = Uri.parse(imgUri);
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Check out this file!");
                startActivity(whatsappIntent);
            }
        });
    }
}