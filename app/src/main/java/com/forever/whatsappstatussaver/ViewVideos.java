package com.forever.whatsappstatussaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.service.controls.ControlsProviderService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ViewVideos extends AppCompatActivity {
    FloatingActionButton btnDownload;
    FloatingActionButton btnWhatsappShare;
    int position;
    String imgUri;
    ArrayList<String> stringArrayList;
    File[] file;
    VideoView videoView;
    LinearLayout linearLayout;
    FloatingActionButton btnSharall;
    final long CLICK_DELAY = 2000; // 1 second
    long lastClickTime = 0;

    String videoFileName;
    File picturesDirectory;

    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    boolean isComeFromShare = false;
    boolean isComeFromWahtsappShare = false;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    RewardedInterstitialAd rewardedInterstitialAd;

    public void loadAd() {
        // Use the test ad unit ID to load an ad.
        RewardedInterstitialAd.load(ViewVideos.this, getString(R.string.rewardadunit),
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        videoView.pause();
                        Log.d(ControlsProviderService.TAG, "Ad was loaded.");
                        rewardedInterstitialAd = ad;
                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                doActionAfterAd();
                                videoView.start();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                doActionAfterAd();
                                videoView.start();
                            }
                        });
                        rewardedInterstitialAd.show(ViewVideos.this, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                videoView.pause();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(ControlsProviderService.TAG, loadAdError.toString());
                        rewardedInterstitialAd = null;
                        doActionAfterAd();
                    }
                });
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = linearLayout.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setContentView(R.layout.activity_view_videos);

        linearLayout = findViewById(R.id.adView);
        btnSharall = findViewById(R.id.shareall);

        if(Constant.is_ad_enable&&!SessionManger.getInstance().getIsPurchaseUser()){
            AdView adView = new AdView(getApplicationContext());
            adView.setAdSize(getAdSize());
            adView.setAdUnitId(getString(R.string.banneradunit));
            linearLayout.removeAllViews();
            linearLayout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }


        videoView = findViewById(R.id.videoView);
        btnDownload = findViewById(R.id.btn_download);
        btnWhatsappShare = findViewById(R.id.btn_share);

        Drawable icon = btnWhatsappShare.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnWhatsappShare.setImageDrawable(icon);
        icon = btnDownload.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);

        icon = btnSharall.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnSharall.setImageDrawable(icon);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        videoFileName = "Video_" + timeStamp + "_" + new Random().nextInt(1000) + ".mp4";
        Intent intent = getIntent();
        imgUri = intent.getStringExtra("seletedfile");
        position = intent.getIntExtra("postionofvideo", 0);
        stringArrayList = intent.getStringArrayListExtra("arraylistofvideos");
        picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
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

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime >= CLICK_DELAY) {
                    lastClickTime = currentTime;
                    isComeFromShare = false;
                    Constant.shareCounter++;
                    if (Constant.shareCounter >= 3&&Constant.is_ad_enable&&!SessionManger.getInstance().getIsPurchaseUser()) {
                        Constant.shareCounter = 0;
                        loadAd();
                    } else {
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
                            Toast.makeText(ViewVideos.this, "Saved", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }



            }
        });

        btnSharall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isComeFromShare = true;long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime >= CLICK_DELAY) {
                    lastClickTime = currentTime;
                    isComeFromWahtsappShare = false;
                    Constant.shareCounter++;
                    if (Constant.shareCounter >= 3&&Constant.is_ad_enable&&!SessionManger.getInstance().getIsPurchaseUser()) {
                        Constant.shareCounter = 0;
                        loadAd();
                    } else {

                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("video/*");  // Change to video MIME type
                        Uri uri = Uri.parse(String.valueOf(Uri.parse(imgUri)));  // Assuming videoUri contains your video file path
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "");
                        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(whatsappIntent);
                    }
                }
            }
        });

        btnWhatsappShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime >= CLICK_DELAY) {
                    lastClickTime = currentTime;
                    isComeFromShare = true;
                    isComeFromWahtsappShare = true;
                    Constant.shareCounter++;
                    if (Constant.shareCounter >= 3&&Constant.is_ad_enable&&!SessionManger.getInstance().getIsPurchaseUser()) {
                        Constant.shareCounter = 0;
                        loadAd();
                    } else {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("image/*");
                        whatsappIntent.setPackage("com.whatsapp");
                        Uri uri = Uri.parse(String.valueOf(Uri.parse(imgUri)));
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(whatsappIntent);
                    }
                }

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

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
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

    public void doActionAfterAd() {
        if (isComeFromShare) {
            if (isComeFromWahtsappShare) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("image/*");
                whatsappIntent.setPackage("com.whatsapp");
                Uri uri = Uri.parse(String.valueOf(Uri.parse(imgUri)));
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(whatsappIntent);
            }else {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("video/*");  // Change to video MIME type
                Uri uri = Uri.parse(String.valueOf(Uri.parse(imgUri)));  // Assuming videoUri contains your video file path
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "");
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(whatsappIntent);
            }
        } else {
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
                Toast.makeText(ViewVideos.this, "Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void onSwipeLeft() {
        nextImg();
    }

    private void onSwipeRight() {
        prevImg();
    }

}