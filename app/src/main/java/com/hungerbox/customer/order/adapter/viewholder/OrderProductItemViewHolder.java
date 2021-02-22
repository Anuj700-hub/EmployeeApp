package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by peeyush on 7/7/16.
 */
public class OrderProductItemViewHolder extends RecyclerView.ViewHolder {

    public TextView tvOrderCal;
    public ImageView ivVegNonVeg;
    public TextView tvOrderProduct, tvOrderPrice;

    public OrderProductItemViewHolder(View itemView) {
        super(itemView);
        ivVegNonVeg = itemView.findViewById(R.id.iv_veg_non);
        tvOrderProduct = itemView.findViewById(R.id.tv_product);
        tvOrderCal = itemView.findViewById(R.id.tv_cal);
        tvOrderPrice = itemView.findViewById(R.id.tv_product_price);
    }
}
