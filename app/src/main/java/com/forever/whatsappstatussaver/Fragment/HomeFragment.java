package com.forever.whatsappstatussaver.Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Half;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.forever.whatsappstatussaver.BillingManger;
import com.forever.whatsappstatussaver.Constant;
import com.forever.whatsappstatussaver.Interface.RefreshInterface;
import com.forever.whatsappstatussaver.Interface.VideoRefreshInterface;
import com.forever.whatsappstatussaver.MainActivity;
import com.forever.whatsappstatussaver.R;
import com.forever.whatsappstatussaver.SessionManger;
import com.forever.whatsappstatussaver.viewpagerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;


public class HomeFragment extends Fragment implements BillingManger.BillingCallback {
    TabLayout tabLayout;
    ViewPager viewPager;
    viewpagerAdapter viewpagerAdapter;
    Spinner spinner;
    LinearLayout adContainer;
    ImageView btnNoAds;
    FloatingActionButton btn_refresh;


    RefreshInterface refreshInterface;
    VideoRefreshInterface videoRefreshInterface;
    private int WHATSAPP = 0;
    private int WHATSAPPBUSINES = 1;
    private TextView txtRemoveAd, txtNotNow, txtTitle, txtSubText, txtPrice;
    private ImageView imgNoAds;
    private RelativeLayout btnRemoveAd;


    private static final String TAG = "HomeFragment";

    private Dialog purchaseDailog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity.isFirsttime = false;
        spinner = root.findViewById(R.id.spinner);
        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);
        adContainer = root.findViewById(R.id.adView);
        btnNoAds = root.findViewById(R.id.noadsicon);
        btn_refresh = root.findViewById(R.id.btn_refresh);
        if (Constant.is_ad_enable && !SessionManger.getIsPurchaseUser(getActivity())) {
            AdView adView = new AdView(getActivity());
            adView.setAdSize(getAdSize());
            adView.setAdUnitId(getString(R.string.banneradunit));
            adContainer.removeAllViews();
            adContainer.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d(TAG, "onAdFailedToLoad: " + loadAdError.toString());
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdLoaded() {
                    Log.d(TAG, "onAdLoaded: ");
                    super.onAdLoaded();
                }

                @Override
                public void onAdOpened() {
                    Log.d(TAG, "onAdOpened: ");
                    super.onAdOpened();
                }

                @Override
                public void onAdSwipeGestureClicked() {
                    super.onAdSwipeGestureClicked();
                }
            });
        }

        if (!SessionManger.getIsPurchaseUser(getActivity()) && SessionManger.checkAndShowRemoveAdDialog(getActivity())) {
            openRemoveAdDailog();
        }
        Log.d(TAG, "onCreateView: Tag of frag " + getTag());

        if (btn_refresh != null) {
            btn_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "onClick: (((((((((((((((((( 1");
                    if (refreshInterface != null) {
                        Log.d(TAG, "onClick: (((((((((((((((((( 1");
                        refreshInterface.onRefreshImage();
                    }

                    if (videoRefreshInterface != null) {
                        videoRefreshInterface.onRefreshVideo();
                    }


                }
            });
        }




        /*if (viewpagerAdapter.getItem(1) instanceof imagelistFragment) {
            ((imagelistFragment) viewpagerAdapter.getItem(1)).setInterface(this);
        }*/
        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        BillingManger.getInstance().setBillingLisner(this);
        if (SessionManger.getIsPurchaseUser(getActivity())) {
            if (btnNoAds != null) {
                btnNoAds.setImageDrawable(getResources().getDrawable(R.drawable.pro_user));
                btnNoAds.clearAnimation();
            }
        } else {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.plus);
            if (btnNoAds != null) {
                btnNoAds.setImageDrawable(getResources().getDrawable(R.drawable.remove_ads_icon));
                btnNoAds.startAnimation(animation);
            }
        }
        btnNoAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillingManger.getInstance().queryPurchases();
                openRemoveAdDailog();
            }
        });


        viewpagerAdapter = new viewpagerAdapter(getActivity().getSupportFragmentManager());
        if (viewpagerAdapter != null) {
            viewPager.setAdapter(viewpagerAdapter);
        }
        tabLayout.setupWithViewPager(viewPager);

        String[] options = {"WHATSAPP", "WHATSAPP BUSINESS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_layout, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (adapter != null) {
            spinner.setAdapter(adapter);
        }

        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == WHATSAPPBUSINES) {
                        boolean isWhatsAppBusinessInstalled = isWhatsAppBusinessInstalled();
                        if (isWhatsAppBusinessInstalled) {
                            if (refreshInterface != null) {
                                refreshInterface.onExecuteNew(WHATSAPPBUSINES);
                            }
                            if (videoRefreshInterface != null) {
                                videoRefreshInterface.onExecuteNew(WHATSAPPBUSINES);
                            }
                            // WhatsApp Business is installed
                        } else {
                            Toast.makeText(getActivity(), "Please Install WhatsApp Business App ", Toast.LENGTH_SHORT).show();
                            if (spinner != null) {
                                spinner.setSelection(WHATSAPP);
                            }
                            // WhatsApp Business is not installed
                        }
                    } else if (position == WHATSAPP) {
                        if (refreshInterface != null) {
                            refreshInterface.onExecuteNew(WHATSAPP);
                        }
                        if (videoRefreshInterface != null) {
                            videoRefreshInterface.onExecuteNew(WHATSAPP);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


    }

    public void hideBannerAd() {
        if (adContainer != null) {
            adContainer.setVisibility(View.GONE);
        }
    }

    private void openRemoveAdDailog() {

        BillingManger.getInstance().queryProductDetails();

        Log.d(TAG, "openRemoveAdDailog: is pro " + SessionManger.getIsPurchaseUser(getActivity()));

        purchaseDailog = new Dialog(getActivity());

        // Set the custom layout
        purchaseDailog.setContentView(R.layout.popup_remove_ads);
        btnRemoveAd = purchaseDailog.findViewById(R.id.btnRemoveAd);
        txtRemoveAd = purchaseDailog.findViewById(R.id.txtRemoveAd);
        txtNotNow = purchaseDailog.findViewById(R.id.txtNotNow);
        imgNoAds = purchaseDailog.findViewById(R.id.imgNoAds);
        txtTitle = purchaseDailog.findViewById(R.id.txtTitle);
        txtSubText = purchaseDailog.findViewById(R.id.txtSubText);
        txtPrice = purchaseDailog.findViewById(R.id.txtPrice);


        Log.d(TAG, "openRemoveAdDailog: getIsPurchaseUser " + (SessionManger.getIsPurchaseUser(getActivity())));


        if (txtNotNow != null) {
            txtNotNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (purchaseDailog != null && purchaseDailog.isShowing()) {
                        purchaseDailog.dismiss();
                    }
                }
            });
        }

        if (btnRemoveAd != null) {
            btnRemoveAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SessionManger.getIsPurchaseUser(getActivity())) {
                        String subscriptionId = "remove_ads_subscription"; // Replace with your subscription ID
                        Intent manageSubscriptionIntent = new Intent(Intent.ACTION_VIEW);
                        manageSubscriptionIntent.setData(Uri.parse("https://play.google.com/store/account/subscriptions?sku=" + subscriptionId + "&package=" + getActivity().getPackageName()));
                        manageSubscriptionIntent.setPackage("com.android.vending");
                        startActivity(manageSubscriptionIntent);
                    } else {
                        BillingManger.getInstance().lunchPurcheshFlow(getActivity());
                        Log.d(TAG, "onClick: BillingManger.getInstance() " + BillingManger.getInstance());
                    }
                }
            });
        }
        purchaseDailog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        purchaseDailog.show();
        updateUionPurchase();
        /*updateUionPurchase();*/
    }

    public void updateUionPurchase() {
        Log.d(TAG, "updateUionPurchase: SessionManger.getIsPurchaseUser(getActivity()) " + SessionManger.getIsPurchaseUser(getActivity()));
        if (SessionManger.getIsPurchaseUser(getActivity())) {
            if (txtPrice != null) {
                txtPrice.setVisibility(View.GONE);
                txtPrice.invalidate();
            }
            if (btnNoAds != null) {
                btnNoAds.setImageDrawable(getResources().getDrawable(R.drawable.pro_user));
                btnNoAds.clearAnimation();
            }
            if (txtSubText != null) {
                txtSubText.setText("Relax and enjoy the app without interruptions from ads.");
            }
            if (imgNoAds != null) {
                imgNoAds.setImageDrawable(getResources().getDrawable(R.drawable.pro_user));
            }
            if (txtTitle != null) {
                txtTitle.setText("Ad-Free Experience Unlocked");
            }

            Log.d(TAG, "updateUionPurchase: getIsAutoRenew " + (BillingManger.getInstance().getIsAutoRenew()));
            if (txtRemoveAd != null) {
                if (!BillingManger.getInstance().getIsAutoRenew()) {
                    txtRemoveAd.setText("Resubscribe");
                } else {
                    txtRemoveAd.setText("Mange Subscription");
                }
            }
            if (btnNoAds != null) {
                btnNoAds.invalidate();
            }
        } else {
            if (txtPrice != null) {
                txtPrice.setVisibility(View.VISIBLE);
                txtPrice.invalidate();
            }
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.plus);
            if (btnNoAds != null) {
                btnNoAds.setImageDrawable(getResources().getDrawable(R.drawable.remove_ads_icon));
                btnNoAds.startAnimation(animation);
            }
            if (txtSubText != null) {
                txtSubText.setText("No more ads! Just you and your favorite WhatsApp statuses.");
            }
            if (txtTitle != null) {
                txtTitle.setText("Remove Ads, Save Faster!");
            }
            if (imgNoAds != null) {
                imgNoAds.setImageDrawable(getResources().getDrawable(R.drawable.no_ads));
            }
            if (txtRemoveAd != null) {
                txtRemoveAd.setText("Remove Ads Now");
            }
            if (btnNoAds != null) {
                btnNoAds.invalidate();
            }
        }
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainer.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getActivity(), adWidth);
    }

    public void setRefreshInterface(RefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }

    public boolean isWhatsAppBusinessInstalled() {

        PackageManager packageManager = getActivity().getPackageManager();
        try {
            packageManager.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES);
            return true; // WhatsApp Business is installed
        } catch (PackageManager.NameNotFoundException e) {
            return false; // WhatsApp Business is not installed
        }
    }

    public void setVideoRefreshInterface(VideoRefreshInterface videoRefreshInterface) {
        this.videoRefreshInterface = videoRefreshInterface;
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            SessionManger.setIsPurchaseUser(getActivity(), true);
            hideBannerAd();
            updateUionPurchase();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "onPurchasesUpdated: user cancel ");
            // User canceled the purchase
        } else {
            Log.d(TAG, "onPurchasesUpdated: other error ");
            SessionManger.setIsPurchaseUser(getActivity(), false);
            updateUionPurchase();
            // Handle other errors
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (purchaseDailog != null) {
            Log.d(TAG, "onResume: purchaseDailog != null " + (purchaseDailog != null) + " purchaseDailog.isShowing() " + purchaseDailog.isShowing());
            if (purchaseDailog.isShowing()) {
                BillingManger.getInstance().queryPurchases();
                updateUionPurchase();
            }
        }
    }

    @Override
    public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> purchasesList) {
        boolean isPurchase = false;
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            if (purchasesList != null && !purchasesList.isEmpty()) {
                for (Purchase purchase : purchasesList) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        /*updateUionPurchase();*/
                        Log.d(TAG, "onQueryPurchasesResponse: is subscribed " + purchase.isAutoRenewing());
                        isPurchase = true;
                    }
                    if (isPurchase) {
                        updateUionPurchase();
                    } else {
                    }
                }
            } else {
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
        } else {
        }
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
            for (SkuDetails skuDetails : skuDetailsList) {
                String price = skuDetails.getPrice();
                String currencyCode = skuDetails.getPriceCurrencyCode();
                if (txtPrice != null) {
                    txtPrice.setText(price + " / " + "Year");
                }
                Log.d("Billing", "Price: " + price + ", Currency: " + currencyCode);
                // Display localized prices in the UI
            }
        }
    }
}
