package com.hungerbox.customer.offline;

import android.content.Context;
import androidx.multidex.MultiDexApplication;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.offline.eventOffline.CartItemAddedEventOffline;
import com.hungerbox.customer.offline.modelOffline.Cart;
import com.hungerbox.customer.offline.modelOffline.OcassionOffline;
import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;
import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.util.ApplicationConstants;
import com.phonepe.android.sdk.api.PhonePe;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;

public class MainApplicationOffline extends MultiDexApplication {
    public static Bus bus = new Bus(ThreadEnforcer.ANY);
    public static long selectedOcassionId;
    public static Context appContext;
    private static Cart cart;
    private static HashMap<Long, OcassionOffline> selectedOcassionMap = new HashMap<>();
    public Config config;
    private static HashMap<Long, HashMap<Long, Integer>> order = new HashMap<>();
    private static HashMap<Long, VendorOffline> vendorHashMap = new HashMap<>();

    public static OcassionOffline getOcassionForId(long ocassionId,int offline) {
        return selectedOcassionMap.get(ocassionId);
    }

    public static void addOcassion(OcassionOffline ocassion) {
        try {
            OcassionOffline clonedOcassion = (OcassionOffline) ocassion.clone();
            selectedOcassionMap.put(clonedOcassion.id, clonedOcassion);
            selectedOcassionId = clonedOcassion.id;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }

    public static boolean isCartCreated() {
        if (cart == null)
            return false;
        else {
            if (cart.getOrderProducts().size() <= 0)
                return false;
            else {
                return true;
            }
        }
    }

    public static void updateCartTime() {
        if (cart != null)
            cart.setLastUpdatedTime(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public void onCreate() {


        ActivityLifecycleCallback.register(this);
        CleverTapAPI cleverTapAPI = CleverTapAPI.getDefaultInstance(getApplicationContext());
        if(!BuildConfig.BUILD_TYPE.equals("release")){
            CleverTapAPI.setDebugLevel(1277182231);
        }
        cleverTapAPI.enableDeviceNetworkInfoReporting(true);

        super.onCreate();

        Realm.init(this);
        PhonePe.init(this);

        appContext = getApplicationContext();

    }

    public static int getOrderQuantityForProduct(long productId,int offli) {
        int quantity = 0;
        if (cart != null)
            for (OrderProductOffline orderProduct : cart.getOrderProducts()) {
                if (orderProduct.getId() == productId)
                    quantity += orderProduct.quantity;
            }
        return quantity;
    }

    public static boolean isVendorValid(long vendorId,int offline) {
        if (cart != null)
            return cart.getVendorId() == vendorId || cart.getVendor() == null;
        else
            return true;
    }


    public static void addProduct(ProductOffline product, VendorOffline vendor, OrderProductOffline orderProductToAdd, long occasionId) {

        if (cart == null) {
            cart = new Cart();
            cart.setLastUpdatedTime(new Date().getTime()).setVendorId(vendor.getId()).setOccasionId(occasionId).setVendor(vendor);
        } else if (cart.getVendorId() == 0) {
            cart.setLastUpdatedTime(new Date().getTime()).setVendorId(vendor.getId()).setOccasionId(occasionId).setVendor(vendor);
        }

        if (vendor.getId() == cart.getVendorId()) {
            if (!product.isConfigurable()) {
                boolean added = false;
                if (!product.isFree()) {
                    added = cart.addProductQuantityIfInCartIfNonFree(orderProductToAdd);
                } else {
                    int freeQtyAdded = getFreeQuantityAdded(product);
                    if (freeQtyAdded >= product.getFreeQuantity() && cart.getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_PERSONAL)) {
                        orderProductToAdd.setIsFree(false);
                        added = cart.addProductQuantityIfInCartIfNonFree(orderProductToAdd);
                    } else {
                        added = cart.addProductQuantityIfInCart(orderProductToAdd);
                        orderProductToAdd.setIsFree(true);
                    }
                }
                if (!added) {
                    orderProductToAdd.setQuantity(1);
                    orderProductToAdd.setProduct(product);
                    cart.getOrderProducts().add(orderProductToAdd);
                }
            } else {
                if (orderProductToAdd.getQuantity() < 1)
                    orderProductToAdd.setQuantity(1);
                if (product.isFree()) {
                    int freeQtyAdded = getFreeQuantityAdded(product);
                    if (freeQtyAdded >= product.getFreeQuantity() && cart.getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_PERSONAL)) {
                        orderProductToAdd.setIsFree(false);
                    }
                }
                orderProductToAdd.setProduct(product);
                cart.getOrderProducts().add(orderProductToAdd);

            }
            bus.post(new CartItemAddedEventOffline(orderProductToAdd));
        }
    }

    public static void removeProductFromCart(OrderProductOffline orderProductToRemove) {
        if (cart != null) {
            for (int i = 0; i < cart.getOrderProducts().size(); i++) {
                OrderProductOffline orderProduct = cart.getOrderProducts().get(i);
                if (orderProduct.getOrderItemId() == orderProductToRemove.getOrderItemId()) {
                    if (orderProduct.getQuantity() > 1)
                        orderProduct.setQuantity(orderProduct.getQuantity() - 1);
                    else {
                        orderProduct.setQuantity(0);
                        cart.getOrderProducts().remove(orderProduct);
                    }
                }
            }
            if (getGuestItemCount(1) == 0) {
                cart.setGuestType("");
            }

            if (cart.getOrderProducts().size() == 0) {
                clearOrder(1);
            }
        } else {
            clearOrder(1);
        }
        refreshCartOnUi();
    }

    public static int getGuestItemCount(int of) {

        int guestItemCount = 0;
        int freeQuantity = 0;
        if (cart == null)
            return 0;
        for (OrderProductOffline product : cart.getOrderProducts()) {
            if (product.getProduct().isFree()) {
                freeQuantity = product.getProduct().getFreeQuantity();
                break;
            }
        }
        if (freeQuantity >= 0) {
            for (OrderProductOffline product : cart.getOrderProducts()) {
                if (product.getProduct().isFree())
                    guestItemCount += product.quantity;
            }
            guestItemCount -= freeQuantity;
        }
        return guestItemCount;
    }

    private static void refreshCartOnUi() {
        bus.post(new RemoveProductFromCart());
    }

    public static boolean removeProductFromCart(ProductOffline product) {


        if (cart == null) {
            refreshCartOnUi();
            return true;
        }


        if (!product.isConfigurable()) {
            for (int i = 0; i < cart.getOrderProducts().size(); i++) {
                OrderProductOffline orderProduct = cart.getOrderProducts().get(i);
                if (orderProduct.getId() == product.getId()) {
                    if (orderProduct.getQuantity() == 1) {
                        cart.getOrderProducts().remove(i);
                        if (cart.getOrderProducts().size() == 0)
                            clearOrder(1);
                    } else {
                        orderProduct.setQuantity(orderProduct.getQuantity() - 1);

                    }
                    break;
                }
            }
        } else {
            ArrayList<OrderProductOffline> cartProducts = new ArrayList<>();
            for (int i = 0; i < cart.getOrderProducts().size(); i++) {
                OrderProductOffline orderProduct = cart.getOrderProducts().get(i);
                if (orderProduct.getId() == product.getId()) {
                    cartProducts.add(orderProduct);
                }
            }

            if (cartProducts.size() == 1) {
                cart.getOrderProducts().remove(cartProducts.get(0));
            } else {
                refreshCartOnUi();
                return false;
            }
            if (cart.getOrderProducts().size() == 0)
                clearOrder(1);
        }

        refreshCartOnUi();
        return true;
    }

    public static int getTotalOrderCount(int offline) {
        int qty = 0;
        if (cart != null) {
            for (OrderProductOffline orderProduct : cart.getOrderProducts()) {
                qty += orderProduct.getQuantity();
            }
        }
        return qty;
    }

    public static double getTotalOrderAmount(int offline) {
        double amount = 0;

        if (cart != null) {
            for (OrderProductOffline orderProduct : cart.getOrderProducts()) {
                amount += orderProduct.getTotalPrice();

            }
        }
        return amount;
    }

    public HashMap<Long, HashMap<Long, Integer>> getOrder() {
        return order;
    }

    public static void clearOrder(int offline) {
        order = new HashMap<>();
        vendorHashMap = new HashMap<>();
        cart = null;
    }

    public Config getConfig() {
        if (config == null || config.getCompany_id() == -1) {
            if (!DbHandler.isStarted())
                DbHandler.start(getApplicationContext());
            config = DbHandler.getDbHandler(getApplicationContext()).getConfig(getApplicationContext());
        }
        return config;
    }

    public void setConfig(Config configToSet) {
        if (!DbHandler.isStarted())
            DbHandler.start(getApplicationContext());
        config = configToSet;
        DbHandler.getDbHandler(getApplicationContext()).setConfig(configToSet);
    }

    public static Cart getCart(int offline) {
        if (cart == null)
            cart = new Cart();
        return cart;
    }

    public static void clearCart(){
        cart = null;
    }

    public static int getFreeQuantityAdded(ProductOffline product) {
        int quantity = 0;
        if (cart != null)
            for (OrderProductOffline orderProduct : cart.getOrderProducts()) {
                if (product.getCategory().equalsIgnoreCase(orderProduct.getProduct().getCategory()) && orderProduct.isFree())
                    quantity += orderProduct.getQuantity();
            }
        return quantity;
    }

    public static void addProductQuantity(OrderProductOffline orderProductToAdd) {
        if (cart != null)
            for (OrderProductOffline orderProduct : cart.getOrderProducts()) {
                if (orderProduct.getOrderItemId() == orderProductToAdd.getOrderItemId()) {
                    orderProduct.setQuantity(orderProduct.getQuantity() + 1);
                    break;
                }
            }
    }

    public static int getOrderForOrderItem(long orderItemId,int offline) {
        int quantity = 0;
        if (cart != null)
            for (OrderProductOffline orderProduct : cart.getOrderProducts()) {
                if (orderProduct.getOrderItemId() == orderItemId)
                    quantity += orderProduct.quantity;
            }
        return quantity;
    }
}
