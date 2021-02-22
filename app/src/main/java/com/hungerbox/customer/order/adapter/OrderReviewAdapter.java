package com.hungerbox.customer.order.adapter;

import android.app.Activity;
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
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.fragment.NutritionFragment;
import com.hungerbox.customer.order.listeners.OrderReviewProductRefreshListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by peeyush on 27/6/16.
 */
public class OrderReviewAdapter extends RecyclerView.Adapter<ProductItemViewHolder> {

    Activity activity;
    LayoutInflater inflater;
    MainApplication mainApplication;

    Order order;
    Cart cart;
    Config config;
    OrderReviewProductRefreshListener orderReviewProductRefreshListener;

    public OrderReviewAdapter(Activity activity, Order order, Cart cart,
                              OrderReviewProductRefreshListener listener) {
        this.activity = activity;
        this.mainApplication = (MainApplication) activity.getApplication();
        this.order = order;
        this.cart = cart;
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
        final OrderProduct orderProduct = order.getProducts().get(position);
        final Product product = orderProduct.getProduct();
        final Vendor vendor = cart.getVendor();
        int orderQty = mainApplication.getOrderForOrderItem(orderProduct.getOrderItemId());
        orderProduct.setQuantity(orderQty);
        holder.tvQuantity.setText(String.format("%d", orderQty));
        holder.tvName.setText(orderProduct.getName());
        if (product.isFree()) {
            if (product.discountedPrice == 0) {
                if (AppUtils.getConfig(activity).isHide_price())
                    holder.tvPrice.setText("");
                else
                    holder.tvPrice.setText(AppUtils.getConfig(activity).getCompany_paid_text());
            } else if(AppUtils.getConfig(activity).isHide_discount()){
                holder.tvPrice.setText("₹ " + product.getDiscountedPrice() + " ");
            }else {
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
            holder.tvCal.setVisibility(View.GONE);
        }

        holder.tvCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNutritionPopup(product, orderProduct);
            }
        });

        String optionText = orderProduct.getMultiLineOrderOptionText();
        if(optionText!=null && optionText.length()>0) {
            holder.tvDescription.setText(optionText);
            holder.tvDescription.setVisibility(View.VISIBLE);
        }else{
            holder.tvDescription.setVisibility(View.GONE);
        }

        if (orderProduct.isVeg()) {
            holder.ivIsVeg.setImageResource(R.drawable.ic_veg_icon);
        } else {
            holder.ivIsVeg.setImageResource(R.drawable.ic_non_veg);
        }


        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutTask.updateTime();

                try {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    map.put(CleverTapEvent.PropertiesNames.getVendor_id(),vendor.getId());
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getAdd_quantity_click(), map, activity);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

                int orderQty = mainApplication.getFreeQuantityAdded(product);

                if(vendor.isVendingMachine() && mainApplication.getOrderQuantityForProduct(product.getId())>=product.getMaxQty()){
                    AppUtils.showToast(String.format("Oops, Cannot add %d items.\nWe only have %d items available", product.getMaxQty() + 1, product.getMaxQty()),false,2);
                    return;
                }

                if (orderProduct.isFree()
                        && product.getFreeQuantity() > 0 && orderQty >= product.getFreeQuantity()) {
                    //cannot more than free quantity
                    if (!cart.getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_OFFICIAL)) {
                        if(vendor.isSlotBookingVendor()){
                            AppUtils.showToast(AppUtils.getConfig(activity).getCongestion_msg(),false,2);
                        }else {
                            AppUtils.showToast(AppUtils.getConfig(activity).getFree_meal_exhaust_msg(), false, 2);
                        }
                        return;
                    }
                }
//                orderProduct.setQuantity(orderProduct.getQuantity()+1);
                //TODO order
                mainApplication.addProductQuantity(orderProduct);
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
                mainApplication.removeProductFromCart(orderProduct);
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

    private void showNutritionPopup(Product product, OrderProduct orderProduct) {
        if (product.nutrition.getNutritionItems().size() > 0) {
            if (activity instanceof AppCompatActivity)
                NutritionFragment.show((AppCompatActivity) activity, product.nutrition, product.getName(), orderProduct.getQuantity());
        }
    }

    @Override
    public int getItemCount() {
        return order.getProducts().size();
    }

    public void changeOrder(Order order, Cart cart) {
        this.order = order;
        this.cart = cart;
    }
}
