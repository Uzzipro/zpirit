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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private static String TAG = "CartAdapter";
    private Activity context;
    private List<CartDto> cardinfoList;
    private int quantityCountint = 0;


    public CartAdapter(Activity mContext, List<CartDto> cardinfoList) {
        this.context = mContext;
        this.cardinfoList = cardinfoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.extra_layout_product_view, parent, false);
        return new MyViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final CartDto cardData = cardinfoList.get(position);

        if (cardData.getPd().getProductImg() != null) {
            Picasso.get().load(cardData.getPd().getProductImg()).into(holder.ivProductImg);
        }
        String price = "Rs. "+cardData.getPd().getProductPrice();
        holder.tvProductPrice.setText(price);
        holder.tvProductName.setText(cardData.getPd().getProductName());
        holder.btMinus.setOnClickListener(v -> setMinusQuantity(holder, position));

        holder.btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlusQuantity(holder, position);
            }
        });
        getData(holder, position);
    }


    private void setMinusQuantity(final MyViewHolder holder, int pos) {
        quantityCountint = quantityCountint - 1;
        addToCart2(holder, false, pos);
    }

    public void setPlusQuantity(final MyViewHolder holder, int pos) {
        quantityCountint = quantityCountint + 1;
        addToCart2(holder, true, pos);
    }

    private void getData(final MyViewHolder holder, int pos) {
        final String pKey;
        CartDto cdc = cardinfoList.get(pos);

        ProductDto productsClass = cdc.getPd();
        pKey = productsClass.getProductID();

        final String phNumber = context.getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("cart_table").child(phNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                        CartDto cx = dataSnapshot2.getValue(CartDto.class);
                        if (TextUtils.equals(cx.getProductKey(), pKey)) {
                            quantityCountint = Integer.parseInt(cx.getProductQuantity());
                            holder.quantityCount.setText(cx.getProductQuantity());
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToCart2(final MyViewHolder holder, final boolean minusOrPlus, final int pos) {
        final String phNumber = context.getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Adding products to your cart.......");
        progressDialog.show();
        CartDto cdc2 = cardinfoList.get(pos);


        final ProductDto productsClass = cdc2.getPd();
        final String productDbKey = dbRef.child("cart_table").child(phNumber).push().getKey();

        dbRef.child("cart_table").child(phNumber).orderByChild("productKey").equalTo(productsClass.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {//cart has already been made
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        CartDto c3 = dataSnapshot1.getValue(CartDto.class);
                        int quantityDb = Integer.parseInt(c3.getProductQuantity());
                        int totalprice = 0;
                        int productPrice = Integer.parseInt(productsClass.getProductPrice());
                        if (quantityDb != 0) {
                            if (minusOrPlus) {
                                if(quantityDb == 1)
                                {
                                    Toast.makeText(context, "Quantity for test purposes is set to only 1 for whiskey", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();

                                }
                                else
                                {

                                quantityDb = quantityDb + 1;
                                totalprice = productPrice * quantityDb;
                                cardinfoList.remove(pos);
                                cardinfoList.clear();
                                c3.setProductTotalPrice(String.valueOf(totalprice));
                                c3.setProductQuantity(String.valueOf(quantityDb));
//                                dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productTotalPrice").setValue(String.valueOf(totalprice));
//                                dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productQuantity").setValue(String.valueOf(quantityDb));
                                dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).setValue(c3);
                                progressDialog.dismiss();
                                }

                            }
                            if (minusOrPlus == false) {
                                if (quantityDb == 1) {
                                    cardinfoList.remove(pos);
                                    cardinfoList.clear();
                                    notifyDataSetChanged();
                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).removeValue();
                                    quantityDb = 0;
                                    Toast.makeText(context, "Items cannot go below 0", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    quantityDb = quantityDb - 1;
                                    totalprice = productPrice * quantityDb;
//                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productTotalPrice").setValue(String.valueOf(totalprice));
//                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productQuantity").setValue(String.valueOf(quantityDb));
                                    cardinfoList.remove(pos);
                                    cardinfoList.clear();
                                    c3.setProductTotalPrice(String.valueOf(totalprice));
                                    c3.setProductQuantity(String.valueOf(quantityDb));
                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).setValue(c3);
                                    progressDialog.dismiss();

                                }

                            }
                            holder.quantityCount.setText(String.valueOf(quantityDb));
                        }

                    }

                } else//cart has not been made yet
                {
                    if (minusOrPlus) {
                        quantityCountint = 1;
                        holder.quantityCount.setText(String.valueOf(quantityCountint));
                        CartDto c4 = new CartDto();
                        c4.setPd(productsClass);
                        c4.setProductQuantity(String.valueOf(quantityCountint));
                        c4.setProductTotalPrice(productsClass.getProductPrice());
                        c4.setNodeKey(productDbKey);
                        c4.setProductKey(productsClass.getProductID());
                        progressDialog.dismiss();
                        dbRef.child("cart_table").child(phNumber).child(productDbKey).setValue(c4);
                    } else {
                        Toast.makeText(context, "Items cannot go below 0", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }

//                dbRef.removeEventListener(val
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    public int getItemCount() {
        return cardinfoList.size();
    }

    public void clearList() {
        cardinfoList.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvProductName, tvProductPrice, quantityCount;
        private ImageView ivProductImg;
        private Button btMinus, btPlus;

        public MyViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tvProductName);
            tvProductPrice = view.findViewById(R.id.tvProductPrice);
//            tvViewMoreDetails = view.findViewById(R.id.tvViewMoreDetails);
            ivProductImg = view.findViewById(R.id.ivProductImg);
            btMinus = view.findViewById(R.id.minusQuantity);
            btPlus = view.findViewById(R.id.plusQuantity);
            quantityCount = view.findViewById(R.id.quantityCount);
        }
    }
}
