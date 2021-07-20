package com.canatme.zpirit.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Activities.OrderDetailActivity;
import com.canatme.zpirit.Dataclasses.OrderDto;
import com.canatme.zpirit.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    //AM = 0
    //PM = 1
    private static String TAG = "ProductAdapter";
    private Activity context;
    private List<OrderDto> cardinfoList;
    private int quantityCountint = 0;


    public MyOrdersAdapter(Activity mContext, List<OrderDto> cardinfoList) {
        this.context = mContext;
        this.cardinfoList = cardinfoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderslayout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final OrderDto orderDataClass = cardinfoList.get(position);
        long dAt = Long.parseLong(orderDataClass.getOrderTime());
        holder.tvOrderNumber.setText(orderDataClass.getOrderID());
        String items = "";
        int month = getDate(dAt).get(Calendar.MONTH);
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        String dayToday = DateFormat.format("EEEE", getDate(dAt)).toString();
        String am_pm = null;

        if (getDate(dAt).get(Calendar.AM_PM) == 0 || getDate(dAt).get(Calendar.AM_PM) == 1) {
            if (getDate(dAt).get(Calendar.AM_PM) == 0) {
                am_pm = "AM";
            }
            if (getDate(dAt).get(Calendar.AM_PM) == 1) {
                am_pm = "PM";
            }
            String hour = String.valueOf(getDate(dAt).get(Calendar.HOUR_OF_DAY));
            String minute = String.valueOf(getDate(dAt).get(Calendar.MINUTE));
            if (hour.length() == 1) {
                hour = "0" + hour;
            }
            if (minute.length() == 1) {
                minute = "0" + minute;
            }
//            holder.tvOrderPlacedTimeAndDate.setText("Time: " + hour + ":" + minute + am_pm);
        }
        holder.tvOrderPlacedTimeAndDate.setText(getDate(dAt).get(Calendar.DAY_OF_MONTH) + " " + months[month] + ", " + dayToday + ", " + getDate(dAt).get(Calendar.YEAR));
        holder.tvOrderStatus.setText(orderDataClass.getPaymentStatus());
        holder.tvGrandTotal.setText("Rs. " + orderDataClass.getGrandTotal());

        holder.orderCard.setOnClickListener(view -> {
            Intent i = new Intent(context, OrderDetailActivity.class);
            i.putExtra("orderID", orderDataClass.getOrderID());
            i.putExtra("phNumber", orderDataClass.getPhNumber());
            context.startActivity(i);

        });

        for (int i = 0; i < orderDataClass.getCdc().size(); i++) {
            items = items + ", " + orderDataClass.getCdc().get(i).getProductQuantity() + " x " + orderDataClass.getCdc().get(i).getPd().getProductName();
        }

        String subStringItems = items.substring(2);
        Log.e(TAG, "onBindViewHolder: "+subStringItems);
        holder.tvItems.setText(subStringItems);
    }

    private Calendar getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
//        Log.e(TAG, "getDate: "+cal.get(Calendar.HOUR_OF_DAY)+"\n"+cal.get(Calendar.AM_PM));
        return cal;
    }

    @Override
    public int getItemCount() {
        return cardinfoList.size();
    }

    public void clearList() {
        cardinfoList.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvOrderNumber, tvOrderPlacedTimeAndDate, tvOrderStatus, tvItems, tvGrandTotal;
        private ConstraintLayout orderCard;

        public MyViewHolder(View view) {
            super(view);
            tvOrderNumber = view.findViewById(R.id.tvOrderNumber);
            tvOrderPlacedTimeAndDate = view.findViewById(R.id.tvOrderPlacedTimeAndDate);
            tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
            tvItems = view.findViewById(R.id.tvItems);
            tvGrandTotal = view.findViewById(R.id.tvGrandTotal);
            orderCard = view.findViewById(R.id.clOrderParent);
        }
    }
}
