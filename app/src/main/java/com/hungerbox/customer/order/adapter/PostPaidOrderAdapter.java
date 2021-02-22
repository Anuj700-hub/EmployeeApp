package com.hungerbox.customer.order.adapter;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.PostPaidOrder;
import com.hungerbox.customer.util.view.OfflineDuesDialog;

import java.util.ArrayList;

public class PostPaidOrderAdapter extends RecyclerView.Adapter<PostPaidOrderAdapter.ViewHolder> {

    ArrayList<PostPaidOrder> postPaidOrders;
    OfflineDuesDialog.ItemClick itemClickListener;

    public PostPaidOrderAdapter(ArrayList<PostPaidOrder> postPaidOrders, OfflineDuesDialog.ItemClick itemClickListener){
        this.postPaidOrders = postPaidOrders;
        this.itemClickListener = itemClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.postpaid_order_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final PostPaidOrder postPaidOrder = postPaidOrders.get(position);
        viewHolder.amount.setText("₹ "+postPaidOrder.getAmount());
        viewHolder.date.setText(postPaidOrder.getTxnDate());
        viewHolder.orderID.setText(postPaidOrder.getMerchantTxnID());
        //click listener
        viewHolder.parentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener!=null){
                    itemClickListener.onItemClick(postPaidOrder.getMerchantOrderId());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return postPaidOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView amount,date,orderID;
        CardView parentCV;
        LinearLayout parentLL;

        public ViewHolder(View view) {
            super(view);

            amount = view.findViewById(R.id.tv_amount);
            date = view.findViewById(R.id.tv_date);
            orderID = view.findViewById(R.id.tv_order_id);
            parentCV = view.findViewById(R.id.cv_parent);
            parentLL = view.findViewById(R.id.ll_parent);


        }
    }
}
