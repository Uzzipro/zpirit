package com.canatme.zpirit.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Dataclasses.CartDto;
import com.canatme.zpirit.Dataclasses.ProductDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailsProductAdapter extends RecyclerView.Adapter<OrderDetailsProductAdapter.MyViewHolder> {

    private static String TAG = "CartAdapter";
    private Activity context;
    private List<CartDto> cardinfoList;
    private int quantityCountint = 0;


    public OrderDetailsProductAdapter(Activity mContext, List<CartDto> cardinfoList) {
        this.context = mContext;
        this.cardinfoList = cardinfoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);
        return new MyViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final CartDto cardData = cardinfoList.get(position);

        if (cardData.getPd().getProductImg() != null) {
            Picasso.get().load(cardData.getPd().getProductImg()).into(holder.ivProductImg);
        }
        Log.e(TAG, "onBindViewHolder: "+cardData.getProductQuantity());

        String priceMultipled = cardData.getProductQuantity()+" x "+"Rs. "+cardData.getPd().getProductPrice();
        holder.tvProductPrice.setText(priceMultipled);
        holder.tvProductName.setText(cardData.getPd().getProductName());
        String grandTotal = "Rs. "+cardData.getProductTotalPrice();
        holder.tvGrandTotal.setText(grandTotal);

    }

    @Override
    public int getItemCount() {
        return cardinfoList.size();
    }

    public void clearList() {
        cardinfoList.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvProductName, tvProductPrice, tvGrandTotal;
        private ImageView ivProductImg;

        public MyViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tvProductName);
            tvProductPrice = view.findViewById(R.id.tvProductPrice);
//            tvViewMoreDetails = view.findViewById(R.id.tvViewMoreDetails);
            ivProductImg = view.findViewById(R.id.ivProductImg);
            tvGrandTotal = view.findViewById(R.id.tvGrandTotal);
        }
    }
}
