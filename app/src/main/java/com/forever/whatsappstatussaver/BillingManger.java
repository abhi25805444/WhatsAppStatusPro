package com.forever.whatsappstatussaver;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class BillingManger implements PurchasesUpdatedListener {
    private static BillingManger instance;
    private Activity activity;
    private static BillingClient billingClient;
    private static boolean isAutoRenew;
    private static final String TAG = "BillingManger";
    private static BillingCallback billingCallback;

    // Private constructor to prevent direct instantiation


    // Public method to get the instance (singleton)
    public static BillingManger getInstance() {
        if (instance == null) {
            instance = new BillingManger();
        }
        return instance;
    }

    public void init(Activity activity) {
        this.activity = activity;
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                queryPurchases();
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try reconnecting or handle connection loss
            }
        });
    }

    private void acknowledgePurchase(Purchase purchase) {

        Log.d(TAG, "acknowledgePurchase: isAcknowledged "+purchase.isAcknowledged());
        // Check if the purchase has already been acknowledged
        if (!purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            if (billingClient != null) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // Purchase acknowledged successfully
                            Log.d(TAG, "Purchase acknowledged");
                        } else {
                            // Handle any error that occurs
                            Log.e(TAG, "Error acknowledging purchase: " + billingResult.getResponseCode());
                        }
                    }
                });

            }
        }
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        Log.d(TAG, "onPurchasesUpdated: user is pro billingCallback " + billingCallback);
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            /*updateUionPurchase();*/
            for (Purchase purchase : list) {
                acknowledgePurchase(purchase);
                SessionManger.setIsPurchaseUser(activity, true);
                isAutoRenew = purchase.isAutoRenewing();
                if (billingCallback != null) {
                    billingCallback.onPurchasesUpdated(billingResult, list);
                }
                /*handlePurchase(purchase);*/
            }
            /* queryPurchases();*/
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "onPurchasesUpdated: user cancel ");
            // User canceled the purchase
        } else {
            Log.d(TAG, "onPurchasesUpdated: other error ");
            SessionManger.setIsPurchaseUser(activity, false);
            // Handle other errors
        }

    }


    public void queryPurchases() {
        if (billingClient != null) {
            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> purchasesList) {
                    boolean isPurchase = false;
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (purchasesList != null && !purchasesList.isEmpty()) {
                            for (Purchase purchase : purchasesList) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    /*updateUionPurchase();*/
                                    Log.d(TAG, "onQueryPurchasesResponse: is subscribed " + purchase.isAutoRenewing());
                                    isAutoRenew = purchase.isAutoRenewing();
                                    isPurchase = true;
                                }
                                if (isPurchase) {
                                    Log.d(TAG, "onQueryPurchasesResponse: is pro ");
                                    SessionManger.setIsPurchaseUser(activity, isPurchase);
                                } else {
                                    Log.d(TAG, "onQueryPurchasesResponse: is free " + isPurchase);
                                    SessionManger.setIsPurchaseUser(activity, isPurchase);
                                }
                            }
                        } else {
                            SessionManger.setIsPurchaseUser(activity, false);
                            Log.d(TAG, "onQueryPurchasesResponse:  No Active SubScription  ");
                            // No active subscriptions
                        }
                    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        SessionManger.setIsPurchaseUser(activity, false);
                        Log.d(TAG, "onQueryPurchasesResponse: user cancel sub");
                    } else {
                        Log.d(TAG, "onQueryPurchasesResponse: error f");
                    }

                    if (billingCallback != null) {
                        billingCallback.onQueryPurchasesResponse(billingResult, purchasesList);
                    }
                }
            });
        }

    }


    public boolean getIsAutoRenew() {
        return isAutoRenew;
    }

    public void lunchPurcheshFlow(Activity activity) {

        List<String> skuList = new ArrayList<>();
        skuList.add("remove_ads_subscription");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        if (skuDetails.getSku().equals("remove_ads_subscription")) {
                            Log.d(TAG, "onSkuDetailsResponse: purchase successful ");
                            // Show the subscription option to the user
                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetails)
                                    .build();
                            billingClient.launchBillingFlow(activity, flowParams);
                        }
                    }
                }
            }
        });
    }

    public void setBillingLisner(BillingCallback billingCallback) {
        this.billingCallback = billingCallback;
    }

    public interface BillingCallback {
        void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list);

        void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> purchasesList);
    }

}
