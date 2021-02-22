package com.hungerbox.customer.payment.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.PaymentMethodMethod;
import com.hungerbox.customer.payment.ItemSelectedListener;
import com.hungerbox.customer.payment.adapter.viewholder.NetbankingOptionItemViewHolder;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 4/13/18.
 */

public class NetBankingOptionItemAdapter extends RecyclerView.Adapter<NetbankingOptionItemViewHolder> {

    Activity activity;
    LayoutInflater inflater;
    ArrayList<PaymentMethodMethod> paymentMethods;
    ItemSelectedListener itemSelectedListener;

    public NetBankingOptionItemAdapter(Activity activity, ArrayList<PaymentMethodMethod> paymentMethods
            , ItemSelectedListener itemSelectedListener) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.paymentMethods = paymentMethods;
        this.itemSelectedListener = itemSelectedListener;
    }


    @Override
    public NetbankingOptionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NetbankingOptionItemViewHolder(inflater.inflate(R.layout.netbanking_option_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NetbankingOptionItemViewHolder holder, int position) {
        final PaymentMethodMethod paymentMethod = paymentMethods.get(position);
        holder.tvOptionName.setText(paymentMethod.getName());
        if (paymentMethod.isSelected()) {
            holder.cbSelected.setChecked(true);
        } else {
            holder.cbSelected.setChecked(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemSelectedListener.itemSelected(paymentMethod);
            }
        });
        holder.cbSelected.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }
}
