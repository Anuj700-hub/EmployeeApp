package com.hungerbox.customer.order.adapter.viewholder;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;

public class VendorListItemHolderRounded extends RecyclerView.ViewHolder {

    public TextView tvVendorName, tvVendorTime, tvCheckMenu, tvBookBuffet, tvCuisines,
            tvVendorTiming, tvMinOrderAmount, tvRating, tvCoronaSafe, tvDeliveryType;
    public ImageView ivStar, ivTick;
    public ShapeableImageView ivVendor;
    public View vView;
    //public CardView card;


    public VendorListItemHolderRounded(View itemView, Activity activity) {
        super(itemView);
        tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
        //card = itemView.findViewById(R.id.card);
        tvVendorTime = itemView.findViewById(R.id.tv_vendor_time);
        tvCheckMenu = itemView.findViewById(R.id.bt_check_menu);
        tvCuisines = itemView.findViewById(R.id.tv_vendor_cuisines);
        ivVendor = itemView.findViewById(R.id.iv_vendor_item);
        ivStar = itemView.findViewById(R.id.iv_star);
        tvVendorTiming = itemView.findViewById(R.id.tv_vendor_timing);
        tvMinOrderAmount = itemView.findViewById(R.id.tv_min_order_amount);
        vView = itemView.findViewById(R.id.v_disabled);

        ivTick = itemView.findViewById(R.id.iv_tick);
        tvCoronaSafe = itemView.findViewById(R.id.tv_corona_safe);
        tvDeliveryType = itemView.findViewById(R.id.tv_delivery_type);


        tvRating = itemView.findViewById(R.id.tv_v_rating);
        float radius = 25f;
        if (activity!=null) {
            radius = AppUtils.convertDpToPixel(10, activity);
        }

        ivVendor.setShapeAppearanceModel(ivVendor.getShapeAppearanceModel()
                .toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                .build());
    }
}
