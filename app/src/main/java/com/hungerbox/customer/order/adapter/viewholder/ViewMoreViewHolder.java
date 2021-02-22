package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by ranjeet on 17,January,2019
 */
public class ViewMoreViewHolder extends RecyclerView.ViewHolder {
    public TextView tvMoreOption;
    public ViewMoreViewHolder(View itemView) {
        super(itemView);
        tvMoreOption = itemView.findViewById(R.id.tv_more_payment);
    }
}
