package com.canatme.zpirit.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Dataclasses.AddressDto;
import com.canatme.zpirit.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private static String TAG = "AddressAdapter";
    private Activity context;
    private List<AddressDto> cardinfoList;
    private int quantityCountint = 0;

    public AddressAdapter(Activity mContext, List<AddressDto> cardinfoList) {
        this.context = mContext;
        this.cardinfoList = cardinfoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.addressbooklayout, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final AddressDto cardData = cardinfoList.get(position);
        holder.tvAddressTag.setText(cardData.getTag());
        String fullAddress = cardData.getHouseNumber()+", "+cardData.getFloor();
        holder.tvFullAddress.setText(fullAddress);
        holder.tvAddressTag.setText(cardData.getTag());
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

        public MyViewHolder(View view) {
            super(view);
            tvAddressTag = view.findViewById(R.id.tvAddressTag);
            tvFullAddress = view.findViewById(R.id.tvFullAddress);
        }
    }

}
