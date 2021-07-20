package com.canatme.zpirit.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.canatme.zpirit.Adapters.MyOrdersAdapter;
import com.canatme.zpirit.Adapters.ProductAdapter;
import com.canatme.zpirit.Dataclasses.OrderDto;
import com.canatme.zpirit.Dataclasses.ProductDto;
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

public class OrdersActivity extends AppCompatActivity {
    private List<OrderDto> ordersList;
    private MyOrdersAdapter adapter;
    private RecyclerView rvOrders;
    private ImageView ivBack;
    private DatabaseReference dbRefGetOrders;
    private String phNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(view -> onBackPressed());
        rvOrders = findViewById(R.id.rvOrders);
        phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "nophNumberfound");
        dbRefGetOrders = FirebaseDatabase.getInstance().getReference("orders");

        ordersList = new ArrayList<>();
        adapter = new MyOrdersAdapter(this, ordersList);
        int numberOfColumns = 1;
        rvOrders.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        rvOrders.setAdapter(adapter);
        getOrders();
    }

    private void getOrders()
    {
        Query getOrders = dbRefGetOrders.child(phNumber);
        getOrders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.hasChildren())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        OrderDto orderDto = dataSnapshot.getValue(OrderDto.class);
                        ordersList.add(orderDto);
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