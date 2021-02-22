package com.hungerbox.customer.payment.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by sandipanmitra on 4/13/18.
 */

public class NetbankingOptionItemViewHolder extends RecyclerView.ViewHolder {

    public TextView tvOptionName;
    public ImageView ivLogo;
    public CheckBox cbSelected;

    public NetbankingOptionItemViewHolder(View itemView) {
        super(itemView);
        tvOptionName = itemView.findViewById(R.id.tv_option_name);
        ivLogo = itemView.findViewById(R.id.iv_logo);
        cbSelected = itemView.findViewById(R.id.cb_option_name);
    }
}
