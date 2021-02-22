package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by sandipanmitra on 12/4/17.
 */

public class NutritionItemViewHolder extends RecyclerView.ViewHolder {

    public TextView tvProteins, tvCarbs, tvFats, tvFibre, tvName;

    public NutritionItemViewHolder(View itemView) {
        super(itemView);
        tvProteins = itemView.findViewById(R.id.tv_protein);
        tvCarbs = itemView.findViewById(R.id.tv_carbs);
        tvFats = itemView.findViewById(R.id.tv_fats);
        tvFibre = itemView.findViewById(R.id.tv_fibre);
        tvName = itemView.findViewById(R.id.tv_item_name);
    }
}
