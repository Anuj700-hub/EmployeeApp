package com.hungerbox.customer.offline.adapterOffline.viewHolderOffline;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.hungerbox.customer.R;

public class BannerOfflineViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivVendor;
    public BannerOfflineViewHolder(@NonNull View itemView) {
        super(itemView);
        ivVendor = itemView.findViewById(R.id.iv_banner);

    }
}
