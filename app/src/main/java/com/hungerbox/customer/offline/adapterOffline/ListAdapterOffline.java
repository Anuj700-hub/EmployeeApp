package com.hungerbox.customer.offline.adapterOffline;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.MenuHeader;
import com.hungerbox.customer.model.MenuSwitch;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.activityOffline.MenuActivityOffline;
import com.hungerbox.customer.offline.fragmentOffline.OptionSelectionDialog;
import com.hungerbox.customer.offline.listenersOffline.AddToCardLisenterOffline;
import com.hungerbox.customer.offline.listenersOffline.VendorValidationListener;
import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;
import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.order.adapter.viewholder.ProductHeaderViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.ProductVegOnlySwitch;
import com.hungerbox.customer.order.fragment.GeneralDialogFragment;
import com.hungerbox.customer.order.listeners.VegNonVegSwitchListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ListAdapterOffline extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    boolean whiteFont = false, fromBookmark = false, isPreOrder = false;
    final int viewTypeHeader = 0;
    final int viewTypeProduct = 1;
    final int viewTypeFiller = 2;
    final int viewTypeEnd = 3;
    AppCompatActivity activity;
    List<Object> products;
    LayoutInflater inflater;
    MainApplicationOffline mainApplication;
    VendorValidationListener vendorValidationListener;
    VendorOffline vendor;
    long occasionId;
    Config config;
    private AlertDialog progressDialog;
    private long locationId;
    List<VendorOffline> vendors;
    int expressCheckoutAction = 0;
    TextView empty;
    VegNonVegSwitchListener vegNonSwitchListener;
    private boolean[] descShowing;


    public ListAdapterOffline(AppCompatActivity activity, List<Object> products, VendorOffline vendor, VendorValidationListener vendorValidationListener, long occasionId, VegNonVegSwitchListener vegNonSwitchListener) {
        this.activity = activity;
        this.products = products;
        this.vendor = vendor;
        descShowing = new boolean[products.size()];
        Arrays.fill(descShowing, false);
        this.occasionId = occasionId;
        this.vendorValidationListener = vendorValidationListener;
        inflater = LayoutInflater.from(activity);
        config = AppUtils.getConfig(activity);
        this.vegNonSwitchListener = vegNonSwitchListener;
        getLocationId();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == viewTypeHeader) {
            //for category name
            ProductHeaderViewHolder holder = new ProductHeaderViewHolder(inflater.inflate(R.layout.product_header_item_offline, parent, false));
            return holder;
        } else if (viewType == viewTypeProduct) {
            //for products
            if (whiteFont) {
                ProductItemViewHolder holder = new ProductItemViewHolder(inflater.inflate(R.layout.product_list_menuitem_white_offline, parent, false));
                return holder;
            } else {
                ProductItemViewHolder holder = new ProductItemViewHolder(inflater.inflate(R.layout.product_list_menuitem_offline, parent, false));
                return holder;
            }
        } else if (viewType == viewTypeEnd) {
            //Append to end of the list for easy scrolling till end
            ProductVegOnlySwitch holder = new ProductVegOnlySwitch(inflater.inflate(R.layout.menu_list_end_offline, parent, false));
            return holder;
        } else {
            ProductVegOnlySwitch holder = new ProductVegOnlySwitch(inflater.inflate(R.layout.product_list_end_filler_offline, parent, false));
            return holder;
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mHolder, final int position) {
        if (products.get(position) instanceof ProductOffline) {

            final ProductOffline product = (ProductOffline) products.get(position);
            final ProductItemViewHolder holder = (ProductItemViewHolder) mHolder;
            holder.tvName.setText(product.getName());

            final int cartQty = MainApplicationOffline.getTotalOrderCount(1);

            //BookMarkMenu bookMarkMenu = realm.where(BookMarkMenu.class).equalTo(BookMarkMenu.ID, product.getId()).findFirst();
            //Product expProduct = realm.where(Product.class).equalTo(Product.ID,product.getId()).findFirst();

            if (product.isBookmarked()) {
                holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_red);
                holder.ivBookmark.setTag("red");
            } else {
                holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_grey);
                holder.ivBookmark.setTag("grey");
            }

            if (product.isFree()) {
                if (product.discountedPrice == 0) {
                    if (AppUtils.getConfig(activity).isHide_price())
                        holder.tvPrice.setText("");
                    else
                        holder.tvPrice.setText(AppUtils.getConfig(activity).getCompany_paid_text());
                    if (product.isFree() && AppUtils.getConfig(activity).is_guest_order()) {
                        holder.tvPrice.setText(activity.getString(R.string.guest_ordering_text));
                    }
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

            double totalCalories = product.getTotalCalories();

            holder.tvCal.setVisibility(View.GONE);


            final String desc = "";
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

                } else {
                    holder.ivDescriptionArrow.setVisibility(View.GONE);
                    holder.tvDescription.setText(Html.fromHtml(desc));
                }

            } else {
                holder.tvDescription.setVisibility(View.GONE);
                holder.ivDescriptionArrow.setVisibility(View.GONE);
            }


            if (fromBookmark) {
                holder.tvVendorName.setText(product.getVendorName());
                holder.tvDescription.setVisibility(View.GONE);
                holder.ivDescriptionArrow.setVisibility(View.GONE);
                holder.tvVendorName.setVisibility(View.VISIBLE);
            }


            int orderQty = mainApplication.getOrderQuantityForProduct(product.getId(), 1);

            if (product.isProductVeg()) {
                holder.ivIsVeg.setImageResource(R.drawable.ic_veg_icon);
            } else {
                holder.ivIsVeg.setImageResource(R.drawable.ic_non_veg);
            }

            updateOrderActionItem(orderQty, holder);


            holder.tvAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (cartQty > 3) {
                        AppUtils.showToast("You cannot order more than " + ApplicationConstants.MAX_QUANTITIY_OFFLINE + " quantity", false, 0);
                        return;
                    }

                    try {
                        if (product.isRecommended()) {
                            EventUtil.FbEventLog(activity, EventUtil.MENU_RECOMMENDED_CLICK, EventUtil.SCREEN_MENU);
                            HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_RECOMMENDED_CLICK);
                        } else {
                            EventUtil.FbEventLog(activity, EventUtil.MENU_ADD_ITEM, EventUtil.SCREEN_MENU);
                        }
                    } catch (Exception exp) {
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


                        if (mainApplication.getCart(1).getOrderProducts().size() > 0)
                            showRemoveCartItemDialog(product.clone(), vendor, holder);
                        else
                            addProductToCart(product.clone(), vendor, holder);

                    } else {
                        addProductToCart(product.clone(), vendor, holder);
                    }

                }
            });

            holder.ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventUtil.FbEventLog(activity, EventUtil.MENU_ITEM_INCR, EventUtil.SCREEN_MENU);

                    if (cartQty > 3) {
                        AppUtils.showToast("You cannot order more than " + ApplicationConstants.MAX_QUANTITIY_OFFLINE + " quantity", false, 0);
                        return;
                    }

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
                    int orderQty = mainApplication.getOrderQuantityForProduct(product.getId(), 1);
                    holder.tvQuantity.setText(String.format("%d", orderQty));
                    updateOrderActionItem(orderQty, holder);
                    if (activity instanceof MenuActivityOffline)
                        ((MenuActivityOffline) activity).setUpCart();
                    if (activity instanceof AddToCardLisenterOffline) {
                        AddToCardLisenterOffline addToCardLisenter = (AddToCardLisenterOffline) activity;
                        ProductOffline productClone = product.clone();
                        productClone.quantity = orderQty;
                        ((AddToCardLisenterOffline) activity).removeFromCart(vendor.clone(), productClone);
                    }
                }
            });


            if (product.getOptionResponse().getSubProducts().size() > 0) {
                holder.tvAddCart.setText("ADD +");
                holder.tvCustomize.setVisibility(View.VISIBLE);
            } else {
                holder.tvAddCart.setText(" ADD ");
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


            holder.ivBookmark.setVisibility(View.GONE);


            if (fromBookmark && !product.isOrderingAllowed()) {
                setDisableMenu(holder);
            } else {
                setEnabledMenu(holder);
            }


            LogoutTask.updateTime();
        } else if (products.get(position) instanceof MenuHeader) {
            final ProductHeaderViewHolder holder = (ProductHeaderViewHolder) mHolder;
            holder.header.setText(((MenuHeader) products.get(position)).getCategory());
        } else {
            //veg only switch
            if (products.get(position) instanceof MenuSwitch) {
                ProductVegOnlySwitch holder = (ProductVegOnlySwitch) mHolder;
                MenuSwitch menuSwitch = (MenuSwitch) products.get(position);
                holder.switchVegOnly.setChecked(menuSwitch.isChecked);
                holder.switchVegOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        vegNonSwitchListener.onUpdate(isChecked);
                    }
                });
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (products.get(position) instanceof MenuHeader) {
            return viewTypeHeader;
        } else if (products.get(position) instanceof ProductOffline) {
            return viewTypeProduct;
        } else if (products.get(position) instanceof String) {
            return viewTypeEnd;
        } else {
            return viewTypeFiller;
        }
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

        if (orderQty <= 0) {
            holder.rvContiner.setBackgroundResource(R.drawable.menu_add_back);
        } else {
            holder.rvContiner.setBackgroundResource(R.drawable.menu_add_selected_back);
        }
    }

    private void addProductToCart(ProductOffline product, VendorOffline vendor, ProductItemViewHolder holder) {

        HashMap<String, Object> map = new HashMap<>();

        try {
            String source = "";
            if (fromBookmark) {
                if (expressCheckoutAction == ApplicationConstants.BOOKMARK_FROM_MAINACTIVITY) {
                    source = "Exp";
                } else {
                    source = "Nav";
                }
            } else {
                source = "Menu";
            }

            map.put(CleverTapEvent.PropertiesNames.getSource(), source);
            map.put(CleverTapEvent.PropertiesNames.is_bookmarked(), product.isBookmarked());
            map.put(CleverTapEvent.PropertiesNames.is_trending(), product.isTrendingItem());
            map.put(CleverTapEvent.PropertiesNames.getItem_name(), product.getName());
            map.put(CleverTapEvent.PropertiesNames.is_customised(), !product.containsSubProducts());

        } catch (Exception e) {
            e.printStackTrace();
        }


        MainApplicationOffline.getCart(1).addProductToCart(product, holder, mainApplication, vendorValidationListener, activity, vendor, occasionId, map);
        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId(), 1);
        updateOrderActionItem(orderQty, holder);
    }

    private void addNonFreeNormalProductToCart(ProductOffline product, VendorOffline vendor, ProductItemViewHolder holder) {
        OrderProductOffline orderProduct = new OrderProductOffline();
        orderProduct.copy(product);
        mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occasionId);
        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId(), 1);
        updateOrderActionItem(orderQty, holder);
        holder.tvQuantity.setText(String.format("%d", orderQty));
        updateOrderActionItem(orderQty, holder);
        notifyDataSetChanged();
        if (activity instanceof MenuActivityOffline)
            ((MenuActivityOffline) activity).setUpCart();
        if (activity instanceof AddToCardLisenterOffline) {
            AddToCardLisenterOffline addToCardLisenter = (AddToCardLisenterOffline) activity;
            ProductOffline productClone = product.clone();
            productClone.quantity = orderQty;
            ((AddToCardLisenterOffline) activity).addToCart(vendor.clone(), productClone);
        }
    }


    private void addNonFreeProductToCart(ProductOffline product, VendorOffline vendor, ProductItemViewHolder holder) {
        if (product.containsSubProducts()) {
            addNonFreeNormalProductToCart(product, vendor, holder);
        } else {
            showOptionDialog(vendor, product, holder);
        }
    }


    private void showOptionDialog(final VendorOffline vendor, final ProductOffline product, final ProductItemViewHolder holder) {
        OptionSelectionDialog optionSelectionDialog = OptionSelectionDialog.newInstance(vendor, product, new OptionSelectionDialog.OnOptionSelectionListener() {
            @Override
            public void onFragmentInteraction(OrderProductOffline orderProduct) {
                mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occasionId);
                if (activity instanceof MenuActivityOffline)
                    ((MenuActivityOffline) activity).setUpCart();
                int orderQuantity = mainApplication.getOrderQuantityForProduct(product.getId(), 1);
                holder.tvQuantity.setText(String.format("%d", orderQuantity));
                updateOrderActionItem(orderQuantity, holder);
            }
        });
        optionSelectionDialog.setCancelable(false);

        optionSelectionDialog.show(activity.getSupportFragmentManager(), "menu_option");
    }


    private void showRemoveCartItemDialog(final ProductOffline product, final VendorOffline vendor, final ProductItemViewHolder holder) {
        if (activity == null)
            return;
        GeneralDialogFragment generalDialogFragment = GeneralDialogFragment.newInstance("Replace cart item?",
                "You are entering the \"Express Lane\". All the previous items in cart will be discarded.", new GeneralDialogFragment.OnDialogFragmentClickListener() {
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
        if (activity != null) {
            progressDialog = new AlertDialog.Builder(activity).setMessage(message).setCancelable(false).create();
            progressDialog.show();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null && activity != null) {
            progressDialog.dismiss();
        }
    }

    private void setEnabledMenu(ProductItemViewHolder holder) {
        holder.rvContiner.setVisibility(View.VISIBLE);
        if(whiteFont){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvName.setTextColor(ContextCompat.getColor(activity, R.color.white));
                holder.tvPrice.setTextColor(ContextCompat.getColor(activity, R.color.white));
            }else{
                holder.tvName.setTextColor(activity.getResources().getColor(R.color.white));
                holder.tvPrice.setTextColor(activity.getResources().getColor(R.color.white));
            }
        }else{
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvName.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
                holder.tvPrice.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            }else{
                holder.tvName.setTextColor(activity.getResources().getColor(R.color.text_dark));
                holder.tvPrice.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            }
        }

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.tvVendorName.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
        }else{
            holder.tvPrice.setTextColor(activity.getResources().getColor(R.color.text_dark));
        }
    }

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

    private void getLocationId() {
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        isPreOrder = SharedPrefUtil.getBoolean(ApplicationConstants.IS_PREORDER_AVAILABLE, false);
    }

    private void clearLocalOrder() {
        mainApplication.clearOrder(1);
    }


    @Override
    public int getItemCount() {
        if (products != null) {
            if (empty != null) {
                if (products.size() == 0) empty.setVisibility(View.VISIBLE);
                else empty.setVisibility(View.GONE);
            }
            descShowing = new boolean[products.size()];
            Arrays.fill(descShowing, false);
            return products.size();
        } else {
            return 0;
        }
    }

    public void changeProducts(List<Object> products, VendorOffline vendor) {
        this.vendor = vendor;
        this.products = products;
        descShowing = new boolean[products.size()];
        Arrays.fill(descShowing, false);
    }

    public void changeProducts(List<Object> products, List<VendorOffline> vendors) {
        this.vendors = vendors;
        this.products = products;
        descShowing = new boolean[products.size()];
        Arrays.fill(descShowing, false);
    }


}




