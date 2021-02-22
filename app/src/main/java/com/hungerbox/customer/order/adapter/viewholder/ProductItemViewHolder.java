package com.hungerbox.customer.order.adapter.viewholder;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by peeyush on 24/6/16.
 */
public class ProductItemViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout rvContiner,relativeLayout, itemParentRl,rlBookmark;
    public TextView tvName, tvPrice, tvQuantity, tvDescription, tvAddCart, tvCal ,tvVendorName,tvCustomize,tvStockLeft;
    public ImageView ivRemove, ivAdd, ivIsVeg, ivBookmark ,ivDescriptionArrow;
    public View ivInactive;
    public LinearLayout llContainer;
    public CardView cv_menu_item;
    public boolean isDescShowing = false;

    public ProductItemViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_name);
        tvPrice = itemView.findViewById(R.id.tv_price);
        tvCal = itemView.findViewById(R.id.tv_cal);
        tvQuantity = itemView.findViewById(R.id.tv_quantity);
        tvDescription = itemView.findViewById(R.id.tv_description);
        ivAdd = itemView.findViewById(R.id.iv_add);
        ivRemove = itemView.findViewById(R.id.iv_remove);
        ivIsVeg = itemView.findViewById(R.id.iv_veg_non);
        tvAddCart = itemView.findViewById(R.id.tv_add_card);
        rvContiner = itemView.findViewById(R.id.rl_add_container);
        ivBookmark = itemView.findViewById(R.id.iv_bookmark);
        rlBookmark = itemView.findViewById(R.id.rl_bookmark);
        tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
        ivInactive = itemView.findViewById(R.id.iv_inactive);
        tvCustomize = itemView.findViewById(R.id.tv_customize);
        llContainer = itemView.findViewById(R.id.ll_add_container);
        ivDescriptionArrow = itemView.findViewById(R.id.description_arrow);
        relativeLayout = itemView.findViewById(R.id.rl1);
        itemParentRl = itemView.findViewById(R.id.rl);
        cv_menu_item = itemView.findViewById(R.id.cv_menu_item);
        tvStockLeft = itemView.findViewById(R.id.tv_stock_left);
    }
}
