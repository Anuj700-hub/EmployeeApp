package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by sandipanmitra on 11/11/17.
 */

public class MenuOptionViewHolder extends ViewHolder {
    public TextView tvName;
    public LinearLayout llOptionItemContainer;


    public MenuOptionViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_menu_option_name);
        llOptionItemContainer = itemView.findViewById(R.id.ll_option_item_container);
    }
}
