package com.hungerbox.customer.offline.adapterOffline;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderWallet;
import com.hungerbox.customer.order.adapter.viewholder.OrderWalletItemViewHolder;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 12/11/17.
 */

public class OrderWalletListAdapterOffline extends RecyclerView.Adapter<OrderWalletItemViewHolder> {

    ArrayList<OrderWallet> orderWallets;
    Activity activity;
    LayoutInflater layoutInflater;

    public OrderWalletListAdapterOffline(Activity activity, ArrayList<OrderWallet> orderWallets) {
        this.activity = activity;
        layoutInflater = LayoutInflater.from(activity);
        this.orderWallets = orderWallets;
    }

    public void updateWallets(ArrayList<OrderWallet> orderWallets) {
        this.orderWallets = orderWallets;
    }

    @Override
    public OrderWalletItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderWalletItemViewHolder(layoutInflater.inflate(R.layout.order_wallet_item_offline, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderWalletItemViewHolder holder, int position) {

        OrderWallet orderWallet = orderWallets.get(position);

        holder.tvTransactionTitle.setVisibility(View.VISIBLE);
        holder.tvTransactionId.setVisibility(View.VISIBLE);
        holder.tvStatus.setVisibility(View.VISIBLE);
        holder.tvStatusTitle.setVisibility(View.VISIBLE);
        if (orderWallet.getTransactionId()==null || orderWallet.getTransactionId().isEmpty()){
            holder.tvTransactionId.setText("NA");
        }
        else{
            holder.tvTransactionId.setText(orderWallet.getTransactionId());
        }
        if (orderWallet.getStatus().equalsIgnoreCase("1")) {
            holder.tvStatus.setText("Success");
        } else if (orderWallet.getStatus().equalsIgnoreCase("0")) {
            holder.tvStatus.setText("Failed");
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            holder.tvStatusTitle.setVisibility(View.GONE);
        }


        if(orderWallet.getReferenceType().equalsIgnoreCase("Cancel Order")){
            holder.tvTitle.setText("Refunded To");
        }else{
            holder.tvTitle.setText("Paid Via");
        }

        holder.tvPaymentMethod.setText(orderWallet.getWalletName());
        holder.tvAmount.setText(String.format("%.2f", Math.abs(orderWallet.getChange())));
        holder.tvTransactionType.setText(orderWallet.getReferenceType());
    }

    @Override
    public int getItemCount() {
        return this.orderWallets.size();
    }
}
