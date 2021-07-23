package com.canatme.zpirit.Fragments.ui.favourites;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.canatme.zpirit.Adapters.ProductAdapter;
import com.canatme.zpirit.Dataclasses.FavouritesDto;
import com.canatme.zpirit.Dataclasses.ProductDto;
import com.canatme.zpirit.R;
import com.canatme.zpirit.Utils.Constants;
import com.canatme.zpirit.databinding.FragmentFavouriteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class FavouritesFragment extends Fragment {
    private static final String TAG = "FavouritesFragment";
    private ProductAdapter adapter;
    private List<ProductDto> favProductList;
    private RecyclerView rvProduct;
    private AlertDialog loadingDialog;
    private DatabaseReference dbRefGetFavs;

    private FavouritesViewModel favouritesViewModel;
    private FragmentFavouriteBinding binding;
    String phNumber;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favouritesViewModel =
                new ViewModelProvider(this).get(FavouritesViewModel.class);

        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rvProduct = binding.rvProduct;
        dbRefGetFavs = FirebaseDatabase.getInstance().getReference();
        favProductList = new ArrayList<>();
        favProductList = new ArrayList<>();
        adapter = new ProductAdapter(getActivity(), favProductList);
        int numberOfColumns = 2;
        rvProduct.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        rvProduct.setAdapter(adapter);
        phNumber = getActivity().getSharedPreferences(Constants.ACCESS_PREFS, Context.MODE_PRIVATE).getString(Constants.PH_NUMBER, "No phone number detected");
//        getProducts2();
        return root;
    }

    private void getProducts2() {
            loadingScreen();
            favProductList.clear();
            adapter.notifyDataSetChanged();
            Query q1 = dbRefGetFavs.child("favourites").child(phNumber);
            q1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            ProductDto productDto = dataSnapshot1.getValue(ProductDto.class);
                            favProductList.add(productDto);
                            adapter.notifyDataSetChanged();
                        }
                        loadingDialog.dismiss();
                    }
                    else
                    {

                        loadingDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


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
        getProducts2();
    }
}