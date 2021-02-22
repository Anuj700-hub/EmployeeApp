package com.hungerbox.customer.health.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by manas dadheech on 12/7/17.
 */

public class WeightHistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView tvDate, tvWeight;


    public WeightHistoryViewHolder(View itemView) {
        super(itemView);
        tvDate = itemView.findViewById(R.id.tv_date);
        tvWeight = itemView.findViewById(R.id.tv_weight);

    }
}
