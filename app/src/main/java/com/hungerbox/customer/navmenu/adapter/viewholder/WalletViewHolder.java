package com.hungerbox.customer.navmenu.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;


public class WalletViewHolder extends RecyclerView.ViewHolder {

    public TextView tv_txn_type, tv_txn_id, tv_txn_id_label, tv_txn_amount, tv_txn_date, tv_txn_time,tvComment,tv_wallet_label,tv_wallet_amount,tv_transaction_status;
    public ImageView iv_txn_status;


    public WalletViewHolder(View itemView) {
        super(itemView);
        tv_txn_type = itemView.findViewById(R.id.transaction_type);
        tv_txn_id = itemView.findViewById(R.id.transaction_id);
        tv_txn_id_label = itemView.findViewById(R.id.order_id_label);
        tv_txn_amount = itemView.findViewById(R.id.transaction_amount);
        tv_txn_date = itemView.findViewById(R.id.tv_wallet_date);
        tv_txn_time = itemView.findViewById(R.id.tv_wallet_time);
        iv_txn_status = itemView.findViewById(R.id.transaction_status);
        tvComment = itemView.findViewById(R.id.tv_comment);
        tv_wallet_label = itemView.findViewById(R.id.wallet_amount_label);
        tv_wallet_amount = itemView.findViewById(R.id.wallet_amount);
        tv_transaction_status = itemView.findViewById(R.id.tv_transcation_status);

    }
}
