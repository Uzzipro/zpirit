package com.canatme.zpirit.Service;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.canatme.zpirit.Utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {


    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    Context context;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        storeRegIdInPref(s);
    }


    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.
                ACCESS_PREFS, MODE_PRIVATE).edit();
        editor.putString(Constants.FCM_TOKEN, token);
        editor.putBoolean(Constants.LOGIN_INFO, true);
        editor.commit();
    }
}