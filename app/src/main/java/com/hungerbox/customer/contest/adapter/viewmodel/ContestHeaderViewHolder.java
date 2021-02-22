package com.hungerbox.customer.contest.adapter.viewmodel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.R;

public class ContestHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView tvHeader;
    public ContestHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        tvHeader = itemView.findViewById(R.id.tv_title);
    }
}
