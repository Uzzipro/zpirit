package com.canatme.zpirit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Adapters.CartAdapter;
import com.canatme.zpirit.Adapters.OrderDetailsProductAdapter;
import com.canatme.zpirit.Dataclasses.CartDto;
import com.canatme.zpirit.Dataclasses.OrderDto;
import com.canatme.zpirit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailActivity";
    private String orderID, phNumber;
    private ImageView ivBack;
    private RecyclerView rvProduct;
    private TextView tvProductTotal, tvDeliveryCharge, tvGrandTotal;
    private DatabaseReference dbRefGetOrder;
    private List<CartDto> cartDataList;
    private OrderDetailsProductAdapter adapter;
    private int productTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        if (getIntent() != null) {
            Intent intent = getIntent();
            orderID = intent.getStringExtra("orderID");
            phNumber = intent.getStringExtra("phNumber");
            Log.e(TAG, "onCreate: " + orderID + " " + phNumber);
        }
        dbRefGetOrder = FirebaseDatabase.getInstance().getReference();
        ivBack = findViewById(R.id.ivBack);
        rvProduct = findViewById(R.id.rvProduct);
        tvProductTotal = findViewById(R.id.tvProductTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);


        cartDataList = new ArrayList<>();
        adapter = new OrderDetailsProductAdapter(this, cartDataList);
        int numberOfColumns = 1;
        rvProduct.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        rvProduct.setAdapter(adapter);

        ivBack.setOnClickListener(view -> onBackPressed());
        getOrdderDetails();
    }

    private void getOrdderDetails() {
        Query getOrderDetails = dbRefGetOrder.child("orders").child(phNumber).child(orderID);
        getOrderDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    OrderDto orderDto = snapshot.getValue(OrderDto.class);
                    for(int z = 0; z<orderDto.getCdc().size(); z++)
                    {
                        cartDataList.add(orderDto.getCdc().get(z));
                        adapter.notifyDataSetChanged();
                        productTotal = productTotal + Integer.parseInt(orderDto.getCdc().get(z).getProductTotalPrice());
                    }
                    Log.e(TAG, "onDataChange: "+productTotal);
                    String strDeliveryCharge = "Rs. "+orderDto.getDeliveryCharge();
                    String strGrandTotal = "Rs. "+orderDto.getGrandTotal();
                    String strProductTotal = "Rs. "+String.valueOf(productTotal);
                    tvProductTotal.setText(strProductTotal);
                    tvDeliveryCharge.setText(strDeliveryCharge);
                    tvGrandTotal.setText(strGrandTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}