package com.hungerbox.customer.offline.fragmentOffline;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.BookmarkAddRemoveEvent;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.MenuHeader;
import com.hungerbox.customer.model.MenuSwitch;

import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.activityOffline.MenuActivityOffline;
import com.hungerbox.customer.offline.activityOffline.UrlConstantsOffline;
import com.hungerbox.customer.offline.adapterOffline.ListAdapterOffline;
import com.hungerbox.customer.offline.eventOffline.CartItemAddedEventOffline;
import com.hungerbox.customer.offline.listenersOffline.AddToCardLisenterOffline;
import com.hungerbox.customer.offline.listenersOffline.VendorValidationListener;
import com.hungerbox.customer.offline.modelOffline.CategoryOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;
import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.offline.modelOffline.VendorResponseOffline;
import com.hungerbox.customer.order.listeners.VegNonVegSwitchListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuFragmentOffline extends Fragment implements CartCancelDialogOffline.OnFragmentInteractionListener, VendorValidationListener {

    public static boolean IS_VEG_ONLY = false;
    //ViewPager vpMenu;
    TextView tvVendorRating, tvVendorserveType, tvVendorDesc;
    ImageView ivSearchMenu;
    TabLayout menuTabLayout;
    LinearLayout llSearchListContainer;
    VendorOffline vendor;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    RelativeLayout vendorInfoContainer;
    Toolbar toolbar;
    List<ProductOffline> products = new ArrayList<>();
    RecyclerView rvSearchList;
    RecyclerView rvMenuList;
    ArrayList<String> categoriesToShow = new ArrayList<>();
    ArrayList<Object> productListWithHeaders = new ArrayList<>();
    HashMap<Integer, Integer> headerIndexes = new HashMap<>();
    HashMap<Integer, Integer> productTabIndexes = new HashMap<>();
    LinearLayoutManager layoutManager;
    ListAdapterOffline menuListAdapter;
    Context context;
    LinkedHashMap<String, ArrayList<ProductOffline>> categoryProductHashMap = new LinkedHashMap<>();
    VegNonVegSwitchListener vegNonSwitchListener;
    private final String CATEGORY_RECOMMENDED = "Recommended";
    private final int REQUEST_SEARCH = 123;

    boolean containsRecommmended = false;

    private long vendorId, locationId;
    private String vendorName, location;
    private long occasionId;
    private OnFragmentInteractionListener mListener;
    private TextView tvNoItemsFound;
    TextWatcher searchtextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String searchString = s.toString().trim();
            if (searchString.length() >= 3) {
                doSearch(searchString);
            } else {
                cancelSerach(false);
                tvNoItemsFound.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private CardView cvCallToOrder;
    private Button btnCallToOrder;
    private ImageView ivBack;
    private boolean isRunning = false;

    public MenuFragmentOffline() {
    }

    public static MenuFragmentOffline newInstance(long vendorId, String vendorName, long ocassionId, Context context, VendorOffline vendor) {
        MenuFragmentOffline fragment = new MenuFragmentOffline();
        fragment.vendorId = vendorId;
        fragment.vendorName = vendorName;
        fragment.occasionId = ocassionId;
        fragment.context = context;
        fragment.vendor = vendor;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isRunning = true;
        View view = inflater.inflate(R.layout.fragment_menu_offline, container, false);
        rvSearchList = view.findViewById(R.id.rv_search_list);
        llSearchListContainer = view.findViewById(R.id.serach_list_conatiner);
        //vpMenu = view.findViewById(R.id.vp_menu_pager);
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

        cvCallToOrder = getActivity().findViewById(R.id.cv_call_To_order);
        btnCallToOrder = getActivity().findViewById(R.id.btn_call_to_order);

        TextView tvTitle = view.findViewById(R.id.tv_toolbar_title);
        tvVendorserveType = view.findViewById(R.id.tv_vendor_serve_type);
        tvVendorDesc = view.findViewById(R.id.tv_vendor_desc);

        EventUtil.FbEventLog(getActivity(), EventUtil.SCREEN_OPEN_MENU, EventUtil.SCREEN_HOME);

        HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.SCREEN_OPEN_MENU);

        ivSearchMenu = view.findViewById(R.id.iv_search);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtil.FbEventLog(getActivity(), EventUtil.MENU_BACK, EventUtil.SCREEN_MENU);
                HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.MENU_CLICK_BACK);
                showBackPressedMessage();
            }
        });
        if (vendor != null) {
            if (vendor.getRating() <= 0) {
                tvVendorRating.setVisibility(View.INVISIBLE);
            }
            tvVendorRating.setText(vendor.getRatingText());
            tvVendorDesc.setText(vendor.getDesc());
            tvVendorserveType.setText(vendor.getCuisinesString());

            if (vendor.getImageSrc().equalsIgnoreCase(""))
                setVendorImage(vendor.getLogoImageSrc());
            else
                setVendorImage(vendor.getImageSrc());

            tvTitle.setText(vendor.getVendorName());
            if (vendor.isRestaurant())
                tvVendorserveType.setText("Restaurant");
            else {
                tvVendorserveType.setText(AppUtils.getConfig(getContext()).getOn_campus());
            }

            if (vendor.getDesc().length() > 1)
                tvVendorDesc.setText(vendor.getDesc());
//            vendor.removeChangeListeners();

//            vendor.removeChangeListeners();

            vendor = vendor.clone();
//            if (!AppUtils.getConfig(getActivity()).isPlace_order()) {
//                if (vendor.getVendorPhone().length() == 0) {
//                    cvCallToOrder.setVisibility(View.GONE);
//                } else {
//                    cvCallToOrder.setVisibility(View.VISIBLE);
//                }
//            }
        } else {
            AppUtils.showToast("Something went wrong.", true, 0);
            getActivity().finish();
        }

//        initCollapsingToolbar();
        btnCallToOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + vendor.getVendorPhone()));
                startActivity(intent);
            }
        });

        IS_VEG_ONLY = false;

        String userName = SharedPrefUtil.getString(ApplicationConstants.PREF_USER_NAME, "");
        location = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
        getCategoriesFor(vendorId, vendorName);

        vegNonSwitchListener = new VegNonVegSwitchListener() {
            @Override
            public void onUpdate(boolean isChecked) {
                LogoutTask.updateTime();
                if (isChecked) {
                    EventUtil.FbEventLog(getActivity(), EventUtil.MENU_CLICK_VEGONLY, EventUtil.SCREEN_MENU);
                    HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.MENU_CLICK_VEGONLY);

                    try {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getVendor_name(), vendorName);
                        map.put(CleverTapEvent.PropertiesNames.getAction(), "On");
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getVeg_item_click(), map, getActivity());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    showVegOnly();
                } else {

                    try {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getVendor_name(), vendorName);
                        map.put(CleverTapEvent.PropertiesNames.getAction(), "Off");
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getVeg_item_click(), map, getActivity());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    showAll();
                }
            }
        };

        ivSearchMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuSearchActivity();
            }
        });

        populateList();

        return view;
    }

    private void showMenuSearchActivity() {

    }

    private void showBackPressedMessage() {

        int cartQty = MainApplicationOffline.getTotalOrderCount(1);

        if (cartQty == 0) {
            getActivity().finish();
        } else {

            FreeOrderErrorHandleDialogOffline orderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(new OrderOffline(),
                    "Your cart will get cleared",
                    new FreeOrderErrorHandleDialogOffline.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            MainApplicationOffline.clearOrder(1);
                            MainApplicationOffline.bus.post(new OrderClear());
                            getActivity().finish();
                        }

                        @Override
                        public void onNegativeButtonClicked() {

                        }
                    }, "OK", "CANCEL");

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(orderErrorHandleDialog, "cart_clear")
                    .commitAllowingStateLoss();
        }
    }


    private void setVendorImage(String image) {
        if (image.contains("/original_"))
            image = image.replaceFirst("/original_", "/original_");

        final String finalImage = image;
        vendorInfoContainer.post(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    int width = vendorInfoContainer.getMeasuredWidth();
                    int height = width / 2;
                    ViewGroup.LayoutParams lp = vendorInfoContainer.getLayoutParams();
                    lp.height = height;
                    vendorInfoContainer.setLayoutParams(lp);
                    vendorInfoContainer.requestLayout();

                    vendorInfoContainer.setVisibility(View.VISIBLE);


                    vendorInfoContainer.setBackgroundResource(R.drawable.vendor_menu);

                }
            }
        });
    }

    private void cancelSerach(boolean clearText) {
        llSearchListContainer.setVisibility(View.GONE);
        if (clearText) {
        }
        tvNoItemsFound.setVisibility(View.GONE);
        refreshList();
    }

    private void doSearch(String searchString) {
        if (llSearchListContainer.getVisibility() != View.VISIBLE)
            llSearchListContainer.setVisibility(View.VISIBLE);


        ArrayList<ProductOffline> searchedProducts = new ArrayList<>();
        for (ProductOffline product : products) {
            if (product.getName().toLowerCase().contains(searchString.toLowerCase())) {
                searchedProducts.add(product);
            }
        }

        if (searchedProducts.isEmpty()) {
            tvNoItemsFound.setVisibility(View.VISIBLE);
            rvSearchList.setVisibility(View.GONE);
        } else {
            tvNoItemsFound.setVisibility(View.GONE);
            rvSearchList.setVisibility(View.VISIBLE);
        }
    }


    private void showVegOnly() {
        IS_VEG_ONLY = true;
        //refreshList();
        getProductsFromDB();
    }

    private void showAll() {
        IS_VEG_ONLY = false;
        //refreshList();
        getProductsFromDB();
    }


    private void refreshList() {
        getHeadersFromProduct();
        addTabs();
        buildList();
        menuListAdapter.notifyDataSetChanged();
    }

    private void initCollapsingToolbar() {
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
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
        MainApplicationOffline.bus.register(this);
        isRunning = true;
        if (menuListAdapter != null) {
            menuListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

//        if (realmResults != null)
//            realmResults.removeChangeListeners();

        MainApplicationOffline.bus.unregister(this);
        isRunning = false;
    }

    private void getCategoriesFor(final long vendorId, final String vendorName) {
        try {
            String url = UrlConstantsOffline.VENDOR_MENU + "?vendorId=" + vendorId + "&occasionId=" + occasionId + "&locationId=" + locationId;
            SimpleHttpAgent<VendorResponseOffline> categoryHttpAgent = new SimpleHttpAgent<>(
                    getActivity(),
                    url,
                    new ResponseListener<VendorResponseOffline>() {
                        @Override
                        public void response(VendorResponseOffline responseObject) {
                            LogoutTask.updateTime();
                            if (responseObject != null && responseObject.vendor != null &&
                                    responseObject.vendor.menu != null &&
                                    responseObject.vendor.menu.products != null &&
                                    responseObject.vendor.menu.products.size() > 0) {
                                products.addAll(responseObject.vendor.menu.products);
                                storeAndUpdateUi(responseObject.vendor.menu.products, true);
                            } else {
                                if (getActivity() != null && getActivity() instanceof MenuActivityOffline) {
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
                            if (getActivity() != null && getActivity() instanceof MenuActivityOffline) {

                            }
                        }
                    },
                    VendorResponseOffline.class
            );

            categoryHttpAgent.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean[] containsRecommended;

    private void refreshData(final List<ProductOffline> products, final boolean fresh) {


        containsRecommended = new boolean[1];

        if (fresh) {

            //start anuj
            Collections.sort(products, new Comparator<ProductOffline>() {
                @Override
                public int compare(ProductOffline s1, ProductOffline s2) {
                    return s2.getCategorySortOrder() - s1.getCategorySortOrder();
                }
            });
            //end anuj

        }

        final List<CategoryOffline> categories = new ArrayList<CategoryOffline>();
        HashMap<String, CategoryOffline> addedCategories = new HashMap<String, CategoryOffline>();


        categoryProductHashMap.clear();
        categoryProductHashMap.put(CATEGORY_RECOMMENDED, new ArrayList<ProductOffline>());


        for (ProductOffline product : products) {
            if (!IS_VEG_ONLY || product.isProductVeg()) {
                if (product.isRecommended()) {
                    containsRecommended[0] = true;
                    ArrayList<ProductOffline> productArrayList = categoryProductHashMap.get(CATEGORY_RECOMMENDED);
                    productArrayList.add(product);
                    categoryProductHashMap.put(CATEGORY_RECOMMENDED, productArrayList);

                }

                //adding products category wise
                if (categoryProductHashMap.containsKey(product.getCategory())) {
                    ArrayList<ProductOffline> productArrayList = categoryProductHashMap.get(product.getCategory());
                    productArrayList.add(product);
                    categoryProductHashMap.put(product.getCategory(), productArrayList);
                } else {
                    ArrayList<ProductOffline> productArrayList = new ArrayList<>();
                    productArrayList.add(product);
                    categoryProductHashMap.put(product.getCategory(), productArrayList);
                }

                if (addedCategories.containsKey(product.getCategory())) {
                    //Category category = addedCategories.get(product.getCategory());
                } else {
                    CategoryOffline category = new CategoryOffline();
                    category.id = product.id;
                    category.name = product.category;
                    categoriesToShow.add(product.category);
                    category.categorySortOrder = product.categorySortOrder;
                    addedCategories.put(product.getCategory(), category);

                }
            }
        }

        //start anuj
        if (fresh) {
            LinkedHashMap<String, ArrayList<ProductOffline>> categoryProductHashMapLocal = new LinkedHashMap<>();

            for (Map.Entry<String, ArrayList<ProductOffline>> entry : categoryProductHashMap.entrySet()) {

                ArrayList<ProductOffline> pl = entry.getValue();
                Collections.sort(pl, new Comparator<ProductOffline>() {
                    @Override
                    public int compare(ProductOffline s1, ProductOffline s2) {
                        return s2.getSortOrder() - s1.getSortOrder();
                    }
                });
                categoryProductHashMapLocal.put(entry.getKey(), pl);
            }

            categoryProductHashMap.clear();
            categoryProductHashMap.putAll(categoryProductHashMapLocal);
        }
        //end anuj

        if (!containsRecommended[0]) {
            categoryProductHashMap.remove(CATEGORY_RECOMMENDED);
        }

        categories.addAll(addedCategories.values());


    }

    private void storeAndUpdateUi(final List<ProductOffline> products, final boolean fresh) {
        try {


            refreshData(products, fresh);


            getMenuAndUpdateUi(containsRecommended[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMenuAndUpdateUi(boolean containsRecommedned) {

        //updateUi(realmResults, containsRecommedned);
        addTabs();
        addListeners();
        buildList();
        menuListAdapter.notifyDataSetChanged();

    }


    private void addTabs() {
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
            menuTabLayout.scrollTo(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deSelectTab(TabLayout.Tab tab) {
        try {
            String text = tab.getText().toString();
            TextView tabHeader = tab.getCustomView().findViewById(R.id.text);
            tabHeader.setText(text);
            tabHeader.setTextColor(getResources().getColor(R.color.colorAccent));
            tabHeader.setBackground(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectTab(TabLayout.Tab tab) {
        try {

            String text = tab.getText().toString();
            TextView tabHeader = tab.getCustomView().findViewById(R.id.text);
            tabHeader.setText(text);
            tabHeader.setTextColor(getResources().getColor(R.color.white));
            tabHeader.setBackground(getResources().getDrawable(R.drawable.tab_custom_background));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHeadersFromProduct() {
        try {
            boolean containsRecommended = false;
            categoryProductHashMap.clear();
            categoryProductHashMap.put(CATEGORY_RECOMMENDED, new ArrayList<ProductOffline>());
            for (ProductOffline product : products) {
                if ((IS_VEG_ONLY && product.isProductVeg()) || !IS_VEG_ONLY) {
                    if (product.isRecommended()) {
                        containsRecommended = true;
                        ArrayList<ProductOffline> productArrayList = categoryProductHashMap.get(CATEGORY_RECOMMENDED);
                        productArrayList.add(product);
                        categoryProductHashMap.put(CATEGORY_RECOMMENDED, productArrayList);

                    }

                    if (categoryProductHashMap.containsKey(product.getCategory())) {
                        ArrayList<ProductOffline> productArrayList = categoryProductHashMap.get(product.getCategory());
                        productArrayList.add(product);
                        categoryProductHashMap.put(product.getCategory(), productArrayList);
                    } else {
                        ArrayList<ProductOffline> productArrayList = new ArrayList<>();
                        productArrayList.add(product);
                        categoryProductHashMap.put(product.getCategory(), productArrayList);
                    }
                }
            }
            if (!containsRecommended) {
                categoryProductHashMap.remove(CATEGORY_RECOMMENDED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildList() {
        try {
            productListWithHeaders.clear();
            productListWithHeaders.add(new MenuSwitch(IS_VEG_ONLY));
            headerIndexes.clear();
            productTabIndexes.clear();
            int headerIndexCount = 0;

            for (String category : categoryProductHashMap.keySet()) {
                productListWithHeaders.add(new MenuHeader(category));
                headerIndexes.put(headerIndexCount, headerIndexCount == 0 ? 0 : productListWithHeaders.size() - 1);
                productTabIndexes.put(productListWithHeaders.size() - 1, headerIndexCount);
                productListWithHeaders.addAll(categoryProductHashMap.get(category));
                productTabIndexes.put(productListWithHeaders.size() - 1, headerIndexCount);
                ++headerIndexCount;
            }
            productListWithHeaders.add("end of list");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateList() {
        try {
            if (menuListAdapter == null ||
                    rvMenuList.getAdapter() == null) {
                menuListAdapter = new ListAdapterOffline((MenuActivityOffline) getActivity(), productListWithHeaders, vendor, this, occasionId, vegNonSwitchListener);
                rvMenuList.setAdapter(menuListAdapter);
                layoutManager = new LinearLayoutManager(context);
                rvMenuList.setLayoutManager(layoutManager);
            } else {
                menuListAdapter.changeProducts(productListWithHeaders, vendor);
                menuListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addListeners() {
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

                    //rvMenuList.stopScroll();

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
//                    when list reaches the end, the last tab is selected
                    if (!recyclerView.canScrollVertically(1)) {
//                        menuTabLayout.addOnTabSelectedListener(null);
                        TabLayout.Tab tab = menuTabLayout.getTabAt(menuTabLayout.getTabCount() - 1);
                        if (tab != null) {
                            tab.select();
                        }
//                        smoothScroller.setTargetPosition(productListWithHeaders.size() - 3);
//                        layoutManager.startSmoothScroll(smoothScroller);
//                        menuTabLayout.addOnTabSelectedListener(tabSelectedListener);

                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    if (productTabIndexes.containsKey(firstVisibleItem)) {
                        TabLayout.Tab tab = menuTabLayout.getTabAt(productTabIndexes.get(firstVisibleItem));
                        if (tab != null)
                            tab.select();
                    }


                }
            });
        } catch (Exception e) {
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
    public void onFragmentInteraction(VendorOffline vendor, ProductOffline product, boolean isbuffet) {
        try {
            if (getActivity() == null) {
                return;
            }
            MainApplicationOffline mainApplication = new MainApplicationOffline();

            MainApplicationOffline.clearOrder(1);
            HashMap<String, Object> map = new HashMap<>();
            try {

                map.put(CleverTapEvent.PropertiesNames.getSource(), "Menu");
                map.put(CleverTapEvent.PropertiesNames.is_bookmarked(), product.isBookmarked());
                map.put(CleverTapEvent.PropertiesNames.is_trending(), product.isTrendingItem());
                map.put(CleverTapEvent.PropertiesNames.getItem_name(), product.getName());
                map.put(CleverTapEvent.PropertiesNames.is_customised(), !product.containsSubProducts());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!product.isConfigurable()) {
                OrderProductOffline orderProduct = new OrderProductOffline();
                orderProduct.copy(product);
                MainApplicationOffline.getCart(1).addProductToCart(product.clone(), null,
                        mainApplication,
                        this,
                        (AppCompatActivity) getActivity(),
                        vendor.clone(), occasionId, map);
                int orderQty = mainApplication.getOrderQuantityForProduct(product.getId(), 1);
                MenuActivityOffline activity = (MenuActivityOffline) getActivity();
                activity.setUpCart();
                if (activity instanceof AddToCardLisenterOffline) {
                    AddToCardLisenterOffline addToCardLisenter = (AddToCardLisenterOffline) activity;
                    ProductOffline productClone = product.clone();
                    productClone.quantity = orderQty;
                    ((AddToCardLisenterOffline) activity).addToCart(vendor.clone(), productClone);
                }
                if (menuListAdapter != null) {
                    menuListAdapter.notifyDataSetChanged();
                }
            } else {
                MainApplicationOffline.getCart(1).addProductToCart(product.clone(), null,
                        mainApplication,
                        this,
                        (AppCompatActivity) getActivity(),
                        vendor.clone(), occasionId, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showCancelCartProduct(VendorOffline vendor, ProductOffline product, boolean isBuffet) {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            CartCancelDialogOffline cartCancelDialog = CartCancelDialogOffline.newInstance(vendor, product, this, isBuffet);
            cartCancelDialog.setCancelable(false);

            fragmentManager.beginTransaction()
                    .add(cartCancelDialog, "cart_cancel")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void validateAndAddProduct(VendorOffline vendor, ProductOffline product, boolean isBuffetVendor) {
        if (getActivity() != null) {
            if (!MainApplicationOffline.isVendorValid(vendor.getId(), 1)) {
                showCancelCartProduct(vendor, product, isBuffetVendor);
            }
        }
    }


    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEventOffline cartItemAddedEvent) {
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
            //menuListAdapter.changeProducts(productListWithHeaders, vendor);
            menuListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SEARCH) {//comes back from search activity
            getProductsFromDB();
        }
    }

    private void getProductsFromDB() {


        refreshData(products, false);

        getMenuAndUpdateUi(containsRecommended[0]);

    }

}
