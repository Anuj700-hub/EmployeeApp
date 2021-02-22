package com.hungerbox.customer.health.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by sandipanmitra on 11/23/17.
 */

public class FoodItemDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView dishName, dishQty, dishCalories;
    public ImageView dishDelete, dishRemove, dishAdd;

    public FoodItemDetailViewHolder(View itemView) {
        super(itemView);
        dishName = itemView.findViewById(R.id.dishName);
        dishQty = itemView.findViewById(R.id.dishQty);
        dishDelete = itemView.findViewById(R.id.dishDelete);
        dishCalories = itemView.findViewById(R.id.dishCalories);
        dishRemove = itemView.findViewById(R.id.iv_remove);
        dishAdd = itemView.findViewById(R.id.iv_add);
    }
}
