package com.hungerbox.customer.payment.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

public class PaymentMethodUpiListViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlContainer;
    public LinearLayout llAlert;
    public ImageView ivIcon,offerBadge;
    public TextView tvUpiAppName,tvAlertMessage,tvOfferText;
    public CheckBox cbSelect;

    public PaymentMethodUpiListViewHolder(@NonNull View itemView) {
        super(itemView);
        rlContainer = itemView.findViewById(R.id.rl_container);
        llAlert = itemView.findViewById(R.id.ll_alert);
        ivIcon = itemView.findViewById(R.id.iv_upi_app_icon);
        offerBadge = itemView.findViewById(R.id.iv_offers_badge);
        tvUpiAppName = itemView.findViewById(R.id.tv_upi_app_name);
        tvAlertMessage = itemView.findViewById(R.id.tv_alert_message);
        tvOfferText = itemView.findViewById(R.id.tv_offer_text);
        cbSelect = itemView.findViewById(R.id.cb_payment_method);
    }
}
