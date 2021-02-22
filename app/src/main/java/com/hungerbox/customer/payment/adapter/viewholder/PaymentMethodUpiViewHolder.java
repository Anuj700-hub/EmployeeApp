package com.hungerbox.customer.payment.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

public class PaymentMethodUpiViewHolder extends RecyclerView.ViewHolder {

    public TextView tvMethodName;
    public RecyclerView rvPaymentUpi;
    public RelativeLayout rlContainer;

    public PaymentMethodUpiViewHolder(@NonNull View itemView) {
        super(itemView);
        tvMethodName = itemView.findViewById(R.id.tv_payment_method);
        rvPaymentUpi = itemView.findViewById(R.id.rv_upi_method);
        rlContainer = itemView.findViewById(R.id.rl_container);
    }
}
