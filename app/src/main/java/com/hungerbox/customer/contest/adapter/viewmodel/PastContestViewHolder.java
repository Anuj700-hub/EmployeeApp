package com.hungerbox.customer.contest.adapter.viewmodel;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

public class PastContestViewHolder extends RecyclerView.ViewHolder {

    public TextView tvOfferText,tvOfferExpired;
    public ImageView ivLogo;
    public CardView cvContainer;
    public RelativeLayout rl_offer_expired_container;

    public PastContestViewHolder(@NonNull View itemView) {
        super(itemView);
        tvOfferText = itemView.findViewById(R.id.tv_title);
        ivLogo = itemView.findViewById(R.id.iv_logo);
        cvContainer = itemView.findViewById(R.id.cv_container);
        tvOfferExpired = itemView.findViewById(R.id.tv_offer_expired);
        rl_offer_expired_container = itemView.findViewById(R.id.rl_offer_expired_container);
    }
}
