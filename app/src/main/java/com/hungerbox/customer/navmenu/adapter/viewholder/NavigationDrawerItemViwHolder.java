package com.hungerbox.customer.navmenu.adapter.viewholder;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by peeyush on 22/6/16.
 */
public class NavigationDrawerItemViwHolder extends RecyclerView.ViewHolder {

    public AppCompatImageView ivNavIcon;
    public TextView tvNavItem;
    public TextView ivNewIndicator;

    public NavigationDrawerItemViwHolder(View itemView) {
        super(itemView);
        tvNavItem = itemView.findViewById(R.id.tv_nav_item_text);
        ivNavIcon = itemView.findViewById(R.id.iv_nav_icon);
        ivNewIndicator = itemView.findViewById(R.id.iv_new);

    }
}
