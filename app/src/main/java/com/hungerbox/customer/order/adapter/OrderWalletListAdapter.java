package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderWallet;
import com.hungerbox.customer.order.adapter.viewholder.OrderWalletItemViewHolder;
import com.hungerbox.customer.util.ImageHandling;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 12/11/17.
 */

public class OrderWalletListAdapter extends RecyclerView.Adapter<OrderWalletItemViewHolder> {

    ArrayList<OrderWallet> orderWallets;
    Activity activity;
    LayoutInflater layoutInflater;

    public OrderWalletListAdapter(Activity activity, ArrayList<OrderWallet> orderWallets) {
        this.activity = activity;
        layoutInflater = LayoutInflater.from(activity);
        this.orderWallets = orderWallets;
    }

    public void updateWallets(ArrayList<OrderWallet> orderWallets) {
        this.orderWallets = orderWallets;
    }

    @Override
    public OrderWalletItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderWalletItemViewHolder(layoutInflater.inflate(R.layout.order_wallet_item, parent, false));
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
           if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.green));
            }else{
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.green));
            }
        } else if (orderWallet.getStatus().equalsIgnoreCase("0")) {
            holder.tvStatus.setText("Failed");
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.red));
            }else{
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.red));
            }
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            holder.tvStatusTitle.setVisibility(View.GONE);
        }


        if(orderWallet.getReferenceType().equalsIgnoreCase("Cancel Order")){
            holder.tvTitle.setText("Refunded To");
        }else{
            holder.tvTitle.setText("Paid Via");
        }

        if(orderWallet.hasLogoImg()){
            holder.ivPaymentLogo.setVisibility(View.VISIBLE);
            holder.tvPaymentMethod.setVisibility(View.GONE);

            ImageHandling.loadRemoteImage(orderWallet.getLogoImg(), holder.ivPaymentLogo,-1,-1, activity);
        }
        else{
            holder.ivPaymentLogo.setVisibility(View.GONE);
            holder.tvPaymentMethod.setVisibility(View.VISIBLE);
            holder.tvPaymentMethod.setText(orderWallet.getWalletName());
        }

        holder.tvAmount.setText(String.format("\u20B9 %.2f", Math.abs(orderWallet.getChange())));
        holder.tvTransactionType.setText(orderWallet.getReferenceType());

        if(position == 0){
            holder.ivDivider.setVisibility(View.GONE);
        }else{
            holder.ivDivider.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return this.orderWallets.size();
    }
}
