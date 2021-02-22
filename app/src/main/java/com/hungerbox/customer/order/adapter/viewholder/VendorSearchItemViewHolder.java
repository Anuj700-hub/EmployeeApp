package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;

/**
 * Created by sandipanmitra on 12/18/17.
 */

public class VendorSearchItemViewHolder extends ViewHolder {

    public ShapeableImageView ivVendorLogo;
    public ImageView ivStar, ivTick;
    public TextView tvVendorName, tvCoronaSafe, tvDeliveryType;;
    public TextView tvVendorCuisines;
    public TextView tvVendorTime;
    public TextView tvRating;


    public VendorSearchItemViewHolder(View itemView, Activity activity) {
        super(itemView);
        tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
        tvVendorCuisines = itemView.findViewById(R.id.tv_vendor_cuisines);
        tvVendorTime = itemView.findViewById(R.id.tv_vendor_time);
        ivVendorLogo = itemView.findViewById(R.id.iv_vendor_item);
        ivStar = itemView.findViewById(R.id.iv_star);
        tvRating = itemView.findViewById(R.id.tv_v_rating);

        ivTick = itemView.findViewById(R.id.iv_tick);
        tvCoronaSafe = itemView.findViewById(R.id.tv_corona_safe);
        tvDeliveryType = itemView.findViewById(R.id.tv_delivery_type);

        float radius = AppUtils.convertDpToPixel(10,activity);

        ivVendorLogo.setShapeAppearanceModel(ivVendorLogo.getShapeAppearanceModel()
                .toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                .build());
    }
}
