package com.canatme.zpirit.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Activities.PaymentActivity;
import com.canatme.zpirit.Dataclasses.AddressDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SelectAddressAdapter extends RecyclerView.Adapter<SelectAddressAdapter.MyViewHolder> {

    private static String TAG = "SelectAddressAdapter";
    private Activity context;
    private List<AddressDto> cardinfoList;
    private AlertDialog addressDialog;


    public SelectAddressAdapter(Activity mContext, List<AddressDto> cardinfoList) {
        this.context = mContext;
        this.cardinfoList = cardinfoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.addressbooklayout2, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final AddressDto cardData = cardinfoList.get(position);
        holder.tvAddressTag.setText(cardData.getTag());
        String fullAddress = cardData.getHouseNumber() + ", " + cardData.getFloor();
        holder.tvFullAddress.setText(fullAddress);
        holder.tvAddressTag.setText(cardData.getTag());
        holder.llAddressParent.setOnClickListener(view -> {
            if (context instanceof PaymentActivity) {
                ((PaymentActivity)context).getDeliverAddress(cardData.getAddressID());
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return cardinfoList.size();
    }

    public void clearList() {
        cardinfoList.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAddressTag, tvFullAddress;
        private LinearLayoutCompat llAddressParent;

        public MyViewHolder(View view) {
            super(view);
            tvAddressTag = view.findViewById(R.id.tvAddressTag);
            tvFullAddress = view.findViewById(R.id.tvFullAddress);
            llAddressParent = view.findViewById(R.id.llAddressParent);
        }
    }

}
