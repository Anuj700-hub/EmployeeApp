package com.hungerbox.customer.offline.modelOffline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.fragmentOffline.OptionSelectionDialog;
import com.hungerbox.customer.offline.listenersOffline.VendorValidationListener;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.fragment.FreeCartErrorHandleDialog;
import com.hungerbox.customer.order.fragment.GuestPopUpFragment;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class Cart {
    long occasionId;
    long vendorId;
    long lastUpdatedTime;
    long orderTime;
    VendorOffline vendor;
    ArrayList<OrderProductOffline> orderProducts;
    String guestType = "";

    private boolean isGuestPopupShowing = false;

    public String getGuestType() {
        return guestType;
    }

    public void setGuestType(String guestType) {
        this.guestType = guestType;
    }

    public long getOccasionId() {
        return occasionId;
    }

    public Cart setOccasionId(long occasionId) {
        this.occasionId = occasionId;
        return this;
    }

    public long getVendorId() {
        return vendorId;
    }

    public Cart setVendorId(long vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public Cart setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
        return this;
    }

    public VendorOffline getVendor() {
        return vendor;
    }

    public Cart setVendor(VendorOffline vendor) {
        this.vendor = vendor;
        return this;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public ArrayList<OrderProductOffline> getOrderProducts() {
        if (orderProducts == null)
            orderProducts = new ArrayList<>();
        return orderProducts;
    }

    public void setOrderProducts(ArrayList<OrderProductOffline> orderProducts) {
        this.orderProducts = orderProducts;
    }


    public OrderProductOffline getFirstProductMathcing(OrderProductOffline orderProductToCheck) {
        for (OrderProductOffline orderProduct : getOrderProducts()) {
            if (orderProduct.getId() == orderProductToCheck.getId()) {
                return orderProduct;
            }
        }
        return null;
    }

    public int getQuantityIfProductExistsInCart(long productId) {
        for (OrderProductOffline orderProduct : getOrderProducts()) {
            if (orderProduct.getId() == productId) {
                return orderProduct.getQuantity();
            }
        }
        return 0;
    }

    public OrderProductOffline getFirstNonFreeProductMathcing(OrderProductOffline orderProductToCheck) {
        for (OrderProductOffline orderProduct : getOrderProducts()) {
            if (orderProduct.getId() == orderProductToCheck.getId() && !orderProduct.isFree()) {
                return orderProduct;
            }
        }
        return null;
    }

    public boolean addProductQuantityIfInCart(OrderProductOffline orderProductToCheck) {
        OrderProductOffline orderProduct = getFirstProductMathcing(orderProductToCheck);
        if (orderProduct != null) {
            orderProduct.addQuantity();
            return true;
        } else {
            return false;
        }

    }

    public boolean addProductQuantityIfInCartIfNonFree(OrderProductOffline orderProductToCheck) {
        OrderProductOffline orderProduct = getFirstNonFreeProductMathcing(orderProductToCheck);
        if (orderProduct != null) {
            orderProduct.addQuantity();
            return true;
        } else {
            return false;
        }
    }

    public void addProductToCart(ProductOffline product, RecyclerView.ViewHolder holder,
                                 MainApplicationOffline mainApplication, VendorValidationListener vendorValidationListener,
                                 AppCompatActivity activity, VendorOffline vendor, long occasionId, HashMap<String, Object> map) {
        LogoutTask.updateTime();
        if (MainApplicationOffline.isVendorValid(vendor.getId(),1)) {
            //check for free quantity
            /*
             * If there is free quantity then that item cannot got beyond free quantity
             */
            if (!addedForFree(product, vendor, holder, mainApplication, activity, occasionId, map)) {
                addNonFreeProductToCart(product, holder, mainApplication, activity, occasionId, vendor, map);

            }
        } else {
            vendorValidationListener.validateAndAddProduct(vendor.clone(), product.clone(), false);
        }
    }

    private void addNonFreeNormalProductToCart(ProductOffline product, RecyclerView.ViewHolder holder,
                                               MainApplicationOffline mainApplication, AppCompatActivity activity,
                                               long occasionId, VendorOffline vendor, HashMap<String, Object> map) {
        OrderProductOffline orderProduct = new OrderProductOffline();
        int freeQtyAdded = mainApplication.getFreeQuantityAdded(product);
        if (freeQtyAdded >= product.getFreeQuantity() && mainApplication.getCart(1).getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_PERSONAL)) {
            product.isFree = 0;
            product.free = 0;
            orderProduct.copy(product);
            product.isFree = 1;
            product.free = 1;
        } else
            orderProduct.copy(product);
        mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occasionId);
        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId(),1);
        addCleverTapEvent(map,orderProduct,activity);

        if (holder instanceof com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder) {
            ProductItemViewHolder tempViewHolder = (ProductItemViewHolder) holder;


            updateOrderActionItem(orderQty, tempViewHolder);
            tempViewHolder.tvQuantity.setText(String.format("%d", orderQty));
            updateOrderActionItem(orderQty, tempViewHolder);
        }

        //TODO uncomment this for updating cart on product added to cart

//        if(activity instanceof GlobalActivity)
//        {
//            GlobalActivity globalActivity = (GlobalActivity) activity;
//            globalActivity.setUpCart();
//        }
//        if (activity instanceof AddToCardLisenter) {
//            AddToCardLisenter addToCardLisenter = (AddToCardLisenter) activity;
//            Product productClone = product.clone();
//            productClone.quantity = orderQty;
//            ((AddToCardLisenter) activity).addToCart(vendor.clone(), productClone);
//        }
    }

    private void addCleverTapEvent(HashMap<String, Object> map, OrderProductOffline product, AppCompatActivity activity) {
        try {
            map.put(CleverTapEvent.PropertiesNames.getAmount(), product.getTotalPrice());
            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getAdd_item(), map, activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addNonFreeProductToCart(ProductOffline product, RecyclerView.ViewHolder holder,
                                         MainApplicationOffline mainApplication, AppCompatActivity activity, long occasionId, VendorOffline vendor, HashMap<String, Object> map) {
        if (product.containsSubProducts()) {
            addNonFreeNormalProductToCart(product, holder, mainApplication, activity, occasionId, vendor, map);
        } else {
            showOptionDialog(vendor, product, holder, mainApplication, activity, occasionId, map);
        }
    }

    private boolean addedForFree(ProductOffline product, VendorOffline vendor, RecyclerView.ViewHolder holder,
                                 MainApplicationOffline mainApplication, AppCompatActivity activity, long occasionId, HashMap<String, Object> map) {
        int freeQuantityAdded = mainApplication.getFreeQuantityAdded(product);
        if (product.isFree() &&
                freeQuantityAdded >= product.getFreeQuantity() && !isGuestItem(mainApplication) && product.getDiscountedPrice() >= 0) {
            int emp_id = SharedPrefUtil.getInt(ApplicationConstants.PREF_USER_EMP_TYPE_ID, 0);
            MainApplication mainApplication1 = (MainApplication)activity.getApplication();
            if (mainApplication1.getConfig().is_guest_order() &&
                    mainApplication1.getConfig().getAllowed_for_guest_ordering().contains("" + emp_id)) {
                if (!isGuestPopupShowing)
                    showGuestPopup(product, holder, mainApplication, activity, occasionId, vendor,map);
                else
                    return false;
            } else {
                showCartErrorPopup(product, activity);
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isGuestItem(MainApplicationOffline mainApplication) {
        Cart cart = MainApplicationOffline.getCart(1);
        if (cart.getVendorId() > 0) {
            switch (cart.getGuestType()) {
                case ApplicationConstants.GUEST_TYPE_OFFICIAL:
                case ApplicationConstants.GUEST_TYPE_PERSONAL:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }

    }

    private void showGuestPopup(final ProductOffline product, final RecyclerView.ViewHolder holder,
                                final MainApplicationOffline mainApplication, final AppCompatActivity activity, final long occasionId, final VendorOffline vendor, final HashMap<String, Object> map) {
        GuestPopUpFragment guestPopUpFragment = GuestPopUpFragment.newInstance(new GuestPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction(String guestType) {
                isGuestPopupShowing = false;
//                MainApplication mainApplication = (MainApplication) activity.getApplication();
                Cart cart = mainApplication.getCart(1);
                cart.setGuestType(guestType);

                addNonFreeProductToCart(product, holder, mainApplication, activity, occasionId, vendor,map);
            }

            @Override
            public void onNegativeInteraction() {
                isGuestPopupShowing = false;

            }
        });
        isGuestPopupShowing = true;
        guestPopUpFragment.setCancelable(false);
        guestPopUpFragment.show(activity.getSupportFragmentManager(), "guest_popup");

    }

    private void showOptionDialog(final VendorOffline vendor, final ProductOffline product, final RecyclerView.ViewHolder holder,
                                  final MainApplicationOffline mainApplication, final AppCompatActivity activity, final long occasionId, final HashMap<String, Object> map) {
        //TODO uncomment this for updating cart on product added to cart
        OptionSelectionDialog optionSelectionDialog = OptionSelectionDialog.newInstance(vendor, product, new OptionSelectionDialog.OnOptionSelectionListener() {
            @Override
            public void onFragmentInteraction(OrderProductOffline orderProduct) {
                mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occasionId);


                //TODO have to see menu fragment changes
//                if(activity instanceof GlobalActivity)
//                {
//                    GlobalActivity globalActivity = (GlobalActivity) activity;
//                    globalActivity.setUpCart();
//                }
                int orderQuantity = mainApplication.getOrderQuantityForProduct(product.getId(),1);
                addCleverTapEvent(map, orderProduct, activity);
                if (holder instanceof ProductItemViewHolder) {
                    ProductItemViewHolder tempViewHolder = (ProductItemViewHolder) holder;
                    tempViewHolder.tvQuantity.setText(String.format("%d", orderQuantity));
                    updateOrderActionItem(orderQuantity, tempViewHolder);
                }

            }
        });
        optionSelectionDialog.setCancelable(false);

        optionSelectionDialog.show(activity.getSupportFragmentManager(), "menu_option");
    }

    private void showCartErrorPopup(ProductOffline product, AppCompatActivity activity) {
        FreeCartErrorHandleDialog freeCartErrorHandleDialog = FreeCartErrorHandleDialog.newInstance("Free meal over", null);
        freeCartErrorHandleDialog.show(activity.getSupportFragmentManager(), "cart_error");
    }

    private void updateOrderActionItem(int orderQty, ProductItemViewHolder holder) {
        if (orderQty > 0) {
            holder.rvContiner.setVisibility(View.VISIBLE);
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.ivRemove.setVisibility(View.VISIBLE);
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvAddCart.setVisibility(View.GONE);
            holder.tvQuantity.setText(String.format("%d", orderQty));
        } else {
            holder.rvContiner.setVisibility(View.VISIBLE);
            holder.ivAdd.setVisibility(View.GONE);
            holder.ivRemove.setVisibility(View.GONE);
            holder.tvQuantity.setVisibility(View.GONE);
            holder.tvAddCart.setVisibility(View.VISIBLE);
        }
    }

    public boolean contains(ProductOffline productToCheck) {
        for (OrderProductOffline product : getOrderProducts()) {
            if (product.getId() == productToCheck.getId())
                return true;
        }
        return false;
    }


    public interface OnCartUpdateListener {
        void onCartUpdated();
    }
}
