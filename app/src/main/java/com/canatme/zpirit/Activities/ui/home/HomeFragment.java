package com.canatme.zpirit.Activities.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.canatme.zpirit.R;
import com.canatme.zpirit.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private LinearLayoutCompat llDrinks, llSnacks;
    private TextView tvDrinks, tvSnacks;
    private View vDrinks, vSnacks;
    private Spinner spinnerDrinkType;

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
        /*                              */

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


    private void setSpinner()
    {
        String[] arraySpinner = new String[] {
                "Select Drink type", "Whiskey", "Beer", "Breezer", "Vodka", "Rum", "Gin", "Wine", "Desi"

        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        spinnerDrinkType.setAdapter(adapter);
    }
    private void drinksClick()
    {
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

    private void snacksClick()
    {
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