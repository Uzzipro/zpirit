package com.canatme.zpirit.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Activities.Service.ApiService;
import com.canatme.zpirit.Dataclasses.CartDto;
import com.canatme.zpirit.Dataclasses.OrderCreatedResponseDto;
import com.canatme.zpirit.Dataclasses.OrderDto;
import com.canatme.zpirit.Dataclasses.RazorPayCreateOrderDtoBody;
import com.canatme.zpirit.Dataclasses.RpKeyDto;
import com.canatme.zpirit.Dataclasses.UserDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = "PaymentActivity";
    private ImageView ivBack;
    private Button btMakePayment;
    private TextView tvTotal;
    private int grandTotal;
    private String phNumber, emailAddress, orderID;
    private CartDto cartData;
    private DatabaseReference dbRef;
    private AlertDialog loadingDialog;
    private ArrayList<CartDto> cdcList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Checkout.preload(getApplicationContext());
        phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
        ivBack = findViewById(R.id.ivBack);
        tvTotal = findViewById(R.id.tvTotal);
        cdcList = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance().getReference();
        btMakePayment = findViewById(R.id.btMakePayment);
        ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        Query q1 = dbRef.child("cart_table").child(phNumber);
        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        cartData = dataSnapshot1.getValue(CartDto.class);
                        cdcList.add(cartData);

                        int price = Integer.parseInt(cartData.getProductTotalPrice());
                        grandTotal = grandTotal + price;
                    }
                    //add here
                    String tvText = "Rs. " + String.valueOf(grandTotal);
                    tvTotal.setText(tvText);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        Query q2 = dbRef.child("users").orderByChild("phNumber").equalTo(phNumber);
        q2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                        UserDto userDto = dataSnapshot2.getValue(UserDto.class);
                        Log.e(TAG, "onDataChange: " + userDto.getEmailAddress());
                        emailAddress = userDto.getEmailAddress();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        btMakePayment.setOnClickListener(view -> {
            loadingScreen();
            String value_in_paisa = String.valueOf(grandTotal * 100);
//            initializePayment("ref_1", "100", phNumber, emailAddress);

            Query q3 = dbRef.child("keys").child("test_key");
            q3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        RpKeyDto rpKeyDto = snapshot.getValue(RpKeyDto.class);
                        String rpKeyId = rpKeyDto.getKey_id();
                        String rpKeySecret = rpKeyDto.getKey_secret();
                        String description = "zpirit" + phNumber + System.currentTimeMillis();
                        getClient(PaymentActivity.this, description, value_in_paisa, phNumber, emailAddress, rpKeyId, rpKeySecret);
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        });

    }

    private Retrofit getClient(final Context context, String description, String amount, String phNumber, String email, String rp_key_id, String rp_key_secret) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.readTimeout(60, TimeUnit.SECONDS);
        client.writeTimeout(60, TimeUnit.SECONDS);
        client.connectTimeout(60, TimeUnit.SECONDS);
        client.addInterceptor(new BasicAuthInterceptor(rp_key_id, rp_key_secret));

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.RP_ORDERS)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        int amounttoInt;
        amounttoInt = Integer.parseInt(amount);
        final ApiService apiService = retrofit.create(ApiService.class);
        RazorPayCreateOrderDtoBody razorPayCreateOrderDtoBody = new RazorPayCreateOrderDtoBody();
        razorPayCreateOrderDtoBody.setAmount(amounttoInt);
        razorPayCreateOrderDtoBody.setCurrency("INR");
        razorPayCreateOrderDtoBody.setPayment_capture(1);
        razorPayCreateOrderDtoBody.setReceipt(description);


        final Call<OrderCreatedResponseDto> call = apiService.sendFeedback(razorPayCreateOrderDtoBody);
        call.enqueue(new Callback<OrderCreatedResponseDto>() {
            @Override
            public void onResponse(Call<OrderCreatedResponseDto> call, Response<OrderCreatedResponseDto> response) {
                String order_id = response.body().getId();
                orderID = response.body().getId();
                initializePayment(description, amounttoInt, phNumber, email, order_id, rp_key_id);
            }

            @Override
            public void onFailure(Call<OrderCreatedResponseDto> call, Throwable t) {
                Log.e(TAG, "onResponse: fail " + t.getMessage());
            }
        });

        return retrofit;
    }


    private void initializePayment(String description, int amount, String phNumber, String email, String order_id, String rp_key_id) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(rp_key_id);
        checkout.setImage(R.drawable.ic_logozpirit_outlinesfortext);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            /*Parameters to regenerate on every order*/
            options.put(Constants.RP_DESCRIPTION, description);
            options.put(Constants.RP_AMOUNT, amount);//pass amount in currency subunits
            options.put(Constants.RP_PREFILLEMAIL, email);
            options.put(Constants.RP_PREFILLCONTACT, phNumber);
            options.put(Constants.RP_ORDERID, order_id);//from response of step 3.
            /**/

            /*Parameteres not to change*/
            options.put(Constants.RP_NAME, "Zpirit");
            options.put(Constants.RP_IMAGE, "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put(Constants.RP_THEMECOLOR, "#3399cc");
            options.put(Constants.RP_CURRENCY, "INR");
            options.put(Constants.RP_SENDSMSHASH, true);
            JSONObject retryObj = new JSONObject();
            retryObj.put(Constants.RP_ENABLED, true);
            retryObj.put(Constants.RP_MAXCOUNT, 4);
            options.put(Constants.RP_RETRY, retryObj);

            loadingDialog.dismiss();
            checkout.open(activity, options);

            /**/


        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {

        placeOrder(s);
        Log.e(TAG, "onPaymentSuccess: " + s);
        showToast("Payment Successfull" + s);
    }

    private void placeOrder(String paymentID) {
        Log.e(TAG, "placeOrder: " + orderID);
        String orderTime = String.valueOf(System.currentTimeMillis());

        OrderDto orderDto = new OrderDto(orderID, phNumber, String.valueOf(grandTotal), cdcList, orderTime, "paid", paymentID);

        dbRef.child("orders").child(phNumber).child(orderID).setValue(orderDto);
        dbRef.child("cart_table").child(phNumber).removeValue();

        Intent i = new Intent(PaymentActivity.this, OrderPlacedActivity.class);
        Log.e(TAG, "placeOrder: "+orderTime);
        i.putExtra("deliveryDate", orderTime);
        startActivity(i);
//        String orderTime = String.valueOf(System.currentTimeMillis());
//        OrderDto orderDataClass = new OrderDto(orderID, phNumber, String.valueOf(grandTotal), cdcList, orderTime, "Not Paid");

//        dbRef = FirebaseDatabase.getInstance().getReference();
//        dbRef.child("orders").child(phNumber).child(orderID).setValue(orderDataClass);
//        dbRef.child("cart_table").child(phNumber).removeValue();
    }

    @Override
    public void onPaymentError(int i, String s) {
        showToast("Payment Failed");
    }

    private void loadingScreen() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogLoading = factory.inflate(R.layout.loading, null);
        loadingDialog = new AlertDialog.Builder(this).create();
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.setView(dialogLoading);
        loadingDialog.show();
        TextView tvLoading = dialogLoading.findViewById(R.id.tvLoading);
        LottieAnimationView animation_view = dialogLoading.findViewById(R.id.animation_view);
        animation_view.setAnimation(R.raw.payment_animation);
        tvLoading.setText("Initializing payment");
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}