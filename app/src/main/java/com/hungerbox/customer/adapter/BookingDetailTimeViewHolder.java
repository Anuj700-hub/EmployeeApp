package com.hungerbox.customer.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by sandipanmitra on 11/23/17.
 */

public class BookingDetailTimeViewHolder extends RecyclerView.ViewHolder {

    public TextView tvTime, tvCancel;


    public BookingDetailTimeViewHolder(View itemView) {
        super(itemView);
        tvTime = itemView.findViewById(R.id.tv_time);
        tvCancel = itemView.findViewById(R.id.btn_cancel);
    }
}
