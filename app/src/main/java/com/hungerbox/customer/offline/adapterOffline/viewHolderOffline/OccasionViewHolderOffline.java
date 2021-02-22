package com.hungerbox.customer.offline.adapterOffline.viewHolderOffline;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;

/**
 * Created by manas on 17/11/16.
 */
public class OccasionViewHolderOffline extends RecyclerView.ViewHolder {

    public final TextView tvOccasion;
    public final ImageView ivOccasion;
    public final RelativeLayout rlOccasion;

    public OccasionViewHolderOffline(View itemView) {
        super(itemView);

        tvOccasion = itemView.findViewById(R.id.tv_occasion);
        ivOccasion = itemView.findViewById(R.id.iv_occasion);
        rlOccasion = itemView.findViewById(R.id.rl_occasion);


    }
}
