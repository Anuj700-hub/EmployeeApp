package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.order.adapter.viewholder.PickupItemViewHolder;

import java.util.ArrayList;

public class PickupItemAdapter extends RecyclerView.Adapter<PickupItemViewHolder> {

    private Activity activity;
    private ArrayList<OrderProduct> products;
    private LayoutInflater inflater;

    public PickupItemAdapter(Activity activity, ArrayList<OrderProduct> products){
        this.activity = activity;
        this.products = products;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public PickupItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PickupItemViewHolder(inflater.inflate(R.layout.pickup_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PickupItemViewHolder holder, int position) {
        OrderProduct item = products.get(position);
        holder.tvItemName.setText(item.name);
        holder.itemStatusView.setItemStatus(item);
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
