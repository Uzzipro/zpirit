package com.canatme.zpirit.Fragments.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.canatme.zpirit.Adapters.ProductAdapter;
import com.canatme.zpirit.Dataclasses.ProductDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private LinearLayoutCompat llDrinks, llSnacks;
    private TextView tvDrinks, tvSnacks;
    private View vDrinks, vSnacks;
    private LinearLayoutCompat llProductNotFound, llRv;
    private Spinner spinnerDrinkType;
    /*Adapter components, lists and adapters*/
    private List<ProductDto> listProduct;
    private ProductAdapter adapter;
    private RecyclerView rvProduct;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
        /*Linear layouts*/
        llDrinks = binding.llDrinks;
        llSnacks = binding.llSnacks;
        llProductNotFound = binding.llProductNotFound;
        llRv = binding.llRv;
        /**/

        /*Text Views*/
        tvDrinks = binding.tvDrinks;
        tvSnacks = binding.tvSnacks;
        /**/

        /*Views underlining the text*/
        vDrinks = binding.vDrinks;
        vSnacks = binding.vSnacks;
        /**/

        /*Initializing and setting the spinner*/
        spinnerDrinkType = binding.spinnerDrinkType;
        setSpinner();
        /**/

        /*Default chosen should be drinks*/
        drinksClick();
        /**/

        /*Recycler View*/
        rvProduct = binding.rvProduct;
        /**/

        /*Setting adapters and populating it*/
        listProduct = new ArrayList<>();
        adapter = new ProductAdapter(getActivity(), listProduct);
        int numberOfColumns = 2;
        rvProduct.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        rvProduct.setAdapter(adapter);
        getProducts("all");

        /**/

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        llDrinks.setOnClickListener(view -> drinksClick());
        llSnacks.setOnClickListener(view -> snacksClick());

        return root;
    }


    private void getProducts(String itemType) {
        String[] productID = {"pd1", "pd2", "pd3", "pd4"};
        String[] productImg = {"https://i.ibb.co/sKrh6xz/brocode.png", "https://i.ibb.co/1s75D5L/allseasonspng.png", "https://i.ibb.co/TbZsDtM/absolut.png", "https://i.ibb.co/mC4Dxss/oldmonk.png"};
        String[] productType = {"Beer", "Whiskey", "Vodka", "Rum"};
        String[] productName = {"Bro Code", "All Seasons", "Absolut", "Old Monk"};
        String[] productMeasurement = {"250ml", "750ml", "700ml", "300ml"};
        String[] productPrice = {"120", "700", "1200", "400"};
        String[] productInfo = {getResources().getString(R.string.product_info_testtext), getResources().getString(R.string.product_info_testtext), getResources().getString(R.string.product_info_testtext), getResources().getString(R.string.product_info_testtext)};
        listProduct.clear();

        if (itemType.equalsIgnoreCase("all")) {
            llRv.setVisibility(View.VISIBLE);
            llProductNotFound.setVisibility(View.GONE);
            for (int i = 0; i < productID.length; i++) {
                ProductDto productDto = new ProductDto();
                productDto.setProductID(productID[i]);
                productDto.setProductType(productType[i]);
                productDto.setProductImg(productImg[i]);
                productDto.setProductName(productName[i]);
                productDto.setProductMeasurement(productMeasurement[i]);
                productDto.setProductPrice(productPrice[i]);
                productDto.setProductInfo(productInfo[i]);
                listProduct.add(productDto);
                adapter.notifyDataSetChanged();
            }
        }
        if(!itemType.equalsIgnoreCase("all") && !itemType.equalsIgnoreCase("Select Drink type")) {
            llRv.setVisibility(View.VISIBLE);
            llProductNotFound.setVisibility(View.GONE);
            for (int i = 0; i < productID.length; i++) {
                if (productType[i].equalsIgnoreCase(itemType)) {
                    ProductDto productDto = new ProductDto();
                    productDto.setProductID(productID[i]);
                    productDto.setProductType(productType[i]);
                    productDto.setProductImg(productImg[i]);
                    productDto.setProductName(productName[i]);
                    productDto.setProductMeasurement(productMeasurement[i]);
                    productDto.setProductPrice(productPrice[i]);
                    productDto.setProductInfo(productInfo[i]);
                    listProduct.add(productDto);
                }
                if (listProduct.size() == 0) {
                    llRv.setVisibility(View.GONE);
                    llProductNotFound.setVisibility(View.VISIBLE);
                } else {
                    llRv.setVisibility(View.VISIBLE);
                    llProductNotFound.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

        }
    }

    private void setSpinner() {
        String[] arraySpinner = new String[]{
                "Select Drink type", "Whiskey", "Beer", "Breezer", "Vodka", "Rum", "Gin", "Wine", "Desi"

        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerDrinkType.setAdapter(adapter);

        spinnerDrinkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemSelected: " + spinnerDrinkType.getSelectedItem().toString());
                getProducts(spinnerDrinkType.getSelectedItem().toString());
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}