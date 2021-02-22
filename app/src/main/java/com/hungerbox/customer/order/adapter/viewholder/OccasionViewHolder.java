package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by manas on 17/11/16.
 */
public class OccasionViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvOccasion;
    public final TextView tvOccasionTime;
    public final ImageView ivOccasion;
    public final RelativeLayout rlOccasion;

    public OccasionViewHolder(View itemView) {
        super(itemView);

        tvOccasion = itemView.findViewById(R.id.tv_occasion);
        tvOccasionTime = itemView.findViewById(R.id.tv_occasion_time);
        ivOccasion = itemView.findViewById(R.id.iv_occasion);
        rlOccasion = itemView.findViewById(R.id.rl_occasion);


    }
}
