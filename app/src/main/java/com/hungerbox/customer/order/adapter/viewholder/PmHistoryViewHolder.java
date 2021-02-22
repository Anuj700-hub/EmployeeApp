package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by manas dadheech on 12/7/17.
 */

public class PmHistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView tvRemarks;
    public TextView tvDate, tvCreditDebit;


    public PmHistoryViewHolder(View itemView) {
        super(itemView);
        tvDate = itemView.findViewById(R.id.tv_date);
        tvCreditDebit = itemView.findViewById(R.id.tv_credit_debit);
        tvRemarks = itemView.findViewById(R.id.tv_remarks);

    }
}
