package com.canatme.zpirit.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Dataclasses.ProductDto;
import com.canatme.zpirit.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private static String TAG = "ProductAdapter";
    private Context context;
    private List<ProductDto> cardinfoList;
    private int quantityCountint = 0;


    public ProductAdapter(Context mContext, List<ProductDto> cardinfoList) {
        this.context = mContext;
        this.cardinfoList = cardinfoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_showcase_layout, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ProductDto cardData = cardinfoList.get(position);

        // https://i.ibb.co/1s75D5L/allseasonspng.png

        if (cardData.getProductImg() != null) {
            Picasso.get().load(cardData.getProductImg()).into(holder.ivProductImg);
        }

        Log.e(TAG, "onBindViewHolder: "+cardData.getProductID());
        holder.tvProductName.setText(cardData.getProductName());
        holder.tvProductType.setText(cardData.getProductType());
        holder.tvProductMeasurement.setText(cardData.getProductMeasurement());
        holder.tvProductPrice.setText("Rs. " + cardData.getProductPrice());

    }


//    private void getData(final MyViewHolder holder, int pos) {
//        final String pKey;
//        ProductDto productsClass = cardinfoList.get(pos);
//        pKey = productsClass.getProductKeyDto();
//
//        final String phNumber = context.getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PHONE_NUMBER, "No phone number detected");
//        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
//        db.child("cart_table").child(phNumber).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChildren()) {
//                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
//                        CartDataClass cx = dataSnapshot2.getValue(CartDataClass.class);
//                        if (TextUtils.equals(cx.getProductKey(), pKey)) {
//                            quantityCountint = Integer.parseInt(cx.getProductQuantity());
//                            holder.quantityCount.setText(cx.getProductQuantity());
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void addToCart2(final MyViewHolder holder, final boolean minusOrPlus, final int pos) {
//        final String phNumber = context.getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PHONE_NUMBER, "No phone number detected");
//        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//
//        final ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setTitle("Adding products to your cart.......");
//        progressDialog.show();
//
//        final ProductDto productsClass = cardinfoList.get(pos);
//        final String productDbKey = dbRef.child("cart_table").child(phNumber).push().getKey();
//
//
//        dbRef.child("cart_table").child(phNumber).orderByChild("productKey").equalTo(productsClass.getProductKeyDto()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.hasChildren()) {//cart has already been made
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                        CartDataClass c3 = dataSnapshot1.getValue(CartDataClass.class);
//                        int quantityDb = Integer.parseInt(c3.getProductQuantity());
//                        int totalprice = 0;
//                        int productPrice = Integer.parseInt(productsClass.getProductPriceDto());
//                        if (quantityDb != 0) {
//                            if (minusOrPlus) {
//                                quantityDb = quantityDb + 1;
//                                totalprice = productPrice * quantityDb;
//                                c3.setProductTotalPrice(String.valueOf(totalprice));
//                                c3.setProductQuantity(String.valueOf(quantityDb));
//                                dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productTotalPrice").setValue(String.valueOf(totalprice));
//                                dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productQuantity").setValue(String.valueOf(quantityDb));
//                                progressDialog.dismiss();
//                            }
//                            if (minusOrPlus == false) {
//                                if (quantityDb == 1) {
//                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).removeValue();
//                                    quantityDb = 0;
//                                    Toast.makeText(context, "Items cannot go below 0", Toast.LENGTH_SHORT).show();
//                                    progressDialog.dismiss();
//                                } else {
//                                    quantityDb = quantityDb - 1;
//                                    totalprice = productPrice * quantityDb;
//                                    c3.setProductTotalPrice(String.valueOf(totalprice));
//                                    c3.setProductQuantity(String.valueOf(quantityDb));
//                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productTotalPrice").setValue(String.valueOf(totalprice));
//                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productQuantity").setValue(String.valueOf(quantityDb));
//                                    progressDialog.dismiss();
//
//                                }
//
//                            }
//                            holder.quantityCount.setText(String.valueOf(quantityDb));
//                        }
//
//                    }
//
//                } else//cart has not been made yet
//                {
//                    if (minusOrPlus) {
//                        quantityCountint = 1;
//                        holder.quantityCount.setText(String.valueOf(quantityCountint));
//                        CartDataClass c4 = new CartDataClass();
//                        c4.setPd(productsClass);
//                        c4.setProductQuantity(String.valueOf(quantityCountint));
//                        c4.setProductTotalPrice(productsClass.getProductPriceDto());
//                        c4.setNodeKey(productDbKey);
//                        c4.setProductKey(productsClass.getProductKeyDto());
//                        progressDialog.dismiss();
//                        dbRef.child("cart_table").child(phNumber).child(productDbKey).setValue(c4);
//                    } else {
//                        Toast.makeText(context, "Items cannot go below 0", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }


    @Override
    public int getItemCount() {
        return cardinfoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvProductName, tvProductType, tvProductMeasurement, tvProductPrice;
        private ImageView ivProductImg;
        private Button btAddToCart;

        public MyViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tvProductName);
            tvProductType = view.findViewById(R.id.tvProductType);
            tvProductMeasurement = view.findViewById(R.id.tvProductMeasurement);
            tvProductPrice = view.findViewById(R.id.tvProductPrice);
//            tvViewMoreDetails = view.findViewById(R.id.tvViewMoreDetails);
            ivProductImg = view.findViewById(R.id.ivProductImg);
            btAddToCart = view.findViewById(R.id.btAddToCart);

        }
    }
}
