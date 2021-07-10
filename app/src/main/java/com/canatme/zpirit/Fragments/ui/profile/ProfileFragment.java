package com.canatme.zpirit.Fragments.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Dataclasses.UserDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.canatme.zpirit.databinding.FragmentProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private DatabaseReference dbRefPersonalDetails;
    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private String phNumber, fullName, phNumberConcat;
    private AlertDialog loadingDialog;
    private TextView tvName, tvEmailAddress, tvPhNumber, tvBio;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvName = binding.tvName;
        tvEmailAddress = binding.tvEmailAddress;
        tvPhNumber = binding.tvPhNumber;
        tvBio = binding.tvBio;

        phNumber = getActivity().getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "nophNumberfound");
        dbRefPersonalDetails = FirebaseDatabase.getInstance().getReference("users");
        getPersonalDetails();
        return root;
    }

    private void loadingScreen() {
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
        tvLoading.setText("Loading your information....");
    }
    private void getPersonalDetails()
    {
        loadingScreen();
        Query getPersonalDetails = dbRefPersonalDetails.orderByChild("phNumber").equalTo(phNumber);
        getPersonalDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.hasChildren())
                {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        UserDto userDto = dataSnapshot1.getValue(UserDto.class);
                        Log.e(TAG, "onDataChange: "+userDto.getfName());
                        fullName = userDto.getfName()+" "+userDto.getlName();
                        phNumberConcat = "+91 "+userDto.getPhNumber();
                        tvName.setText(fullName);
                        tvEmailAddress.setText(userDto.getEmailAddress());
                        tvPhNumber.setText(phNumberConcat);
                        tvBio.setText(userDto.getBio());
                    }
                    loadingDialog.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}