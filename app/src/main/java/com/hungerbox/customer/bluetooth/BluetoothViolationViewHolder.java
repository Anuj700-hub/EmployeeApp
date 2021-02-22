package com.hungerbox.customer.bluetooth;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;

class BluetoothViolationViewHolder extends RecyclerView.ViewHolder {

    public TextView tv_total_violations, tv_user_percentage, tv_day,violation_text;
    public RecyclerView rv_violation_hour;

    public BluetoothViolationViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_total_violations = itemView.findViewById(R.id.tv_total_violations);
        tv_user_percentage = itemView.findViewById(R.id.tv_user_percentage);
        tv_day = itemView.findViewById(R.id.tv_day);
        rv_violation_hour = itemView.findViewById(R.id.rv_violation_hour);
        violation_text = itemView.findViewById(R.id.violation_text);
    }
}
