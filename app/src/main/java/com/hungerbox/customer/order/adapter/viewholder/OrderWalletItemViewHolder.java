package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by sandipanmitra on 12/11/17.
 */

public class OrderWalletItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvPaymentMethod;
    public TextView tvTitle, tvAmount, tvTransactionTitle, tvTransactionId, tvStatusTitle, tvStatus,
            tvTransactionType;
    public ImageView ivDivider, ivPaymentLogo;

    public OrderWalletItemViewHolder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tv_title);
        tvPaymentMethod = itemView.findViewById(R.id.tv_method_name);
        ivPaymentLogo = itemView.findViewById(R.id.tv_method_image);
        tvAmount = itemView.findViewById(R.id.tv_amount);
        tvTransactionTitle = itemView.findViewById(R.id.tv_transaction_id_title);
        tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
        tvStatusTitle = itemView.findViewById(R.id.tv_status_title);
        tvStatus = itemView.findViewById(R.id.tv_status);
        tvTransactionType = itemView.findViewById(R.id.tv_transaction_type);
        ivDivider = itemView.findViewById(R.id.iv_divider);
    }
}
