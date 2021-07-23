package com.canatme.zpirit.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Adapters.AddressAdapter;
import com.canatme.zpirit.Dataclasses.AddressDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddressBookActivity extends AppCompatActivity {
    private static final String TAG = "AddressBookActivity";
    private ImageView ivBack;
    private List<AddressDto> addressDataList;
    private RecyclerView rvAddress;
    private AddressAdapter adapter;
    private DatabaseReference addressDbRef;
    private AlertDialog addressDialog;
    private TextView tvAddAddress;
    private String phNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);
        ivBack = findViewById(R.id.ivBack);
        addressDataList = new ArrayList<>();
        addressDbRef = FirebaseDatabase.getInstance().getReference();
        rvAddress = findViewById(R.id.rvProduct);
        tvAddAddress = findViewById(R.id.tvAddAddress);
        adapter = new AddressAdapter(this, addressDataList);
        int numberOfColumns = 1;
        rvAddress.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        rvAddress.setAdapter(adapter);
        tvAddAddress.setOnClickListener(view -> addAddressDialog());
        phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "nophNumberfound");

        ivBack.setOnClickListener(view -> onBackPressed());
        getData();
    }


    private void getData() {
        final String phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
        Query q1 = addressDbRef.child("address_book").child(phNumber);

        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        AddressDto addressData = dataSnapshot1.getValue(AddressDto.class);
                        addressDataList.add(addressData);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

//
//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.edit_address:
////                archive(item);
//                return true;
//            case R.id.delete_address:
////                delete(item);
//                return true;
//            default:
//                return false;
//        }
//    }

    private void addAddressDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogLoading = factory.inflate(R.layout.addaddressdialogbox, null);
        addressDialog = new AlertDialog.Builder(this).create();
        Window window = addressDialog.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addressDialog.setCancelable(true);
        addressDialog.setView(dialogLoading);
        addressDialog.show();
        addressDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView tvCloseDialog = addressDialog.findViewById(R.id.tvCloseDialog);
        Button btSaveAddress = addressDialog.findViewById(R.id.btSaveAddress);
        TextView etHouseNumber, etFloor, etTowerBlock, etHowToReach, etTag;
        etHouseNumber = addressDialog.findViewById(R.id.etHouseNumber);
        etFloor = addressDialog.findViewById(R.id.etFloor);
        etTowerBlock = addressDialog.findViewById(R.id.etTowerBlock);
        etHowToReach = addressDialog.findViewById(R.id.etHowToReach);
        etTag = addressDialog.findViewById(R.id.etTag);

        tvCloseDialog.setOnClickListener(view -> addressDialog.dismiss());
        btSaveAddress.setOnClickListener(view -> {
            if (TextUtils.isEmpty(etHouseNumber.getText().toString().trim())) {
                showToast("Please enter your house number");
            } else if (TextUtils.isEmpty(etFloor.getText().toString().trim())) {
                showToast("Please enter your floor");
            } else if (TextUtils.isEmpty(etTowerBlock.getText().toString().trim())) {
                showToast("Please enter your tower/block number");
            } else if (TextUtils.isEmpty(etTag.getText().toString().trim())) {
                showToast("Please set a tag for your address");
            } else {
                String addressID = phNumber + System.currentTimeMillis();
                String strHouseNumber = etHouseNumber.getText().toString().trim();
                String strFloor = etFloor.getText().toString().trim();
                String strTowerBlock = etTowerBlock.getText().toString().trim();
                String strHowToReach;
                if (!TextUtils.isEmpty(etHowToReach.getText().toString().trim())) {
                    strHowToReach = etHowToReach.getText().toString().trim();
                } else {
                    strHowToReach = "empty";
                }
                String strTag = etTag.getText().toString().trim();
                AddressDto addressDto = new AddressDto(addressID, strHouseNumber, strFloor, strTowerBlock, strHowToReach, strTag);
                addressDbRef.child("address_book").child(phNumber).child(addressID).setValue(addressDto);
                showToast("Address saved");
                adapter.notifyDataSetChanged();
                addressDialog.dismiss();
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}