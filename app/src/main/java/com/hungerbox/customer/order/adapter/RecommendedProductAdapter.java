package com.hungerbox.customer.order.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.fragment.NutritionFragment;
import com.hungerbox.customer.order.listeners.OrderReviewProductRefreshListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.EventUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sandipanmitra on 2/15/18.
 */

public class RecommendedProductAdapter extends RecyclerView.Adapter<ProductItemViewHolder> {

    AppCompatActivity activity;
    LayoutInflater inflater;
    MainApplication mainApplication;
    Vendor vendor;
    long occasionId;
    Order order;
    Config config;
    OrderReviewProductRefreshListener orderReviewProductRefreshListener;
    ArrayList<Product> products;


    public RecommendedProductAdapter(AppCompatActivity activity, Order order, Cart cart,
                                     ArrayList<Product> products,
                                     OrderReviewProductRefreshListener listener) {
        this.activity = activity;
        this.mainApplication = (MainApplication) activity.getApplication();
        this.order = order;
        this.products = products;
        this.vendor = cart.getVendor();
        this.occasionId = cart.getOccasionId();
        this.orderReviewProductRefreshListener = listener;
        inflater = LayoutInflater.from(activity);
        config = AppUtils.getConfig(activity);
    }

    @Override
    public ProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductItemViewHolder(inflater.inflate(R.layout.product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ProductItemViewHolder holder, final int position) {
        final Product product = products.get(position);

        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
        holder.tvQuantity.setText(String.format("%d", orderQty));
        holder.tvName.setText(product.getName());
        if (product.isFree()) {
            if (AppUtils.getConfig(activity).isHide_price())
                holder.tvPrice.setText("");
            else
                holder.tvPrice.setText(AppUtils.getConfig(activity).getCompany_paid_text());
        } else {
            holder.tvPrice.setText(product.getFinalPriceText(activity));
        }

        /**
         * Only add to cart to be shown in this
         * */
        holder.ivAdd.setVisibility(View.GONE);
        holder.ivRemove.setVisibility(View.GONE);
        holder.tvQuantity.setVisibility(View.GONE);
        holder.tvAddCart.setVisibility(View.VISIBLE);

        double totalCalories = product.getTotalCalories();

        if (totalCalories > 0 && config.isHealthEnabled()) {
            String caloriesStr = String.format("%.2f cal", 1);
            holder.tvCal.setText(caloriesStr);
            holder.tvCal.setVisibility(View.VISIBLE);
        } else {
            holder.tvCal.setVisibility(View.INVISIBLE);
        }

        holder.tvCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNutritionPopup(product);
            }
        });

        holder.tvDescription.setText(product.getDesc());

        if (product.isProductVeg()) {
            holder.ivIsVeg.setImageResource(R.drawable.ic_veg_icon);
        } else {
            holder.ivIsVeg.setImageResource(R.drawable.ic_non_veg);
        }


        holder.tvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutTask.updateTime();
                //TODO order
                HashMap<String,Object> map =new HashMap<>() ;

                EventUtil.FbEventLog(activity, EventUtil.CART_RECOMMENDED_ADD, EventUtil.SCREEN_CART);
                HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.CART_RECOMMENDED_ADD);
                mainApplication.getCart().addProductToCart(product, holder, mainApplication, null, activity, vendor, occasionId,map);
//        LogoutTask.updateTime();
//        if(mainApplication.isVendorValid(vendor.getId())){
//            //check for free quantity
//                    /*
//                    * If there is free quantity then that item cannot got beyond free quantity
//                    */
//            if(!addedForFree(product, vendor, holder)){
//                addNonFreeProductToCart(product, holder);
//            }
//        }else{
//            vendorValidationListener.validateAndAddProduct(vendor.clone(), product.clone(), false);
//        }
                int orderQty = mainApplication.getOrderQuantityForProduct( product.getId());
                updateOrderActionItem(orderQty, holder);
//                TODO check this
//                if (orderReviewProductRefreshListener != null)
//                    orderReviewProductRefreshListener.refreshOrderList(orderProduct, position, true);
            }
        });

        if (product.getOptionResponse().getSubProducts().size() > 0) {
            holder.tvAddCart.setText("ADD +");
            holder.tvCustomize.setVisibility(View.VISIBLE);
        } else {
            holder.tvAddCart.setText(" ADD ");
            holder.tvCustomize.setVisibility(View.GONE);
        }
        LogoutTask.updateTime();

        updateOrderActionItem(orderQty, holder);
    }


    private void updateOrderActionItem(int orderQty, ProductItemViewHolder holder) {
        if (orderQty > 0) {
            holder.itemView.setEnabled(false);
            holder.itemView.setAlpha(0.4f);
            holder.llContainer.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setEnabled(true);
            holder.itemView.setAlpha(1.0f);
            holder.llContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void changeOrder(Order order, Cart cart) {
        this.order = order;
    }

    private void showNutritionPopup(Product product) {
        if (product.nutrition.getNutritionItems().size() > 0) {
            if (activity instanceof AppCompatActivity)
                NutritionFragment.show(activity, product.nutrition, product.getName(), 1);
        }
    }
}
