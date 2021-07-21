package com.canatme.zpirit.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Adapters.CartAdapter;
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
import java.util.HashMap;
import java.util.List;

public class FaqActivity extends AppCompatActivity {
    private static final String TAG = "FaqActivity";
    private ImageView ivBack;
    private DatabaseReference getFaqs;
    private RecyclerView rvFaqs;
    private List<FaqDto> faqList;
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




        getFaqs();
    }


    private void getFaqs() {
        getFaqs.child("faqs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.hasChildren())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        FaqDto faqDto = dataSnapshot.getValue(FaqDto.class);
                        Log.e(TAG, "onDataChange: "+faqDto.getQuestion());
                        faqList.add(faqDto);
                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}