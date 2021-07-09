package com.canatme.zpirit.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.canatme.zpirit.R;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    /*Strings*/
    private final static String TAG = "SignupFragment";
    /**/

    /*Database Components*/
    private DatabaseReference dbRefSignup;
    /**/

    /*Edit texts*/
    private EditText etFirstName, etLastName, etPhNumber, etEmailAddress, etPassword, etConfirmPassword;
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
        btSignUp.setOnClickListener(view -> btSignUpClick());

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e(TAG, "TextWatcher: beforeTextChanged: "+charSequence.toString());

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.e(TAG, "TextWatcher: onTextChanged: "+charSequence.toString());

                if(etPassword.getText().toString().trim().equals(charSequence.toString()))
                {
                    Log.e(TAG, "onTextChanged: Matched!");
                    tvPasswordMatch.setText("Password Matched!");
                    tvPasswordMatch.setTextColor(Color.parseColor("#00FF00"));

                }
                else
                {
                    Log.e(TAG, "onTextChanged: Not Matched!");
                    tvPasswordMatch.setText("Password not matched");
                    tvPasswordMatch.setTextColor(Color.parseColor("#FF0000"));


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                Log.e(TAG, "afterTextChanged: "+charSequence.toString());
            }
        });

        return v;
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
        } else if(TextUtils.isEmpty(etConfirmPassword.getText().toString().trim()))
        {
            showToast("Please Confirm your password");
        }
        else {
            showToast("Entering the app");
        }

    }
}