package com.hungerbox.customer.order.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.adapter.MenuListAdapter;
import com.hungerbox.customer.order.fragment.CartCancelDialog;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MenuSearchActivity extends ParentActivity implements VendorValidationListener {


    public TextView tvTitle,tvCart, tvCartAmount;
    //public ImageView ivLogo, ivOcassions, ivSearch;
    Realm realm;
    RealmResults<Product> productRealmResultss;
    ArrayList<Product> tempProductRealmResults;
    RecyclerView rvMenu;
    EditText textSearchText;
    protected RelativeLayout flFloatingContainer;
    AppCompatButton btCheckout;
    MenuListAdapter menuListAdapter;
    ImageView ivBack;
    LinearLayout ll_search_result,llCategory;

    long occassionId, vendorId;
    String locationName, vendorName;
    Vendor vendor;
    //filter related views
    private AppCompatImageView btFilter;
    private Button btFilterOk;
    private Button btClearFilter;
    private View backgroundView;
    private AppCompatImageView ivFilterClose;
    private AppCompatRadioButton  radioYes, radioNo;
    private CheckBox radioVeg, radioNonVeg;
    private LinearLayout filterSheet;
    AppCompatImageView btClear;
    private BottomSheetBehavior bottomSheetBehavior;
    private RealmQuery<Product> realmQuery;
    private ArrayList<String> categoryList;
    private SeekBar priceSeekBar, calorieSeekbar;
    private TextView tvPrice,tvCalorie, tvCategory;
    private HorizontalScrollView svCategory;
    private TextView tvPriceValue,tvCalorieValue;
    private TextView tvPriceUpperValue,tvPriceLowerValue,tvCalorieUpperValue,tvCalorieLowerValue;
    private double[] minMaxValues;//[minPrice,maxPrice,minCalorie,maxCalorie]
    double filteredCalorieVal = -1;
    double filteredPriceVal = -1;

    TextWatcher searchtextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            applyFilters(s.toString().trim());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_search);

        vendorId = getIntent().getLongExtra(ApplicationConstants.VENDOR_ID, 0);
        occassionId = getIntent().getLongExtra(ApplicationConstants.OCASSION_ID, 0);
        locationName = getIntent().getStringExtra(ApplicationConstants.LOCATION_NAME);
        vendorName = getIntent().getStringExtra(ApplicationConstants.VENDOR_NAME);
        categoryList = getIntent().getStringArrayListExtra(ApplicationConstants.CATEGORY_LIST);
        minMaxValues = getIntent().getDoubleArrayExtra(ApplicationConstants.MIN_MAX_VALUES);
        flFloatingContainer = findViewById(R.id.fl_cart_container);


        ll_search_result = findViewById(R.id.ll_search_result);


        ivBack = findViewById(R.id.iv_back);

        rvMenu = findViewById(R.id.rv_search_list);

        tvCategory = findViewById(R.id.tv_category);
        svCategory = findViewById(R.id.sv_category);

        textSearchText = findViewById(R.id.et_search);
        textSearchText.setHint("Find Menu Item");
        tvCart = findViewById(R.id.tv_cart);
        tvTitle = findViewById(R.id.tv_title);
        tvCartAmount = findViewById(R.id.tv_order_amount);
        btCheckout = findViewById(R.id.bt_checkout);
        btClear = findViewById(R.id.iv_clear);


        textSearchText.addTextChangedListener(searchtextWatcher);

        ll_search_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboard(MenuSearchActivity.this,null);
            }
        });

//        rvMenu.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true);
        rvMenu.setLayoutManager(llm);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResult(RESULT_OK);
                finish();
            }
        });
        btClear.setOnClickListener(v -> {
            textSearchText.setText("");
        });


        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Search");

        LogoutTask.updateTime();
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());

        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HashMap map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    map.put(CleverTapEvent.PropertiesNames.getVendor_id(), vendorId);
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getView_cart_click(), map, MenuSearchActivity.this);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                navigateToOrderReview();
            }
        });
        flFloatingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToOrderReview();
            }
        });

        btFilter = findViewById(R.id.bt_filter);
        ivFilterClose = findViewById(R.id.iv_filter_close);
        btFilterOk = findViewById(R.id.bt_filter_ok);
        btClearFilter = findViewById(R.id.bt_filter_clear);

        radioVeg = findViewById(R.id.cb_veg);
        radioNonVeg = findViewById(R.id.cb_nonveg);
        radioYes = findViewById(R.id.radio_yes);
        radioNo = findViewById(R.id.radio_no);

        llCategory = findViewById(R.id.ll_category);
        priceSeekBar = findViewById(R.id.sb_price);
        calorieSeekbar = findViewById(R.id.sb_calorie);
        tvPrice = findViewById(R.id.tv_price);
        tvPriceValue = findViewById(R.id.tv_price_value);
        tvPriceUpperValue = findViewById(R.id.tv_price_upper_value);
        tvPriceLowerValue = findViewById(R.id.tv_price_lower_value);
        tvCalorie = findViewById(R.id.tv_calorie);
        tvCalorieValue = findViewById(R.id.tv_calorie_value);
        tvCalorieUpperValue = findViewById(R.id.tv_calorie_upper_value);
        tvCalorieLowerValue = findViewById(R.id.tv_calorie_lower_value);

        backgroundView = findViewById(R.id.transparent_view);
        backgroundView.setVisibility(View.GONE);

        filterSheet = findViewById(R.id.filter_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(filterSheet);
        initFilterViews();

        AppUtils.showKeyboard(this, textSearchText);

    }

    private void initFilterViews(){
        try {
            btFilter.setOnClickListener(v -> {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
            backgroundView.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
            ivFilterClose.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED: {
                            backgroundView.setVisibility(View.VISIBLE);
                            backgroundView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_fade_in));
                        }
                        break;
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            backgroundView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_fade_out));
                            backgroundView.setVisibility(View.GONE);
                        }
                        break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
            findViewById(R.id.cl_filter_parent).setOnClickListener(v ->{
                //prevent background click
            });
            btFilter.setOnClickListener(v -> {
                AppUtils.hideKeyboard(MenuSearchActivity.this,null);
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
            inflateCategoriesInFilter();
            backgroundView.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
            ivFilterClose.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
            btFilterOk.setOnClickListener(v -> applyFilters(textSearchText.getText().toString().trim()));
            tvPriceValue.setX(priceSeekBar.getX() + priceSeekBar.getThumbOffset() / 2);
            btClearFilter.setOnClickListener(v -> {
                radioNonVeg.setChecked(false);
                radioVeg.setChecked(false);
                radioYes.setChecked(false);
                radioNo.setChecked(false);
                filteredPriceVal = -1;
                filteredCalorieVal = -1;
                priceSeekBar.setProgress(0);
                calorieSeekbar.setProgress(0);
                for (int i = 0; i < llCategory.getChildCount(); i++) {
                    CheckBox cb = (CheckBox) llCategory.getChildAt(i);
                    cb.setChecked(false);
                }
            });
            priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (minMaxValues!=null) {
                        tvPriceValue.setText("₹ " + (progress+(int) minMaxValues[0]));
                        int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
                        int thumbPos = seekBar.getPaddingLeft() + width * seekBar.getProgress() / seekBar.getMax();

                        tvPriceValue.measure(0, 0);
                        int txtW = tvPriceValue.getMeasuredWidth();
                        int delta = txtW / 2;
                        tvPriceValue.setX(seekBar.getX() + thumbPos - delta);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            calorieSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if (minMaxValues!=null) {
                        tvCalorieValue.setText((progress+(int) minMaxValues[2])+" Cal");
                        int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
                        int thumbPos = seekBar.getPaddingLeft() + width * seekBar.getProgress() / seekBar.getMax();

                        tvCalorieValue.measure(0, 0);
                        int txtW = tvCalorieValue.getMeasuredWidth();
                        int delta = txtW / 2;
                        tvCalorieValue.setX(seekBar.getX() + thumbPos - delta);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            //seek-bar labels and min - max values

            if (minMaxValues != null) {

                if (AppUtils.getConfig(this).getCompany_type()!=null && AppUtils.getConfig(this).getCompany_type().equalsIgnoreCase(ApplicationConstants.COMP_TYPE_CASH_N_CARRY) && (minMaxValues[1] != minMaxValues[0])) {
                    tvPriceLowerValue.setText("₹ " + (int)minMaxValues[0]);
                    tvPriceUpperValue.setText("₹ " + (int)Math.round(minMaxValues[1]));
                    priceSeekBar.setMax(( ((int)minMaxValues[1] - (int)Math.round(minMaxValues[0]))));
                }
                else{
                    tvPrice.setVisibility(View.GONE);
                    priceSeekBar.setVisibility(View.GONE);
                    tvPriceLowerValue.setVisibility(View.GONE);
                    tvPriceUpperValue.setVisibility(View.GONE);
                    tvPriceValue.setVisibility(View.GONE);
                }
                if (AppUtils.getConfig(this).isHealthEnabled() && (minMaxValues[3] != minMaxValues[2])) {
                    tvCalorieLowerValue.setText((int)minMaxValues[2]+" Cal");
                    tvCalorieUpperValue.setText((int)Math.round(minMaxValues[3])+" Cal");
                    calorieSeekbar.setMax(( ((int)minMaxValues[3] - (int)Math.round(minMaxValues[2]))));
                }
                else{
                    tvCalorie.setVisibility(View.GONE);
                    calorieSeekbar.setVisibility(View.GONE);
                    tvCalorieLowerValue.setVisibility(View.GONE);
                    tvCalorieUpperValue.setVisibility(View.GONE);
                    tvCalorieValue.setVisibility(View.GONE);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void inflateCategoriesInFilter(){
        if (categoryList!=null){

            if(categoryList.size() == 0){
                tvCategory.setVisibility(View.GONE);
                svCategory.setVisibility(View.GONE);
                return;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 20, 10);
            for (String category : categoryList){
                CheckBox categoryCheckBox = new CheckBox(MenuSearchActivity.this);
                categoryCheckBox.setButtonDrawable(R.drawable.radio_selector);
                categoryCheckBox.setText(category);
                llCategory.addView(categoryCheckBox,params);
            }
        }
    }

    private void applyFilters(String searchString){
        if (minMaxValues!=null) {
            if((filteredPriceVal == -1 && priceSeekBar.getProgress() > minMaxValues[0]) || (filteredPriceVal != -1)){
                filteredPriceVal = priceSeekBar.getProgress() + minMaxValues[0];
            }
            if((filteredCalorieVal == -1 && calorieSeekbar.getProgress() > minMaxValues[2]) || (filteredCalorieVal != -1)){
                filteredCalorieVal = calorieSeekbar.getProgress() + minMaxValues[2];
            }
        }
        if (searchString.length() > 0) {
            realmQuery = realm.where(Product.class)
                    .equalTo(Product.VENDOR_ID, vendor.getId())
                    .beginGroup()
                    .contains(Product.NAME, searchString, Case.INSENSITIVE)
                    .or()
                    .contains(Product.DESCRIPTION, searchString, Case.INSENSITIVE)
                    .endGroup();
            int checked = 0;
            for (int i = 0; i < llCategory.getChildCount(); i++) {
                CheckBox cb = (CheckBox) llCategory.getChildAt(i);
                if (cb.isChecked()) {
                    if (checked>0){
                        realmQuery = realmQuery.or().equalTo("category", cb.getText().toString()).contains(Product.NAME, searchString, Case.INSENSITIVE);
                    } else{
                        realmQuery = realmQuery.equalTo("category", cb.getText().toString());
                    }
                    ++checked;
                }
            }
            if (radioVeg.isChecked() || radioNonVeg.isChecked() || radioYes.isChecked() || radioNo.isChecked()) {
                if ((radioVeg.isChecked() || radioNonVeg.isChecked()) && (radioYes.isChecked() || radioNo.isChecked())) {
                    realmQuery = realmQuery
                            .equalTo("isVeg", (radioVeg.isChecked() ? 1 : 0))
                            .equalTo("isMrpItem",(radioYes.isChecked() ? 1 : 0));
                }
                else if (radioVeg.isChecked() || radioNonVeg.isChecked()) {
                    if (!(radioVeg.isChecked() && radioNonVeg.isChecked()) ){
                        realmQuery = realmQuery
                                .equalTo("isVeg", (radioVeg.isChecked() ? 1 : 0));
                    }
                } else {//if (radioYes.isChecked() || radioNo.isChecked())
                    realmQuery = realmQuery
                            .equalTo("isMrpItem",(radioYes.isChecked() ? 1 : 0));
                }
            }

        }
        search(searchString);
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {
            backgroundView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_out));
            backgroundView.setVisibility(View.GONE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        findVendor();
    }

    private void findVendor() {
        vendor = AppUtils.getVendorById(getApplicationContext(), vendorId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.bus.register(this);
        setUpCart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.bus.unregister(this);
    }

    @Override
    public void validateAndAddProduct(Vendor vendor, Product product, boolean isBuffetVendor) {
        MainApplication mainApplication = (MainApplication) getApplication();
        if (!mainApplication.isVendorValid(vendor.getId())) {
            showCancelCartProduct(vendor, product, isBuffetVendor);
        }
    }
    private void navigateToOrderReview() {
        LogoutTask.updateTime();
        finish();
        //Intent intent = new Intent(MenuSearchActivity.this, OrderReviewActivity.class);
        //Intent intent = new Intent(MenuSearchActivity.this, BookmarkPaymentActivity.class);
        Intent intent = new Intent(MenuSearchActivity.this, BookmarkPaymentActivity.class);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
        intent.putExtra("anim", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }


    public void showCancelCartProduct(Vendor vendor, Product product, boolean isBuffet) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CartCancelDialog cartCancelDialog = CartCancelDialog.newInstance(vendor, product, new CartCancelDialog.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Vendor vendor, Product product, boolean isBuffet) {
                MainApplication mainApplication = (MainApplication) getApplication();
                mainApplication.clearOrder();
                //TODO clear order
                if (!product.isConfigurable()) {
                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.copy(product);
                    mainApplication.addProduct(product.clone(), vendor.clone(), orderProduct, occassionId);
//                    int orderQty = mainApplication.getOrderQuantityForProduct(vendor.getId(), product.getId());
//                    MenuActivity activity = (MenuActivity) getActivity();
//                    activity.setUpCart();
//                    if(activity instanceof AddToCardLisenter){
//                        AddToCardLisenter addToCardLisenter = (AddToCardLisenter) activity;
//                        Product productClone = product.clone();
//                        productClone.quantity = orderQty;
//                        ((AddToCardLisenter) activity).addToCart(vendor.clone(), productClone);
//                    }
                    if (rvMenu.getAdapter() != null) {
                        rvMenu.getAdapter().notifyDataSetChanged();
                    }
                } else {
                    showOptionDialog(vendor, product);
                }
            }
        }, isBuffet);
        cartCancelDialog.setCancelable(false);
        fragmentManager.beginTransaction()
                .add(cartCancelDialog, "cart_cancel")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();
    }

    private void showOptionDialog(Vendor vendor, Product product) {

    }

    private void createMenuList(List<Product> productResults) {

        if (productResults.size() > 0) {
            EventUtil.FbEventLog(MenuSearchActivity.this, EventUtil.MENU_SEARCH_SUCCESS, EventUtil.SCREEN_MENU);
            HBMixpanel.getInstance().addEvent(MenuSearchActivity.this, EventUtil.MixpanelEvent.MENU_SEARCH_SUCCESS);
        }
        if (rvMenu.getAdapter() == null) {
             menuListAdapter = new MenuListAdapter(this, productResults,
                    vendor,
                    this, occassionId,false);
            rvMenu.setAdapter(menuListAdapter);
        } else {
            if (rvMenu.getAdapter() instanceof MenuListAdapter) {
                MenuListAdapter menuListAdapter = (MenuListAdapter) rvMenu.getAdapter();
                menuListAdapter.changeProducts(productResults, vendor);
                menuListAdapter.notifyDataSetChanged();
            }
        }
    }
    public void setUpCart() {
        MainApplication mainApplication = (MainApplication) MenuSearchActivity.this.getApplication();
        int cartQty = mainApplication.getTotalOrderCount();
        double cardAmount = mainApplication.getTotalOrderAmount();
        if (cartQty <= 0) {
            if (flFloatingContainer.getVisibility() != View.GONE) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.cart_close);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        flFloatingContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                flFloatingContainer.startAnimation(animation);
            }
        } else {
            if (flFloatingContainer.getVisibility() != View.VISIBLE) {
                flFloatingContainer.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.cart_open);
                flFloatingContainer.startAnimation(animation);
            }
            if (cartQty >= 10) {
                tvCart.setText(String.format("%d Items  ", cartQty));
            } else {
                tvCart.setText(String.format("%d Items ", cartQty));
            }
            tvCartAmount.setText(String.format("₹ %.2f", cardAmount));

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bounce_in);
            tvCartAmount.startAnimation(animation);
            tvCart.startAnimation(animation);
        }

        if(menuListAdapter!=null){
            menuListAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        setUpCart();
    }

    @Subscribe
    public void onRemoveItemCartEvent(RemoveProductFromCart removeProductFromCart) {
        setUpCart();
    }

    @Subscribe
    public void onOrderClearEvent(OrderClear orderClear) {
        if (flFloatingContainer.getVisibility() != View.GONE) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.cart_close);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    flFloatingContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            flFloatingContainer.startAnimation(animation);
        }
    }

    private void search(String searchString){
        if(vendor!=null) {
            if (realmQuery == null) {
                realmQuery = realm.where(Product.class)
                        .equalTo(Product.VENDOR_ID, vendor.getId())
                        .beginGroup()
                        .contains(Product.NAME, searchString, Case.INSENSITIVE)
                        .or()
                        .contains(Product.DESCRIPTION, searchString, Case.INSENSITIVE)
                        .endGroup();
            }
            if (searchString.length() > 0) {
                productRealmResultss = realmQuery.findAll();
            } else {
                productRealmResultss = realm.where(Product.class).equalTo(Product.VENDOR_ID, vendor.getId()).findAll();
            }
            if (filteredPriceVal > -1 || filteredCalorieVal > -1){
                tempProductRealmResults = new ArrayList<>();
                for (Product product : productRealmResultss){
                    if(filteredPriceVal>-1 && filteredCalorieVal > -1){
                        if(product.getPrice()<=filteredPriceVal && product.getCalories()<=filteredCalorieVal){
                            tempProductRealmResults.add(product);
                        }
                    }
                    else if(filteredPriceVal>-1){
                        if(product.getPrice()<=filteredPriceVal){
                            tempProductRealmResults.add(product);
                        }
                    }
                    else{
                        if(product.getCalories()<=filteredCalorieVal){
                            tempProductRealmResults.add(product);
                        }
                    }
                }
                createMenuList(tempProductRealmResults);
            } else {
                createMenuList(productRealmResultss);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }
}
