package com.hungerbox.customer.offline.adapterOffline;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.order.adapter.viewholder.OrderProductItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peeyush on 7/7/16.
 */
public class OrderProductSummaryListAdapterOffline extends RecyclerView.Adapter<OrderProductItemViewHolder> {

    Activity activity;
    LayoutInflater inflater;
    List<OrderProduct> orderProducts;

    public OrderProductSummaryListAdapterOffline(Activity activity, List<OrderProduct> products) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.orderProducts = products;
    }

    @Override
    public OrderProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderProductItemViewHolder(inflater.inflate(R.layout.order_product_item_offline, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderProductItemViewHolder holder, int position) {
        OrderProduct orderProduct = orderProducts.get(position);
        holder.tvOrderCal.setText("");
        String name = orderProduct.getName();
        String fullProductText = orderProduct.getMultiLineOrderOptionText();
        if (fullProductText.length() > 2) {
            name += "\n" + fullProductText;
        }
        holder.tvOrderProduct.setText(name + " x " + orderProduct.getQuantity());
        holder.tvOrderPrice.setText(orderProduct.getPriceString());

        if (orderProduct.isVeg()) {
            holder.ivVegNonVeg.setBackgroundResource(R.color.green);
        } else {
            holder.ivVegNonVeg.setBackgroundResource(R.color.red);
        }

        holder.ivVegNonVeg.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return (orderProducts == null) ? 0 : orderProducts.size();
    }

    public void changeProducts(ArrayList<OrderProduct> products) {
        this.orderProducts = products;
    }
}
