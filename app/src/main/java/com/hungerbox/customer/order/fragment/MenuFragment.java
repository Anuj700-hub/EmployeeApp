package com.hungerbox.customer.order.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.textfield.TextInputEditText;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.BookmarkAddRemoveEvent;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.Category;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.MenuHeader;
import com.hungerbox.customer.model.MenuOptionResponse;
import com.hungerbox.customer.model.MenuSwitch;
import com.hungerbox.customer.model.Nutrition;
import com.hungerbox.customer.model.NutritionItem;
import com.hungerbox.customer.model.OptionItem;
import com.hungerbox.customer.model.OptionItemResponse;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.ProductResponse;
import com.hungerbox.customer.model.SubProduct;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.VendorResponse;
import com.hungerbox.customer.navmenu.UrlNavigationHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.AddToCardLisenter;
import com.hungerbox.customer.order.activity.MenuActivity;
import com.hungerbox.customer.order.activity.MenuSearchActivity;
import com.hungerbox.customer.order.adapter.ListAdapter;
import com.hungerbox.customer.order.adapter.MenuListAdapter;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.ProductVegOnlySwitch;
import com.hungerbox.customer.order.listeners.VegNonVegSwitchListener;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.ACTION_ADD_ITEM;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.ACTION_BOOKMARK_ITEM;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.ACTION_SCROLL_CATEGORY;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.ACTION_SCROLL_ITEM;

public class MenuFragment extends Fragment implements CartCancelDialog.OnFragmentInteractionListener, VendorValidationListener {

    public static boolean IS_VEG_ONLY = false;
    public final long CATEGORY_RECOMMENDED_ID = 911;
    TextView tvVendorRating,tvVendorserveType,tvVendorDesc;
    TabLayout menuTabLayout;
    LinearLayout llSearchListContainer;
    RealmResults<Category> realmResults;
    Vendor vendor;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    RelativeLayout vendorInfoContainer;
    Toolbar toolbar;
    List<Product> products = new ArrayList<>();
    RecyclerView rvSearchList,rvVendingTrayItem;
    RecyclerView rvMenuList;
    ArrayList<String> categoriesToShow = new ArrayList<>();
    ArrayList<Object> productListWithHeaders = new ArrayList<>();
    HashMap<Integer,Integer> headerIndexes = new HashMap<>();
    HashMap<Integer,Integer> productTabIndexes = new HashMap<>();
    LinearLayoutManager layoutManager;
    ListAdapter menuListAdapter;
    MenuListAdapter trayListAdapter;
    Context context;
    LinkedHashMap<String,ArrayList<Product>> categoryProductHashMap = new LinkedHashMap<>();
    VegNonVegSwitchListener vegNonSwitchListener;
    private final String CATEGORY_RECOMMENDED = "Recommended";
    private final int REQUEST_SEARCH = 123;
    private HashMap<Long,Category> categoryIdMap;
    private HashMap<String,Long> categoryNameIdMap;
    private boolean fromNotification = false;
    private Long notificationItemId = -1l;
    private Long notificationCategoryId = -1l;
    private String notificationCategoryName = "";
    private String itemCategoryName = "";
    private boolean categoryFoundInLoop = false;
    private boolean itemFoundInLoop = false;
    private boolean categoryToBeFound = false;
    private boolean itemToBeFound = false;
    private String action = "";
    private int approxItemStartPos = -1;
    private int exactCategoryPosition = -1;
    private int approxItemEndPos = -1;

    Realm realm;
    private long vendorId, locationId;
    private String vendorName, location;
    private long occasionId;
    private OnFragmentInteractionListener mListener;
    private TextView tvNoItemsFound;
    private Button btnCallToOrder;
    private ImageView ivBack;
    private boolean isRunning = false;
    ImageView vendorImage;
    ImageView ivOverlay;
    private double minPrice = 0, maxPrice = 0, minCalorie = 0, maxCalorie = 0;
    private TextView tvTitle,tvTrayHint;
    public EditText etTrayNo;
    CardView cvVendingTray;


    //filter related views
    private EditText etSearch;
    private AppCompatImageView ivClearSearchText;
    LinearLayout llCategory;
    private AppCompatImageView btFilter;
    private Button btFilterOk;
    private Button btClearFilter;
    private View backgroundView;
    private AppCompatImageView ivFilterClose;
    private AppCompatRadioButton radioYes, radioNo;
    private CheckBox radioVeg, radioNonVeg;
    private LinearLayout filterSheet;
    private ConstraintLayout clFilterParent;
    AppCompatImageView btClear;
    private BottomSheetBehavior bottomSheetBehavior;
    private RealmQuery<Product> realmQuery;
    private SeekBar priceSeekBar, calorieSeekbar;
    private TextView tvPrice,tvCalorie, tvCategory;
    private HorizontalScrollView svCategory;
    private TextView tvPriceValue,tvCalorieValue;
    private TextView tvPriceUpperValue,tvPriceLowerValue,tvCalorieUpperValue,tvCalorieLowerValue;
    private double[] minMaxValues;//[minPrice,maxPrice,minCalorie,maxCalorie]
    double filteredCalorieVal = -1;
    double filteredPriceVal = -1;
    private LinearLayout.LayoutParams categoryCbParams;
    private boolean vegOnlySwitchInvisible;
    String tagForApiRequest = "";

    public MenuFragment() {
    }

    public static MenuFragment newInstance(long vendorId, String vendorName, long ocassionId, Context context) {
        MenuFragment fragment = new MenuFragment();
        fragment.vendorId = vendorId;
        fragment.vendorName = vendorName;
        fragment.occasionId = ocassionId;
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isRunning = true;
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        if(getActivity() != null){
            tagForApiRequest = ((MenuActivity) getActivity()).getApiTag();
        }

        initViewComponents(view);

        tvTitle.setSelected(true);
        getNotificationValues();

        EventUtil.FbEventLog(getActivity(), EventUtil.SCREEN_OPEN_MENU, EventUtil.SCREEN_HOME);
        HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.SCREEN_OPEN_MENU);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtil.FbEventLog(getActivity(), EventUtil.MENU_BACK, EventUtil.SCREEN_MENU);
                HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.MENU_CLICK_BACK);


                if(Build.VERSION.SDK_INT > 24){
                    getActivity().supportFinishAfterTransition();
                }else{
                    getActivity().finish();
                }
            }
        });
        vendor = AppUtils.getVendorById(getActivity(), vendorId);
        if (vendor != null) {
            if (vendor.getRating() <= 0) {
                tvVendorRating.setVisibility(View.INVISIBLE);
            }
            tvVendorRating.setText(vendor.getRatingText());
            tvVendorDesc.setText(vendor.getDesc());


            setVendorImage();

            tvTitle.setText(vendor.getVendorName());
            if (vendor.isRestaurant())
                tvVendorserveType.setText("Restaurant");
            else {
                tvVendorserveType.setText(AppUtils.getConfig(getContext()).getOn_campus());
            }

            if (vendor.getDesc().length() > 1)
                tvVendorDesc.setText(vendor.getDesc());

            if(vendor.isVendingMachine()){
                cvVendingTray.setVisibility(View.VISIBLE);
            }

            vendor = vendor.clone();
        } else {
            AppUtils.showToast("Something went wrong.", true, 0);
            getActivity().finish();
        }

        btnCallToOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + vendor.getVendorPhone()));
                startActivity(intent);
            }
        });

        IS_VEG_ONLY = false;
        vegOnlySwitchInvisible = false;

        location = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
        getCategoriesFor(vendorId, vendorName);

        vegNonSwitchListener = new VegNonVegSwitchListener() {
            @Override
            public void onUpdate(boolean isChecked) {
                LogoutTask.updateTime();
                try {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    map.put(CleverTapEvent.PropertiesNames.getVendor_id(), vendorId);
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getVeg_item_click(), map, getActivity());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isChecked) {
                    EventUtil.FbEventLog(getActivity(), EventUtil.MENU_CLICK_VEGONLY, EventUtil.SCREEN_MENU);
                    HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.MENU_CLICK_VEGONLY);
                    checkVegFilter(true);
                    showVegOnly();
                } else {
                    checkVegFilter(false);
                    showAll();
                }
            }
        };
        initFilterViews();
        populateList();
        setBannerClickListener();
        hideVendorImageLayover();

        return view;
    }
    private void initViewComponents(View view){
        rvSearchList = view.findViewById(R.id.rv_search_list);
        llSearchListContainer = view.findViewById(R.id.serach_list_conatiner);
        rvMenuList = view.findViewById(R.id.rv_menu_list);
        menuTabLayout = view.findViewById(R.id.tl_menu);
        tvNoItemsFound = view.findViewById(R.id.tv_no_menu_items_found);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_container);
        appBarLayout = view.findViewById(R.id.my_appbar_container);
        toolbar = view.findViewById(R.id.toolbar_menu);
        vendorInfoContainer = view.findViewById(R.id.ll_vendor_info_container);
        ivBack = view.findViewById(R.id.iv_back);
        tvVendorRating = view.findViewById(R.id.tv_vendor_rating);
        llSearchListContainer.setVisibility(View.GONE);
        btnCallToOrder = getActivity().findViewById(R.id.btn_call_to_order);
        tvTitle = view.findViewById(R.id.tv_toolbar_title);
        tvVendorserveType = view.findViewById(R.id.tv_vendor_serve_type);
        tvVendorDesc = view.findViewById(R.id.tv_vendor_desc);
        vendorImage = view.findViewById(R.id.vendor_image);
        ivOverlay = view.findViewById(R.id.iv_overlay);
        rvVendingTrayItem = view.findViewById(R.id.rv_vending_tray_item);
        etTrayNo = view.findViewById(R.id.et_tray_no);
        cvVendingTray  = view.findViewById(R.id.cv_vending_list);
        tvTrayHint = view.findViewById(R.id.tv_tray_hint);

        //filter and search related
        etSearch = view.findViewById(R.id.et_search);
        ivClearSearchText = view.findViewById(R.id.iv_clear);
        btFilter = view.findViewById(R.id.bt_filter);
        ivFilterClose = view.findViewById(R.id.iv_filter_close);
        btFilterOk = view.findViewById(R.id.bt_filter_ok);
        btClearFilter = view.findViewById(R.id.bt_filter_clear);

        radioVeg = view.findViewById(R.id.cb_veg);
        radioNonVeg = view.findViewById(R.id.cb_nonveg);
        radioYes = view.findViewById(R.id.radio_yes);
        radioNo = view.findViewById(R.id.radio_no);

        llCategory = view.findViewById(R.id.ll_category);
        priceSeekBar = view.findViewById(R.id.sb_price);
        calorieSeekbar = view.findViewById(R.id.sb_calorie);
        tvPrice = view.findViewById(R.id.tv_price);
        tvPriceValue = view.findViewById(R.id.tv_price_value);
        tvPriceUpperValue = view.findViewById(R.id.tv_price_upper_value);
        tvPriceLowerValue = view.findViewById(R.id.tv_price_lower_value);
        tvCalorie = view.findViewById(R.id.tv_calorie);
        tvCategory = view.findViewById(R.id.tv_category);
        tvCalorieValue = view.findViewById(R.id.tv_calorie_value);
        tvCalorieUpperValue = view.findViewById(R.id.tv_calorie_upper_value);
        tvCalorieLowerValue = view.findViewById(R.id.tv_calorie_lower_value);

        backgroundView = view.findViewById(R.id.transparent_view);
        clFilterParent = view.findViewById(R.id.cl_filter_parent);

        filterSheet = view.findViewById(R.id.filter_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(filterSheet);

        backgroundView.setVisibility(View.GONE);

        etTrayNo.addTextChangedListener(trayTextWatcher);
    }

    private void initFilterViews(){
        try {
            etSearch.addTextChangedListener(new TextWatcher() {
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
            });
            ivClearSearchText.setOnClickListener((v)->{
                etSearch.setText("");
                applyFilters("");
            });
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
                            backgroundView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_fade_in));
                        }
                        break;
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            backgroundView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_fade_out));
                            backgroundView.setVisibility(View.GONE);
                        }
                        break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
            clFilterParent.setOnClickListener(v ->{
                //prevent background click
            });
            btFilter.setOnClickListener(v -> {
                AppUtils.hideKeyboard(getActivity(),null);
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    liftUpFilterSheet();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
            categoryCbParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            categoryCbParams.setMargins(0, 10, 20, 10);
            backgroundView.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
            ivFilterClose.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
            btFilterOk.setOnClickListener(view -> {
                if (!vegOnlySwitchInvisible) {
                    hideMenuScreenVegSwitch(etSearch.getText().toString().trim());
                }else {
                    applyFilters(etSearch.getText().toString().trim());
                }
            });
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
                if (!vegOnlySwitchInvisible) {
                    hideMenuScreenVegSwitch(null);
                }
            });
            priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    tvPriceValue.setText("₹ " + (progress+(int) minPrice));
                    int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
                    int thumbPos = seekBar.getPaddingLeft() + width * seekBar.getProgress() / seekBar.getMax();

                    tvPriceValue.measure(0, 0);
                    int txtW = tvPriceValue.getMeasuredWidth();
                    int delta = txtW / 2;
                    tvPriceValue.setX(seekBar.getX() + thumbPos - delta);
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

                    tvCalorieValue.setText((progress+(int) minCalorie)+" Cal");
                    int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
                    int thumbPos = seekBar.getPaddingLeft() + width * seekBar.getProgress() / seekBar.getMax();

                    tvCalorieValue.measure(0, 0);
                    int txtW = tvCalorieValue.getMeasuredWidth();
                    int delta = txtW / 2;
                    tvCalorieValue.setX(seekBar.getX() + thumbPos - delta);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void liftUpFilterSheet(){
        if (getActivity() != null && getActivity() instanceof MenuActivity){
            if (((MenuActivity) getActivity()).flFloatingContainer.getVisibility() == View.VISIBLE){
                filterSheet.setPadding(0,0,0,((MenuActivity) getActivity()).flFloatingContainer.getHeight()-20);
            } else{
                filterSheet.setPadding(0,0,0,0);
            }
        }
    }
    private void checkVegFilter(boolean isChecked){
        if (radioVeg!=null){
            radioVeg.setChecked(isChecked);
        }
    }
    public void hideMenuScreenVegSwitch(String searchText){
        rvMenuList.scrollToPosition(0);
        IS_VEG_ONLY = false;
        new Handler().postDelayed(() -> {
            RecyclerView.ViewHolder holder = rvMenuList.findViewHolderForAdapterPosition(0);
            if (holder!=null && holder instanceof ProductVegOnlySwitch){
                ((ProductVegOnlySwitch) holder).switchVegOnly.setVisibility(View.INVISIBLE);
                vegOnlySwitchInvisible = true;
                IS_VEG_ONLY = false;
                if (searchText!=null) {
                    applyFilters(searchText);
                }
            }
        },500);
    }
    private void inflateSingleCategoryInFilter(String category){
        if (llCategory!=null){
            CheckBox categoryCheckBox = new CheckBox(getContext());
            categoryCheckBox.setButtonDrawable(R.drawable.radio_selector);
            categoryCheckBox.setText(category);
            llCategory.addView(categoryCheckBox,categoryCbParams);
        }
    }

    private void setFilterMinMaxValues(){
        //seek-bar labels and min - max values


            if (AppUtils.getConfig(getContext()).getCompany_type()!=null && AppUtils.getConfig(getContext()).getCompany_type().equalsIgnoreCase(ApplicationConstants.COMP_TYPE_CASH_N_CARRY) && (maxPrice != minPrice)) {
                tvPriceLowerValue.setText("₹ " + (int)minPrice);
                tvPriceUpperValue.setText("₹ " + (int)Math.round(maxPrice));
                priceSeekBar.setMax(( ((int)maxPrice - (int)Math.round(minPrice))));
            }
            else{
                tvPrice.setVisibility(View.GONE);
                priceSeekBar.setVisibility(View.GONE);
                tvPriceLowerValue.setVisibility(View.GONE);
                tvPriceUpperValue.setVisibility(View.GONE);
                tvPriceValue.setVisibility(View.GONE);
            }
            if (AppUtils.getConfig(getContext()).isHealthEnabled() && (maxCalorie != minCalorie)) {
                tvCalorieLowerValue.setText((int)minCalorie+" Cal");
                tvCalorieUpperValue.setText((int)Math.round(maxCalorie)+" Cal");
                calorieSeekbar.setMax(( ((int)maxCalorie - (int)Math.round(minCalorie))));
            }
            else{
                tvCalorie.setVisibility(View.GONE);
                calorieSeekbar.setVisibility(View.GONE);
                tvCalorieLowerValue.setVisibility(View.GONE);
                tvCalorieUpperValue.setVisibility(View.GONE);
                tvCalorieValue.setVisibility(View.GONE);
            }
    }

    private void applyFilters(String searchString){
        if((filteredPriceVal == -1 && priceSeekBar.getProgress() > minPrice) || (filteredPriceVal != -1)){
            filteredPriceVal = priceSeekBar.getProgress() + minPrice;
        }
        if((filteredCalorieVal == -1 && calorieSeekbar.getProgress() > minCalorie) || (filteredCalorieVal != -1)){
            filteredCalorieVal = calorieSeekbar.getProgress() + minCalorie;
        }
        if (searchString.length() > 0) {
            realmQuery = realm.where(Product.class)
                    .equalTo(Product.VENDOR_ID, vendor.getId())
                    .beginGroup()
                    .contains(Product.NAME, searchString, Case.INSENSITIVE)
                    .or()
                    .contains(Product.DESCRIPTION, searchString, Case.INSENSITIVE)
                    .endGroup();
        } else{
            realmQuery = realm.where(Product.class)
                    .equalTo(Product.VENDOR_ID, vendor.getId());
        }
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
                if(radioVeg.isChecked() && radioNonVeg.isChecked()){
                    realmQuery = realmQuery
                            .equalTo("isMrpItem",(radioYes.isChecked() ? 1 : 0));
                } else {
                    realmQuery = realmQuery
                            .equalTo("isVeg", (radioVeg.isChecked() ? 1 : 0))
                            .equalTo("isMrpItem", (radioYes.isChecked() ? 1 : 0));
                }
            }
            else if (radioVeg.isChecked() || radioNonVeg.isChecked()) {
                if (!(radioVeg.isChecked() && radioNonVeg.isChecked()) ){
                    realmQuery = realmQuery
                            .equalTo("isVeg", (radioVeg.isChecked() ? 1 : 0));
                }
            } else if (radioYes.isChecked() || radioNo.isChecked()){
                realmQuery = realmQuery
                        .equalTo("isMrpItem",(radioYes.isChecked() ? 1 : 0));
            }
        }

        filterPriceAndCal(realmQuery.findAll());

    }

    private void filterPriceAndCal(List<Product> productRealmResults){
        ArrayList<Product> productFilterResult;

        if (filteredPriceVal > -1 || filteredCalorieVal > -1){
            productFilterResult = new ArrayList<>();
            for (Product product : productRealmResults){
                if(filteredPriceVal>-1 && filteredCalorieVal > -1){
                    if(product.getPrice()<=filteredPriceVal && product.getCalories()<=filteredCalorieVal){
                        productFilterResult.add(product);
                    }
                }
                else if(filteredPriceVal>-1){
                    if(product.getPrice()<=filteredPriceVal){
                        productFilterResult.add(product);
                    }
                }
                else{
                    if(product.getCalories()<=filteredCalorieVal){
                        productFilterResult.add(product);
                    }
                }
            }
            storeAndUpdateUi(productFilterResult,false);
        } else {
            storeAndUpdateUi(productRealmResults,false);
        }
        dismissBottomSheet();
    }

    private void dismissBottomSheet(){
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {
            backgroundView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_fade_out));
            backgroundView.setVisibility(View.GONE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void hideVendorImageLayover(){
        if (AppUtils.isSocialDistancingActive(null) && vendor.isCoronaSafe()){
           ivOverlay.setVisibility(View.GONE);
           tvVendorserveType.setVisibility(View.GONE);
           tvVendorRating.setVisibility(View.GONE);
           tvVendorDesc.setVisibility(View.GONE);
        }
    }

    private void setBannerClickListener(){
        if (AppUtils.isSocialDistancingActive(null) && AppUtils.getConfig(getActivity()).getSocial_distancing().getInfo_url() != null && !AppUtils.getConfig(getActivity()).getSocial_distancing().getInfo_url().trim().equalsIgnoreCase("") && vendor.isCoronaSafe()){
            ivOverlay.setOnClickListener((v)->{bannerClickListener();});
            vendorImage.setOnClickListener((v)->{bannerClickListener();});
        }
    }

    private void bannerClickListener(){
        if(!vendor.getBannerImage().trim().equalsIgnoreCase(""))
            return;

        if (AppUtils.getConfig(context).getSocial_distancing() != null && AppUtils.getConfig(context).getSocial_distancing().getInfo_url() !=null && !AppUtils.getConfig(context).getSocial_distancing().getInfo_url().trim().equalsIgnoreCase("")) {
            UrlNavigationHandler urlNavigationHandler = new UrlNavigationHandler(getActivity());
            Intent intent = urlNavigationHandler.getUrlNavPath(AppUtils.getConfig(getActivity()).getSocial_distancing().getInfo_url());
            if (intent != null) {
                startActivity(intent);
            }
        }
    }

    private void handleNotificationNavigation(){
        try {

            if (fromNotification) {

                if (!action.isEmpty()) {
                    switch (action) {
                        case ACTION_ADD_ITEM: {
                            int itemPosition = findMenuItemPositionById("item");
                            if (itemPosition>-1){

                                final Handler handler1 = new Handler();
                                final Handler handler2 = new Handler();
                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        selectRespectiveTab(itemPosition);
                                        ((LinearLayoutManager) rvMenuList.getLayoutManager()).scrollToPositionWithOffset(itemPosition > 0 ? itemPosition - 1 : itemPosition, 0);
                                    }
                                }, 800);

                                handler2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProductItemViewHolder itemViewHolder = (ProductItemViewHolder) rvMenuList.findViewHolderForAdapterPosition(itemPosition);
                                        if (itemViewHolder!=null){
                                            itemViewHolder.ivAdd.performClick();
                                        }
                                    }
                                },1400);

                            }
                        }
                        break;
                        case ACTION_SCROLL_ITEM: {
                            int itemPosition = findMenuItemPositionById("item");
                            if (itemPosition>-1) {
                                final Handler handler1 = new Handler();
                                final Handler handler2 = new Handler();
                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        selectRespectiveTab(itemPosition);
                                        ((LinearLayoutManager) rvMenuList.getLayoutManager()).scrollToPositionWithOffset(itemPosition > 0 ? itemPosition - 1 : itemPosition, 0);
                                    }
                                }, 800);
                                handler2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        highlightOnce(itemPosition);
                                    }
                                }, 1400);
                            }
                        }
                        break;
                        case ACTION_SCROLL_CATEGORY:{
                            int itemPosition = findMenuItemPositionById("category");
                            if (itemPosition>-1){
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LinearLayoutManager) rvMenuList.getLayoutManager()).scrollToPositionWithOffset(itemPosition, 0);
                                    }
                                }, 800);

                            }
                        }
                        break;
                        case ACTION_BOOKMARK_ITEM: {
                            int menuItemPosition = findMenuItemPositionById("item");
                            if (menuItemPosition>-1) {

                                if (productListWithHeaders.get(menuItemPosition) instanceof Product) {
                                    Product product = (Product) productListWithHeaders.get(menuItemPosition);
                                    if (product != null && !product.isBookmarked) {

                                        final Handler handler1 = new Handler();
                                        final Handler handler2 = new Handler();
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                selectRespectiveTab(menuItemPosition);
                                                ((LinearLayoutManager) rvMenuList.getLayoutManager()).scrollToPositionWithOffset(menuItemPosition > 0 ? menuItemPosition - 1 : menuItemPosition, 0);
                                            }
                                        }, 800);

                                        handler2.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                ProductItemViewHolder itemViewHolder = (ProductItemViewHolder) rvMenuList.findViewHolderForAdapterPosition(menuItemPosition);
                                                String message = String.format(getActivity().getString(R.string.bookmark_dialog_message), product.getName());
                                                GeneralDialogFragment dialog = GeneralDialogFragment.newInstance(
                                                        getActivity().getString(R.string.bookmark_dialog_title),
                                                        message,
                                                        new GeneralDialogFragment.OnDialogFragmentClickListener() {
                                                            @Override
                                                            public void onPositiveInteraction(GeneralDialogFragment dialog) {
                                                                if (itemViewHolder!=null){
                                                                    itemViewHolder.ivBookmark.performClick();
                                                                }
                                                            }

                                                            @Override
                                                            public void onNegativeInteraction(GeneralDialogFragment dialog) {
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                );
                                                if (getActivity() != null && itemViewHolder!=null) {
                                                    dialog.show(getActivity().getSupportFragmentManager(), "dialog");
                                                }                                            }
                                        }, 1400);

                                    } else {
                                        AppUtils.showToast("Item already bookmarked", true, 2);
                                    }
                                }
                            }
                        }
                        break;
                        default: {
                            //do nothing
                        }
                        break;
                    }
                }
            }
        } catch (Exception e){
            AppUtils.logException(e);
            e.printStackTrace();
        }
    }

    TextWatcher trayTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()>0){
                showResultOfTrayId(s.toString());
            }else{
                hideVendingTrayItemView(getActivity().getResources().getString(R.string.vending_trending_hint));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void showResultOfTrayId(String trayId){

        if(products==null || products.size()<1 || getActivity() == null){
            return;
        }

        ArrayList<Product> traySearchProduct = new ArrayList<>();
        for(Product product : products){
            if(product.getTrayNamesList().contains(trayId)){
                traySearchProduct.add(product);
                break;
            }
        }

        if(traySearchProduct.size()>0) {
            rvVendingTrayItem.setVisibility(View.VISIBLE);
            tvTrayHint.setVisibility(View.GONE);
            trayListAdapter = new MenuListAdapter((MenuActivity) getActivity(), traySearchProduct, vendor, this, occasionId, true);
            rvVendingTrayItem.setAdapter(trayListAdapter);
            trayListAdapter.notifyDataSetChanged();
        }else{
            hideVendingTrayItemView(getActivity().getResources().getString(R.string.vending_item_not_found));
        }

    }

    private void hideVendingTrayItemView(String hint){
        rvVendingTrayItem.setVisibility(View.GONE);
        if (hint!=null){
            tvTrayHint.setText(hint);
        }
        tvTrayHint.setVisibility(View.VISIBLE);
    }

    private void selectRespectiveTab(int itemPosition){
        try {
            if (menuTabLayout != null && itemPosition > -1) {
                for (int i = itemPosition; i >= 0; i--) {
                    if (productTabIndexes.containsKey(i)) {
                        TabLayout.Tab tab = menuTabLayout.getTabAt(productTabIndexes.get(i));
                        if (tab != null) {
                            tab.select();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void highlightOnce(int itemPosition){

        try {
            RelativeLayout cardView = layoutManager.findViewByPosition(itemPosition).findViewById(R.id.rl1);



            if (cardView != null && getActivity() != null) {
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(cardView, View.ALPHA, 0.0f, 1.0f);
                alphaAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(final Animator animation) {
                        cardView.setBackground(getActivity().getResources().getDrawable(R.drawable.menu_item_highlight));
                    }
                });
                alphaAnimator.setDuration(800);
                alphaAnimator.start();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        resetHolderBackground(itemPosition,cardView);
                    }
                }, 1800);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void resetHolderBackground(int position, RelativeLayout relativeLayout){
        if (getActivity()!=null) {
            if (productListWithHeaders.get(position + 1) instanceof Product && productListWithHeaders.get(position - 1) instanceof Product) {
                relativeLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.menu_item_middle));
            } else if (productListWithHeaders.get(position + 1) instanceof Product && productListWithHeaders.get(position - 1) instanceof MenuHeader) {
                relativeLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.menu_item_top));
            } else if (productListWithHeaders.get(position + 1) instanceof MenuHeader && productListWithHeaders.get(position - 1) instanceof Product) {
                relativeLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.menu_item_bottom));
            } else {
                relativeLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.menu_item_top_bottom));
            }
        }
    }
    private int findMenuItemPositionById(String type){
        try {
            if (productListWithHeaders != null && productListWithHeaders.size() > 0) {

                int startIndex = -1;
                int endIndex = -1;

                if (type.equalsIgnoreCase("item")) {
                    startIndex = approxItemStartPos;
                    endIndex = approxItemEndPos == productListWithHeaders.size()?approxItemEndPos-1:approxItemEndPos;
                } else {
                    return exactCategoryPosition;
                }

                if (startIndex > -1 && endIndex > -1) {
                    for (int i = startIndex; i <= endIndex; i++) {
                        if (productListWithHeaders.get(i) instanceof Product) {
                            if (((Product) productListWithHeaders.get(i)).id == notificationItemId) {
                                return i;
                            }
                        }
                    }
                }
            }
            return -1;
        } catch(Exception e){
            e.printStackTrace();
            return  -1;
        }
    }


    private void showMenuSearchActivity() {
        EventUtil.FbEventLog(getActivity(), EventUtil.MENU_SEARCH_CLICK, EventUtil.SCREEN_MENU);

        HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.MENU_SEARCH_CLICK);
        Intent intent = new Intent(getActivity(), MenuSearchActivity.class);
        intent.putExtra(ApplicationConstants.VENDOR_ID, vendorId);
        intent.putExtra(ApplicationConstants.OCASSION_ID, occasionId);
        intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);
        intent.putExtra(ApplicationConstants.LOCATION_NAME, location);
        intent.putExtra(ApplicationConstants.VENDOR_NAME,vendorName);
        intent.putExtra(ApplicationConstants.CATEGORY_LIST,categoriesToShow);
        double[] minMaxValues = {minPrice,maxPrice,minCalorie,maxCalorie};
        intent.putExtra(ApplicationConstants.MIN_MAX_VALUES,minMaxValues);
        getActivity().startActivityForResult(intent,REQUEST_SEARCH);
    }

    private void setVendorImage() {
        String image = getVendorImageUrl();
        if (image.contains("/original_"))
            image = image.replaceFirst("/original_", "/original_");

        final String finalImage = image;
        vendorInfoContainer.post(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    int width = vendorInfoContainer.getMeasuredWidth();
                    int height = 3*width / 5;
                    ViewGroup.LayoutParams lp = vendorInfoContainer.getLayoutParams();
                    lp.height = height;
                    vendorInfoContainer.setLayoutParams(lp);
                    vendorInfoContainer.requestLayout();

                    vendorInfoContainer.setVisibility(View.VISIBLE);

                    if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT && !AppUtils.isCafeApp()){
                        vendorImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.default_vendor_banner));
                    } else {
                        ImageHandling.loadRemoteImage(finalImage, vendorImage, R.drawable.default_vendor_banner, R.drawable.default_vendor_banner, getActivity());
                    }
                }
            }
        });
    }

    private void showVegOnly() {
        IS_VEG_ONLY = true;
        getProductsFromDB();
    }

    private void showAll() {
        if (!vegOnlySwitchInvisible) {
            IS_VEG_ONLY = false;
            getProductsFromDB();
        }
    }


    private void refreshList() {
        getHeadersFromProduct();
        addTabs();
        buildList();
        menuListAdapter.notifyDataSetChanged();
    }

    private void initCollapsingToolbar() {
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                verticalOffset = Math.abs(verticalOffset);
                int difference = appBarLayout.getTotalScrollRange() - toolbar.getHeight();
                if (verticalOffset >= difference) {
                    tvVendorRating.setVisibility(View.GONE);
                    tvVendorDesc.setVisibility(View.GONE);
                    tvVendorserveType.setVisibility(View.GONE);

                } else {
                    tvVendorRating.setVisibility(View.VISIBLE);
                    tvVendorserveType.setVisibility(View.VISIBLE);
                    tvVendorDesc.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    @Override
    public void onResume() {
        LogoutTask.updateTime();
        super.onResume();
        MainApplication.bus.register(this);
        isRunning = true;
        notifyMenuPagerAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();

        MainApplication.bus.unregister(this);
        isRunning = false;
    }

    private void getCategoriesFor(final long vendorId, final String vendorName) {
        try {
            String url = UrlConstant.VENDOR_MENU_GET + "?vendorId=" + vendorId + "&occasionId=" + occasionId + "&locationId=" + locationId;
            SimpleHttpAgent<VendorResponse> categoryHttpAgent = new SimpleHttpAgent<>(
                    getActivity(),
                    url,
                    new ResponseListener<VendorResponse>() {
                        @Override
                        public void response(VendorResponse responseObject) {
                            LogoutTask.updateTime();
                            if (responseObject != null && responseObject.vendor != null &&
                                    responseObject.vendor.menu != null &&
                                    responseObject.vendor.menu.products != null) {

                                if(responseObject.vendor.menu.products.size() > 0){
                                    products.addAll(responseObject.vendor.menu.products);
                                    storeAndUpdateUi(responseObject.vendor.menu.products, true);
                                }

                            }
                            else {
                                if (getActivity() != null && getActivity() instanceof MenuActivity) {
                                }
                            }
                            if (products.isEmpty()) {
                                tvNoItemsFound.setVisibility(View.VISIBLE);
                                rvMenuList.setVisibility(View.GONE);
                                menuTabLayout.setVisibility(View.GONE);
                            } else {
                                tvNoItemsFound.setVisibility(View.GONE);
                                rvMenuList.setVisibility(View.VISIBLE);
                                menuTabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    },
                    new ContextErrorListener() {
                        @Override
                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                            if (getActivity() != null && getActivity() instanceof MenuActivity) {

                            }
                        }
                    },
                    VendorResponse.class
            );

            categoryHttpAgent.get(tagForApiRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    boolean[] containsRecommended;

    private void refreshData(final List<Product> products, final boolean fresh){


        containsRecommended = new boolean[1];

        if(fresh){

            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product s1, Product s2) {
                    return s2.getCategorySortOrder() - s1.getCategorySortOrder();
                }
            });

                    realm.where(Product.class).equalTo("vendorId", vendorId)
                            .findAll().deleteAllFromRealm();

                    try {
                        realm.where(OptionItemResponse.class).findAll().deleteAllFromRealm();
                        realm.where(SubProduct.class).findAll().deleteAllFromRealm();
                        realm.where(Nutrition.class).findAll().deleteAllFromRealm();
                        realm.where(Product.class).findAll().deleteAllFromRealm();
                        realm.where(NutritionItem.class).findAll().deleteAllFromRealm();
                        realm.where(OptionItem.class).findAll().deleteAllFromRealm();
                        realm.where(ProductResponse.class).findAll().deleteAllFromRealm();
                        realm.where(MenuOptionResponse.class).findAll().deleteAllFromRealm();
                        realm.where(Category.class).findAll().deleteAllFromRealm();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
        }

        final List<Category> categories = new ArrayList<Category>();
        HashMap<String, Category> addedCategories = new HashMap<String, Category>();
        createCategoryUtilityMaps();


        categoryProductHashMap.clear();
        categoryProductHashMap.put(CATEGORY_RECOMMENDED,new ArrayList<Product>());
        if (fresh) {
            if (products.size() > 0) {
                minPrice = products.get(0).price;//just to set an initial value
            }
            if (AppUtils.getConfig(getContext()).isHealthEnabled()) {
                if (products.size() > 0) {
                    minCalorie = products.get(0).getCalories();//just to set an initial value
                }
            }
        }
        for (Product product : products) {
            //independent from veg/non veg criteria
            if (fresh) {
                calculateMinMaxValues(product);
            }

            if (!IS_VEG_ONLY||product.isProductVeg()) {
                if (product.isRecommended()) {
                    containsRecommended[0] = true;
                    ArrayList<Product> productArrayList = categoryProductHashMap.get(CATEGORY_RECOMMENDED);
                    Product recommendedProduct = product.clone();
                    recommendedProduct.setRecommendationType(ApplicationConstants.RecommendationType.RECOMMENDED_MENU);
                    productArrayList.add(recommendedProduct);
                    categoryProductHashMap.put(CATEGORY_RECOMMENDED, productArrayList);

                }
                if (product.category.trim().equalsIgnoreCase("MRP Items")){
                    product.setIsMrpItem(1);
                }

                updateNotificationVariables(product);

                //adding products category wise
                product.setRecommendationType(ApplicationConstants.RecommendationType.REGULAR_MENU);
                if (categoryProductHashMap.containsKey(product.getCategory())) {
                    ArrayList<Product> productArrayList = categoryProductHashMap.get(product.getCategory());
                    productArrayList.add(product);
                    categoryProductHashMap.put(product.getCategory(), productArrayList);
                } else {
                    ArrayList<Product> productArrayList = new ArrayList<>();
                    productArrayList.add(product);
                    categoryProductHashMap.put(product.getCategory(), productArrayList);
                }

                if (addedCategories.containsKey(product.getCategory())) {
                    //Category category = addedCategories.get(product.getCategory());
                } else {
                    Category category = new Category();
                    category.id = product.categoryId;
                    category.name = product.category;
                    categoriesToShow.add(product.category);
                    category.categorySortOrder = product.categorySortOrder;
                    addedCategories.put(product.getCategory(), category);
                    categoryIdMap.put(category.id,category);
                    categoryNameIdMap.put(category.name,category.id);
                    inflateSingleCategoryInFilter(product.category);

                }
            }
        }

        if(fresh){
            setFilterMinMaxValues();
            LinkedHashMap<String,ArrayList<Product>> categoryProductHashMapLocal = new LinkedHashMap<>();

            for(Map.Entry<String,ArrayList<Product>> entry : categoryProductHashMap.entrySet()){

                ArrayList<Product> pl = entry.getValue();
                Collections.sort(pl, new Comparator<Product>() {
                    @Override
                    public int compare(Product s1, Product s2) {
                        return s2.getSortOrder() - s1.getSortOrder();
                    }
                });
                categoryProductHashMapLocal.put(entry.getKey(), pl);
            }

            categoryProductHashMap.clear();
            categoryProductHashMap.putAll(categoryProductHashMapLocal);
        }

        if (!containsRecommended[0]){
            categoryProductHashMap.remove(CATEGORY_RECOMMENDED);
        }

        categories.addAll(addedCategories.values());

        realm.copyToRealmOrUpdate(products);
                realm.copyToRealmOrUpdate(categories);

    }
    private void calculateMinMaxValues(Product product){
        //if cash and carry
        if (product.price<minPrice){
            minPrice = product.price;
        }
        if (product.price>maxPrice){
            maxPrice = product.price;
        }
        if (AppUtils.getConfig(getContext()).isHealthEnabled()){
            if (product.getCalories()<minCalorie){
                minCalorie = product.getCalories();
            }
            if (product.getCalories()>maxCalorie){
                maxCalorie = product.getCalories();
            }
        }

    }

    private void updateNotificationVariables(Product product){
        if (!itemFoundInLoop && itemToBeFound && notificationItemId>-1 && product.id==notificationItemId){
            itemCategoryName = product.getCategory();
            itemFoundInLoop = true;
        }
        if (!categoryFoundInLoop && categoryToBeFound){
            if (notificationCategoryId>-1 && product.categoryId == notificationCategoryId){
                itemCategoryName = product.getCategory();
                categoryFoundInLoop = true;
            } else if (notificationCategoryName!=null && !notificationCategoryName.isEmpty() && product.getCategory().equalsIgnoreCase(notificationCategoryName)){
                itemCategoryName = product.getCategory();
                categoryFoundInLoop = true;
            }

        }
    }
    private void createCategoryUtilityMaps(){
        categoryIdMap = new HashMap<>();
        categoryNameIdMap = new HashMap<>();
        Category recommendedCategory = new Category();
        recommendedCategory.id = CATEGORY_RECOMMENDED_ID;
        recommendedCategory.name = CATEGORY_RECOMMENDED;
        categoryIdMap.put(recommendedCategory.id,recommendedCategory);
        categoryNameIdMap.put(recommendedCategory.name,recommendedCategory.id);
    }
    private void storeAndUpdateUi(final List<Product> products, final boolean fresh) {
        try {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                   refreshData(products, fresh);
                }
            });

            getMenuAndUpdateUi(containsRecommended[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getMenuAndUpdateUi(boolean containsRecommedned) {
        try {
            if (realmResults == null) {
                RealmQuery<Category> query = realm.where(Category.class);
                for (int i = 0; i < categoriesToShow.size(); i++) {
                    if (i > 0)
                        query.or();
                    query.equalTo("name", categoriesToShow.get(i));
                }
                realmResults = query.findAll().sort("categorySortOrder", Sort.DESCENDING);
            }

            addTabs();
            addListeners();
            buildList();
            menuListAdapter.notifyDataSetChanged();
            rvMenuList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    rvMenuList.removeOnLayoutChangeListener(this);
                    handleNotificationNavigation();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void addTabs(){
        try {
            menuTabLayout.removeAllTabs();

            for (String categoryName : categoryProductHashMap.keySet()) {
                TabLayout.Tab tab = menuTabLayout.newTab();
                tab.setCustomView(R.layout.tab_custom_layout);
                TextView tabHeader = tab.getCustomView().findViewById(R.id.text);
                tabHeader.setText(categoryName);
                tab.setText(categoryName);
                menuTabLayout.addTab(tab);
            }
            selectTab(menuTabLayout.getTabAt(0));
            menuTabLayout.scrollTo(0,0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deSelectTab(TabLayout.Tab tab){
        try {
            String text = tab.getText().toString();
            TextView tabHeader = tab.getCustomView().findViewById(R.id.text);
            tabHeader.setText(text);
            tabHeader.setTextColor(getResources().getColor(R.color.text_black_alpha_90));
            tabHeader.setBackground(null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void selectTab(TabLayout.Tab tab){
        try {

            String text = tab.getText().toString();
            TextView tabHeader = tab.getCustomView().findViewById(R.id.text);
            tabHeader.setText(text);
            tabHeader.setPadding(40,20,40,20);
            tabHeader.setTextColor(getResources().getColor(R.color.colorAccent));
            tabHeader.setBackground(getResources().getDrawable(R.drawable.tab_custom_background));

            try{
                HashMap<String,Object> map = new HashMap<>();
                map.put(CleverTapEvent.PropertiesNames.getVendor_id(),vendorId);
                map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getCategory_click(),map,getActivity());
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getHeadersFromProduct(){
        try {
            boolean containsRecommended = false;
            categoryProductHashMap.clear();
            categoryProductHashMap.put(CATEGORY_RECOMMENDED,new ArrayList<Product>());
            for (Product product : products) {
                if ((IS_VEG_ONLY && product.isProductVeg()) || !IS_VEG_ONLY) {
                    if (product.isRecommended()){
                        containsRecommended = true;
                        ArrayList<Product> productArrayList = categoryProductHashMap.get(CATEGORY_RECOMMENDED);
                        productArrayList.add(product);
                        categoryProductHashMap.put(CATEGORY_RECOMMENDED, productArrayList);

                    }

                    if (categoryProductHashMap.containsKey(product.getCategory())) {
                        ArrayList<Product> productArrayList = categoryProductHashMap.get(product.getCategory());
                        //match
                        //get category name
                        productArrayList.add(product);
                        categoryProductHashMap.put(product.getCategory(), productArrayList);
                    } else {
                        //match
                        //get category name
                        ArrayList<Product> productArrayList = new ArrayList<>();
                        productArrayList.add(product);
                        categoryProductHashMap.put(product.getCategory(), productArrayList);
                    }
                }
            }
            if (!containsRecommended){
                categoryProductHashMap.remove(CATEGORY_RECOMMENDED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void buildList(){
        try {
            productListWithHeaders.clear();
            productListWithHeaders.add(new MenuSwitch(IS_VEG_ONLY));
            headerIndexes.clear();
            productTabIndexes.clear();
            int headerIndexCount = 0;

            for (String category : categoryProductHashMap.keySet()) {
                if(categoryToBeFound && itemCategoryName.equalsIgnoreCase(category)){
                    exactCategoryPosition = productListWithHeaders.size();
                }
                if (itemCategoryName.equalsIgnoreCase(category)){
                    approxItemStartPos = productListWithHeaders.size();
                }
                productListWithHeaders.add(new MenuHeader(category));//add category name header in list
                headerIndexes.put(headerIndexCount, headerIndexCount == 0 ? 0 : productListWithHeaders.size() - 1);//header position in list
                productTabIndexes.put(productListWithHeaders.size() - 1, headerIndexCount);
                productListWithHeaders.addAll(categoryProductHashMap.get(category));
                productTabIndexes.put(productListWithHeaders.size() - 1, headerIndexCount);
                ++headerIndexCount;
                if (itemCategoryName.equalsIgnoreCase(category)){
                    approxItemEndPos = productListWithHeaders.size();
                }
            }
            productListWithHeaders.add("end of list");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getNotificationValues(){
        if (getActivity() instanceof ParentActivity) {
            ParentActivity activity = (ParentActivity) getActivity();

            if (activity.getFromNotification()) {
                fromNotification = true;
                Bundle extras = activity.getIntent().getExtras();
                if (extras != null) {
                    action = extras.getString(ApplicationConstants.NotificationsConstants.ACTION, "");
                    notificationItemId = extras.getLong(ApplicationConstants.NotificationsConstants.ITEM_ID, -1);
                    notificationCategoryId = extras.getLong(ApplicationConstants.NotificationsConstants.CATEGORY_ID, -1);
                }
            }
        }
        if (notificationItemId>-1){
            itemToBeFound = true;
        }
        if (notificationCategoryId>-1){
            categoryToBeFound = true;
        }
        else if (!notificationCategoryName.isEmpty()){
            categoryToBeFound = true;
        }
    }


    private void populateList(){
        try {
            if (menuListAdapter == null ||
                    rvMenuList.getAdapter() == null) {
                menuListAdapter = new ListAdapter((MenuActivity) getActivity(), productListWithHeaders, vendor, this, occasionId, vegNonSwitchListener);
                rvMenuList.setAdapter(menuListAdapter);
                layoutManager = new LinearLayoutManager(context);
                rvMenuList.setLayoutManager(layoutManager);
            } else {
                menuListAdapter.changeProducts(productListWithHeaders, vendor);
                menuListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addListeners(){
        try {
            final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(context) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
            final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    selectTab(tab);
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    if (headerIndexes.get(tab.getPosition() + 1) != null) {
                        if (firstVisibleItem >= headerIndexes.get(tab.getPosition()) && (firstVisibleItem < headerIndexes.get(tab.getPosition() + 1))) {
                            //belongs to same category, no tab change if these items are shown on scrolling
                        } else {
                            layoutManager.scrollToPositionWithOffset(headerIndexes.get(tab.getPosition()), 0);

                        }
                    } else {
                        if (firstVisibleItem >= headerIndexes.get(tab.getPosition()) && (firstVisibleItem <= productListWithHeaders.size() - 1)) {
                            //belongs to same category, no tab change if these items are shown on top on scrolling
                        } else {
                            layoutManager.scrollToPositionWithOffset(headerIndexes.get(tab.getPosition()), 0);
                        }
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    deSelectTab(tab);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            };
            menuTabLayout.addOnTabSelectedListener(tabSelectedListener);

            rvMenuList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!recyclerView.canScrollVertically(1)) {
                        TabLayout.Tab tab = menuTabLayout.getTabAt(menuTabLayout.getTabCount() - 1);
                        if (tab!=null) {
                            tab.select();
                        }

                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    if (productTabIndexes.containsKey(firstVisibleItem)) {
                        TabLayout.Tab tab = menuTabLayout.getTabAt(productTabIndexes.get(firstVisibleItem));
                        if (tab!=null)
                        tab.select();
                    }


                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFragmentInteraction(Vendor vendor, Product product, boolean isbuffet) {
        try {
            if (getActivity() == null) {
                return;
            }
            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
            mainApplication.clearOrder();
            HashMap<String, Object> map = new HashMap<>();
            try {
                map.put(CleverTapEvent.PropertiesNames.getSource(),ApplicationConstants.ADD_ITEM_SOURCE_NORMAL);
                map.put(CleverTapEvent.PropertiesNames.is_bookmarked(), product.isBookmarked()?"Yes":"No");
                map.put(CleverTapEvent.PropertiesNames.is_trending(),product.isTrendingItem()?"Yes":"No");
                map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                map.put(CleverTapEvent.PropertiesNames.getVendor_id(),vendor.getId());
                map.put(CleverTapEvent.PropertiesNames.getMenu_item_id(),product.getId());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!product.isConfigurable()) {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.copy(product);
                mainApplication.getCart().addProductToCart(product.clone(), null,
                        mainApplication,
                        this,
                        (AppCompatActivity) getActivity(),
                        vendor.clone(), occasionId, map);
                int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
                MenuActivity activity = (MenuActivity) getActivity();
                activity.setUpCart();
                if (activity instanceof AddToCardLisenter) {
                    AddToCardLisenter addToCardLisenter = (AddToCardLisenter) activity;
                    Product productClone = product.clone();
                    productClone.quantity = orderQty;
                    ((AddToCardLisenter) activity).addToCart(vendor.clone(), productClone);
                }
                if (menuListAdapter != null) {
                    menuListAdapter.notifyDataSetChanged();
                }
            } else {
                mainApplication.getCart().addProductToCart(product.clone(), null,
                        mainApplication,
                        this,
                        (AppCompatActivity) getActivity(),
                        vendor.clone(), occasionId, map);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void showCancelCartProduct(Vendor vendor, Product product, boolean isBuffet) {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            CartCancelDialog cartCancelDialog = CartCancelDialog.newInstance(vendor, product, this, isBuffet);
            cartCancelDialog.setCancelable(false);

            fragmentManager.beginTransaction()
                    .add(cartCancelDialog, "cart_cancel")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void validateAndAddProduct(Vendor vendor, Product product, boolean isBuffetVendor) {
        if(getActivity()!=null) {
            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
            if (!mainApplication.isVendorValid(vendor.getId())) {
                showCancelCartProduct(vendor, product, isBuffetVendor);
            }
        }
    }


    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        notifyMenuPagerAdapter();
    }

    @Subscribe
    public void RemoveProductFromCart(RemoveProductFromCart removeProductFromCart) {
        notifyMenuPagerAdapter();
    }

    @Subscribe
    public void onBookmarkChangeEvent(BookmarkAddRemoveEvent bookmarkAddRemoveEvent) {
        notifyMenuPagerAdapter();
    }

    private void notifyMenuPagerAdapter() {
        if (menuListAdapter != null) {
            menuListAdapter.notifyDataSetChanged();
        }
        if(trayListAdapter!=null){
            trayListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SEARCH){//comes back from search activity
            getProductsFromDB();
        }
    }

    private void getProductsFromDB(){

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realms) {

                    RealmResults<Product> realmResults = realm.where(Product.class)
                            .equalTo("vendorId",vendorId).findAll();
                    final List<Product> productList = realmResults.subList(0,realmResults.size());

                    refreshData(productList, false);
                }
            });
            getMenuAndUpdateUi(containsRecommended[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getVendorImageUrl(){

         if(!vendor.getBannerImage().trim().equalsIgnoreCase(""))
            return vendor.getBannerImage();
        else if(AppUtils.isSocialDistancingActive(null) && !AppUtils.getConfig(getActivity()).getSocial_distancing().getVendor_image().trim().equalsIgnoreCase("") && vendor.isCoronaSafe())
            return AppUtils.getConfig(getActivity()).getSocial_distancing().getVendor_image();
        else if (vendor.getImageSrc().equalsIgnoreCase(""))
            return vendor.getLogoImageSrc();
        else
            return vendor.getImageSrc();

    }

}
