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
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Dataclasses.AddressDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private static String TAG = "AddressAdapter";
    private Activity context;
    private List<AddressDto> cardinfoList;
    private AlertDialog addressDialog;


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
        String fullAddress = cardData.getHouseNumber() + ", " + cardData.getFloor();
        holder.tvFullAddress.setText(fullAddress);
        holder.tvAddressTag.setText(cardData.getTag());
        holder.ivDots.setOnClickListener(view -> openOptionsMenu(holder, cardData, position));
    }

    private void openOptionsMenu(MyViewHolder holder, AddressDto addressDto, int pos) {
        PopupMenu popup = new PopupMenu(context, holder.ivDots);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.address_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit_address:
                    editAddress(addressDto, pos);
                    return true;
                case R.id.delete_address:
                    deleteAddress();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void editAddress(AddressDto addressDto, int pos) {
        addAddressDialog(addressDto, pos);
    }

    private void deleteAddress() {

    }

    private void addAddressDialog(AddressDto addressDto, int pos) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogLoading = factory.inflate(R.layout.addaddressdialogbox, null);
        addressDialog = new AlertDialog.Builder(context).create();
        Window window = addressDialog.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addressDialog.setCancelable(true);
        addressDialog.setView(dialogLoading);
        addressDialog.show();
        addressDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView tvCloseDialog = addressDialog.findViewById(R.id.tvCloseDialog);
        Button btSaveAddress = addressDialog.findViewById(R.id.btSaveAddress);
        TextView etHouseNumber, etFloor, etTowerBlock, etHowToReach, etTag;
        etHouseNumber = addressDialog.findViewById(R.id.etHouseNumber);
        etFloor = addressDialog.findViewById(R.id.etFloor);
        etTowerBlock = addressDialog.findViewById(R.id.etTowerBlock);
        etHowToReach = addressDialog.findViewById(R.id.etHowToReach);
        etTag = addressDialog.findViewById(R.id.etTag);

        etHouseNumber.setText(addressDto.getHouseNumber());
        etFloor.setText(addressDto.getFloor());
        etTowerBlock.setText(addressDto.getTowerBlock());
        etHowToReach.setText(addressDto.getHowToReachOptional());
        etTag.setText(addressDto.getTag());

        DatabaseReference addressDbRef;
        addressDbRef = FirebaseDatabase.getInstance().getReference();
        tvCloseDialog.setOnClickListener(view -> addressDialog.dismiss());
        btSaveAddress.setOnClickListener(view -> {
            if (TextUtils.isEmpty(etHouseNumber.getText().toString().trim())) {
                showToast("Please enter your house number");
            } else if (TextUtils.isEmpty(etFloor.getText().toString().trim())) {
                showToast("Please enter your floor");
            } else if (TextUtils.isEmpty(etTowerBlock.getText().toString().trim())) {
                showToast("Please enter your tower/block number");

            } else if (TextUtils.isEmpty(etTag.getText().toString().trim())) {
                showToast("Please set a tag for your address");
            } else {
                final String phNumber = context.getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
                String addressID = addressDto.getAddressID();
                String strHouseNumber = etHouseNumber.getText().toString().trim();
                String strFloor = etFloor.getText().toString().trim();
                String strTowerBlock = etTowerBlock.getText().toString().trim();
                String strHowToReach = etHowToReach.getText().toString().trim();
                String strTag = etTag.getText().toString().trim();
                addressDto.setHouseNumber(strHouseNumber);
                addressDto.setFloor(strFloor);
                addressDto.setTowerBlock(strTowerBlock);
                addressDto.setHowToReachOptional(strHowToReach);
                addressDto.setTag(strTag);
                addressDbRef.child("address_book").child(phNumber).child(addressID).setValue(addressDto);
                showToast("Address saved");
                notifyDataSetChanged();
                addressDialog.dismiss();
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
        private ImageView ivDots;

        public MyViewHolder(View view) {
            super(view);
            tvAddressTag = view.findViewById(R.id.tvAddressTag);
            tvFullAddress = view.findViewById(R.id.tvFullAddress);
            ivDots = view.findViewById(R.id.ivDots);
        }
    }

}
