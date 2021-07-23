package com.canatme.zpirit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.common.collect.Lists;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        createNotificationChannel();
        final boolean logininfo = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getBoolean(Constants.LOGIN_INFO, false);
        if(logininfo)
        {
            Intent loggedinActivity = new Intent(SplashActivity.this, MainActivity.class);
            loggedinActivity.putExtra(Constants.WELCOME_BACK, "Welcome Back");
            startActivity(loggedinActivity);
            finish();
        }
        else
        {
            Intent loggedinActivity = new Intent(SplashActivity.this, LoginOrSignupActivity.class);
            startActivity(loggedinActivity);
            finish();
        }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel marketing = new NotificationChannel(
                    getString(R.string.notif_channel_marketing_id),
                    getString(R.string.notif_channel_marketing),
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel alerts = new NotificationChannel(
                    getString(R.string.notif_channel_alerts_id),
                    getString(R.string.notif_channel_alerts), NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel reminders = new NotificationChannel(
                    getString(R.string.notif_channel_reminders_id),
                    getString(R.string.notif_channel_reminders),
                    NotificationManager.IMPORTANCE_DEFAULT);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannels(Lists.newArrayList(marketing, alerts,
                        reminders));
            }
        }
    }
}