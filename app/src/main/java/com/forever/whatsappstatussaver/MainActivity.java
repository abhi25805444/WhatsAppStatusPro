package com.forever.whatsappstatussaver;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.forever.whatsappstatussaver.Fragment.PermisionFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_MANAGE_ALL_FILES_ACCESS_PERMISSION = 101;
    private static final int PERMISSION_REQUEST_STORAGE = 1;

    FloatingActionButton btnRefresh;
    RewardedInterstitialAd rewardedInterstitialAd;
    public static boolean isFirsttime = true;

    private static final int IMMEDIATE_UPDATE_REQUEST_CODE = 123;
    private static final int FLEXIBLE_UPDATE_REQUEST_CODE = 124;

    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable edge-to-edge with null check
        try {
            EdgeToEdge.enable(this);
        } catch (Exception e) {
            Log.e(TAG, "Error enabling edge-to-edge", e);
        }

        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Log.d(TAG, "onInitializationComplete: ");
            }
        });
        if (!isFirsttime) {
            /*loadAd();*/
        }

        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Check for updates
        checkForAppUpdate();

        showPermissonFrag();


    }

    private void checkForAppUpdate() {
        // Request the update information
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

                Log.d(TAG, "onFailure: "+e.getMessage());

            }
        });

        // Add listener to check for updates
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {

            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.d(TAG, "onSuccess: 1");
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // If an update is available
                    Log.d(TAG, "onSuccess: ");
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // Immediate update
                        startAppUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE, IMMEDIATE_UPDATE_REQUEST_CODE);
                    } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        // Flexible update
                        startAppUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE, FLEXIBLE_UPDATE_REQUEST_CODE);
                    }
                } else {
                    Log.i("UpdateCheck", "No update available.");
                }
            }
        });
    }


    private void startAppUpdate(AppUpdateInfo appUpdateInfo, int appUpdateType, int requestCode) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    appUpdateType,
                    this,
                    requestCode
            );
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }


    public void showPermissonFrag() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment PermisionFrag = new PermisionFragment();
        ft.replace(R.id.container, PermisionFrag);
        ft.commit();
        Log.d(TAG, "showPermissonFrag: ");
    }

    public void loadAd() {
        // Use the test ad unit ID to load an ad.
        RewardedInterstitialAd.load(MainActivity.this, getString(R.string.rewardadunit),
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        Log.d(TAG, "Ad was loaded.");
                        rewardedInterstitialAd = ad;
                        rewardedInterstitialAd.show(MainActivity.this, null);
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        rewardedInterstitialAd = null;
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (installStateUpdatedListener == null) {
            installStateUpdatedListener = state -> {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                }
            };
        }
        try {
            appUpdateManager.registerListener(installStateUpdatedListener);
        } catch (Exception e) {
            Log.e(TAG, "Error registering install state updated listener", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener if not needed anymore
        if (appUpdateManager != null && installStateUpdatedListener != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMMEDIATE_UPDATE_REQUEST_CODE || requestCode == FLEXIBLE_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("UpdateFlow", "Update failed or cancelled.");
            }
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void requestPermistion() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                },
                PERMISSION_REQUEST_STORAGE);
    }



}