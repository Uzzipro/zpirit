package com.canatme.zpirit.Activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Adapters.FaqAdapter;
import com.canatme.zpirit.Dataclasses.FaqDto;
import com.canatme.zpirit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {
    private static final String TAG = "FaqActivity";
    private ImageView ivBack;
    private DatabaseReference getFaqs;
    private RecyclerView rvFaqs;
    private List<FaqDto> faqList;
    private AlertDialog loadingDialog;
    private FaqAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(view -> onBackPressed());
        getFaqs = FirebaseDatabase.getInstance().getReference();
        rvFaqs = findViewById(R.id.rvFaqs);
        faqList = new ArrayList<>();
        adapter = new FaqAdapter(this, faqList);
        int numberOfColumns = 1;
        rvFaqs.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        rvFaqs.setAdapter(adapter);
        loadingScreen();
        getFaqs();
    }

    private void getFaqs() {
        getFaqs.child("faqs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        FaqDto faqDto = dataSnapshot.getValue(FaqDto.class);
                        faqList.add(faqDto);
                        adapter.notifyDataSetChanged();
                    }
                    loadingDialog.dismiss();
                } else {
                    loadingDialog.dismiss();

                }

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
        tvLoading.setOnClickListener(view -> {
            animation_view.cancelAnimation();
            loadingDialog.dismiss();
        });
    }
}