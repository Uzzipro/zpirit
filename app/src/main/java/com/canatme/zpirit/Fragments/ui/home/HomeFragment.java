package com.canatme.zpirit.Fragments.ui.home;

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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Activities.CartActivity;
import com.canatme.zpirit.Adapters.ProductAdapter;
import com.canatme.zpirit.Dataclasses.CategoryDto;
import com.canatme.zpirit.Dataclasses.ProductDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.canatme.zpirit.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private LinearLayoutCompat llDrinks, llSnacks;
    private String selectedItemType, phNumber;
    private TextView tvDrinks, tvSnacks;
    private View vDrinks, vSnacks;
    private LinearLayoutCompat llProductNotFound, llRv;
    private Spinner spinnerDrinkType;
    private LottieAnimationView animationView;
    /*Adapter components, lists and adapters*/
    private DatabaseReference dbRefProductType, dbRefGetProducts, dbSetFCMToken;
    private RelativeLayout rlDrinkType;
    private TextView tvLoading;
    private ProductAdapter adapter;
    private List<ProductDto> listProduct;
    private RecyclerView rvProduct;
    private ArrayList<String> productTypesList;
    private ImageView ivCart;

    /*Alert dialog box for loading screen*/
    private AlertDialog loadingDialog;
    /**/

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final String phNumber = getActivity().getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");

//        final TextView textView = binding.textHome;
        /*Linear layouts*/
        llDrinks = binding.llDrinks;
        llSnacks = binding.llSnacks;
        llProductNotFound = binding.llProductNotFound;
        llRv = binding.llRv;
        /**/

        /*Relative layouts*/
        rlDrinkType = binding.rlDrinkType;
        /**/
        /*Text Views*/
        tvDrinks = binding.tvDrinks;
        tvSnacks = binding.tvSnacks;
        /**/

        tvLoading = binding.tvLoading;
        /*Animation view*/
        animationView = binding.animationView;
        /**/

        /*Views underlining the text*/
        vDrinks = binding.vDrinks;
        vSnacks = binding.vSnacks;
        /**/

        dbRefProductType = FirebaseDatabase.getInstance().getReference();
        dbRefGetProducts = FirebaseDatabase.getInstance().getReference();
        dbSetFCMToken = FirebaseDatabase.getInstance().getReference();

        productTypesList = new ArrayList<>();
        /*Initializing and setting the spinner*/
        spinnerDrinkType = binding.spinnerDrinkType;
        setSpinner();
        /**/


        /*Recycler View*/
        rvProduct = binding.rvProduct;
        /**/

        /*ImageViews*/
        ivCart = binding.ivCart;
        /**/

        /*Setting adapters and populating it*/
        listProduct = new ArrayList<>();
        adapter = new ProductAdapter(getActivity(), listProduct);
        int numberOfColumns = 2;
        rvProduct.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        rvProduct.setAdapter(adapter);
        getProducts2("drinks");
        /**/

        /*Default chosen should be drinks*/
        drinksClick();
        /**/

        /*Initializing the Product type arraylist*/
        /**/


        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
//                textView.setText(s);
        });

        llDrinks.setOnClickListener(view -> drinksClick());
        llSnacks.setOnClickListener(view -> snacksClick());

        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CartActivity.class);
                startActivity(i);
            }
        });

        return root;
    }

    private void sendTokenToServer()
    {
        final String fcmToken = getActivity().getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.FCM_TOKEN, "No Token Detected");

    }

    private void getProducts2(String itemType) {
        if (itemType.equalsIgnoreCase("drinks")) {
            rlDrinkType.setVisibility(View.VISIBLE);
            llRv.setVisibility(View.GONE);
            llProductNotFound.setVisibility(View.VISIBLE);
            animationView.cancelAnimation();
            animationView.setVisibility(View.GONE);
            tvLoading.setText("Please select your Drink Type");
        } else {
            if(itemType.equalsIgnoreCase("snacks"))
            {
                rlDrinkType.setVisibility(View.GONE);
                selectedItemType = "Snacks";
            }
            else {
                rlDrinkType.setVisibility(View.VISIBLE);
            }
            loadingScreen();
            llRv.setVisibility(View.VISIBLE);
            llProductNotFound.setVisibility(View.GONE);
            animationView.cancelAnimation();

            Query q1 = dbRefGetProducts.child("products_added").child(itemType);
            q1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        listProduct.clear();
                        adapter.notifyDataSetChanged();
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            ProductDto productDto = dataSnapshot1.getValue(ProductDto.class);
                            listProduct.add(productDto);
                            adapter.notifyDataSetChanged();
                        }
                        loadingDialog.dismiss();
                    }
                    else
                    {
                        llRv.setVisibility(View.GONE);
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        llProductNotFound.setVisibility(View.VISIBLE);
                        tvLoading.setText("No products found with this category");
                        loadingDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

    }


    private void setSpinner() {
        loadingScreen();
        productTypesList.add("Select Drink type");
        dbRefProductType.child("product_category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        CategoryDto cD = dataSnapshot1.getValue(CategoryDto.class);
                        productTypesList.add(cD.getCategory());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, productTypesList);
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    spinnerDrinkType.setAdapter(adapter);
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

//    String[] arraySpinner = new Stri/**/ng[]{
//            "Select Drink type", "Whiskey", "Beer", "Breezer", "Vodka", "Rum", "Gin", "Wine", "Desi"};

        spinnerDrinkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinnerDrinkType.getSelectedItem().toString().equalsIgnoreCase("Select Drink type")) {
                    getProducts2("drinks");
                } else {
                    getProducts2(spinnerDrinkType.getSelectedItem().toString());
                    selectedItemType = spinnerDrinkType.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void drinksClick() {
        vDrinks.setVisibility(View.VISIBLE);
        vSnacks.setVisibility(View.GONE);
        vSnacks.animate().alpha(0.0f);
        vDrinks.animate().alpha(1.0f);
//        insertLoginFragment();
        llDrinks.setOnClickListener(null);
        llSnacks.setOnClickListener(view -> snacksClick());
//        loadingScreen();

        tvDrinks.setTextColor(Color.parseColor("#FA4A0C"));
        tvSnacks.setTextColor(Color.parseColor("#B1B1B3"));
        getProducts2("drinks");
    }

    private void snacksClick() {
        vDrinks.setVisibility(View.GONE);
        vSnacks.setVisibility(View.VISIBLE);
        vDrinks.animate().alpha(0.0f);
        vSnacks.animate().alpha(1.0f);
//        insertSignupFragment();
        llSnacks.setOnClickListener(null);
        llDrinks.setOnClickListener(view -> drinksClick());
        tvSnacks.setTextColor(Color.parseColor("#FA4A0C"));
        tvDrinks.setTextColor(Color.parseColor("#B1B1B3"));
        getProducts2("Snacks");
    }


    private void loadingScreen() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dialogLoading = factory.inflate(R.layout.loading, null);
        loadingDialog = new AlertDialog.Builder(getActivity()).create();
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
        tvLoading.setText("Loading the good stuff....");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(selectedItemType!=null)
        {
            getProducts2(selectedItemType);
        }
    }
}