package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by sandipanmitra on 4/6/18.
 */

public class PaymentHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView tvHeader;
    public RelativeLayout llPaymentHeader;
    public ImageView ivDropDown;

    public PaymentHeaderViewHolder(View itemView) {
        super(itemView);
        tvHeader = itemView.findViewById(R.id.tv_text);
        llPaymentHeader = itemView.findViewById(R.id.ll_payment_header);
        ivDropDown = itemView.findViewById(R.id.header_dropdown);

    }
}
