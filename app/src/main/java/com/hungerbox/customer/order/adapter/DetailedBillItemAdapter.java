package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.order.adapter.viewholder.DetailedBillItemViewHolder;
import com.hungerbox.customer.util.OrderUtil;

import java.util.ArrayList;

public class DetailedBillItemAdapter extends RecyclerView.Adapter<DetailedBillItemViewHolder> {

    Activity activity;
    ArrayList<OrderProduct> products;
    private LayoutInflater inflater;

    public DetailedBillItemAdapter(Activity activity, ArrayList<OrderProduct> products){
        this.activity = activity;
        this.products = products;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public DetailedBillItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailedBillItemViewHolder(inflater.inflate(R.layout.detailed_bill_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedBillItemViewHolder holder, int position) {
        OrderProduct item = products.get(position);
        holder.tvItemName.setText(item.name);
        holder.tvQty.setText(Integer.toString(item.quantity));
        holder.tvPrice.setText(String.format("\u20B9 %.2f", item.price));

        switch (item.getStatus()){
            case OrderUtil.NEW:
                holder.tvStatus.setText("Placed");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorAccent));
                break;
            case OrderUtil.CONFIRMED:
                holder.tvStatus.setText("Accepted");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorAccent));
                break;
            case OrderUtil.PROCESSED:
                holder.tvStatus.setText("Ready");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorAccent));
                break;
            case OrderUtil.DELIVERED:
                holder.tvStatus.setText("Delivered");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.green));
                break;
            case OrderUtil.REJECTED:
                holder.tvStatus.setText("Cancelled");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.red));
                strikeThroughGivenRow(holder);
                break;
            case OrderUtil.NOT_COLLECTED:
                holder.tvStatus.setText("Not Collected");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.not_collected_color));
                break;
            case OrderUtil.PAYMENT_FAILED:
                holder.tvStatus.setVisibility(View.GONE);
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.red));
                holder.paymentFailedLine.setVisibility(View.VISIBLE);
                break;
            case OrderUtil.PRE_ORDER:
                holder.tvStatus.setText("Pre-Order");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorAccent));
                break;
            case OrderUtil.APPROVAL_PENDING:
                holder.tvStatus.setText("Approval\nPending");
                holder.tvStatus.setGravity(Gravity.CENTER);
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.green));
                break;
            case OrderUtil.FULFILLED:
                holder.tvStatus.setText("Fulfilled");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.green));
                break;
            default:
                holder.tvStatus.setText("-");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorAccent));
        }
    }

    private void strikeThroughGivenRow(DetailedBillItemViewHolder holder){
        holder.tvItemName.setPaintFlags(holder.tvItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvQty.setPaintFlags(holder.tvQty.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public int getItemCount() {
        if (products != null) {
            return products.size();
        }
        else {
            return 0;
        }
    }

    public void changeProducts(ArrayList<OrderProduct> products) {
        this.products = products;
        notifyDataSetChanged();
    }
}
