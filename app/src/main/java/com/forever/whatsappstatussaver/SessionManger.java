package com.forever.whatsappstatussaver;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class SessionManger {

    private String isPurchaseUser = "is_purchesh_user";
    private final String LAST_DIALOG_TIME = "LastDialogTime";

    private String KEY_SELECTION_TYPE = "key_selection_type";
    private final String TAG = "SessionManger";

    private final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;

    private static SessionManger instance;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    private Context context;

    public void init(Context context) {
        Log.d(TAG, "init: context " + context);
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SessionManger getInstance() {
        if (instance == null) {
            instance = new SessionManger(); // Instance created when first accessed
        }
        return instance;
    }


    public void setKeySelectionType(int type) {
        Log.d(TAG, "setKeySelectionType: " + type);
        editor.putInt(KEY_SELECTION_TYPE, type);
        editor.apply();
    }

    public int getSelectionType() {
        return sharedPreferences.getInt(KEY_SELECTION_TYPE, 0);
    }

    public void setIsPurchaseUser(boolean isPurchaseUser_) {
        editor.putBoolean(isPurchaseUser, isPurchaseUser_);
        editor.apply();
    }

    public boolean getIsPurchaseUser() {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
            boolean isPurchase = sharedPreferences.getBoolean(isPurchaseUser, false);
            return isPurchase;
        } else {
            return false;
        }
    }

    public boolean checkAndShowRemoveAdDialog() {
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
