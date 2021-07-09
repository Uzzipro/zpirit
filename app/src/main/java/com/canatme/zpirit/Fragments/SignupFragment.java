package com.canatme.zpirit.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    /*Strings*/
    private final static String TAG = "SignupFragment";
    /**/

    /*Alert dialog box for loading screen*/
    private AlertDialog loadingDialog;
    /**/

    /*Boolean for password match*/
    boolean passwordMatch = false;
    /**/

    /*Database Components*/
    private DatabaseReference dbRefSignup;
    /**/

    /*Edit texts*/
    private EditText etFirstName, etLastName, etPhNumber, etEmailAddress, etPassword, etConfirmPassword;
    /**/

    /*Views*/
    View parentLayout;
    /**/
    /*Buttons*/
    private Button btSignUp;
    /**/

    /*TextViews*/
    private TextView tvPasswordMatch;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        /*Initializing all the Edit texts*/
        etFirstName = v.findViewById(R.id.etFirstName);
        etLastName = v.findViewById(R.id.etLastName);
        etPhNumber = v.findViewById(R.id.etPhNumber);
        etEmailAddress = v.findViewById(R.id.etEmailAddress);
        etPassword = v.findViewById(R.id.etPassword);
        etConfirmPassword = v.findViewById(R.id.etConfirmPassword);
        btSignUp = v.findViewById(R.id.btSignUp);
        tvPasswordMatch = v.findViewById(R.id.tvPasswordMatch);
        parentLayout = v.findViewById(android.R.id.content);

        dbRefSignup = FirebaseDatabase.getInstance().getReference("users");
        btSignUp.setOnClickListener(view -> btSignUpClick());
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e(TAG, "TextWatcher: beforeTextChanged: " + charSequence.toString());

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (etPassword.getText().toString().trim().equals(charSequence.toString())) {
                    tvPasswordMatch.setText("Password Matched!");
                    tvPasswordMatch.setTextColor(Color.parseColor("#00FF00"));
                    passwordMatch = true;
                } else {
                    tvPasswordMatch.setText("Password not matched");
                    tvPasswordMatch.setTextColor(Color.parseColor("#FF0000"));
                    passwordMatch = false;

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return v;
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
        tvLoading.setText("Signing you up...");
    }

    private void btSignUpClick() {
        if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
            showToast("Please fill first name");
        } else if (TextUtils.isEmpty(etLastName.getText().toString().trim())) {
            showToast("Please fill last name");
        } else if (TextUtils.isEmpty(etPhNumber.getText().toString().trim())) {
            showToast("Please enter your Phone number");
        } else if (TextUtils.isEmpty(etEmailAddress.getText().toString().trim())) {
            showToast("Please enter your Email address");
        } else if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            showToast("Please enter a Password");
        } else if (TextUtils.isEmpty(etConfirmPassword.getText().toString().trim())) {
            showToast("Please Confirm your password");
        } else {
            if (passwordMatch) {
                loadingScreen();
                Query checkIfAlreadyRegistered = dbRefSignup.orderByChild("phNumber").equalTo(etPhNumber.getText().toString().trim());
                checkIfAlreadyRegistered.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                UserDto UD = dataSnapshot1.getValue(UserDto.class);
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Phone number already exists. Please login", Snackbar.LENGTH_LONG)
                                        .setAction("Login", view -> ((LoginOrSignupActivity)getActivity()).loginClick())
                                        .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.orange))
                                        .show();
                                loadingDialog.dismiss();

                            }
                        } else {
                            UserDto registerUserDto = new UserDto(etFirstName.getText().toString().trim(), etLastName.getText().toString().trim(), etPhNumber.getText().toString().trim(), etEmailAddress.getText().toString().trim(), etPassword.getText().toString().trim(), "0", "0", "0", "false");
                            dbRefSignup.push().setValue(registerUserDto);
                            showToast("Registered");

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


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            } else {
                showToast("Password doesn't match");
            }

        }

    }
}