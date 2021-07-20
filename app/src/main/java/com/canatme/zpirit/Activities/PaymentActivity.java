package com.canatme.zpirit.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Activities.Service.ApiService;
import com.canatme.zpirit.Adapters.SelectAddressAdapter;
import com.canatme.zpirit.Dataclasses.AddressDto;
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
import java.util.List;
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
    private TextView tvTotal, tvDeliveryAddress, tvDeliveryCharge, tvProductTotal;
    private int grandTotal;
    private String deliveryCharges;
    private LinearLayoutCompat llDeliveryAddress;
    private String phNumber, emailAddress, orderID;
    private CartDto cartData;
    private DatabaseReference dbRef;
    private AlertDialog loadingDialog, addressDialog, addAddressDialog;
    private ArrayList<CartDto> cdcList;
    private String deliverOrderID;
    private AppCompatRadioButton rbPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Checkout.preload(getApplicationContext());
        phNumber = getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
        ivBack = findViewById(R.id.ivBack);
        tvTotal = findViewById(R.id.tvTotal);
        rbPaymentMethod = findViewById(R.id.rbPaymentMethod);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        llDeliveryAddress = findViewById(R.id.llDeliveryAddress);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvProductTotal = findViewById(R.id.tvProductTotal);
        cdcList = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance().getReference();
        btMakePayment = findViewById(R.id.btMakePayment);
        llDeliveryAddress.setOnClickListener(view -> selectAddressDialog());
        ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        //Calculating the total
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
                    tvProductTotal.setText(tvText);
                    getDeliveryCharges(grandTotal);

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
            if (!rbPaymentMethod.isChecked()) {
                showToast("Please select a payment method");
            } else {
                if (tvDeliveryAddress.getText().toString().equalsIgnoreCase("Delivery address")) {
                    showToast("Please select a delivery address");
                } else {

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
                }

            }

        });

//        getDeliveryCharges();

    }
    private void getDeliveryCharges(int grandTotalx)
    {
        Query getDeliveryCharges = dbRef.child(Constants.CONSTANTS_FOR_ANDROID_APP_FIREBASE);
        getDeliveryCharges.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.e(TAG, "onDataChange: deliverCharge"+snapshot.child(Constants.CONSTANTS_DELIVERY_CHARGE).getValue());
//                int deliveryChargeint = (int) snapshot.child(Constants.CONSTANTS_DELIVERY_CHARGE).getValue();
//                int deliveryChargesum = deliveryChargeint + grandTotal;
//                String deliveryCharge = "Rs. "+ snapshot.child(Constants.CONSTANTS_DELIVERY_CHARGE).getValue();
//                tvDeliveryCharge.setText(deliveryCharge);
//                tvTotal.setText(String.valueOf(deliveryChargesum));

                deliveryCharges = String.valueOf(snapshot.child(Constants.CONSTANTS_DELIVERY_CHARGE).getValue());
                int deliveryChargesint = Integer.parseInt(deliveryCharges);
                int deliveryChargesum = deliveryChargesint + grandTotalx;
                String strDeliveryChargesSum = String.valueOf(deliveryChargesum);
                String finalTextTotal = "Rs."+strDeliveryChargesSum;
                String finalTextDeliveryCharge = "Rs."+deliveryCharges;
                tvTotal.setText(finalTextTotal);
                tvDeliveryCharge.setText(finalTextDeliveryCharge);
                grandTotal = grandTotalx + deliveryChargesint;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void selectAddressDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogLoading = factory.inflate(R.layout.select_delivery_address_dialog, null);
        addressDialog = new AlertDialog.Builder(this).create();
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


        List<AddressDto> addressDataList;
        LinearLayoutCompat llRv;
        RecyclerView rvShowAddress;
        TextView tvAddAddress;
        SelectAddressAdapter adapter;
        addressDataList = new ArrayList<>();
        ImageView tvCloseDialog;


        tvCloseDialog = addressDialog.findViewById(R.id.tvCloseDialog);
        llRv  = addressDialog.findViewById(R.id.llRv);
        tvAddAddress = addressDialog.findViewById(R.id.tvAddAddress);
        tvCloseDialog.setOnClickListener(view -> addressDialog.dismiss());
        rvShowAddress = addressDialog.findViewById(R.id.rvShowAddress);
        adapter = new SelectAddressAdapter(this, addressDataList);
        int numberOfColumns = 1;
        rvShowAddress.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        rvShowAddress.setAdapter(adapter);
        Query getAddressBook = dbRef.child("address_book").child(phNumber);
        getAddressBook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    llRv.setVisibility(View.VISIBLE);
                    tvAddAddress.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AddressDto addressDto = dataSnapshot.getValue(AddressDto.class);
                        addressDataList.add(addressDto);
                        adapter.notifyDataSetChanged();
                    }
                }
                else {
                    Log.e(TAG, "onDataChange: else");
                    llRv.setVisibility(View.GONE);
                    tvAddAddress.setVisibility(View.VISIBLE);
                    tvAddAddress.setOnClickListener(view -> {
                        addAddressDialog();
                        addressDialog.dismiss();

                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void addAddressDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogLoading = factory.inflate(R.layout.addaddressdialogbox, null);
        addAddressDialog = new AlertDialog.Builder(this).create();
        Window window = addAddressDialog.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        addAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addAddressDialog.setCancelable(true);
        addAddressDialog.setView(dialogLoading);
        addAddressDialog.show();
        addAddressDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView tvCloseDialog = addAddressDialog.findViewById(R.id.tvCloseDialog);
        Button btSaveAddress = addAddressDialog.findViewById(R.id.btSaveAddress);
        TextView etHouseNumber, etFloor, etTowerBlock, etHowToReach, etTag;
        etHouseNumber = addAddressDialog.findViewById(R.id.etHouseNumber);
        etFloor = addAddressDialog.findViewById(R.id.etFloor);
        etTowerBlock = addAddressDialog.findViewById(R.id.etTowerBlock);
        etHowToReach = addAddressDialog.findViewById(R.id.etHowToReach);
        etTag = addAddressDialog.findViewById(R.id.etTag);
        DatabaseReference addressDbRef;
        addressDbRef = FirebaseDatabase.getInstance().getReference();

        tvCloseDialog.setOnClickListener(view -> addAddressDialog.dismiss());
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
                String addressID = phNumber + System.currentTimeMillis();
                String strHouseNumber = etHouseNumber.getText().toString().trim();
                String strFloor = etFloor.getText().toString().trim();
                String strTowerBlock = etTowerBlock.getText().toString().trim();
                String strHowToReach;
                if (!TextUtils.isEmpty(etHowToReach.getText().toString().trim())) {
                    strHowToReach = etHowToReach.getText().toString().trim();
                } else {
                    strHowToReach = "empty";
                }
                String strTag = etTag.getText().toString().trim();
                AddressDto addressDto = new AddressDto(addressID, strHouseNumber, strFloor, strTowerBlock, strHowToReach, strTag);
                addressDbRef.child("address_book").child(phNumber).child(addressID).setValue(addressDto);
                showToast("Address saved");
                addressDialog.dismiss();
                addAddressDialog.dismiss();
            }
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
        showToast("Payment Successfull" + s);
    }

    public void getDeliverAddress(String addressID) {
        deliverOrderID = addressID;
        Query getDeliverAddressQuery = dbRef.child("address_book").child(phNumber).orderByChild("addressID").equalTo(addressID);
        getDeliverAddressQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AddressDto addressDto = dataSnapshot.getValue(AddressDto.class);
                        String fullAddress = addressDto.getTag() + " - " + addressDto.getHouseNumber();
                        tvDeliveryAddress.setText(fullAddress);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        addressDialog.dismiss();
    }

    private void placeOrder(String paymentID) {
        Log.e(TAG, "placeOrder: " + orderID);
        String orderTime = String.valueOf(System.currentTimeMillis());

        OrderDto orderDto = new OrderDto(orderID, phNumber, String.valueOf(grandTotal), cdcList, orderTime, "paid", paymentID, deliverOrderID, deliveryCharges, "not_delivered", "", "");

        dbRef.child("orders").child(phNumber).child(orderID).setValue(orderDto);
        dbRef.child("cart_table").child(phNumber).removeValue();

        Intent i = new Intent(PaymentActivity.this, OrderPlacedActivity.class);
        i.putExtra("deliveryDate", orderTime);
        startActivity(i);
        finish();
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