package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by manas on 17/11/16.
 */
public class LocationViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvLocationName, isDefault;
    public final ImageView ivSelected;
    public final RelativeLayout rlLocation;
    public final LinearLayout llDensity;
    public final CheckBox cbLocation;

    public LocationViewHolder(View itemView) {
        super(itemView);
        tvLocationName = itemView.findViewById(R.id.tv_location_change);
        isDefault = itemView.findViewById(R.id.tv_default_location);
        ivSelected = itemView.findViewById(R.id.iv_selected);
        rlLocation = itemView.findViewById(R.id.rv_location);
        llDensity = itemView.findViewById(R.id.ll_density);
        cbLocation = itemView.findViewById(R.id.cb_location);
    }
}
