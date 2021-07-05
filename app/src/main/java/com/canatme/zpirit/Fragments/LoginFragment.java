package com.canatme.zpirit.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.canatme.zpirit.Activities.MainActivity;
import com.canatme.zpirit.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private EditText etEmailAddress, etPassword;
    private Button btLogin;



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

         etEmailAddress = v.findViewById(R.id.etEmailAddress);
         etPassword = v.findViewById(R.id.etPassword);
         btLogin = v.findViewById(R.id.btLogin);
         btLogin.setOnClickListener(view -> checkCredentials());

        return v;
    }

    private void checkCredentials()
    {
        String stEmailAddress, stPassword;

        stEmailAddress = etEmailAddress.getText().toString().trim();
        stPassword = etPassword.getText().toString().trim();

        if(stEmailAddress.equals("ujjwal6669") || stPassword.equals("ujjwal6669"))
        {
            Intent i = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(i);
        }
        else
        {
            Toast.makeText(getActivity(), "Please enter correct credentials", Toast.LENGTH_LONG).show();
        }

    }
}