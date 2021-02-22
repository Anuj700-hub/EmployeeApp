package com.hungerbox.customer.order.adapter.viewholder;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by peeyush on 22/6/16.
 */
public class VendorListItemHolder extends RecyclerView.ViewHolder {

    public TextView tvVendorName, tvVendorTime, tvCheckMenu, tvBookBuffet, tvCuisines,
            tvVendorTiming, tvMinOrderAmount, tvRating;
    public ImageView ivVendor,ivStar;
    public View vView;
    //public CardView card;

    public VendorListItemHolder(View itemView) {
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

        tvRating = itemView.findViewById(R.id.tv_v_rating);
    }
}
