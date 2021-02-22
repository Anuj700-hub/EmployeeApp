package com.hungerbox.customer;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.facebook.stetho.Stetho;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.activityOffline.HungerBoxOffline;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.phonepe.android.sdk.api.PhonePe;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;

/**
 * Created by peeyush on 21/6/16.
 */
public class MainApplication extends MainApplicationOffline {
    public static Bus bus = new Bus(ThreadEnforcer.ANY);
    public static long selectedOcassionId;
    public static Context appContext;
    public static Cart cart;
    public static HashMap<Long, Ocassion> selectedOcassionMap = new HashMap<>();
    public Config config;
    private HashMap<Long, HashMap<Long, Integer>> order = new HashMap<>();
    private HashMap<Long, Vendor> vendorHashMap = new HashMap<>();
    private static HungerBoxOffline.EmployeeApp employeeApp;

    public static Ocassion getOcassionForId(long ocassionId) {
        return selectedOcassionMap.get(ocassionId);
    }

    public static void addOcassion(Ocassion ocassion) {
        try {
            Ocassion clonedOcassion = (Ocassion) ocassion.clone();
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

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
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
        if (BuildConfig.DEBUG){
            Stetho.initializeWithDefaults(this);
        }


        Realm.init(this);
        PhonePe.init(this);

        appContext = getApplicationContext();
    }

    public int getOrderQuantityForProduct(long productId) {
        int quantity = 0;
        if (cart != null)
            for (OrderProduct orderProduct : cart.getOrderProducts()) {
                if (orderProduct.getId() == productId)
                    quantity += orderProduct.quantity;
            }
        return quantity;
    }

    public boolean isVendorValid(long vendorId) {
        if (cart != null)
            return cart.getVendorId() == vendorId || cart.getVendor() == null;
        else
            return true;
    }


    public void addProduct(Product product, Vendor vendor, OrderProduct orderProductToAdd, long occasionId) {

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
                    if (freeQtyAdded >= product.getFreeQuantity() && (cart.getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_PERSONAL)|| AppUtils.getConfig(appContext).isFree_menu_mapping())) {
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
                    if (freeQtyAdded >= product.getFreeQuantity() && (cart.getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_PERSONAL)|| AppUtils.getConfig(appContext).isFree_menu_mapping())) {
                        orderProductToAdd.setIsFree(false);
                    }
                }
                orderProductToAdd.setProduct(product);
                cart.getOrderProducts().add(orderProductToAdd);

            }
            bus.post(new CartItemAddedEvent(orderProductToAdd));
        }
    }

    public void removeProductFromCart(OrderProduct orderProductToRemove) {
        if (cart != null) {
            for (int i = 0; i < cart.getOrderProducts().size(); i++) {
                OrderProduct orderProduct = cart.getOrderProducts().get(i);
                if (orderProduct.getOrderItemId() == orderProductToRemove.getOrderItemId()) {
                    if (orderProduct.getQuantity() > 1)
                        orderProduct.setQuantity(orderProduct.getQuantity() - 1);
                    else {
                        orderProduct.setQuantity(0);
                        cart.getOrderProducts().remove(orderProduct);
                    }
                }
            }
            if (getGuestItemCount() == 0 && cart.getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_OFFICIAL)) {
                cart.setGuestType("");
            }

            if (cart.getOrderProducts().size() == 0) {
                clearOrder();
            }
        } else {
            clearOrder();
        }
        refreshCartOnUi();
    }

    public int getGuestItemCount() {

        int guestItemCount = 0;
        int freeQuantity = 0;
        if (cart == null)
            return 0;
        for (OrderProduct product : cart.getOrderProducts()) {
            if (product.getProduct().isFree()) {
                freeQuantity = product.getProduct().getFreeQuantity();
                break;
            }
        }
        if (freeQuantity >= 0) {
            for (OrderProduct product : cart.getOrderProducts()) {
                if (product.getProduct().isFree())
                    guestItemCount += product.quantity;
            }
            guestItemCount -= freeQuantity;
        }
        return guestItemCount;
    }

    private void refreshCartOnUi() {
        bus.post(new RemoveProductFromCart());
    }

    public boolean removeProductFromCart(Product product) {


        if (cart == null) {
            refreshCartOnUi();
            return true;
        }


        if (!product.isConfigurable()) {
            for (int i = 0; i < cart.getOrderProducts().size(); i++) {

                //Sorting is done for removing the paid item first
                ArrayList<OrderProduct> cartOrderProducts = cart.getOrderProducts();
                Collections.sort(cartOrderProducts, new Comparator<OrderProduct>() {
                    @Override
                    public int compare(OrderProduct s1, OrderProduct s2) {
                        return (int) s2.getTotalPrice() - (int)s1.getTotalPrice();
                    }
                });

                OrderProduct orderProduct = cartOrderProducts.get(i);
                if (orderProduct.getId() == product.getId()) {
                    if (orderProduct.getQuantity() == 1) {
                        cart.getOrderProducts().remove(i);
                        if (cart.getOrderProducts().size() == 0)
                            clearOrder();
                    } else {
                        orderProduct.setQuantity(orderProduct.getQuantity() - 1);

                    }
                    break;
                }
            }
        } else {
            ArrayList<OrderProduct> cartProducts = new ArrayList<>();
            for (int i = 0; i < cart.getOrderProducts().size(); i++) {
                OrderProduct orderProduct = cart.getOrderProducts().get(i);
                if (orderProduct.getId() == product.getId()) {
                    for(int j=0;j<orderProduct.quantity;j++) {
                        cartProducts.add(orderProduct);
                    }
                }
            }

            if (cartProducts.size() == 1) {
                cart.getOrderProducts().remove(cartProducts.get(0));
            } else {
                refreshCartOnUi();
                return false;
            }
            if (cart.getOrderProducts().size() == 0)
                clearOrder();
        }

        refreshCartOnUi();
        return true;
    }

    public int getTotalOrderCount() {
        int qty = 0;
        if (cart != null) {
            for (OrderProduct orderProduct : cart.getOrderProducts()) {
                qty += orderProduct.getQuantity();
            }
        }
        return qty;
    }

    public double getTotalOrderAmount() {
        double amount = 0;

        if (cart != null) {
            for (OrderProduct orderProduct : cart.getOrderProducts()) {
                amount += orderProduct.getTotalPrice();

            }
        }
        return amount;
    }

    public HashMap<Long, HashMap<Long, Integer>> getOrder() {
        return order;
    }

    public void clearOrder() {
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

    public Cart getCart() {
        if (cart == null)
            cart = new Cart();
        return cart;
    }

    public static void clearCart(){
        cart = null;
    }

    public int getFreeQuantityAdded(Product product) {
        int quantity = 0;
        if (cart != null)
            for (OrderProduct orderProduct : cart.getOrderProducts()) {
                if (product.getCategory().equalsIgnoreCase(orderProduct.getProduct().getCategory()) && orderProduct.isFree())
                    quantity += orderProduct.getQuantity();
            }
        return quantity;
    }

    public void addProductQuantity(OrderProduct orderProductToAdd) {
        if (cart != null)
            for (OrderProduct orderProduct : cart.getOrderProducts()) {
                if (orderProduct.getOrderItemId() == orderProductToAdd.getOrderItemId()) {
                    orderProduct.setQuantity(orderProduct.getQuantity() + 1);
                    break;
                }
            }
    }

    public int getOrderForOrderItem(long orderItemId) {
        int quantity = 0;
        if (cart != null)
            for (OrderProduct orderProduct : cart.getOrderProducts()) {
                if (orderProduct.getOrderItemId() == orderItemId)
                    quantity += orderProduct.quantity;
            }
        return quantity;
    }

    public HungerBoxOffline.EmployeeApp getHungerBoxOffline() {

        try{
            String certificate = SharedPrefUtil.getString(ApplicationConstants.OFFLINE_CERTIFICATE,"");

            String privateKey = SharedPrefUtil.getString(ApplicationConstants.OFFLINE_PRIVATE_KEY,"");

            if(employeeApp == null){
                employeeApp = new HungerBoxOffline.EmployeeApp(
                        UrlConstant.SERVER_PUB_KEY,
                        privateKey,
                        certificate);
            }
            return employeeApp;
        }catch (Exception exp){
            exp.printStackTrace();
            return null;
        }
    }

    public static void setHungerBoxOfflineNull(){
        MainApplication.employeeApp = null;
    }
}
