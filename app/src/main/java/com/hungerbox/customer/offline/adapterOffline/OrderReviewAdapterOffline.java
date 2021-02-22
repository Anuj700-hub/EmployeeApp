package com.hungerbox.customer.offline.adapterOffline;

import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.listenersOffline.OrderReviewProductRefreshListenerOffline;
import com.hungerbox.customer.offline.modelOffline.Cart;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;
import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;

import org.json.JSONObject;

public class OrderReviewAdapterOffline extends RecyclerView.Adapter<ProductItemViewHolder> {

    Activity activity;
    LayoutInflater inflater;

    OrderOffline order;
    Cart cart;
    Config config;
    OrderReviewProductRefreshListenerOffline orderReviewProductRefreshListener;

    public OrderReviewAdapterOffline(Activity activity, OrderOffline order, Cart cart,
                                     OrderReviewProductRefreshListenerOffline listener) {
        this.activity = activity;
        this.order = order;
        this.cart = cart;
        this.orderReviewProductRefreshListener = listener;
        inflater = LayoutInflater.from(activity);
        config = AppUtils.getConfig(activity);
    }

    @Override
    public ProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductItemViewHolder(inflater.inflate(R.layout.product_list_item_offline, parent, false));
    }


    @Override
    public void onBindViewHolder(final ProductItemViewHolder holder, final int position) {
        final OrderProductOffline orderProduct = order.getProducts().get(position);
        final ProductOffline product = orderProduct.getProduct();
        final VendorOffline vendor = cart.getVendor();
        int orderQty = MainApplicationOffline.getOrderForOrderItem(orderProduct.getOrderItemId(), 1);
        orderProduct.setQuantity(orderQty);
        holder.tvQuantity.setText(String.format("%d", orderQty));
        holder.tvName.setText(orderProduct.getName());
        if (product.isFree()) {
            if (product.discountedPrice == 0) {
                if (AppUtils.getConfig(activity).isHide_price())
                    holder.tvPrice.setText("");
                else
                    holder.tvPrice.setText(AppUtils.getConfig(activity).getCompany_paid_text());
            } else if (AppUtils.getConfig(activity).isHide_discount()) {
                holder.tvPrice.setText("₹ " + product.getDiscountedPrice() + " ");
            } else {
                String oldPrice = "₹ " + product.getPrice() + " ";
                String newPrice = "₹ " + product.getDiscountedPrice() + " ";
                holder.tvPrice.setText(newPrice + oldPrice, TextView.BufferType.SPANNABLE);
                Spannable spannable = (Spannable) holder.tvPrice.getText();
                spannable.setSpan(new StrikethroughSpan(), newPrice.length(), (oldPrice.length() + newPrice.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else
            holder.tvPrice.setText(product.getFinalPriceText(activity));
        holder.tvAddCart.setVisibility(View.GONE);
        holder.ivBookmark.setVisibility(View.GONE);

        double totalCalories = product.getTotalCalories();

        if (totalCalories > 0 && config.isHealthEnabled()) {
            String caloriesStr = String.format("%.2f cal", (totalCalories * orderProduct.getQuantity()));
            holder.tvCal.setText(caloriesStr);
            holder.tvCal.setVisibility(View.VISIBLE);
        } else {
            holder.tvCal.setVisibility(View.INVISIBLE);
        }

        holder.tvCal.setVisibility(View.GONE);

        String optionText = orderProduct.getMultiLineOrderOptionText();
        holder.tvDescription.setText(optionText);

        if (orderProduct.isVeg()) {
            holder.ivIsVeg.setImageResource(R.drawable.ic_veg_icon);
        } else {
            holder.ivIsVeg.setImageResource(R.drawable.ic_non_veg);
        }

        final int cartQty = MainApplicationOffline.getTotalOrderCount(1);


        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cartQty>3){
                    AppUtils.showToast("You cannot order more than "+ApplicationConstants.MAX_QUANTITIY_OFFLINE+" quantity",false,0);
                    return;
                }

                LogoutTask.updateTime();
                EventUtil.FbEventLog(activity, EventUtil.CART_ITEM_EDIT, EventUtil.SCREEN_CART);
                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Order Review");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_ITEM_INCR, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

                int orderQty = MainApplicationOffline.getOrderForOrderItem(orderProduct.getOrderItemId(), 1);
                if (orderProduct.isFree()
                        && product.getFreeQuantity() > 0 && orderQty >= product.getFreeQuantity()) {
                    //cannot more than free quantity
                    if (!cart.getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_OFFICIAL)) {
                        AppUtils.showToast("Cannot order more than " + orderQty + " " + product.getName(), false, 2);
                        return;
                    }
                }
//                orderProduct.setQuantity(orderProduct.getQuantity()+1);
                //TODO order
                MainApplicationOffline.addProductQuantity(orderProduct);
                order.addPrice(orderProduct.getTotalPrice());
                holder.tvQuantity.setText(String.format("%d", orderProduct.getQuantity()));
                if (orderReviewProductRefreshListener != null)
                    orderReviewProductRefreshListener.refreshOrderList(orderProduct, position, true);
            }
        });

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutTask.updateTime();
                EventUtil.FbEventLog(activity, EventUtil.CART_RECOMMENDED_ADD, EventUtil.SCREEN_CART);
                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Order Review");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_ITEM_DECR, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

                MainApplicationOffline.removeProductFromCart(orderProduct);
                if (orderProduct.getQuantity() > 1) {
                    order.removePrice(orderProduct.getPrice());
                    holder.tvQuantity.setText(String.format("%d", orderProduct.getQuantity()));
                    if (orderReviewProductRefreshListener != null)
                        orderReviewProductRefreshListener.refreshOrderList(orderProduct, position, true);
                } else {
                    orderProduct.setQuantity(orderProduct.getQuantity());
                    order.removePrice(orderProduct.getPrice());
                    if (orderReviewProductRefreshListener != null)
                        orderReviewProductRefreshListener.refreshOrderList(orderProduct, position, true);
                }

            }
        });
        if (product.getOptionResponse().getSubProducts().size() > 0) {
            holder.tvCustomize.setVisibility(View.VISIBLE);
        } else {
            holder.tvCustomize.setVisibility(View.GONE);
        }
        LogoutTask.updateTime();
    }


    @Override
    public int getItemCount() {
        return order.getProducts().size();
    }

    public void changeOrder(OrderOffline order, Cart cart) {
        this.order = order;
        this.cart = cart;
    }
}
