package com.hungerbox.customer.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.MainApplication;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FeatureSearch;
import com.hungerbox.customer.model.FeatureSearchVendorMenu;
import com.hungerbox.customer.model.MenuDetailSearchResponse;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.SearchInputModel;
import com.hungerbox.customer.model.SearchMenuDetail;
import com.hungerbox.customer.model.SearchVendorMenuFilter;
import com.hungerbox.customer.model.SearchVendorMenuMrpFilter;
import com.hungerbox.customer.model.SearchVendorMenuVegFilter;
import com.hungerbox.customer.model.SearchVendorModel;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.AddToCardLisenter;
import com.hungerbox.customer.order.adapter.GlobalSearchPagerAdapter;
import com.hungerbox.customer.order.fragment.SearchDishesFragment;
import com.hungerbox.customer.order.fragment.SearchVendorFragment;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ListToMapUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class GlobalSearchActivity extends ParentActivity implements AddToCardLisenter {

    private AppCompatImageView btFilter;
    private AppCompatImageView btClear;
    private EditText etSearch;
    private AppCompatImageView ivBack;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout filterSheet;
    public long occasionId;
    public long locationId;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> tabs;
    private GlobalSearchPagerAdapter adapter;
    private FeatureSearch featureSearch;
    Realm realm;
    public String searchInputText;
    public TextView tvCart,tvExtraCharges, tvCartAmount;
    protected RelativeLayout flFloatingContainer;
    private AppCompatButton btCheckout;
    private Button btFilterOk;
    private Button btClearFilter;
    private View backgroundView;
    private AppCompatImageView ivFilterClose;
    private AppCompatRadioButton radioYes, radioNo;
    private CheckBox radioVeg, radioNonVeg;

    private ArrayList<Vendor> listOfVendors;
    private ArrayList<Object> listOfProducts;
    public ArrayList<Long> menuIdList;
    public List<Product> productArrayList;
    public static Map<Long,Product> mapOfProducts;
    private SearchInputModel searchPayload;
    private int foundDishQuantity = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        setContentView(R.layout.activity_global_search);
        filterSheet = findViewById(R.id.filter_bottom_sheet);
        btClear = findViewById(R.id.iv_clear);
        etSearch = findViewById(R.id.et_search);
        ivBack = findViewById(R.id.iv_back);
        tabLayout = findViewById(R.id.tl_vendors);
        viewPager = findViewById(R.id.vp_pager);

        btFilter = findViewById(R.id.bt_filter);
        ivFilterClose = findViewById(R.id.iv_filter_close);
        btFilterOk = findViewById(R.id.bt_filter_ok);
        btClearFilter = findViewById(R.id.bt_filter_clear);

        radioVeg = findViewById(R.id.cb_veg);
        radioNonVeg = findViewById(R.id.cb_nonveg);
        radioYes = findViewById(R.id.radio_yes);
        radioNo = findViewById(R.id.radio_no);

        backgroundView = findViewById(R.id.transparent_view);
        backgroundView.setVisibility(View.GONE);

        flFloatingContainer = findViewById(R.id.fl_cart_container);
        tvCart = findViewById(R.id.tv_cart);
        tvExtraCharges = findViewById(R.id.tv_extra_charge_label);
        tvCartAmount = findViewById(R.id.tv_order_amount);
        btCheckout = findViewById(R.id.bt_checkout);
        bottomSheetBehavior = BottomSheetBehavior.from(filterSheet);

        occasionId = getIntent().getLongExtra(ApplicationConstants.OCASSION_ID,0);
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        initClickListeners();
        initViews();

        AppUtils.showKeyboard(this, etSearch);

    }

    private void initViews(){
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
        btFilter.setOnClickListener(v -> {
            AppUtils.hideKeyboard(GlobalSearchActivity.this,null);
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        backgroundView.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        ivFilterClose.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        btFilterOk.setOnClickListener(v -> applyFilters());
        btClearFilter.setOnClickListener(v -> {
            radioNonVeg.setChecked(false);
            radioVeg.setChecked(false);
            radioYes.setChecked(false);
            radioNo.setChecked(false);
        });
        listOfVendors = new ArrayList<>();
        listOfProducts = new ArrayList<>();
        menuIdList = new ArrayList<>();
        tabs = new ArrayList<>();
        tabs.add(ApplicationConstants.GlobalSearch.TAB_DISH);
        tabs.add(ApplicationConstants.GlobalSearch.TAB_VENDOR);

        adapter = new GlobalSearchPagerAdapter(getSupportFragmentManager(),this,tabs);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);

        flFloatingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartButtonClickAction();
            }
        });
        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartButtonClickAction();
            }
        });
        adjustTabSpacing();
    }
    private void applyFilters(){
        searchPayload = new SearchInputModel(locationId,occasionId,"");
        switch (evaluateFilter()){
            case 1: {
                if (radioVeg.isChecked() && radioNonVeg.isChecked()){
                    //no filter
                } else {
                    searchPayload.getFilters().setVendorMenu(new SearchVendorMenuVegFilter(radioVeg.isChecked() ? 1 : 0));
                }
            }
            break;
            case 2: {
                searchPayload.getFilters().setVendorMenu(new SearchVendorMenuMrpFilter(radioYes.isChecked()?1:0));
            }
            break;
            case 3: {
                if (radioVeg.isChecked() && radioNonVeg.isChecked()){
                    searchPayload.getFilters().setVendorMenu(new SearchVendorMenuMrpFilter(radioYes.isChecked()?1:0));
                } else {
                    searchPayload.getFilters().setVendorMenu(new SearchVendorMenuFilter(radioVeg.isChecked() ? 1 : 0, radioYes.isChecked() ? 1 : 0));
                }
            }
            break;
        }

        backgroundView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_fade_out));
        backgroundView.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (etSearch.getText().toString()!=null && etSearch.getText().toString().trim().length()>2){
            search(etSearch.getText().toString().trim());
        }
    }
    private int evaluateFilter(){
        if (!radioVeg.isChecked() && !radioNonVeg.isChecked() && !radioYes.isChecked() && !radioNo.isChecked()){
            return 0;
        }
        if ((radioVeg.isChecked() || radioNonVeg.isChecked()) && !radioYes.isChecked() && !radioNo.isChecked()){
            return 1;
        }
        if (!radioVeg.isChecked() && !radioNonVeg.isChecked() && (radioYes.isChecked()  || radioNo.isChecked())){
            return 2;
        }
        return 3; //checked from each filter category
    }

    private void adjustTabSpacing(){
        //for spacing between tabs
//        for(int i=0; i < tabLayout.getTabCount(); i++) {
//            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
//            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
//            p.setMargins(0, 0, 50, 0);
//            tab.requestLayout();
//        }
        try {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 50, 0);
            tab.requestLayout();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void cartButtonClickAction(){
        navigateToOrderReview();
    }
    // search() -> processResponse -> getVendorList(),getProductList() -> getMenuDetails() -> setUpViewPager()
    private void search(String searchText){
        String url = UrlConstant.FEATURE_SEARCH;
        SimpleHttpAgent<FeatureSearch> searchSimpleHttpAgent = new SimpleHttpAgent<FeatureSearch>(this, url,
                new ResponseListener<FeatureSearch>() {
                    @Override
                    public void response(final FeatureSearch responseObject) {

                        try{
                            if (responseObject!=null){
                                tabLayout.setVisibility(View.VISIBLE);
                                featureSearch = responseObject;
                                searchInputText = searchText;
                                processResponse();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            AppUtils.showToast("Some error occurred", true, 0);
                        }

                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                    AppUtils.showToast("Please check your network.", true, 0);
                } else {
                    if (error != null && !error.equals("")) {
                        AppUtils.showToast(error, true, 0);
                    } else {
                        AppUtils.showToast("Unable to fetch", true, 0);
                    }
                }
            }
        }, FeatureSearch.class);
        if (searchPayload!=null) {
            searchPayload.setQueryString(searchText);
        } else{
            searchPayload = new SearchInputModel(locationId,occasionId,searchText);
        }
        searchSimpleHttpAgent.post(searchPayload, new HashMap<String, JsonSerializer>());
    }
    private void processResponse(){
        getVendorList();
        getProductList();
        getMenuDetails();

    }
    private ArrayList<Vendor> getVendorList(){
        listOfVendors.clear();
        if (featureSearch.getData()!=null && featureSearch.getData().getVendor()!=null) {
            for (SearchVendorModel searchVendorModel : featureSearch.getData().getVendor()) {
                Vendor vendor = AppUtils.getVendorById(getApplicationContext(), searchVendorModel.getVendorId());
                if (vendor!=null && ((vendor.isVendingMachine() && AppUtils.getConfig(GlobalSearchActivity.this).isVendingMachineEnabled())
                        ||!vendor.isVendingMachine())) {
                    listOfVendors.add(vendor);
                }
            }
        }
        return listOfVendors;
    }
//    private ArrayList<Object> getProductList(){
//        listOfProducts = new ArrayList<>();
//        if (featureSearch.getData()!=null && featureSearch.getData().getVendorMenu()!=null && featureSearch.getData().getVendorMenu().size()>0) {
//            foundDishQuantity = featureSearch.getData().getVendorMenu().size();
//            long lastVendorId = featureSearch.getData().getVendorMenu().get(0).getVendorId();
//            if (firstVendor!=null){
//                listOfProducts.add(firstVendor);
//            }
//
//            for (int i = 0 ; i < featureSearch.getData().getVendorMenu().size() ; i++) {
//                FeatureSearchVendorMenu searchVendorMenu = featureSearch.getData().getVendorMenu().get(i);
//                menuIdList.add(searchVendorMenu.getMenuId());
//                if (searchVendorMenu.getVendorId() == lastVendorId) { // if item is of same vendor
//                    listOfProducts.add(searchVendorMenu);
//                } else{ // different vendor item begins
//                    lastVendorId = searchVendorMenu.getVendorId();
//                    if (vendor!=null){
//                        listOfProducts.add(vendor);
//                    }
//                    listOfProducts.add(searchVendorMenu);
//                }
//            }
//            listOfProducts.add("empty_space");
//        }
//        return listOfProducts;
//    }
    private ArrayList<Object> getProductList(){
        listOfProducts = new ArrayList<>();
        if (featureSearch.getData()!=null && featureSearch.getData().getVendorMenu()!=null && featureSearch.getData().getVendorMenu().size()>0) {
            long lastVendorId = -1 ;
            Vendor firstVendor = null;
            foundDishQuantity = 0;
            int i;
            for (i = 0 ; i < featureSearch.getData().getVendorMenu().size() ; i++){
                lastVendorId = featureSearch.getData().getVendorMenu().get(i).getVendorId();
                firstVendor = AppUtils.getVendorById(getApplicationContext(),lastVendorId);
                if (firstVendor!=null && ((firstVendor.isVendingMachine() && AppUtils.getConfig(GlobalSearchActivity.this).isVendingMachineEnabled())
                        ||!firstVendor.isVendingMachine())){
                    listOfProducts.add(firstVendor);
                    break;
                }
            }
            if (firstVendor!=null){
                for (int j = i ; j < featureSearch.getData().getVendorMenu().size() ; j++){
                    FeatureSearchVendorMenu searchVendorMenu = featureSearch.getData().getVendorMenu().get(j);
                    if (searchVendorMenu.getVendorId() == lastVendorId) { // if item is of same vendor
                        menuIdList.add(searchVendorMenu.getMenuId());
                        listOfProducts.add(searchVendorMenu);
                        foundDishQuantity++;
                    } else{ // different vendor item begins
                        Vendor vendor = AppUtils.getVendorById(getApplicationContext(),searchVendorMenu.getVendorId());
                        if (vendor!=null && ((vendor.isVendingMachine() && AppUtils.getConfig(GlobalSearchActivity.this).isVendingMachineEnabled())
                                ||!vendor.isVendingMachine())){
                            lastVendorId = vendor.getId();
                            listOfProducts.add(vendor);
                            menuIdList.add(searchVendorMenu.getMenuId());
                            listOfProducts.add(searchVendorMenu);
                            foundDishQuantity++;

                        };
                    }
                }
                listOfProducts.add("empty_space");
            } else{
                foundDishQuantity = -1;
            }
        }
        return listOfProducts;
    }
    private void getMenuDetails(){
        try {
            String url = UrlConstant.FEATURE_MENU_DETAIL;
            SimpleHttpAgent<MenuDetailSearchResponse> searchSimpleHttpAgent = new SimpleHttpAgent<MenuDetailSearchResponse>(this, url,
                    new ResponseListener<MenuDetailSearchResponse>() {
                        @Override
                        public void response(final MenuDetailSearchResponse responseObject) {

                            try {
                                if (responseObject != null && responseObject.getData() != null) {
                                    productArrayList = responseObject.getData();
                                    mapOfProducts = new ListToMapUtil().convertToMap(productArrayList);
                                    runOnUiThread(() -> {
                                        setUpViewPager();
                                    });
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new ContextErrorListener() {
                @Override
                public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        AppUtils.showToast("Please check your network.", true, 0);
                    } else {
                        if (error != null && !error.equals("")) {
                            AppUtils.showToast(error, true, 0);
                        } else {
                            AppUtils.showToast("Unable to fetch", true, 0);
                        }
                    }
                }
            }, MenuDetailSearchResponse.class);
            long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
            SearchMenuDetail payload = new SearchMenuDetail(menuIdList, occasionId, locationId);
            searchSimpleHttpAgent.post(payload, new HashMap<String, JsonSerializer>());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setUpViewPager(){
        if (adapter!=null){
            try {

                adapter.setData(listOfVendors, listOfProducts,foundDishQuantity);
                adapter.notifyDataSetChanged();
                adjustTabSpacing();
                if (listOfProducts.size() == 0 && listOfVendors.size()>0){
                    viewPager.setCurrentItem(1);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } else{
            adapter = new GlobalSearchPagerAdapter(getSupportFragmentManager(),this,tabs);
            adapter.setData(listOfVendors,listOfProducts,foundDishQuantity);
            adjustTabSpacing();
            adapter.notifyDataSetChanged();
        }
    }

    public void startFragmentShimmer(){
        if (adapter!=null){
            if (adapter.getFragment(0) instanceof SearchDishesFragment){
                ((SearchDishesFragment) adapter.getFragment(0)).setLoading();
            }
            if (adapter.getFragment(1) instanceof SearchVendorFragment){
                ((SearchVendorFragment) adapter.getFragment(1)).setLoading();
            }
        }
    }

    private void initClickListeners(){

        btClear.setOnClickListener(v -> {
            etSearch.setText("");
        });
        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
        ObservableOnSubscribe<String> searchObservable = new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        emitter.onNext(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        };
        try {
            Observable.create(searchObservable)
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .doOnError(error-> System.err.println("error while search " + error.getMessage()))
                    .filter(s -> s.length() > 2)
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startFragmentShimmer();
                                }
                            });
                            search(s);
                        }
                    });
        } catch(Exception e){
            e.printStackTrace();
        }
//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length()>2) {
//                    startFragmentShimmer();
//                    search(s.toString());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

    }

    public void setUpCart() {
        MainApplication mainApplication = (MainApplication) GlobalSearchActivity.this.getApplication();
        int cartQty = mainApplication.getTotalOrderCount();
        double cardAmount = mainApplication.getTotalOrderAmount();
        Vendor vendor = mainApplication.getCart().getVendor();
        if (vendor!=null){
            if (vendor.getDeliveryCharge()>0
                    || vendor.getConatainerCharge()>0
                    || vendor.getServiceTax()>0
                    || vendor.getTax()>0
                    || vendor.getCgst()>0
                    || vendor.getSgst()>0
                    || AppUtils.getConfig(mainApplication).isConvenience_charge_applicable()){
                tvExtraCharges.setVisibility(View.VISIBLE);
            } else{
                tvExtraCharges.setVisibility(View.GONE);
            }
        }
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
            if (cartQty > 1) {
                tvCart.setText(String.format("%d Items  ", cartQty));
            } else {
                tvCart.setText(String.format("%d Item ", cartQty));
            }
            tvCartAmount.setText(String.format("₹ %.2f", cardAmount));

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bounce_in);
            tvCartAmount.startAnimation(animation);
            tvCart.startAnimation(animation);
        }
    }
    private void navigateToOrderReview() {
        LogoutTask.updateTime();
        Intent intent = new Intent(this, BookmarkPaymentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
        intent.putExtra("anim", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        setUpCart();
    }

    @Override
    public void addToCart(Vendor vendor, Product product) {
        setUpCart();
    }

    @Override
    public void removeFromCart(Vendor vendor, Product product) {
        setUpCart();
    }
}
