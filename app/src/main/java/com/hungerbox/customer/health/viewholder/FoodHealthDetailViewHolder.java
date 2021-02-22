package com.hungerbox.customer.health.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by manas on 12/4/17.
 */

public class FoodHealthDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView tvItemName, tvCal, tvQuantity, tvAddItem, tvPack;
    public ImageView ivIncQty, ivDecQty;


    public FoodHealthDetailViewHolder(View itemView) {
        super(itemView);

        tvItemName = itemView.findViewById(R.id.tv_item_name);
        tvCal = itemView.findViewById(R.id.tv_item_cal);
        tvPack = itemView.findViewById(R.id.tv_item_cal2);
        ivIncQty = itemView.findViewById(R.id.iv_inc_qty);
        ivDecQty = itemView.findViewById(R.id.iv_dec_qty);
        tvQuantity = itemView.findViewById(R.id.tv_quantity);
        tvAddItem = itemView.findViewById(R.id.tv_item_add);


    }
}
