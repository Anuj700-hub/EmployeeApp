package com.hungerbox.customer.order.adapter;

import android.app.AlertDialog;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.BookmarkAddRemoveEvent;
import com.hungerbox.customer.model.BookMark;
import com.hungerbox.customer.model.BookMarkMenu;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.UserBookmarks;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.AddToCardLisenter;
import com.hungerbox.customer.order.activity.MenuActivity;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.fragment.GeneralDialogFragment;
import com.hungerbox.customer.order.fragment.GuestPopUpFragment;
import com.hungerbox.customer.order.fragment.NutritionFragment;
import com.hungerbox.customer.order.fragment.OptionSelectionDialog;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by peeyush on 24/6/16.
 */
public class MenuListAdapter extends RecyclerView.Adapter<ProductItemViewHolder> {

    boolean  fromBookmark = false ,isPreOrder = false,fromTrayList = false;
    AppCompatActivity activity;
    List<Product> products;
    LayoutInflater inflater;
    MainApplication mainApplication;
    VendorValidationListener vendorValidationListener;
    Vendor vendor;
    long occasionId;
    Config config;
    Realm realm;
    private AlertDialog progressDialog;
    private long locationId;
    List<Vendor> vendors;
    int expressCheckoutAction = 0;
    RelativeLayout empty;




    public MenuListAdapter(AppCompatActivity activity, List<Product> products, Vendor vendor, VendorValidationListener vendorValidationListener, long occasionId,boolean fromTrayList) {
        this.activity = activity;
        this.products = products;
        this.vendor = vendor;
        this.occasionId = occasionId;
        this.vendorValidationListener = vendorValidationListener;
        mainApplication = (MainApplication) activity.getApplication();
        inflater = LayoutInflater.from(activity);
        config = AppUtils.getConfig(activity);
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        this.fromTrayList = fromTrayList;
        getLocationId();
    }

    public MenuListAdapter(AppCompatActivity activity, List<Product> products, List<Vendor> vendors,
                           long occasionId, boolean fromBookmark, int expressCheckoutAction, RelativeLayout rlNoList) {
        this.activity = activity;
        this.products = products;
        this.vendors = vendors;
        this.occasionId = occasionId;
        this.fromBookmark = fromBookmark;
        this.expressCheckoutAction = expressCheckoutAction;
        mainApplication = (MainApplication) activity.getApplication();
        inflater = LayoutInflater.from(activity);
        this.empty = rlNoList;
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        getLocationId();

    }



    @Override
    public ProductItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (fromTrayList) {
            ProductItemViewHolder holder = new ProductItemViewHolder(inflater.inflate(R.layout.product_list_menuitem_white, parent, false));
            return holder;
        } else {
            ProductItemViewHolder holder = new ProductItemViewHolder(inflater.inflate(R.layout.product_list_menuitem, parent, false));
            return holder;
        }
    }


    @Override
    public void onBindViewHolder(final ProductItemViewHolder holder, final int position) {
        if(!fromTrayList) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cv_menu_item.getLayoutParams();
            layoutParams.setMargins(15, 0, 15, 20);
            holder.cv_menu_item.setRadius(20);
        }

        final Product product = products.get(position);
        holder.tvName.setText(product.getName());

        //BookMarkMenu bookMarkMenu = realm.where(BookMarkMenu.class).equalTo(BookMarkMenu.ID, product.getId()).findFirst();
        //Product expProduct = realm.where(Product.class).equalTo(Product.ID,product.getId()).findFirst();

        if (product.isBookmarked()) {
            holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_red);
            holder.ivBookmark.setTag("red");
        } else {
            holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_grey);
            holder.ivBookmark.setTag("grey");
        }

        holder.ivBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromBookmark){
                    showRemoveBookmarkDialog(product, position);
                }else {
                    if (String.valueOf(holder.ivBookmark.getTag()).equals("grey")) {
                        addBookmark(product);
                    } else {
                        deleteBookmark(product, position);
                    }
                }
            }
        });

        if (product.isFree()) {
            if (product.discountedPrice == 0) {
                if (AppUtils.getConfig(activity).isHide_price())
                    holder.tvPrice.setText("");
                else
                    holder.tvPrice.setText(AppUtils.getConfig(activity).getCompany_paid_text());
                if (product.isFree() && AppUtils.getConfig(activity).is_guest_order()) {
                    holder.tvPrice.setText(activity.getString(R.string.guest_ordering_text));
                }
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

        if(AppUtils.getConfig(activity).isFree_menu_mapping()){
            if(product.isFree()) {
                int freeQtyAdded = mainApplication.getFreeQuantityAdded(product);
                if(freeQtyAdded>=product.getFreeQuantity()) {
                    holder.tvPrice.setText(String.format("\u20B9 %.2f", product.getPrice()));
                }else if(product.discountedPrice==0){
                    holder.tvPrice.setText(AppUtils.getConfig(activity).getCompany_paid_text());
                }else if(AppUtils.getConfig(activity).isHide_discount()){
                    holder.tvPrice.setText("₹ " + product.getDiscountedPrice() + " ");
                }else{
                    String oldPrice = "₹ " + product.getPrice() + " ";
                    String newPrice = "₹ " + product.getDiscountedPrice() + " ";
                    holder.tvPrice.setText(newPrice + oldPrice, TextView.BufferType.SPANNABLE);
                    Spannable spannable = (Spannable) holder.tvPrice.getText();
                    spannable.setSpan(new StrikethroughSpan(), newPrice.length(), (oldPrice.length() + newPrice.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        double totalCalories = product.getTotalCalories();

        if (totalCalories > 0 && config.isHealthEnabled()) {
            String caloriesStr = String.format("%.2f cal", totalCalories);
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


        final String desc = product.getDesc().trim();
        if (AppUtils.getConfig(activity).isDesc_arrow_visible()) {
            if (holder.ivDescriptionArrow != null) {
                if (desc != null && !desc.isEmpty()) {
                    int index = desc.indexOf("\n", 0);
                    if (index != -1) {
                        holder.ivDescriptionArrow.setVisibility(View.VISIBLE);


                        final String descFirstLine = desc.substring(0, index);
                        holder.tvDescription.setText(Html.fromHtml(descFirstLine));

                        if (!product.isDescShowing) {
                            holder.tvDescription.setText(Html.fromHtml(descFirstLine));
                            holder.ivDescriptionArrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down));

                        } else {
                            holder.tvDescription.setText(Html.fromHtml(desc));
                            holder.ivDescriptionArrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_action_arrow_up));
                        }

                        holder.ivDescriptionArrow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        if (product.isDescShowing) {
                                            holder.tvDescription.setText(Html.fromHtml(descFirstLine));
                                            product.setDescShowing(!product.isDescShowing());
                                            holder.ivDescriptionArrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down));

                                        } else {
                                            holder.tvDescription.setText(Html.fromHtml(desc));
                                            product.setDescShowing(!product.isDescShowing());
                                            holder.ivDescriptionArrow.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_action_arrow_up));
                                        }
                                    }
                                });

                            }
                        });
                    } else {
                        holder.ivDescriptionArrow.setVisibility(View.INVISIBLE);
                        holder.tvDescription.setText(Html.fromHtml(desc));
                    }

                } else {
                    holder.tvDescription.setVisibility(View.GONE);
                    holder.ivDescriptionArrow.setVisibility(View.GONE);
                }
            } else {
                holder.tvDescription.setText(Html.fromHtml(desc));
            }
        }else{
            holder.ivDescriptionArrow.setVisibility(View.INVISIBLE);
            holder.tvDescription.setSingleLine(true);
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(Html.fromHtml(desc));
        }


        if (fromBookmark) {
            holder.tvVendorName.setText(product.getVendorName());
            holder.tvDescription.setVisibility(View.GONE);
            holder.ivDescriptionArrow.setVisibility(View.GONE);
            holder.tvVendorName.setVisibility(View.VISIBLE);
        }


        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());

        if (product.isProductVeg()) {
            holder.ivIsVeg.setImageResource(R.drawable.ic_veg_icon);
        } else {
            holder.ivIsVeg.setImageResource(R.drawable.ic_non_veg);
        }

        updateOrderActionItem(orderQty, holder);

        if(fromBookmark){
            vendor = vendors.get(position);
        }

        View.OnClickListener addButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    if (product.isRecommended()) {
                        EventUtil.FbEventLog(activity, EventUtil.MENU_RECOMMENDED_CLICK, EventUtil.SCREEN_MENU);
                        HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_RECOMMENDED_CLICK);
                    } else {
                        EventUtil.FbEventLog(activity, EventUtil.MENU_ADD_ITEM, EventUtil.SCREEN_MENU);
                    }
                }catch (Exception exp){
                    exp.printStackTrace();
                }


                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_ITEM_ADD, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

                if (fromBookmark) {
                    vendor = vendors.get(position);

                    if(mainApplication.getCart().getOrderProducts().size()>0)
                        showRemoveCartItemDialog(product.clone(), vendor, holder);
                    else
                        addProductToCart(product.clone(), vendor, holder);

                }else {
                    addProductToCart(product.clone(), vendor, holder);
                }

            }
        };


        holder.tvAddCart.setOnClickListener(addButtonListener);

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUtil.FbEventLog(activity, EventUtil.MENU_ITEM_INCR, EventUtil.SCREEN_MENU);

                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_ITEM_INCR, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                EventUtil.logBaseEvent(activity, EventUtil.CART_ADDITION);
                addProductToCart(product.clone(), vendor, holder);
            }
        });

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutTask.updateTime();
                EventUtil.FbEventLog(activity, EventUtil.MENU_ITEM_DECR, EventUtil.SCREEN_MENU);
                EventUtil.logBaseEvent(activity, EventUtil.CART_REMOVAL);

                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_ITEM_DECR, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

                mainApplication.removeProductFromCart(product.clone());
                int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
                holder.tvQuantity.setText(String.format("%d", orderQty));
                updateOrderActionItem(orderQty, holder);
                if (activity instanceof MenuActivity)
                    ((MenuActivity) activity).setUpCart();
                if (activity instanceof AddToCardLisenter) {
                    AddToCardLisenter addToCardLisenter = (AddToCardLisenter) activity;
                    Product productClone = product.clone();
                    productClone.quantity = orderQty;
                    ((AddToCardLisenter) activity).removeFromCart(vendor.clone(), productClone);
                }
            }
        });

            holder.rvContiner.setVisibility(View.VISIBLE);
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.ivRemove.setVisibility(View.VISIBLE);
            holder.tvQuantity.setVisibility(View.VISIBLE);

            updateOrderActionItem(orderQty, holder);

        if (product.getOptionResponse().getSubProducts().size() > 0) {
            holder.tvAddCart.setText("   ADD +   ");
            holder.tvCustomize.setVisibility(View.VISIBLE);
        } else {
            holder.tvAddCart.setText("    ADD    ");
            holder.tvCustomize.setVisibility(View.GONE);
        }

        if (AppUtils.getConfig(activity).isPlace_order()) {
            holder.llContainer.setVisibility(View.VISIBLE);
        }else{
            if(vendor.isPlaceOrderEnabled())
                holder.llContainer.setVisibility(View.VISIBLE);
            else
                holder.llContainer.setVisibility(View.INVISIBLE);
        }

        if(!AppUtils.getConfig(activity).isExpress_checkout() ||
                expressCheckoutAction == ApplicationConstants.BOOKMARK_FROM_MAINACTIVITY
                 || isPreOrder || SharedPrefUtil.getBoolean(ApplicationConstants.IS_GUEST_USER)) {
            holder.ivBookmark.setVisibility(View.GONE);
        }
        else { holder.ivBookmark.setVisibility(View.VISIBLE); }


        if(fromBookmark && !product.isOrderingAllowed()){
            setDisableMenu(holder);
        }else{
            setEnabledMenu(holder);
        }

        if (fromBookmark){
            holder.llContainer.setVisibility(View.INVISIBLE);
            //holder.itemParentRl.setOnClickListener(addButtonListener);
            holder.itemParentRl.setPadding(10,15,10,15);

            if (AppUtils.getConfig(activity).isPlace_order()) {
                holder.itemParentRl.setOnClickListener(addButtonListener);
            }else{
                if(vendor.isPlaceOrderEnabled())
                    holder.itemParentRl.setOnClickListener(addButtonListener);
                else
                    holder.itemParentRl.setOnClickListener(null);
            }


        }

        if(fromTrayList){
            holder.tvDescription.setVisibility(View.GONE);
            holder.ivDescriptionArrow.setVisibility(View.GONE);
            holder.tvCal.setVisibility(View.GONE);
            holder.ivBookmark.setVisibility(View.GONE);
            holder.itemParentRl.setPadding(0,0,0,0);
        }

        if(vendor.isVendingMachine()){
            updateStockText(holder,product);
        }else{
            holder.tvStockLeft.setVisibility(View.GONE);
        }

        LogoutTask.updateTime();
    }


    private void showNutritionPopup(Product product) {
        if (product.nutrition.getNutritionItems().size() > 0)
            NutritionFragment.show(activity, product.nutrition, product.getName(), 1);
    }


    private void updateOrderActionItem(int orderQty, ProductItemViewHolder holder) {
        if (orderQty > 0 && !fromBookmark) {
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

//        if (orderQty <= 0) {
//            holder.rvContiner.setBackgroundResource(R.drawable.menu_add_back);
//        } else {
//            holder.rvContiner.setBackgroundResource(R.drawable.menu_add_selected_back);
//        }
    }

    private void addProductToCart(Product product, Vendor vendor, ProductItemViewHolder holder) {

        HashMap<String,Object> map =new HashMap<>() ;

        try{

            map.put(CleverTapEvent.PropertiesNames.getSource(),fromBookmark?ApplicationConstants.ADD_ITEM_SOURCE_EXP:ApplicationConstants.ADD_ITEM_SOURCE_NORMAL);
            map.put(CleverTapEvent.PropertiesNames.is_bookmarked(), product.isBookmarked()?"Yes":"No");
            map.put(CleverTapEvent.PropertiesNames.is_trending(),product.isTrendingItem()?"Yes":"No");
            map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
            map.put(CleverTapEvent.PropertiesNames.getVendor_id(),vendor.getId());
            map.put(CleverTapEvent.PropertiesNames.getMenu_item_id(),product.getId());

        }catch (Exception e){
            e.printStackTrace();
        }



        mainApplication.getCart().addProductToCart(product, holder, mainApplication, vendorValidationListener, activity, vendor, occasionId,map);
        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
        updateOrderActionItem(orderQty, holder);
    }

    private void addNonFreeNormalProductToCart(Product product, Vendor vendor, ProductItemViewHolder holder) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.copy(product);
        mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occasionId);
        int orderQty = mainApplication.getOrderQuantityForProduct( product.getId());
        updateOrderActionItem(orderQty, holder);
        holder.tvQuantity.setText(String.format("%d", orderQty));
        updateOrderActionItem(orderQty, holder);
        notifyDataSetChanged();
        if (activity instanceof MenuActivity)
            ((MenuActivity) activity).setUpCart();
        if (activity instanceof AddToCardLisenter) {
            AddToCardLisenter addToCardLisenter = (AddToCardLisenter) activity;
            Product productClone = product.clone();
            productClone.quantity = orderQty;
            ((AddToCardLisenter) activity).addToCart(vendor.clone(), productClone);
        }
    }


    private void addNonFreeProductToCart(Product product, Vendor vendor, ProductItemViewHolder holder) {
        if (product.containsSubProducts()) {
            addNonFreeNormalProductToCart(product, vendor, holder);
        } else {
            showOptionDialog(vendor, product, holder);
        }
    }

    private boolean isGuestItem() {
        MainApplication mainApplication = (MainApplication) activity.getApplication();
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

    private void showGuestPopup(final Product product, final Vendor vendor, final ProductItemViewHolder holder) {
        GuestPopUpFragment guestPopUpFragment = GuestPopUpFragment.newInstance(new GuestPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction(String guestType) {
                MainApplication mainApplication = (MainApplication) activity.getApplication();
                Cart cart = mainApplication.getCart();
                cart.setGuestType(guestType);
                addNonFreeProductToCart(product, vendor, holder);
            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        guestPopUpFragment.show(activity.getSupportFragmentManager(), "guest_popup");

    }

    private void showOptionDialog(final Vendor vendor, final Product product, final ProductItemViewHolder holder) {
        OptionSelectionDialog optionSelectionDialog = OptionSelectionDialog.newInstance(vendor, product, new OptionSelectionDialog.OnOptionSelectionListener() {
            @Override
            public void onFragmentInteraction(OrderProduct orderProduct) {
                mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occasionId);
                if (activity instanceof MenuActivity)
                    ((MenuActivity) activity).setUpCart();
                int orderQuantity = mainApplication.getOrderQuantityForProduct(product.getId());
                holder.tvQuantity.setText(String.format("%d", orderQuantity));
                updateOrderActionItem(orderQuantity, holder);
            }
        });
        optionSelectionDialog.setCancelable(false);

        optionSelectionDialog.show(activity.getSupportFragmentManager(), "menu_option");
    }

    private void addBookmark(final Product product) {

        String url = UrlConstant.BOOKMARK_ADD;
        BookMark bookMark = new BookMark();
        bookMark.setMenuId(product.getId());
        bookMark.setOccasionId(occasionId);
        bookMark.setLocationId(locationId);
        showProgressDialog("Adding Bookmark");
        SimpleHttpAgent<UserBookmarks> bookMarkMenuSimpleHttpAgent = new SimpleHttpAgent<UserBookmarks>(activity, url,
                new ResponseListener<UserBookmarks>() {
            @Override
            public void response(final UserBookmarks responseObject) {
                if(activity!=null) {
                    dismissDialog();

                    try{

                        HashMap<String,Object> map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getMenu_item_id(),product.getId());
                        map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getBookmark_add(),map,activity);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Product expProduct = realm.where(Product.class).equalTo(Product.ID, product.getId()).findFirst();
                            if (expProduct != null) {
                                expProduct.setBookmarked(true);
                                realm.insertOrUpdate(expProduct);
                            }
                        }
                    });

                    AppUtils.showToast("Bookmark added", true, 1);
                    MainApplication.bus.post(new BookmarkAddRemoveEvent());
                    notifyDataSetChanged();
                }

            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                if(activity==null)
                    return;

                dismissDialog();
                if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                    AppUtils.showToast("Please check your network.", true, 0);
                } else {
                    if (error != null && !error.equals("")) {
                        AppUtils.showToast(error, true, 0);
                    } else {
                        AppUtils.showToast("Bookmark not added !", true, 0);
                    }
                }
            }
        }, UserBookmarks.class);
        bookMarkMenuSimpleHttpAgent.post(bookMark, new HashMap<String, JsonSerializer>());
    }


    private void deleteBookmark(final Product product, final int position) {
        String url = UrlConstant.BOOKMARK_DELETE + "?menuId=" + product.getId() + "&locationId=" + locationId + "&occasionId=" + occasionId;

        showProgressDialog("Deleting Bookmark");
        SimpleHttpAgent<BookMarkMenu> bookMarkMenuSimpleHttpAgent = new SimpleHttpAgent<BookMarkMenu>(activity, url, new ResponseListener<BookMarkMenu>() {
            @Override
            public void response(BookMarkMenu responseObject) {
                if(activity!=null) {

                    dismissDialog();
//                    try{
//                        HashMap<String,Object> map = new HashMap<>();
//                        map.put(CleverTapEvent.PropertiesNames.getItem_name(),product.getName());
//                        map.put(CleverTapEvent.PropertiesNames.getScreen_name(),fromBookmark?"Nav":"Menu");
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getBookmark_remove(),map,activity);
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }


                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Product expProduct = realm.where(Product.class).equalTo(Product.ID, product.getId()).findFirst();
                            if (expProduct != null) {
                                expProduct.setBookmarked(false);
                                realm.insertOrUpdate(expProduct);
                            }

                            if (fromBookmark) {
                                products.remove(position);
                                vendors.remove(position);
                            }

                        }
                    });
                    MainApplication.bus.post(new BookmarkAddRemoveEvent());
                    notifyDataSetChanged();
                }
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                if(activity==null)
                    return;

                dismissDialog();
                if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                    AppUtils.showToast("Please check your network.", true, 0);
                } else {
                    if (error != null && !error.equals("")) {
                        AppUtils.showToast(error, true, 0);
                    } else {
                        AppUtils.showToast("Bookmark not removed !", true, 0);
                    }
                }
            }
        }, BookMarkMenu.class);
        bookMarkMenuSimpleHttpAgent.get();
    }

    private void showRemoveBookmarkDialog(final Product product, final int position){
        if(activity==null)
            return;
        GeneralDialogFragment generalDialogFragment = GeneralDialogFragment.newInstance("Remove Bookmark item ?",
                "Are you sure you want to remove this bookmark item? ", new GeneralDialogFragment.OnDialogFragmentClickListener() {
            @Override
            public void onPositiveInteraction(GeneralDialogFragment dialog) {
                deleteBookmark(product, position);
            }

            @Override
            public void onNegativeInteraction(GeneralDialogFragment dialog) {
            }
        });
        generalDialogFragment.show(activity.getSupportFragmentManager(), "dialog");
    }
    private void showRemoveCartItemDialog(final Product product, final Vendor vendor, final ProductItemViewHolder holder){
        if(activity==null)
            return;
        GeneralDialogFragment generalDialogFragment = GeneralDialogFragment.newInstance("Replace cart item?",
                "All the previous items in the cart will be discarded.", new GeneralDialogFragment.OnDialogFragmentClickListener() {
                    @Override
                    public void onPositiveInteraction(GeneralDialogFragment dialog) {
                        clearLocalOrder();
                        addProductToCart(product.clone(), vendor, holder);
                    }

                    @Override
                    public void onNegativeInteraction(GeneralDialogFragment dialog) {
                        notifyDataSetChanged();
                    }
                });
        generalDialogFragment.show(activity.getSupportFragmentManager(), "dialog");
    }

    private void showProgressDialog(String message) {
        if(activity!=null) {
            progressDialog = new AlertDialog.Builder(activity).setMessage(message).setCancelable(false).create();
            progressDialog.show();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null && activity!=null) {
            progressDialog.dismiss();
        }
    }
//    private void setEnabledMenu(ProductItemViewHolder holder) {
//        holder.rvContiner.setVisibility(View.VISIBLE);
//        if(whiteFont){
//            holder.tvName.setTextColor(Color.parseColor("#ffffff"));
//            holder.tvPrice.setTextColor(Color.parseColor("#ffffff"));
//        }else{
//            holder.tvName.setTextColor(Color.parseColor("#4a4a4a"));
//            holder.tvPrice.setTextColor(Color.parseColor("#e77817"));
//        }
//
//        holder.tvVendorName.setTextColor(Color.parseColor("#9b9b9b"));
//    }

    private void setEnabledMenu(ProductItemViewHolder holder) {
        holder.rvContiner.setVisibility(View.VISIBLE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.tvName.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
            holder.tvPrice.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        }else{
            holder.tvName.setTextColor(activity.getResources().getColor(R.color.text_dark));
            holder.tvPrice.setTextColor(activity.getResources().getColor(R.color.colorAccent));
        }


        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.tvVendorName.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
        }else{
            holder.tvName.setTextColor(Color.parseColor("#4a4a4a"));
            holder.tvPrice.setTextColor(activity.getResources().getColor(R.color.text_dark));
        }

        holder.tvVendorName.setTextColor(Color.parseColor("#9b9b9b"));
    }
//    private void setDisableMenu(ProductItemViewHolder holder) {
//        holder.rvContiner.setVisibility(View.GONE);
//        holder.tvName.setTextColor(Color.parseColor("#d9d9d9"));
//        holder.tvPrice.setTextColor(Color.parseColor("#d9d9d9"));
//        holder.tvVendorName.setTextColor(Color.parseColor("#d9d9d9"));
//        holder.ivIsVeg.setImageResource(R.drawable.ic_veg_disabled);
//    }

    private void setDisableMenu(ProductItemViewHolder holder) {
        holder.rvContiner.setVisibility(View.GONE);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.tvName.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
            holder.tvPrice.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
            holder.tvVendorName.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
        }else{
            holder.tvName.setTextColor(activity.getResources().getColor(R.color.text_dark));
            holder.tvPrice.setTextColor(activity.getResources().getColor(R.color.text_dark));
            holder.tvVendorName.setTextColor(activity.getResources().getColor(R.color.text_dark));
        }
        holder.ivIsVeg.setImageResource(R.drawable.ic_veg_disabled);
    }

    private void getLocationId(){
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        isPreOrder = SharedPrefUtil.getBoolean(ApplicationConstants.IS_PREORDER_AVAILABLE,false);
    }

    private void clearLocalOrder() {
        mainApplication.clearOrder();
    }


    @Override
    public int getItemCount() {
        if (products != null) {
            if(empty!=null) {
                if (products.size() == 0) empty.setVisibility(View.VISIBLE);
                else empty.setVisibility(View.GONE);
            }
            return products.size();
        } else {
            return 0;
        }
    }

    public void changeProducts(List<Product> products, Vendor vendor) {
        this.vendor = vendor;
        this.products = products;
    }

    public void changeProducts(List<Product> products, List<Vendor> vendors) {
        this.vendors = vendors;
        this.products = products;
    }

    private void updateStockText(ProductItemViewHolder holder,Product product){
        if(product.getMaxQty()<=AppUtils.getConfig(activity).getLeft_threshold()){
            holder.tvStockLeft.setVisibility(View.VISIBLE);
            if(product.getMaxQty()<=0){
                holder.tvAddCart.setEnabled(false);
                holder.rvContiner.setBackgroundResource(R.drawable.rounded_gray);
                holder.tvStockLeft.setText("  Out of stock  ");
            }else{
                holder.tvAddCart.setEnabled(true);
                holder.rvContiner.setBackgroundResource(R.drawable.menu_add_back_rounded);
                holder.tvStockLeft.setText("  Only "+product.getMaxQty() +" left  ");
            }
        }else{
            holder.tvStockLeft.setVisibility(View.GONE);
        }
    }

}
