package com.canatme.zpirit.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.canatme.zpirit.Dataclasses.CartDto;
import com.canatme.zpirit.Dataclasses.ProductDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    private String productID, productImg, productType, productName, productMeasurement, productPrice, productInfo, phNumber, productFavKey;
    private ImageView ivBack, ivProductImg, ivFav;
    private TextView tvProducName, tvProductPrice, tvProductInfo, tvProductType, quantityCount;
    private int quantityCountint = 0;
    private ProductDto productDto;
    private DatabaseReference dbSetFav;
    private LinearLayoutCompat llPlusMinus;
    private Button minusQuantity, plusQuantity, btAddToCart;
    private boolean fav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ivBack = findViewById(R.id.ivBack);
        tvProducName = findViewById(R.id.tvProducName);
        ivFav = findViewById(R.id.ivFav);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductInfo = findViewById(R.id.tvProductInfo);
        tvProductType = findViewById(R.id.tvProductType);
        ivProductImg = findViewById(R.id.ivProductImg);
        btAddToCart = findViewById(R.id.btAddToCart);
        llPlusMinus = findViewById(R.id.llPlusMinus);
        minusQuantity = findViewById(R.id.minusQuantity);
        plusQuantity = findViewById(R.id.plusQuantity);
        quantityCount = findViewById(R.id.quantityCount);
        ivBack.setOnClickListener(view -> onBackPressed());
        dbSetFav = FirebaseDatabase.getInstance().getReference();
        phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "nophNumberfound");


        if (getIntent() != null) {
            Intent intent = getIntent();
            productID = intent.getStringExtra(Constants.PRODUCT_ID);
            productImg = intent.getStringExtra(Constants.PRODUCT_IMG);
            productType = intent.getStringExtra(Constants.PRODUCT_TYPE);
            productName = intent.getStringExtra(Constants.PRODUCT_NAME);
            productMeasurement = intent.getStringExtra(Constants.PRODUCT_MEASUREMENT);
            productPrice = intent.getStringExtra(Constants.PRODUCT_PRICE);
            productInfo = intent.getStringExtra(Constants.PRODUCT_INFO);


            productDto = new ProductDto(productID, productImg, productType, productName, productMeasurement, productPrice, productInfo, false);
        }
        tvProducName.setText(productName);
        tvProductType.setText(productType);
        tvProductInfo.setText(productInfo);
        tvProductPrice.setText("Rs. " + productPrice);
        if (productImg != null) {
            Picasso.get().load(productImg).into(ivProductImg);
        }


        //getting the current status of the product in cart(checking if the item exists in the cart or not)
        final String phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("cart_table").child(phNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                        CartDto cx = dataSnapshot2.getValue(CartDto.class);

                        if (productID.equalsIgnoreCase(cx.getProductKey())) {
                            quantityCountint = Integer.parseInt(cx.getProductQuantity());
                            productDto.setCartHasItem(true);
                        }

                    }
                    if (productDto.isCartHasItem()) {
                        llPlusMinus.setVisibility(View.VISIBLE);
                        btAddToCart.setVisibility(View.GONE);
                        quantityCount.setText(String.valueOf(quantityCountint));
                    } else {
                        llPlusMinus.setVisibility(View.GONE);
                        btAddToCart.setVisibility(View.VISIBLE);
                    }
                } else {
                    llPlusMinus.setVisibility(View.GONE);
                    btAddToCart.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ivFav.setOnClickListener(view -> {
            setFav();
        });

        btAddToCart.setOnClickListener(v -> setPlusQuantity(true));
        minusQuantity.setOnClickListener(v -> setMinusQuantity());
        plusQuantity.setOnClickListener(v -> setPlusQuantity(false));
//        setFav();
        Query getFavs = dbSetFav.child("favourites").child(phNumber).orderByChild("productID").equalTo(productID);
        getFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ProductDto p = dataSnapshot.getValue(ProductDto.class);
                        if(p.getProductID().equalsIgnoreCase(productID))
                        {
                            fav = true;
                            productFavKey = dataSnapshot.getKey();
                            ivFav.setImageResource(R.drawable.ic_heart);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void setFav() {
        if (fav) {
            fav = false;
            ivFav.setImageResource(R.drawable.ic_borderheart);
            dbSetFav.child("favourites").child(phNumber).child(productFavKey).removeValue();
        } else {
            fav = true;
            ivFav.setImageResource(R.drawable.ic_heart);
            productFavKey = dbSetFav.child("favourites").child(phNumber).push().getKey();
            dbSetFav.child("favourites").child(phNumber).child(productFavKey).setValue(productDto);

        }
    }

    private void addProductToCart(final boolean minusOrPlus) {
        final String phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding products to your cart.......");
        progressDialog.show();

//        final ProductDto productsClass = cardinfoList.get(pos);
        final String productDbKey = dbRef.child("cart_table").child(phNumber).push().getKey();
        dbRef.child("cart_table").child(phNumber).orderByChild("productKey").equalTo(productDto.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {//cart has already been made
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        CartDto c3 = dataSnapshot1.getValue(CartDto.class);
                        int quantityDb = Integer.parseInt(c3.getProductQuantity());
                        int totalprice = 0;
                        int productPrice = Integer.parseInt(productDto.getProductPrice());
                        if (quantityDb != 0) {
                            if (minusOrPlus) {
                                quantityDb = quantityDb + 1;
                                totalprice = productPrice * quantityDb;
                                c3.setProductTotalPrice(String.valueOf(totalprice));
                                c3.setProductQuantity(String.valueOf(quantityDb));
                                dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productTotalPrice").setValue(String.valueOf(totalprice));
                                dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productQuantity").setValue(String.valueOf(quantityDb));
                                progressDialog.dismiss();
                            }
                            if (minusOrPlus == false) {
                                if (quantityDb == 1) {
                                    llPlusMinus.setVisibility(View.GONE);
                                    btAddToCart.setVisibility(View.VISIBLE);
                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).removeValue();
                                    quantityDb = 0;
                                    Toast.makeText(getApplicationContext(), "Items cannot go below 0", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    quantityDb = quantityDb - 1;
                                    totalprice = productPrice * quantityDb;
                                    c3.setProductTotalPrice(String.valueOf(totalprice));
                                    c3.setProductQuantity(String.valueOf(quantityDb));
                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productTotalPrice").setValue(String.valueOf(totalprice));
                                    dbRef.child("cart_table").child(phNumber).child(c3.getNodeKey()).child("productQuantity").setValue(String.valueOf(quantityDb));
                                    progressDialog.dismiss();

                                }

                            }
                            quantityCount.setText(String.valueOf(quantityDb));
                        }

                    }

                } else//cart has not been made yet
                {
                    if (minusOrPlus) {
                        quantityCountint = 1;
                        quantityCount.setText(String.valueOf(quantityCountint));
                        CartDto c4 = new CartDto();
                        c4.setPd(productDto);
                        c4.setProductQuantity(String.valueOf(quantityCountint));
                        c4.setProductTotalPrice(productDto.getProductPrice());
                        c4.setNodeKey(productDbKey);
                        c4.setProductKey(productDto.getProductID());
                        progressDialog.dismiss();
                        dbRef.child("cart_table").child(phNumber).child(productDbKey).setValue(c4);
                    } else {
                        Toast.makeText(getApplicationContext(), "Items cannot go below 0", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void setMinusQuantity() {
        quantityCountint = quantityCountint - 1;
        addProductToCart(false);
    }

    public void setPlusQuantity(boolean first) {
        if (first) {
            btAddToCart.setVisibility(View.GONE);
            llPlusMinus.setVisibility(View.VISIBLE);
        }
        quantityCountint = quantityCountint + 1;
        addProductToCart(true);
    }


}