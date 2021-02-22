package com.hungerbox.customer.bluetooth;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;

class ViolationByHourViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_violation_count, tv_time_range;

    public ViolationByHourViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_violation_count = itemView.findViewById(R.id.tv_violation_count);
        tv_time_range = itemView.findViewById(R.id.tv_time_range);
    }
}
