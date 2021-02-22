package com.hungerbox.customer.order.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.view.OrderItemStatusView;

public class PickupItemViewHolder extends RecyclerView.ViewHolder {

    public TextView tvItemName;
    public OrderItemStatusView itemStatusView;

    public PickupItemViewHolder(@NonNull View itemView) {
        super(itemView);
        tvItemName = itemView.findViewById(R.id.tv_item_name);
        itemStatusView = itemView.findViewById(R.id.item_status_view);
    }
}
