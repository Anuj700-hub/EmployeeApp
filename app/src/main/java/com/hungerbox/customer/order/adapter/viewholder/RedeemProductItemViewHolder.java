package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by peeyush on 24/6/16.
 */
public class RedeemProductItemViewHolder extends RecyclerView.ViewHolder {

    public TextView tvName, tvVendorName, tvRedeem;
    public ImageView ivVendorImage;

    public RedeemProductItemViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_name);
        tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
        tvRedeem = itemView.findViewById(R.id.tv_redeem);
        ivVendorImage = itemView.findViewById(R.id.iv_redeem_product);
    }
}
