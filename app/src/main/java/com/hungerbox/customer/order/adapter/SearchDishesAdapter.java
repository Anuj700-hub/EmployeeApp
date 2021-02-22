package com.hungerbox.customer.order.adapter;

        import android.app.Activity;
        import android.content.Intent;
        import android.text.Html;
        import android.text.Spannable;
        import android.text.Spanned;
        import android.text.style.StrikethroughSpan;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.RecyclerView;

        import com.hungerbox.customer.HBMixpanel;
        import com.hungerbox.customer.MainApplication;
        import com.hungerbox.customer.R;
        import com.hungerbox.customer.config.action.LogoutTask;
        import com.hungerbox.customer.model.FeatureSearchVendorMenu;
        import com.hungerbox.customer.model.Product;
        import com.hungerbox.customer.model.Vendor;
        import com.hungerbox.customer.order.AddToCardLisenter;
        import com.hungerbox.customer.order.activity.GlobalActivity;
        import com.hungerbox.customer.order.activity.GlobalSearchActivity;
        import com.hungerbox.customer.order.activity.MenuActivity;
        import com.hungerbox.customer.order.activity.PaymentActivity;
        import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
        import com.hungerbox.customer.order.adapter.viewholder.SearchVendorViewHolder;
        import com.hungerbox.customer.order.listeners.VendorValidationListener;
        import com.hungerbox.customer.util.AppUtils;
        import com.hungerbox.customer.util.ApplicationConstants;
        import com.hungerbox.customer.util.CleverTapEvent;
        import com.hungerbox.customer.util.EventUtil;
        import com.hungerbox.customer.util.SharedPrefUtil;

        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;

        import io.realm.Realm;
        import io.realm.RealmConfiguration;

public class SearchDishesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_TYPE_PRODUCT = 0;
    private final int ITEM_TYPE_VENDOR = 1;
    private final int ITEM_TYPE_FILLER = 2;
    ArrayList<Object> productArrayList;
    LayoutInflater inflater;
    Activity activity;
    MainApplication mainApplication;
    Realm realm;
    long occasionId;
    long lastVendorId = -1;
    VendorValidationListener vendorValidationListener;

    public SearchDishesAdapter(Activity activity, ArrayList<Object> productArrayList, VendorValidationListener vendorValidationListener) {
        this.productArrayList = productArrayList;
        this.activity = activity;
        if (activity instanceof GlobalSearchActivity){
          this.occasionId = ((GlobalSearchActivity) activity).occasionId;
        }
        this.vendorValidationListener = vendorValidationListener;
        this.mainApplication = (MainApplication) activity.getApplication();
        this.realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        this.inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        if (viewType == ITEM_TYPE_PRODUCT) {
            view = inflater.inflate(R.layout.search_dish_item, parent, false);
            return new SearchDishesViewHolder(view);
        } else if(viewType == ITEM_TYPE_FILLER){
            view = inflater.inflate(R.layout.item_type_filler, parent, false);
            return new EmptyViewHolder(view);
        }else{
            view = inflater.inflate(R.layout.search_vendor_dish_item,parent,false);
            return new SearchVendorViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (productArrayList.get(position) instanceof Vendor){
            return ITEM_TYPE_VENDOR;
        } else if (productArrayList.get(position) instanceof String){
            return ITEM_TYPE_FILLER;
        }
        return ITEM_TYPE_PRODUCT;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchDishesViewHolder){
          inflateItem((SearchDishesViewHolder) holder,position);
        } else if (holder instanceof SearchVendorViewHolder){
            inflateVendor((SearchVendorViewHolder)holder,position);
        } //else filler

    }

    private void inflateItem(@NonNull SearchDishesViewHolder holder, int position){
        try {
            FeatureSearchVendorMenu menuProduct = (FeatureSearchVendorMenu) productArrayList.get(position);
            Product product = null;
            if (GlobalSearchActivity.mapOfProducts !=null && !GlobalSearchActivity.mapOfProducts.isEmpty()){
                product = GlobalSearchActivity.mapOfProducts.get(menuProduct.getMenuId());
                product.setRecommendationType(menuProduct.getRecommendationType());
                product.setRecommendationScore(menuProduct.getRecommendationScore());
            }
            if (product == null){
                product = cloneFeatureSearchMenu(menuProduct);
            }
            Vendor vendor = AppUtils.getVendorById(activity, product.getVendorId());
            holder.tvProductName.setText(product.getName());
            holder.tvProductDescription.setText(Html.fromHtml(product.getDesc()));
//            holder.tvProductDescription.setText(product.getDesc());

            if (product.getIsVeg() == 1) {
                holder.ivVegNonVeg.setImageResource(R.drawable.ic_veg_icon);
            } else {
                holder.ivVegNonVeg.setImageResource(R.drawable.ic_non_veg);
            }
            holder.ivAdd.setVisibility(View.INVISIBLE);
            holder.ivRemove.setVisibility(View.INVISIBLE);
            holder.tvQuantity.setVisibility(View.INVISIBLE);


            Product finalProduct = product;
            int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
            updateOrderActionItem(orderQty, holder);
            holder.tvAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addClickListener(finalProduct,holder,position,vendor);
                }
            });

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
                    addProductToCart(finalProduct.clone(), vendor, holder);
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

                    mainApplication.removeProductFromCart(finalProduct.clone());
                    int orderQty = mainApplication.getOrderQuantityForProduct(finalProduct.getId());
                    holder.tvQuantity.setText(String.format("%d", orderQty));
                    updateOrderActionItem(orderQty, holder);
                    if (activity instanceof GlobalSearchActivity)
                        ((GlobalSearchActivity) activity).setUpCart();
                    if (activity instanceof AddToCardLisenter) {
                        AddToCardLisenter addToCardLisenter = (AddToCardLisenter) activity;
                        Product productClone = finalProduct.clone();
                        productClone.quantity = orderQty;
                        ((AddToCardLisenter) activity).removeFromCart(vendor.clone(), productClone);
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

            if (product.getOptionResponse().getSubProducts().size() > 0) {
                holder.tvAddCart.setText("   ADD +   ");
                holder.tvCustomize.setVisibility(View.VISIBLE);
            } else {
                holder.tvAddCart.setText("    ADD    ");
                holder.tvCustomize.setVisibility(View.GONE);
            }

            if(vendor.isVendingMachine()){
                updateStockText(holder,product);
            }else{
                holder.tvStockLeft.setVisibility(View.GONE);
                enableDisableAddButton(holder,true);
            }

            if (AppUtils.getConfig(activity).isPlace_order()) {
                holder.llContainer.setVisibility(View.VISIBLE);
            }
            else{
                if(vendor.isPlaceOrderEnabled())
                    holder.llContainer.setVisibility(View.VISIBLE);
                else
                    holder.llContainer.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void inflateVendor(@NonNull SearchVendorViewHolder holder, int position){
        try {
            Vendor vendor = (Vendor) productArrayList.get(position);
            holder.tvVendorName.setText(vendor.getVendorName());
            holder.tvVendorDescription.setText(vendor.getDesc());
            if (vendor.getRating()>0){
                holder.tvVendorRating.setText(String.valueOf(vendor.getRating()));
                holder.tvVendorRating.setVisibility(View.VISIBLE);
                holder.ivStar.setVisibility(View.VISIBLE);
            } else{
                holder.tvVendorRating.setVisibility(View.GONE);
                holder.ivStar.setVisibility(View.GONE);
            }
            if (AppUtils.isSocialDistancingActive(null)){
                if (vendor.isCoronaSafe()) {
                    showCoronaSafeBadge(true,holder);
                } else{
                    showCoronaSafeBadge(false,holder);
                }
                holder.tvDeliveryType.setVisibility(View.VISIBLE);
                //for delivery type label
                int deskDeliveryType = SharedPrefUtil.getInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, 0);
                holder.tvDeliveryType.setText(vendor.getDeliveryType(deskDeliveryType));
            } else{
                showCoronaSafeBadge(false,holder);
                holder.tvDeliveryType.setVisibility(View.INVISIBLE);
            }
            holder.tvViewMenu.setOnClickListener((v)->{
                moveToVendorDetails(vendor);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void addClickListener(Product product, SearchDishesViewHolder holder, int position, Vendor vendor){
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

        addProductToCart(product.clone(), vendor, holder);
    }

    private void addProductToCart(Product product, Vendor vendor, SearchDishesViewHolder holder) {

        HashMap<String,Object> map =new HashMap<>() ;

        try{
            String source = "global search";

            map.put(CleverTapEvent.PropertiesNames.getSource(),source);
            map.put(CleverTapEvent.PropertiesNames.is_bookmarked(),product.isBookmarked());
            map.put(CleverTapEvent.PropertiesNames.is_trending(),product.isTrendingItem());
            map.put(CleverTapEvent.PropertiesNames.getItem_name(),product.getName());
            map.put(CleverTapEvent.PropertiesNames.is_customised(),!product.containsSubProducts());

        }catch (Exception e){
            e.printStackTrace();
        }

        mainApplication.getCart().addProductToCart(product, holder, mainApplication, vendorValidationListener, (AppCompatActivity) activity, vendor, occasionId,map);
        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
        updateOrderActionItem(orderQty, holder);
        if (activity instanceof GlobalSearchActivity)
            ((GlobalSearchActivity) activity).setUpCart();
    }
    private Product cloneFeatureSearchMenu(FeatureSearchVendorMenu featureSearchVendorMenu){
        Product product = new Product();
        product.setId(featureSearchVendorMenu.getMenuId());
        product.setName(featureSearchVendorMenu.getName());
        product.setDesc(featureSearchVendorMenu.getDescription());
        product.setVendorId(featureSearchVendorMenu.getVendorId());
        product.setCategory(featureSearchVendorMenu.getCategoryName());
        product.setIsVeg(featureSearchVendorMenu.getIsVeg());
        product.setRecommendationType(featureSearchVendorMenu.getRecommendationType());
        product.setRecommendationScore(featureSearchVendorMenu.getRecommendationScore());
        product.isFree = 0;
        return product;

    }

    private void updateOrderActionItem(int orderQty, SearchDishesViewHolder holder) {
        if (orderQty > 0) {
            holder.rvContainer.setVisibility(View.VISIBLE);
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.ivRemove.setVisibility(View.VISIBLE);
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvAddCart.setVisibility(View.GONE);
            holder.tvQuantity.setText(String.format("%d", orderQty));
        } else {
            holder.rvContainer.setVisibility(View.VISIBLE);
            holder.ivAdd.setVisibility(View.INVISIBLE);
            holder.ivRemove.setVisibility(View.INVISIBLE);
            holder.tvQuantity.setVisibility(View.GONE);
            holder.tvAddCart.setVisibility(View.VISIBLE);

        }

    }

    private void showCoronaSafeBadge(boolean show, SearchVendorViewHolder vendorListItemHolder){
        if (show){
            vendorListItemHolder.ivTick.setVisibility(View.VISIBLE);
            vendorListItemHolder.tvCoronaSafe.setVisibility(View.VISIBLE);
        } else{
            vendorListItemHolder.ivTick.setVisibility(View.GONE);
            vendorListItemHolder.tvCoronaSafe.setVisibility(View.GONE);
        }
    }

    private void updateStockText(SearchDishesViewHolder holder, Product product){
        if(product.getMaxQty()<=AppUtils.getConfig(activity).getLeft_threshold()){
            holder.tvStockLeft.setVisibility(View.VISIBLE);
            if(product.getMaxQty()<=0){
                enableDisableAddButton(holder,false);
                holder.tvStockLeft.setText("  Out of stock  ");
            }else{
                enableDisableAddButton(holder,true);
                holder.tvStockLeft.setText("  Only "+product.getMaxQty() +" left  ");
            }
        }else{
            holder.tvStockLeft.setVisibility(View.GONE);
        }
    }

    private void enableDisableAddButton(SearchDishesViewHolder holder,boolean enable){
        if (enable){
            holder.tvAddCart.setEnabled(true);
            holder.rvContainer.setBackgroundResource(R.drawable.menu_add_back_rounded);
        } else{
            holder.tvAddCart.setEnabled(false);
            holder.rvContainer.setBackgroundResource(R.drawable.rounded_gray);
        }
    }

    private void moveToVendorDetails(Vendor vendor){
        Intent intent;
        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
        if(vendor.getSdkType() != null && vendor.getSdkType().equals(ApplicationConstants.BIG_BASKET)){
            intent = new Intent(activity, PaymentActivity.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, vendor.getSdkType());
            intent.putExtra(ApplicationConstants.BIG_BASKET, true);
            intent.putExtra(ApplicationConstants.OCASSION_ID, MainApplication.selectedOcassionId);
            intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);

        }else{
            intent = new Intent(activity, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ApplicationConstants.VENDOR_ID, vendor.getId());
            intent.putExtra(ApplicationConstants.VENDOR_NAME, vendor.getVendorName());
            if (activity!=null && activity instanceof GlobalSearchActivity){
                intent.putExtra(ApplicationConstants.OCASSION_ID, ((GlobalSearchActivity) activity).occasionId);
            }
            intent.putExtra(ApplicationConstants.LOCATION, SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR"));
        }

        activity.startActivity(intent);
    }

    public void setProducts(ArrayList<Object> productArrayList){
        this.productArrayList = productArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (productArrayList!=null){
            return productArrayList.size();
        }
        return 0;
    }
    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class SearchDishesViewHolder extends RecyclerView.ViewHolder {

        public TextView tvProductName, tvProductDescription, tvAddCart, tvQuantity, tvPrice, tvCustomize,tvStockLeft;
        public LinearLayout llContainer;
        public ImageView ivVegNonVeg, ivAdd, ivRemove;
        public RelativeLayout rvContainer;

        public SearchDishesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductDescription = itemView.findViewById(R.id.tv_description);
            tvAddCart = itemView.findViewById(R.id.tv_add_card);
            llContainer = itemView.findViewById(R.id.ll_add_container);
            ivVegNonVeg = itemView.findViewById(R.id.iv_veg_non);
            ivAdd = itemView.findViewById(R.id.iv_add);
            ivRemove = itemView.findViewById(R.id.iv_remove);
            rvContainer = itemView.findViewById(R.id.rl_add_container);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCustomize = itemView.findViewById(R.id.tv_customize);
            tvStockLeft = itemView.findViewById(R.id.tv_stock_left);
        }
    }
}

