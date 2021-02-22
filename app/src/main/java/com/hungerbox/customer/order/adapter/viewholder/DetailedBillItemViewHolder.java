package com.hungerbox.customer.order.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;

public class DetailedBillItemViewHolder extends RecyclerView.ViewHolder{

    public TextView tvItemName, tvQty, tvPrice, tvStatus;
    public View paymentFailedLine;

    public DetailedBillItemViewHolder(@NonNull View itemView) {
        super(itemView);
        tvItemName = itemView.findViewById(R.id.tv_item);
        tvQty = itemView.findViewById(R.id.tv_qty);
        tvPrice = itemView.findViewById(R.id.tv_item_price);
        tvStatus = itemView.findViewById(R.id.tv_item_status);
        paymentFailedLine = itemView.findViewById(R.id.payment_failed_line);
    }
}
