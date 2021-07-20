package com.canatme.zpirit.Fragments.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Activities.AddressBookActivity;
import com.canatme.zpirit.Activities.LoginOrSignupActivity;
import com.canatme.zpirit.Activities.MainActivity;
import com.canatme.zpirit.Activities.OrdersActivity;
import com.canatme.zpirit.Dataclasses.AddressDto;
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
    private ImageView ivDisplayPicture;
    private UserDto userDtoDialogDto;
    private LinearLayoutCompat llAddresses, llOrders, llLogout;
    private String phNumber, fullName, phNumberConcat, userKey;
    private AlertDialog loadingDialog, profileChangeDetailsDialog, logoutDialog;
    private TextView tvName, tvEmailAddress, tvPhNumber, tvBio, tvChangeDetails;

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
        llAddresses = binding.llAddresses;
        tvChangeDetails = binding.tvChangeDetails;
        ivDisplayPicture = binding.ivDisplayPicture;
        llOrders = binding.llOrders;
        llLogout = binding.llLogout;

        phNumber = getActivity().getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "nophNumberfound");
        dbRefPersonalDetails = FirebaseDatabase.getInstance().getReference("users");
        getPersonalDetails();
        llAddresses.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), AddressBookActivity.class);
            startActivity(i);
        });

        tvChangeDetails.setOnClickListener(view -> getPersonalDetailsDialog());
        llOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOrdersActivity();
            }
        });
        llLogout.setOnClickListener(view -> {
//            logout();
            logoutDialog();
        });
        return root;
    }

    private void logout()
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.ACCESS_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        showToast("Logged out successfully");
        Intent i = new Intent(getActivity(), LoginOrSignupActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    private void goToOrdersActivity()
    {
        Intent i = new Intent(getActivity(), OrdersActivity.class);
        startActivity(i);
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
                        userKey = dataSnapshot1.getKey();
                        Log.e(TAG, "onDataChange: Key"+dataSnapshot1.getKey());
                        UserDto userDto = dataSnapshot1.getValue(UserDto.class);
                        userDtoDialogDto = userDto;
                        fullName = userDto.getfName()+" "+userDto.getlName();
                        phNumberConcat = "+91 "+userDto.getPhNumber();
                        tvName.setText(fullName);
                        tvEmailAddress.setText(userDto.getEmailAddress());
                        tvPhNumber.setText(phNumberConcat);
                        tvBio.setText(userDto.getBio());
                        if(userDto.getGender().equalsIgnoreCase("male"))
                        {
                            ivDisplayPicture.setImageResource(R.drawable.ic_avatarmale);
                        }
                        if(userDto.getGender().equalsIgnoreCase("female"))
                        {
                            ivDisplayPicture.setImageResource(R.drawable.ic_avatarfemale);
                        }
                        if(userDto.getGender().equalsIgnoreCase("notsay"))
                        {
                            ivDisplayPicture.setImageResource(R.drawable.ic_profileplaceholder);

                        }

                    }
                    loadingDialog.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }





    private void getPersonalDetailsDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dialogLoading = factory.inflate(R.layout.personal_detail_change_layout, null);
        profileChangeDetailsDialog = new AlertDialog.Builder(getActivity()).create();
        Window window = profileChangeDetailsDialog.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        profileChangeDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileChangeDetailsDialog.setCancelable(true);
        profileChangeDetailsDialog.setView(dialogLoading);
        profileChangeDetailsDialog.show();
        profileChangeDetailsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        TextView etFirstName, etLastName, etEmailAddress, etPhoneNumber, etBio;
        Button btUpdateDetails;

        ImageView tvCloseDialog = profileChangeDetailsDialog.findViewById(R.id.tvCloseDialog);
        etFirstName = profileChangeDetailsDialog.findViewById(R.id.etFirstName);
        etLastName = profileChangeDetailsDialog.findViewById(R.id.etLastName);
        etEmailAddress = profileChangeDetailsDialog.findViewById(R.id.etEmailAddress);
        etPhoneNumber = profileChangeDetailsDialog.findViewById(R.id.etPhoneNumber);
        etBio = profileChangeDetailsDialog.findViewById(R.id.etBio);
        btUpdateDetails = profileChangeDetailsDialog.findViewById(R.id.btUpdateDetails);

        etFirstName.setText(userDtoDialogDto.getfName());
        etLastName.setText(userDtoDialogDto.getlName());
        etEmailAddress.setText(userDtoDialogDto.getEmailAddress());
        etPhoneNumber.setText(userDtoDialogDto.getPhNumber());
        etBio.setText(userDtoDialogDto.getBio());
        tvCloseDialog.setOnClickListener(view -> profileChangeDetailsDialog.dismiss());
        etPhoneNumber.setEnabled(false);
        btUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
                    showToast("Please enter your house number");
                } else if (TextUtils.isEmpty(etLastName.getText().toString().trim())) {
                    showToast("Please enter your floor");
                } else if (TextUtils.isEmpty(etEmailAddress.getText().toString().trim())) {
                    showToast("Please enter your tower/block number");

                } else if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
                    showToast("Please set a tag for your address");
                } else {
//                    String addressID = phNumber + System.currentTimeMillis();
                    String strFirstName = etFirstName.getText().toString().trim();
                    String strLastName = etLastName.getText().toString().trim();
                    String strEmailAddress = etEmailAddress.getText().toString().trim();
                    String strPhNumber = etPhoneNumber.getText().toString().trim();
                    String strBio = etBio.getText().toString().trim();
                    if (!TextUtils.isEmpty(etBio.getText().toString().trim())) {
                        strBio = etBio.getText().toString().trim();
                    } else {
                        strBio = "empty";
                    }

                    userDtoDialogDto.setfName(strFirstName);
                    userDtoDialogDto.setlName(strLastName);
                    userDtoDialogDto.setEmailAddress(strEmailAddress);
                    userDtoDialogDto.setPhNumber(strPhNumber);
                    userDtoDialogDto.setBio(strBio);

                    dbRefPersonalDetails.child(userKey).setValue(userDtoDialogDto);
                    profileChangeDetailsDialog.dismiss();
                    getPersonalDetails();
                    showToast("Information saved");
                }
            }
        });

    }




    private void showToast(String msg)
    {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }






    private void logoutDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dialogLoading = factory.inflate(R.layout.logout_dialog, null);
        logoutDialog = new AlertDialog.Builder(getActivity()).create();
        Window window = logoutDialog.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logoutDialog.setCancelable(true);
        logoutDialog.setView(dialogLoading);
        logoutDialog.show();
        logoutDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        Button noButton, yesButton;
        noButton = logoutDialog.findViewById(R.id.noButton);
        yesButton = logoutDialog.findViewById(R.id.yesButton);

        noButton.setOnClickListener(view -> {
            showToast("Nice choice");
            logoutDialog.dismiss();
        });

        yesButton.setOnClickListener(view -> logout());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}