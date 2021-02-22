package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by manas on 17/11/16.
 */
public class PaymentMethodViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout llAlert;
    public TextView tvRechargeLink, tvConvenienceFee;
    public CheckBox cbPayment;
    public TextView tvPaymentMethod, tvOfferText,tvAlertMessage;
    public ImageView ivPaymentLogo,ivOffersBadge,ivWalletBreakup;
    public RelativeLayout rlPaymentMethodItem;
    public ProgressBar pbPayment;
    public View tvCardDetailsBox;
    public TextView tvCardDetailsCompany, tvCardDetailsType, tvCardDetailsSeprator ,tvOrderAmount;

    public PaymentMethodViewHolder(View itemView) {
        super(itemView);
        tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
        ivWalletBreakup = itemView.findViewById(R.id.iv_wallet);
        ivPaymentLogo = itemView.findViewById(R.id.iv_payment_logo);
        rlPaymentMethodItem = itemView.findViewById(R.id.rl_payment_method_item);
        pbPayment = itemView.findViewById(R.id.pb_payment_methods);
        cbPayment = itemView.findViewById(R.id.cb_payment_method);
        tvRechargeLink = itemView.findViewById(R.id.tv_payment_method_recharge_link);
        tvOfferText = itemView.findViewById(R.id.tv_offer_text);
        tvAlertMessage = itemView.findViewById(R.id.tv_alert_message);
        llAlert = itemView.findViewById(R.id.ll_alert);
        tvCardDetailsBox = itemView.findViewById(R.id.tv_card_detail_text);
        tvCardDetailsCompany = itemView.findViewById(R.id.tv_card_detail_text1);
        tvCardDetailsType = itemView.findViewById(R.id.tv_card_detail_text2);
        tvCardDetailsSeprator = itemView.findViewById(R.id.tv_card_detail_text_seprator);
        tvOrderAmount = itemView.findViewById(R.id.tv_order_amount);
        //tvConvenienceFee = itemView.findViewById(R.id.tv_convenience_text);
        ivOffersBadge = itemView.findViewById(R.id.iv_offers_badge);
    }
}
