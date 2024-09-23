package com.forever.whatsappstatussaver;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


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

public class ViewImages extends AppCompatActivity {
    ImageView imageView;
    FloatingActionButton btnDownload;
    FloatingActionButton btnWhatsappShare,btnShareAll;

    File file1;
    int position;
    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    ArrayList<String> arrayList;

    File[] file;
    LinearLayout linearLayout;

    File picturesDirectory;
    String imageFileName;

    boolean isComeFromShare = false;
    boolean isComeFromWahtsappShare = false;
    String[] imgUri;

    final long CLICK_DELAY = 1000; // 1 second
    long lastClickTime = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    RewardedInterstitialAd rewardedInterstitialAd;

    public void loadAd() {
        // Use the test ad unit ID to load an ad.
        if(Constant.is_ad_enable){
            RewardedInterstitialAd.load(ViewImages.this, getString(R.string.rewardadunit),
                    new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {

                        @Override
                        public void onAdLoaded(RewardedInterstitialAd ad) {
                            Log.d(ControlsProviderService.TAG, "Ad was loaded.");
                            rewardedInterstitialAd = ad;
                            rewardedInterstitialAd.show(ViewImages.this, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                }
                            });
                            rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    doActionAfterAd();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    doActionAfterAd();
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


    }


    public void doActionAfterAd() {
        if (isComeFromShare) {
            if(isComeFromWahtsappShare){
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("image/*");
                whatsappIntent.setPackage("com.whatsapp");
                Uri uri = Uri.parse(imgUri[0]);
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "");
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(whatsappIntent);
            }else {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                Uri uri = Uri.parse(imgUri[0]);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, ""));
            }
        } else {
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
                Toast.makeText(ViewImages.this, "Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        linearLayout = findViewById(R.id.adView);
        btnShareAll=findViewById(R.id.btnShareAll);

        if(Constant.is_ad_enable){
            AdView adView = new AdView(getApplicationContext());
            adView.setAdSize(getAdSize());
            adView.setAdUnitId(getString(R.string.banneradunit));
            linearLayout.removeAllViews();
            linearLayout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }



        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        btnWhatsappShare = findViewById(R.id.btn_share);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        imageFileName = "IMG_" + timeStamp + "_" + new Random().nextInt(1000) + ".jpg";
        btnDownload = findViewById(R.id.btn_download);
        picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imageView = findViewById(R.id.img);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        imgUri = new String[]{intent.getStringExtra("seletedfile")};
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
        Drawable icon = btnWhatsappShare.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnWhatsappShare.setImageDrawable(icon);
        icon = btnDownload.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnDownload.setImageDrawable(icon);
        icon = btnShareAll.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        btnShareAll.setImageDrawable(icon);


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
                // Check if the time elapsed since the last click is more than the defined delay
                if (currentTime - lastClickTime >= CLICK_DELAY) {
                    lastClickTime = currentTime;
                    isComeFromShare = false;
                    Constant.shareCounter++;
                    if (Constant.shareCounter >= 3&&Constant.is_ad_enable) {
                        Constant.shareCounter = 0;
                        loadAd();
                    } else {
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
                            Toast.makeText(ViewImages.this, "Saved", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        if (btnWhatsappShare != null) {
            btnWhatsappShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentTime = System.currentTimeMillis();
                    // Check if the time elapsed since the last click is more than the defined delay
                    if (currentTime - lastClickTime >= CLICK_DELAY) {
                        isComeFromShare = true;
                        lastClickTime = currentTime;
                        isComeFromWahtsappShare = true;
                        Constant.shareCounter++;
                        if (Constant.shareCounter >= 3&&Constant.is_ad_enable) {
                            Constant.shareCounter = 0;
                            loadAd();
                        } else {
                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("image/*");
                            whatsappIntent.setPackage("com.whatsapp");
                            Uri uri = Uri.parse(imgUri[0]);
                            whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "");
                            whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(whatsappIntent);
                        }
                    }

                }
            });
        }

        btnShareAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentTime = System.currentTimeMillis();
                // Check if the time elapsed since the last click is more than the defined delay
                if (currentTime - lastClickTime >= CLICK_DELAY) {
                    lastClickTime = currentTime;
                    isComeFromShare = true;
                    isComeFromWahtsappShare=false;
                    Constant.shareCounter++;
                    if (Constant.shareCounter >= 3&&Constant.is_ad_enable) {
                        Constant.shareCounter = 0;
                        loadAd();
                    } else {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        Uri uri = Uri.parse(imgUri[0]);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Share Image using"));
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
        if (arrayList.size() == position) {
            position = 0;
        }

        Uri uri = Uri.parse(arrayList.get(position).toString());

        imageView.setImageURI(uri);
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