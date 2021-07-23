package com.canatme.zpirit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;

import java.util.Calendar;
import java.util.Locale;

public class OrderPlacedActivity extends AppCompatActivity {
    private static final String TAG = "OrderPlacedActivity";
    private String deliveryDate;
    private TextView tvEstDeliveryDateTime;
    private Button btShopMore, btCheckYourOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        if (getIntent() != null) {
            Intent intent = getIntent();
            deliveryDate = intent.getStringExtra("deliveryDate");
//            showToast(welcome_back_msg);
        }
        tvEstDeliveryDateTime = findViewById(R.id.tvEstDeliveryDateTime);
        btShopMore = findViewById(R.id.btShopMore);
        btCheckYourOrders = findViewById(R.id.btCheckYourOrders);


        long dAt = Long.parseLong(deliveryDate);
        int month = getDate(dAt).get(Calendar.MONTH);
        int date = getDate(dAt).get(Calendar.DATE);
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        String dayToday = android.text.format.DateFormat.format("EEEE", getDate(dAt)).toString();
        String am_pm = null;

        String deliveryDate = "Estimated Delivery Date\n" + date + " , " + months[month] + " , " + getDate(dAt).get(Calendar.YEAR);
        tvEstDeliveryDateTime.setText(deliveryDate);
        btShopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderPlacedActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        btCheckYourOrders.setOnClickListener(view -> {
            Intent i = new Intent(OrderPlacedActivity.this, OrdersActivity.class);
            i.putExtra(Constants.FROM_ORDERS, "from_orders");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();

        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//    }

    private Calendar getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return cal;
    }
}