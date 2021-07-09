package com.canatme.zpirit.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Activities.LoginOrSignupActivity;
import com.canatme.zpirit.Activities.MainActivity;
import com.canatme.zpirit.Activities.SplashActivity;
import com.canatme.zpirit.Dataclasses.UserDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    /*Strings*/
    private static final String TAG = "LoginFragment";
    /**/

    /*Alert dialog box for loading screen*/
    private AlertDialog loadingDialog;
    /**/

    /*Components*/
    private EditText etPhNumber, etPassword;
    private Button btLogin;
    /**/

    private DatabaseReference dbRefLogin;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        dbRefLogin = FirebaseDatabase.getInstance().getReference("users");
        etPhNumber = v.findViewById(R.id.etPhNumber);
        etPassword = v.findViewById(R.id.etPassword);
        btLogin = v.findViewById(R.id.btLogin);
        btLogin.setOnClickListener(view -> checkCredentials());

        return v;
    }

    private void loadingScreen()
    {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dialogLoading = factory.inflate(R.layout.loading, null);
        loadingDialog = new AlertDialog.Builder(getActivity()).create();
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
        tvLoading.setText("Logging you in...");
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void checkCredentials() {
        loadingScreen();
        if (TextUtils.isEmpty(etPhNumber.getText().toString().trim())) {
            showToast("Please enter your phone number");
        } else if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            showToast("Please enter a password");
        } else {
            Query checkCredentials = dbRefLogin.orderByChild("phNumber").equalTo(etPhNumber.getText().toString().trim());
            checkCredentials.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            UserDto UD = dataSnapshot1.getValue(UserDto.class);
                            Log.e(TAG, "onDataChange: " + UD.getEmailAddress());
                            if (UD.getPassword().equals(etPassword.getText().toString().trim())) {
                                Log.e(TAG, "onDataChange: correct password");
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.
                                        ACCESS_PREFS, MODE_PRIVATE).edit();
                                editor.putString(Constants.PH_NUMBER, etPhNumber.getText().toString().trim());
                                editor.putBoolean(Constants.LOGIN_INFO, true);
                                editor.commit();
                                Intent loggedinActivity = new Intent(getActivity(), MainActivity.class);
                                loggedinActivity.putExtra(Constants.WELCOME_BACK, "Welcome Back");
                                loadingDialog.dismiss();
                                startActivity(loggedinActivity);
                                getActivity().finish();

                            } else {
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Wrong password", Snackbar.LENGTH_LONG)
//                                        .setAction("Sign-up", view -> ((LoginOrSignupActivity) getActivity()).signupClick())
                                        .show();
                                loadingDialog.dismiss();
                            }
                        }
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Phone number doesn't exists. Please Sign-up", Snackbar.LENGTH_LONG)
                                .setAction("Sign-up", view -> ((LoginOrSignupActivity) getActivity()).signupClick())
                                .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.orange))
                                .show();
                        loadingDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        }


    }
}