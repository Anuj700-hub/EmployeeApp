package com.hungerbox.customer.model;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.navmenu.activity.HistoryActivity;
import com.hungerbox.customer.order.activity.GlobalSearchActivity;
import com.hungerbox.customer.order.adapter.SearchDishesAdapter;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.fragment.FreeCartErrorHandleDialog;
import com.hungerbox.customer.order.fragment.GuestPopUpFragment;
import com.hungerbox.customer.order.fragment.OptionSelectionDialog;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by peeyush on 17/8/16.
 */
public class Cart {


    long occasionId;
    long vendorId;
    long lastUpdatedTime;
    long orderTime;
    Vendor vendor;
    ArrayList<OrderProduct> orderProducts;
    String guestType = "";
    boolean freeMenuMapping = false;
    public boolean showedCartErrorPopup = false;
    PaymentMethod paymentMethod;
    ArrayList<PaymentMethod> paymentMethods;
    OrderPayment paymentMode = new OrderPayment();
    private boolean isGuestPopupShowing = false;
    private ArrayList<BookingGuest> spaceGuests;

    public OrderPayment getPaymentMode() {
        if (paymentMethod != null) {
            paymentMode.setWalletId(paymentMethod.getId());

        }
        return paymentMode;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getGuestType() {
        return guestType;
    }

    public void setGuestType(String guestType) {
        this.guestType = guestType;
    }

    public boolean isFreeMenuMapping() {
        return freeMenuMapping;
    }

    public void setFreeMenuMapping(boolean freeMenuMapping) {
        this.freeMenuMapping = freeMenuMapping;
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

    public Vendor getVendor() {
        return vendor;
    }

    public Cart setVendor(Vendor vendor) {
        this.vendor = vendor;
        return this;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public ArrayList<OrderProduct> getOrderProducts() {
        if (orderProducts == null)
            orderProducts = new ArrayList<>();
        return orderProducts;
    }

    public void setOrderProducts(ArrayList<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }


    public OrderProduct getFirstProductMathcing(OrderProduct orderProductToCheck) {

        //Sorting is done for getting the free item first

//        ArrayList<OrderProduct> cartOrderProducts = getOrderProducts();
//        Collections.sort(cartOrderProducts, new Comparator<OrderProduct>() {
//            @Override
//            public int compare(OrderProduct s1, OrderProduct s2) {
//                return  (int)s1.getTotalPrice() - (int) s2.getTotalPrice();
//            }
//        });


        for (OrderProduct orderProduct : getOrderProducts()) {
            if (orderProduct.getId() == orderProductToCheck.getId() && orderProduct.isFree()) {
                return orderProduct;
            }
        }
        return null;
    }

    public int getQuantityIfProductExistsInCart(long productId) {
        for (OrderProduct orderProduct : getOrderProducts()) {
            if (orderProduct.getId() == productId) {
                return orderProduct.getQuantity();
            }
        }
        return 0;
    }

    public OrderProduct getFirstNonFreeProductMathcing(OrderProduct orderProductToCheck) {
        for (OrderProduct orderProduct : getOrderProducts()) {
            if (orderProduct.getId() == orderProductToCheck.getId() && !orderProduct.isFree()) {
                return orderProduct;
            }
        }
        return null;
    }

    public boolean addProductQuantityIfInCart(OrderProduct orderProductToCheck) {
        OrderProduct orderProduct = getFirstProductMathcing(orderProductToCheck);
        if (orderProduct != null) {
            orderProduct.addQuantity();
            return true;
        } else {
            return false;
        }

    }

    public boolean addProductQuantityIfInCartIfNonFree(OrderProduct orderProductToCheck) {
        OrderProduct orderProduct = getFirstNonFreeProductMathcing(orderProductToCheck);
        if (orderProduct != null) {
            orderProduct.addQuantity();
            return true;
        } else {
            return false;
        }
    }

    public void addProductToCart(Product product, RecyclerView.ViewHolder holder,
                                 MainApplication mainApplication, VendorValidationListener vendorValidationListener,
                                 AppCompatActivity activity, Vendor vendor, long occasionId , HashMap<String,Object> map ) {
        LogoutTask.updateTime();
        if (mainApplication.isVendorValid(vendor.getId())) {
            //check for free quantity
            /*
             * If there is free quantity then that item cannot got beyond free quantity
             */
            if (!addedForFree(product, vendor, holder, mainApplication, activity, occasionId,map)) {
                addNonFreeProductToCart(product, holder, mainApplication, activity, occasionId, vendor,map);

            }
        } else {
            vendorValidationListener.validateAndAddProduct(vendor.clone(), product.clone(), false);
        }
    }

    private void addNonFreeNormalProductToCart(Product product, RecyclerView.ViewHolder holder,
                                               MainApplication mainApplication, AppCompatActivity activity,
                                               long occasionId, Vendor vendor , HashMap<String,Object> map ) {
        OrderProduct orderProduct = new OrderProduct();
        int freeQtyAdded = mainApplication.getFreeQuantityAdded(product);
        if (freeQtyAdded >= product.getFreeQuantity() &&
                (mainApplication.getCart().getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_PERSONAL) || AppUtils.getConfig(activity).isFree_menu_mapping())) {
            product.isFree = 0;
            product.free = 0;
            orderProduct.copy(product);
//            product.isFree = 1;
//            product.free = 1;
        } else
            orderProduct.copy(product);
        mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occasionId);
        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
        addCleverTapEvent(map,orderProduct,activity);

        if (holder instanceof com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder) {
            ProductItemViewHolder tempViewHolder = (ProductItemViewHolder) holder;


            updateOrderActionItem(orderQty, tempViewHolder);
            tempViewHolder.tvQuantity.setText(String.format("%d", orderQty));
            updateOrderActionItem(orderQty, tempViewHolder);
        }
        else if (holder instanceof SearchDishesAdapter.SearchDishesViewHolder) {
            SearchDishesAdapter.SearchDishesViewHolder tempViewHolder = (SearchDishesAdapter.SearchDishesViewHolder) holder;
            tempViewHolder.tvQuantity.setText(String.format("%d", orderQty));
            updateOrderActionItem(orderQty, tempViewHolder);
            if (activity instanceof GlobalSearchActivity)
                ((GlobalSearchActivity) activity).setUpCart();
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

    private void addCleverTapEvent( HashMap<String,Object> map,OrderProduct product,AppCompatActivity activity){
        try {
            map.put(CleverTapEvent.PropertiesNames.getAmount(),product.getTotalPrice());
            boolean haveAddon = false;
            String subProductsList = "";
            for(int i = 0; i < product.getSubProducts().size(); i++){
                ArrayList<OrderOptionItem> SubItemList = product.getSubProducts().get(i).getOrderOptionItems();
                for(int j = 0; j < SubItemList.size(); j++){
                    if(!haveAddon){
                        haveAddon = true;
                        subProductsList = SubItemList.get(j).getName();
                    }
                    else{
                        subProductsList += ", " + SubItemList.get(j).getName();
                    }

                }
            }
            if(haveAddon){
                map.put(CleverTapEvent.PropertiesNames.getSub_items(), subProductsList);
            }
            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getAdd_item(),map,activity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void addNonFreeProductToCart(Product product, RecyclerView.ViewHolder holder,
                                         MainApplication mainApplication, AppCompatActivity activity, long occasionId, Vendor vendor,HashMap<String,Object> map ) {
        if (product.containsSubProducts()) {
            addNonFreeNormalProductToCart(product, holder, mainApplication, activity, occasionId, vendor,map);
        } else {
            if (activity instanceof HistoryActivity){
                addNonFreeNormalProductToCart(product, holder, mainApplication, activity, occasionId, vendor,map);
            }
            else {
                showOptionDialog(vendor, product, holder, mainApplication, activity, occasionId, map);
            }
        }
    }

    private boolean addedForFree(Product product, Vendor vendor, RecyclerView.ViewHolder holder,
                                 MainApplication mainApplication, AppCompatActivity activity, long occasionId,HashMap<String,Object> map ) {
        int freeQuantityAdded = mainApplication.getFreeQuantityAdded(product);

        if((vendor.isVendingMachine() || vendor.isSpaceBookingVendor())&& mainApplication.getOrderQuantityForProduct(product.getId())>=product.getMaxQty()){
               showCartErrorPopup(product,activity,vendor);
               return true;
        }

        if(AppUtils.getConfig(activity).isFree_menu_mapping()){
            if(product.isFree() && freeQuantityAdded>=product.getFreeQuantity()){
                Cart cart = mainApplication.getCart();
                cart.setGuestType(guestType);
                return false;
            }else{
                addNonFreeProductToCart(product, holder, mainApplication, activity, occasionId, vendor,map);
                return true;
            }
        }else {
            if (product.isFree() &&
                    freeQuantityAdded >= product.getFreeQuantity() && !isGuestItem(mainApplication) && product.getDiscountedPrice() >= 0) {
                int emp_id = SharedPrefUtil.getInt(ApplicationConstants.PREF_USER_EMP_TYPE_ID, 0);
                if (mainApplication.getConfig().is_guest_order() &&
                        mainApplication.getConfig().getAllowed_for_guest_ordering().contains("" + emp_id)) {
                    if (!isGuestPopupShowing)
                        showGuestPopup(product, holder, mainApplication, activity, occasionId, vendor, map);
                    else
                        return false;
                } else {
                    showCartErrorPopup(product,activity,vendor);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean isGuestItem(MainApplication mainApplication) {
        Cart cart = mainApplication.getCart();
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

    private void showGuestPopup(final Product product, final RecyclerView.ViewHolder holder,
                                final MainApplication mainApplication, final AppCompatActivity activity, final long occasionId, final Vendor vendor, final HashMap<String,Object> map ) {
        GuestPopUpFragment guestPopUpFragment = GuestPopUpFragment.newInstance(new GuestPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction(String guestType) {
                isGuestPopupShowing = false;
//                MainApplication mainApplication = (MainApplication) activity.getApplication();
                Cart cart = mainApplication.getCart();
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

    private void showOptionDialog(final Vendor vendor, final Product product, final RecyclerView.ViewHolder holder,
                                  final MainApplication mainApplication, final AppCompatActivity activity, final long occasionId, final HashMap<String,Object> map ) {
        //TODO uncomment this for updating cart on product added to cart

        if (product.isFree()) {
            int freeQtyAdded = mainApplication.getFreeQuantityAdded(product);
            if (freeQtyAdded >= product.getFreeQuantity() && (getGuestType().equalsIgnoreCase(ApplicationConstants.GUEST_TYPE_PERSONAL)|| AppUtils.getConfig(activity).isFree_menu_mapping())) {
                product.isFree = 0;
            }
        }

        OptionSelectionDialog optionSelectionDialog = OptionSelectionDialog.newInstance(vendor, product, new OptionSelectionDialog.OnOptionSelectionListener() {
            @Override
            public void onFragmentInteraction(OrderProduct orderProduct) {
                mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occasionId);



                if(activity instanceof GlobalSearchActivity)
                {
                    GlobalSearchActivity globalActivity = (GlobalSearchActivity) activity;
                    globalActivity.setUpCart();
                }
                int orderQuantity = mainApplication.getOrderQuantityForProduct(product.getId());
                addCleverTapEvent(map,orderProduct,activity);
                if (holder instanceof ProductItemViewHolder) {
                    ProductItemViewHolder tempViewHolder = (ProductItemViewHolder) holder;
                    tempViewHolder.tvQuantity.setText(String.format("%d", orderQuantity));
                    updateOrderActionItem(orderQuantity, tempViewHolder);
                }
                if (holder instanceof SearchDishesAdapter.SearchDishesViewHolder) {
                    SearchDishesAdapter.SearchDishesViewHolder tempViewHolder = (SearchDishesAdapter.SearchDishesViewHolder) holder;
                    tempViewHolder.tvQuantity.setText(String.format("%d", orderQuantity));
                    updateOrderActionItem(orderQuantity, tempViewHolder);
                    if (activity instanceof GlobalSearchActivity)
                        ((GlobalSearchActivity) activity).setUpCart();
                }

            }
        });
        optionSelectionDialog.setCancelable(false);

        optionSelectionDialog.show(activity.getSupportFragmentManager(), "menu_option");
    }

    private void showCartErrorPopup(Product product,AppCompatActivity activity,Vendor vendor) {
        FreeCartErrorHandleDialog freeCartErrorHandleDialog = FreeCartErrorHandleDialog.newInstance(product,vendor);
        freeCartErrorHandleDialog.show(activity.getSupportFragmentManager(), "cart_error");
        showedCartErrorPopup = true;
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
    private void updateOrderActionItem(int orderQty, SearchDishesAdapter.SearchDishesViewHolder holder) {
        try {
            if (orderQty > 0) {
                holder.rvContainer.setVisibility(View.VISIBLE);
                holder.ivAdd.setVisibility(View.VISIBLE);
                holder.ivRemove.setVisibility(View.VISIBLE);
                holder.tvQuantity.setVisibility(View.VISIBLE);
                holder.tvAddCart.setVisibility(View.GONE);
                holder.tvQuantity.setText(String.format("%d", orderQty));
            } else {
                holder.rvContainer.setVisibility(View.VISIBLE);
                holder.ivAdd.setVisibility(View.GONE);
                holder.ivRemove.setVisibility(View.GONE);
                holder.tvQuantity.setVisibility(View.GONE);
                holder.tvAddCart.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean contains(Product productToCheck) {
        for (OrderProduct product : getOrderProducts()) {
            if (product.getId() == productToCheck.getId())
                return true;
        }
        return false;
    }

    public PaymentMethod containsAndGetInternalPaymentMethod() {
        PaymentMethod internalPaymentMethod = null;
        for (PaymentMethod paymentMethod : getPaymentMethods()) {
            if (paymentMethod.isInternal() && paymentMethod.isSelected()) {
                internalPaymentMethod = paymentMethod;
            }
        }
        return internalPaymentMethod;
    }

    public ArrayList<PaymentMethod> getPaymentMethods() {
        if (paymentMethods == null)
            paymentMethods = new ArrayList<>();
        return paymentMethods;
    }

    public void setPaymentMethods(ArrayList<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public PaymentMethod getExternalPaymentMethod() {
        PaymentMethod paymentMethodToReturn = null;
        for (PaymentMethod paymentMethod : getPaymentMethods()) {
            if (!paymentMethod.isInternal())
                paymentMethodToReturn = paymentMethod;
        }

        return paymentMethodToReturn;
    }


    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public ArrayList<BookingGuest> getSpaceGuests() {
        if(spaceGuests == null)
            return new ArrayList<>();
        return spaceGuests;
    }

    public void setSpaceGuests(ArrayList<BookingGuest> spaceGuests) {
        this.spaceGuests = spaceGuests;
    }
}
