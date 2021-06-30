package com.canatme.zpirit.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.canatme.zpirit.R;

public class LoginOrSignupActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginorsignup);


// clear FLAG_TRANSLUCENT_STATUS flag:


// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window


// finally change the color


//        Window window = LoginOrSignupActivity.this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(LoginOrSignupActivity.this,R.color.white));

    }
}