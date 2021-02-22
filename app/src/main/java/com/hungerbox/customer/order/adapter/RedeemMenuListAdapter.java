package com.hungerbox.customer.order.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.RedeemProduct;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.adapter.viewholder.RedeemProductItemViewHolder;
import com.hungerbox.customer.order.listeners.RedeemProductListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ImageHandling;

import java.util.List;

/**
 * Created by peeyush on 24/6/16.
 */
public class RedeemMenuListAdapter extends RecyclerView.Adapter<RedeemProductItemViewHolder> {

    boolean whiteFont = false;
    AppCompatActivity activity;
    List<RedeemProduct> products;
    LayoutInflater inflater;
    MainApplication mainApplication;
    RedeemProductListener redeemProductListener;
    Vendor vendor;
    long occasionId;
    Config config;

    public RedeemMenuListAdapter(AppCompatActivity activity, List<RedeemProduct> products,
                                 RedeemProductListener redeemProductListener) {
        this.activity = activity;
        this.products = products;
        this.redeemProductListener = redeemProductListener;
        mainApplication = (MainApplication) activity.getApplication();
        inflater = LayoutInflater.from(activity);
        config = AppUtils.getConfig(activity);
    }

    @Override
    public RedeemProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RedeemProductItemViewHolder(inflater.inflate(R.layout.redeem_product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RedeemProductItemViewHolder holder, int position) {
        final RedeemProduct product = products.get(position);
        holder.tvName.setText(product.getProduct());
        holder.tvVendorName.setText(product.getVendorName());
        holder.tvRedeem.setText("Redeem for " + ((int) product.getPrice()) + " Points");

        ImageHandling.loadRemoteImage(product.getVendorImage(), holder.ivVendorImage,-1,R.mipmap.ic_launcher,activity);

        holder.tvRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemProductListener.RedeemProduct(product);
            }
        });

        LogoutTask.updateTime();
    }


    @Override
    public int getItemCount() {
        if (products != null) {
            return products.size();
        } else {
            return 0;
        }
    }

    public void changeProducts(List<RedeemProduct> products) {
        this.products = products;
        notifyDataSetChanged();
    }
}
