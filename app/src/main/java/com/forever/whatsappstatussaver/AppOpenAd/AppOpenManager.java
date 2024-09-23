package com.forever.whatsappstatussaver.AppOpenAd;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.FullScreenContentCallback;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks {
    private AppOpenAd appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private final Application application;
    private boolean isShowingAd = false;

    public AppOpenManager(Application application) {
        this.application = application;
        this.application.registerActivityLifecycleCallbacks(this);
        loadAppOpenAd();
    }

    public void loadAppOpenAd() {
        if (appOpenAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            AppOpenAd.load(
                application, 
                "your-ad-unit-id", // Replace with your App Open Ad Unit ID
                adRequest, 
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, 
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        appOpenAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle error
                    }
                });
        }
    }

    public void showAdIfAvailable(Activity activity) {
        if (appOpenAd != null && !isShowingAd) {
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isShowingAd = false;
                    appOpenAd = null;
                    loadAppOpenAd();
                }
            });
            isShowingAd = true;
            appOpenAd.show(activity);
        } else {
            loadAppOpenAd();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        showAdIfAvailable(activity);
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostResumed(activity);
    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPrePaused(activity);
    }

    // Other lifecycle methods...

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostPaused(activity);
    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreStopped(activity);
    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreStarted(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {}

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostDestroyed(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostStarted(activity);
    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreResumed(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostStopped(activity);
    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreSaveInstanceState(activity, outState);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostSaveInstanceState(activity, outState);
    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreDestroyed(activity);
    }
}
