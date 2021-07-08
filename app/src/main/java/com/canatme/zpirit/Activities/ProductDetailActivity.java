package com.canatme.zpirit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    private String productID, productImg, productType, productName, productMeasurement, productPrice, productInfo;
    private ImageView ivBack, ivProductImg, ivFav;
    private TextView tvProducName, tvProductPrice, tvProductInfo, tvProductType;

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
        ivBack.setOnClickListener(view -> onBackPressed());
        if (getIntent() != null) {
            Intent intent = getIntent();
            productID = intent.getStringExtra(Constants.PRODUCT_ID);
            productImg = intent.getStringExtra(Constants.PRODUCT_IMG);
            productType = intent.getStringExtra(Constants.PRODUCT_TYPE);
            productName = intent.getStringExtra(Constants.PRODUCT_NAME);
            productMeasurement = intent.getStringExtra(Constants.PRODUCT_MEASUREMENT);
            productPrice = intent.getStringExtra(Constants.PRODUCT_PRICE);
            productInfo = intent.getStringExtra(Constants.PRODUCT_INFO);
        }
        tvProducName.setText(productName);
        tvProductType.setText(productType);
        tvProductInfo.setText(productInfo);
        tvProductPrice.setText("Rs. "+productPrice);
        if (productImg != null) {
            Picasso.get().load(productImg).into(ivProductImg);
        }


        ivFav.setOnClickListener(view -> ivFav.setImageResource(R.drawable.ic_redheart));
    }
}