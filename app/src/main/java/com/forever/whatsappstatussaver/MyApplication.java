package com.forever.whatsappstatussaver;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.Application;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private AppOpenAd appOpenAd = null;
    private boolean isShowingAd = false;
    private Activity currentActivity;
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)  // 0 means no delay, useful during development
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetch(0)  // Fetch fresh data without cache
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Activate fetched config
                        mFirebaseRemoteConfig.activate()
                                .addOnCompleteListener(activateTask -> {
                                    boolean adsEnabled = mFirebaseRemoteConfig.getBoolean("is_ad_enable");
                                    Log.d(TAG, "onCreate: adsEnabled " + adsEnabled);
                                    Constant.is_ad_enable = adsEnabled;
                                    if (Constant.is_ad_enable && !SessionManger.getIsPurchaseUser(currentActivity)) {
                                        loadAppOpenAd();
                                    }
                                });
                    }
                });
        ; // Load the ad when the app is created

    }

    // Method to load App Open Ad
    public void loadAppOpenAd() {
        if (appOpenAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            AppOpenAd.load(
                    this,
                    getString(R.string.appopenunit), // Replace with your actual App Open Ad Unit ID
                    adRequest,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            Log.d(TAG, "onAdLoaded: ");
                            appOpenAd = ad;
                            ad.show(currentActivity);
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            Log.d(TAG, "onAdFailedToLoad: " + loadAdError);
                            // Handle the error if the ad fails to load
                        }
                    });
        }
    }

    // Method to show App Open Ad

    @Override
    public void onActivityResumed(Activity activity) {
        // Show the ad when the app is resumed
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        BillingManger billingManger = new BillingManger();
        billingManger.init(activity);
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }
}
