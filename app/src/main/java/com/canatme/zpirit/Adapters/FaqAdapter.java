package com.canatme.zpirit.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.canatme.zpirit.Dataclasses.FaqDto;
import com.canatme.zpirit.R;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {
    private static String TAG = "CartAdapter";
    private Activity context;
    private List<FaqDto> cardinfoList;
    private int quantityCountint = 0;
    private boolean open = false;


    public FaqAdapter(Activity mContext, List<FaqDto> cardinfoList) {
        this.context = mContext;
        this.cardinfoList = cardinfoList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final FaqDto cardData = cardinfoList.get(position);
        holder.tvQuestion.setText(cardData.getQuestion());
        holder.tvAnswer.setText(cardData.getAnswer());
        holder.clParent.setOnClickListener(view -> {

            if(open)
            {
                open = false;
                holder.ivArrow.setImageResource(R.drawable.ic_uparrow);
                holder.tvAnswer.setVisibility(View.GONE);
                holder.tvAnswer.animate().alpha(0.0f);

            }
            else
            {
                open = true;
                holder.ivArrow.setImageResource(R.drawable.ic_downarrow);
                holder.tvAnswer.setVisibility(View.VISIBLE);
                holder.tvAnswer.animate().alpha(1.0f);

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

        private TextView tvQuestion, tvAnswer;
        private ImageView ivArrow;
        private LinearLayoutCompat llHeader;
        private ConstraintLayout clParent;

        public MyViewHolder(View view) {
            super(view);
            tvQuestion = view.findViewById(R.id.tvQuestion);
            tvAnswer = view.findViewById(R.id.tvAnswer);
            ivArrow = view.findViewById(R.id.ivArrow);
            llHeader = view.findViewById(R.id.llHeader);
            clParent = view.findViewById(R.id.clParent);
        }
    }
}



