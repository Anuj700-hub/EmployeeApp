package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderProduct;

import java.util.ArrayList;

public class OrderTrackerItemAdapter extends PagerAdapter{
    private LayoutInflater inflater;
    private ArrayList<OrderProduct> items;
    private Activity activity;

    public OrderTrackerItemAdapter(Activity activity, ArrayList<OrderProduct> products){
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.items = products;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.tracker_item_text, collection, false);

        setupItemUI(layout, position);

        collection.addView(layout);
        return layout;
    }

    private void setupItemUI(ViewGroup layout, int position){
        OrderProduct item = items.get(position);

        TextView tvItemName = layout.findViewById(R.id.tv_item_name_ot);
        tvItemName.bringToFront();
        tvItemName.setText(item.getName());

    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void changeProducts(ArrayList<OrderProduct> products) {
        this.items = products;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
