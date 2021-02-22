package com.hungerbox.customer.order.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;

public class SearchVendorViewHolder extends RecyclerView.ViewHolder {

    public TextView tvVendorName, tvVendorDescription, tvVendorRating, tvViewMenu, tvCoronaSafe, tvDeliveryType;
    public ImageView ivStar,ivTick;
    public ConstraintLayout clVendor;

    public SearchVendorViewHolder(@NonNull View itemView) {
        super(itemView);
        tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
        tvVendorDescription = itemView.findViewById(R.id.tv_vendor_description);
        tvVendorRating = itemView.findViewById(R.id.tv_v_rating);
        tvViewMenu = itemView.findViewById(R.id.tv_view_menu);
        ivStar = itemView.findViewById(R.id.iv_star);
        ivTick = itemView.findViewById(R.id.iv_tick);
        tvCoronaSafe = itemView.findViewById(R.id.tv_corona_safe);
        tvDeliveryType = itemView.findViewById(R.id.tv_delivery_type);
        clVendor = itemView.findViewById(R.id.cl_vendor);
    }
}
