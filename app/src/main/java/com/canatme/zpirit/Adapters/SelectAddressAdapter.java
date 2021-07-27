package com.canatme.zpirit.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Activities.PaymentActivity;
import com.canatme.zpirit.Dataclasses.AddressDto;
import com.canatme.zpirit.R;

import java.util.List;

public class SelectAddressAdapter extends RecyclerView.Adapter<SelectAddressAdapter.MyViewHolder> {

    private static final String TAG = "SelectAddressAdapter";
    private final Activity context;
    private final List<AddressDto> cardinfoList;
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
                ((PaymentActivity) context).getDeliverAddress(cardData.getAddressID());
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

        private final TextView tvAddressTag;
        private final TextView tvFullAddress;
        private final LinearLayoutCompat llAddressParent;

        public MyViewHolder(View view) {
            super(view);
            tvAddressTag = view.findViewById(R.id.tvAddressTag);
            tvFullAddress = view.findViewById(R.id.tvFullAddress);
            llAddressParent = view.findViewById(R.id.llAddressParent);
        }
    }

}
