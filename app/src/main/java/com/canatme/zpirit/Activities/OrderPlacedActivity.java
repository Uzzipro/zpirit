package com.canatme.zpirit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;

import java.util.Calendar;
import java.util.Locale;

public class OrderPlacedActivity extends AppCompatActivity {
    private static final String TAG = "OrderPlacedActivity";
    private String deliveryDate;
    private TextView tvEstDeliveryDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        if (getIntent() != null) {
            Intent intent = getIntent();
            deliveryDate = intent.getStringExtra("deliveryDate");
            Log.e(TAG, "onCreate: "+deliveryDate);
//            showToast(welcome_back_msg);
        }
        tvEstDeliveryDateTime = findViewById(R.id.tvEstDeliveryDateTime);


        long dAt = Long.parseLong(deliveryDate);
        int month = getDate(dAt).get(Calendar.MONTH);
        int date = getDate(dAt).get(Calendar.DATE);
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        String dayToday = android.text.format.DateFormat.format("EEEE", getDate(dAt)).toString();
        String am_pm = null;
        Log.e(TAG, "onCreate: "+date + " ," +months[month] + " ,"+getDate(dAt).get(Calendar.YEAR));

        String deliveryDate = "Estimated Delivery Date\n" +date + " , " +months[month] + " , "+getDate(dAt).get(Calendar.YEAR);
        tvEstDeliveryDateTime.setText(deliveryDate);
    }

    private Calendar getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
//        Log.e(TAG, "getDate: "+cal.get(Calendar.HOUR_OF_DAY)+"\n"+cal.get(Calendar.AM_PM));
        return cal;
    }
}