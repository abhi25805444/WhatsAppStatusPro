package com.forever.whatsappstatussaver;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class SessionManger {

    private static String isPurchaseUser = "is_purchesh_user";
    private static final String LAST_DIALOG_TIME = "LastDialogTime";
    private static final String TAG = "SessionManger";

    private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;


    public static void setIsPurchaseUser(Context context, Boolean isPurchaseUser_) {
        Log.d(TAG, "setIsPurchaseUser: from method " + Thread.currentThread().getStackTrace()[3].getMethodName() + " isPurchaseUser_ " + isPurchaseUser_);
        Log.d(TAG, "setIsPurchaseUser: " + isPurchaseUser_);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isPurchaseUser, isPurchaseUser_);
        editor.apply();
    }

    public static boolean getIsPurchaseUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        boolean isPurchase = sharedPreferences.getBoolean(isPurchaseUser, false);
        return isPurchase;
    }

        public static boolean checkAndShowRemoveAdDialog(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        long lastShownTime = sharedPreferences.getLong(LAST_DIALOG_TIME, 0);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastShownTime >= ONE_DAY_MILLIS) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(LAST_DIALOG_TIME, currentTime);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }
}
