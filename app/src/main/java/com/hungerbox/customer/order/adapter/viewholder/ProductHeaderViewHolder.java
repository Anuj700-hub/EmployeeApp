package com.hungerbox.customer.order.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.R;

public class ProductHeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView header;
    public ProductHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.header);
    }
}
