package com.canatme.zpirit.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Adapters.CartAdapter;
import com.canatme.zpirit.Dataclasses.CartDto;
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

public class CartActivity extends AppCompatActivity {
    private ImageView ivBack;
    private List<CartDto> cartDataList;
    private RecyclerView rvProduct;
    private CartAdapter adapter;
    private Button btMakePayment;
    private LinearLayoutCompat llNoItems, llRv;
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ivBack = findViewById(R.id.ivBack);
        btMakePayment = findViewById(R.id.btMakePayment);
        ivBack.setOnClickListener(view -> onBackPressed());
        rvProduct = findViewById(R.id.rvProduct);
        llNoItems = findViewById(R.id.llNoItems);
        llRv = findViewById(R.id.llRv);
        cartDataList = new ArrayList<>();
        adapter = new CartAdapter(this, cartDataList);
        int numberOfColumns = 1;
        rvProduct.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        rvProduct.setAdapter(adapter);

        btMakePayment.setOnClickListener(view -> {
            Intent i = new Intent(CartActivity.this, PaymentActivity.class);
            startActivity(i);
        });
        loadingScreen();
        getData();
    }

    private void getData() {
        final DatabaseReference mgetProductRef;
        mgetProductRef = FirebaseDatabase.getInstance().getReference();
        final String phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
        Query q1 = mgetProductRef.child("cart_table").child(phNumber);

        q1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        CartDto cartData = dataSnapshot1.getValue(CartDto.class);
                        cartDataList.add(cartData);
                        adapter.notifyDataSetChanged();
                    }
                    if (cartDataList.size() == 0) {
                        llNoItems.setVisibility(View.VISIBLE);
                        llRv.setVisibility(View.GONE);
                        btMakePayment.setVisibility(View.GONE);
                    } else {
                        llNoItems.setVisibility(View.GONE);
                        llRv.setVisibility(View.VISIBLE);
                        btMakePayment.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    if (cartDataList.size() == 0) {
                        llNoItems.setVisibility(View.VISIBLE);
                        llRv.setVisibility(View.GONE);
                        btMakePayment.setVisibility(View.GONE);
                    } else {
                        llNoItems.setVisibility(View.GONE);
                        llRv.setVisibility(View.VISIBLE);
                        btMakePayment.setVisibility(View.VISIBLE);
                    }
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadingScreen() {
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
        animation_view.setAnimation(R.raw.two_bottles);
        tvLoading.setText("Initializing payment");
    }
}