package com.hungerbox.customer.contest.adapter.viewmodel;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

public class ContestAdapterViewHolder extends RecyclerView.ViewHolder {

    public TextView tvOfferText,tvExpiryDate,tvReward,tvDaysLeft;
    public ImageView ivLogo;
    public CardView cvContainer;
    public RecyclerView rlTimeline;
    public LinearLayout llTimelineContainer;

    public ContestAdapterViewHolder(@NonNull View itemView) {
        super(itemView);
        tvOfferText = itemView.findViewById(R.id.tv_title);
        tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
        tvReward = itemView.findViewById(R.id.tv_reward);
        ivLogo = itemView.findViewById(R.id.iv_logo);
        cvContainer = itemView.findViewById(R.id.cv_container);
        llTimelineContainer = itemView.findViewById(R.id.ll_timeline_container);
    }
}
