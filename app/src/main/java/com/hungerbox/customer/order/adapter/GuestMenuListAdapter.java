package com.hungerbox.customer.order.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.order.activity.GuestOrderActivity;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.util.EventUtil;

import java.util.List;

/**
 * Created by peeyush on 24/6/16.
 */
public class GuestMenuListAdapter extends RecyclerView.Adapter<ProductItemViewHolder> {

    GuestOrderActivity activity;
    List<Product> products;
    LayoutInflater inflater;
    MainApplication mainApplication;
    CartListener listener;
    Cart cart;

    public GuestMenuListAdapter(GuestOrderActivity activity, List<Product> products,
                                CartListener listener,
                                Cart cart) {
        this.activity = activity;
        this.products = products;
        this.cart = cart;
        this.listener = listener;
        mainApplication = (MainApplication) activity.getApplication();
        inflater = LayoutInflater.from(activity);
    }


    @Override
    public ProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductItemViewHolder(inflater.inflate(R.layout.product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ProductItemViewHolder holder, int position) {
        final Product product = products.get(position);
        holder.tvName.setText(product.getName());


        holder.tvPrice.setText("Free");

        String desc = product.getDesc();
        holder.tvDescription.setText(Html.fromHtml(desc));
        int orderQty = cart.getQuantityIfProductExistsInCart(product.getId());
        holder.tvQuantity.setText(String.format("%d", orderQty));


        updateOrderActionItem(orderQty, holder);

        holder.tvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToCart(product, holder);
            }
        });

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUtil.logBaseEvent(activity, EventUtil.CART_ADDITION);
                addProductToCart(product, holder);
            }
        });

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutTask.updateTime();
                removeProduct(product, holder);
            }
        });

        holder.ivAdd.setVisibility(View.VISIBLE);
        holder.ivRemove.setVisibility(View.VISIBLE);
        holder.tvQuantity.setVisibility(View.VISIBLE);
        holder.tvCal.setVisibility(View.GONE);
        updateOrderActionItem(orderQty, holder);


        holder.tvAddCart.setText("  Add  ");

        LogoutTask.updateTime();
    }

    private void removeProduct(Product product, ProductItemViewHolder holder) {
        if (listener != null)
            listener.removeProduct(product);
    }


    private void updateOrderActionItem(int orderQty, ProductItemViewHolder holder) {
        if (orderQty > 0) {
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.ivRemove.setVisibility(View.VISIBLE);
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvAddCart.setVisibility(View.GONE);
        } else {
            holder.ivAdd.setVisibility(View.GONE);
            holder.ivRemove.setVisibility(View.GONE);
            holder.tvQuantity.setVisibility(View.GONE);
            holder.tvAddCart.setVisibility(View.VISIBLE);
        }
    }

    private void addProductToCart(Product product, ProductItemViewHolder holder) {
        if (listener != null)
            listener.addProduct(product);
    }

    @Override
    public int getItemCount() {
        if (products != null) {
//            AppUtils.HbLog("Peeyush", "products valid");
            return products.size();
        } else {
//            AppUtils.HbLog("Peeyush", "products invalid");
            return 0;
        }
    }

    public void changeProducts(List<Product> products, Cart cart) {
        this.products = products;
        this.cart = cart;
    }

    public interface CartListener {
        void addProduct(Product product);

        void removeProduct(Product product);
    }
}
