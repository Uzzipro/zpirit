package com.canatme.zpirit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        final boolean logininfo = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getBoolean(Constants.LOGIN_INFO, false);
        Log.e(TAG, "onCreate: "+logininfo);
        if(logininfo)
        {
            Intent loggedinActivity = new Intent(SplashActivity.this, MainActivity.class);
            loggedinActivity.putExtra(Constants.WELCOME_BACK, "Welcome Back");
            startActivity(loggedinActivity);
        }
        else
        {
            Intent loggedinActivity = new Intent(SplashActivity.this, LoginOrSignupActivity.class);
            startActivity(loggedinActivity);
        }

//        final Handler handler = new Handler();
//        //Change this with login check(if the user is logged in or not)
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(SplashActivity.this, LoginOrSignupActivity.class);
//                startActivity(i);
//            }
//        }, 1000);
//        //Change this with login check(if the user is logged in or not)
//
    }
}