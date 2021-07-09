package com.canatme.zpirit.Activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Fragments.LoginFragment;
import com.canatme.zpirit.Fragments.SignupFragment;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;

public class LoginOrSignupActivity extends AppCompatActivity {
    private FrameLayout flLoginSignup;
    private LinearLayoutCompat llLogin, llSignup;
    private TextView tvLogin, tvSignup;
    private View vLogin, vSignup;
    private AlertDialog loadingDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginorsignup);
        flLoginSignup = findViewById(R.id.flLoginSignup);
        tvLogin = findViewById(R.id.tvLogin);
        tvSignup = findViewById(R.id.tvSignup);
        vLogin = findViewById(R.id.vLogin);
        vSignup = findViewById(R.id.vSignup);
        llLogin = findViewById(R.id.llLogin);
        llSignup = findViewById(R.id.llSignup);
        flLoginSignup = findViewById(R.id.flLoginSignup);

        /*Default chosen should be login*/
        loginClick();
        /*                              */

        llLogin.setOnClickListener(view -> loginClick());
        llSignup.setOnClickListener(view -> signupClick());

    }

    private void loadingScreen()
    {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogLoading = factory.inflate(R.layout.loading, null);
        loadingDialog = new AlertDialog.Builder(this).create();
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.setView(dialogLoading);
        loadingDialog.show();
        TextView tvLoading = dialogLoading.findViewById(R.id.tvLoading);
        LottieAnimationView animation_view = dialogLoading.findViewById(R.id.animation_view);
        tvLoading.setOnClickListener(view -> {
            animation_view.cancelAnimation();
            loadingDialog.dismiss();
        });
    }

    public void loginClick()
    {
        vLogin.setVisibility(View.VISIBLE);
        vSignup.setVisibility(View.GONE);
        vSignup.animate().alpha(0.0f);
        vLogin.animate().alpha(1.0f);
        insertLoginFragment();
        llLogin.setOnClickListener(null);
        llSignup.setOnClickListener(view -> signupClick());
//        loadingScreen();

    }

    public void signupClick()
    {
        vLogin.setVisibility(View.GONE);
        vSignup.setVisibility(View.VISIBLE);
        vSignup.animate().alpha(1.0f);
        vLogin.animate().alpha(0.0f);
        insertSignupFragment();
        llSignup.setOnClickListener(null);
        llLogin.setOnClickListener(view -> loginClick());
    }
    private void insertLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.flLoginSignup, loginFragment);
        transaction.addToBackStack(Constants.LOGIN_FRAGMENT);
        transaction.commit();
    }

    private void insertSignupFragment() {
        SignupFragment signupFragment = new SignupFragment();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.flLoginSignup, signupFragment);
        transaction.addToBackStack(Constants.SIGNUP_FRAGMENT);
        transaction.commit();
    }
}