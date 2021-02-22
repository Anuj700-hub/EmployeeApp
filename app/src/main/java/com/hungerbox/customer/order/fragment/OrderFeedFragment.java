package com.hungerbox.customer.order.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.DownloadDone;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.AllVendorResponse;
import com.hungerbox.customer.model.BookMarkMenu;
import com.hungerbox.customer.model.BookMarkMenuData;
import com.hungerbox.customer.model.BookMarkMenuResponse;
import com.hungerbox.customer.model.CurrentVendorResponse;
import com.hungerbox.customer.model.DataHandlerExtras;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.HomeBannerItem;
import com.hungerbox.customer.model.HomeBannerItemResponse;
import com.hungerbox.customer.model.HomeBannerResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.RecentOrders;
import com.hungerbox.customer.model.RecentOrdersResponse;
import com.hungerbox.customer.model.TrendingMenuItem;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.navmenu.activity.BookMarkActivity;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.DataHandler;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.AddToCardLisenter;
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.OrderDetailNewActivity;
import com.hungerbox.customer.order.activity.OrderQrActivity;
import com.hungerbox.customer.order.adapter.BottomNavigationItemView;
import com.hungerbox.customer.order.adapter.MenuListAdapter;
import com.hungerbox.customer.order.adapter.OrderFeedAdpater;
import com.hungerbox.customer.order.adapter.OrderTrackerItemAdapter;
import com.hungerbox.customer.order.adapter.viewholder.VendorHeaderItemHolder;
import com.hungerbox.customer.order.listeners.Cartable;
import com.hungerbox.customer.order.listeners.ErrorSnackBarActionListener;
import com.hungerbox.customer.order.listeners.FragmentAttachListener;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.spaceBooking.SpaceBookingActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.SmsRetrieverReceiver;
import com.hungerbox.customer.util.view.CustomNonScrollingLayoutManager;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.GenericPopUpFragment;
import com.hungerbox.customer.util.view.RoundedRectangle;
import com.hungerbox.customer.util.view.TrackerItemStatusView;
import com.hungerbox.customer.util.view.WastagePopUpFragment;
import com.simpl.android.fingerprint.SimplFingerprint;
import com.simpl.android.fingerprint.SimplFingerprintListener;
import com.squareup.otto.Subscribe;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class OrderFeedFragment extends Fragment implements Cartable, VendorValidationListener, CartCancelDialog.OnFragmentInteractionListener , GlobalActivity.SlotBookingOrderSuccessListener {

    public String location = "India T";
    public String city = "Bangalore";
    public long locationId;
    public TextView tvCart, tvExtraCharges, tvTitle, tvCartAmount;
    public SwipeRefreshLayout refreshLayout;
    public RelativeLayout flFloatingContainer;
    protected Button btCheckout;
    public RecyclerView rvOrderFeed;
    public OrderFeedAdpater orderFeedAdpater;
    private OrderTrackerItemAdapter orderTrackerItemAdapter;
    public Button btQr;
    public ImageView btLeft, btRight;
    private ViewPager vpItemName;
    private TrackerItemStatusView itemStatusView;
    private TextView tvOrderId;
    private HashMap<String, Order> idOrderHashmap = new HashMap<String,Order>();
    TextView tvNoVendorFound;
    Realm realm;
    public Handler orderTrackerHandler, onBoardingHandler;
    private Runnable orderTrackerRunnable;
    private int trackerDirection = 0;
    public LinearLayout qrButtonSection;
    long ocassionId;
    private ArrayList<Object> baseBanners = new ArrayList<>();
    private ArrayList<Object> popupBanner = new ArrayList<>();
    private ArrayList<String> bannerKey = new ArrayList<>();
    private Activity activity;
    private RelativeLayout baseView;
    public ShimmerFrameLayout shimmerView;
    private ArrayList<Vendor> vendorList = new ArrayList<>();
    public HomeBannerItem banner;
    public TextView tvAccepted, tvReady, tvDelivered;
    public OrderProduct currentOrderItem;
    private boolean openQR = false;
    //private View currentOrderQrImage;
    private TextView powered;
    private SmsRetrieverReceiver smsRetreiverReceiver;
    private InstallStateUpdatedListener installListener;
    public boolean isFeedbackDialogTOBeShown = false;
    public boolean isWastagePopupToBeShown = false;
    public Order orderForFeedback;
    public FeedbackFragment feedbackFragment;
    private  HashMap<String, String> banners_map;
    //public CardView cvCollect;
    //public RelativeLayout rlQr;
    GenericPopUpFragment errorHandleDialog;
    WastagePopUpFragment wastageDialog;
    //public View view1;
    //private TextView tvSelfCollect;
    MainApplication mainApplication;
    int cartPositionInBottomBar = -1, index=0;
    private ConstraintSet buttonConstraint;
    public ConstraintLayout orderStatusCl;
    public TextView orderPipHeader;
    public Guideline firstVerticalGuide,secondVerticalGuide,thirdVerticalGuide;
    private FragmentAttachListener fragmentAttachListener;
    private MenuListAdapter bookmarkListAdapter, trendingListAdapter;
    private ArrayList<Object> bookmarkProducts;
    private ArrayList<Object> trendingProducts;
    private ArrayList<Vendor> vendors;
    private CardView orderTracker;
    boolean isFeedbackShown = false;
    ArrayList<OrderProduct> trackerItems = new ArrayList<OrderProduct>();
    String tagForApiRequest = "";
    private final int max_retries = 4;
    Runnable activateSlotRunnable;
    Handler activateSlotHandler;


    public OrderFeedFragment() {
    }

    public static OrderFeedFragment newInstance(Activity activity, String campusLocation, String city, long ocassionId,FragmentAttachListener fragmentAttachListener) {
        OrderFeedFragment fragment = new OrderFeedFragment();
        fragment.activity = activity;
        fragment.mainApplication = (MainApplication) activity.getApplication();
        fragment.location = campusLocation;
        fragment.city = city;
        fragment.ocassionId = ocassionId;
        fragment.fragmentAttachListener = fragmentAttachListener;
        fragment.activateSlotHandler = new Handler();
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
        View view = inflater.inflate(R.layout.order_feed_container, container, false);

        if(getActivity() != null){
            if(getActivity() instanceof GlobalActivity){
                tagForApiRequest = ((GlobalActivity) getActivity()).getApiTag();
            }
        }

        powered = view.findViewById(R.id.powered);

        orderPipHeader = view.findViewById(R.id.tv_order_status);
        orderTracker = view.findViewById(R.id.current_order_tracker);
        btQr = view.findViewById(R.id.bt_qr_button_ot);
        btLeft = view.findViewById(R.id.bt_left_ot);
        btRight = view.findViewById(R.id.bt_right_ot);
        itemStatusView = view.findViewById(R.id.item_status_view_ot);
        vpItemName = view.findViewById(R.id.vp_item_name_ot);
        tvOrderId = view.findViewById(R.id.tv_item_id_ot);
        tvAccepted = view.findViewById(R.id.tv_ot_accepted);
        tvReady = view.findViewById(R.id.tv_ot_ready);
        tvDelivered = view.findViewById(R.id.tv_ot_delivered);
        qrButtonSection = view.findViewById(R.id.qr_button_section);
//        cvCollect = view.findViewById(R.id.cv_collect);
//        rlQr = view.findViewById(R.id.rl_qr);
//        view1 = view.findViewById(R.id.view);
        //tvSelfCollect = view.findViewById(R.id.tv_self_collect);
        firstVerticalGuide = view.findViewById(R.id.guideA);
        secondVerticalGuide = view.findViewById(R.id.guideB);
        thirdVerticalGuide= view.findViewById(R.id.guideC);

        AppUtils.createShortcut(getActivity().getApplicationContext());
        generatePayload();

        if (AppUtils.getConfig(activity).getPowered_by_merc() != null && !AppUtils.getConfig(activity).getPowered_by_merc().equals("")) {
            powered.setVisibility(View.VISIBLE);
            powered.setText(AppUtils.getConfig(activity).getPowered_by_merc());
        } else {
            powered.setVisibility(View.GONE);
        }

        //currentOrderQrImage = view.findViewById(R.id.qr);
        orderStatusCl = view.findViewById(R.id.cl_order_status_parent);

        baseView = view.findViewById(R.id.rl);
        shimmerView = view.findViewById(R.id.shimmer_view_container);
        shimmerView.startShimmer();

        //((ShimmerFrameLayout) (view.findViewById(R.id.shimmer_view_container))).startShimmer();
        rvOrderFeed = view.findViewById(R.id.rv_order_feed);
        tvNoVendorFound = view.findViewById(R.id.tv_no_vendors_found);
        refreshLayout = view.findViewById(R.id.srl_vendorList);

        flFloatingContainer = view.findViewById(R.id.fl_cart_container);
        tvCart = view.findViewById(R.id.tv_cart);
        tvExtraCharges = view.findViewById(R.id.tv_extra_charge_label);
        tvCartAmount = view.findViewById(R.id.tv_order_amount);
        btCheckout = view.findViewById(R.id.bt_checkout);

        EventUtil.FbEventLog(activity, EventUtil.SCREEN_OPEN_HOME, EventUtil.SCREEN_HOME);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLogic(false);
            }
        });

        flFloatingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtil.logBaseEvent(activity, EventUtil.CHECKOUT_CLICK);
                navigateToOrderReview(view);

                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_CHECKOUT, jo);

                    Bundle bundle = new Bundle();
                    bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                    EventUtil.FbEventLog(activity, EventUtil.MixpanelEvent.MENU_CHECKOUT, bundle);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        });
        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUtil.FbEventLog(activity, EventUtil.HOME_CHECKOUT_RECOMMENDED, EventUtil.SCREEN_HOME);
                EventUtil.logBaseEvent(activity, EventUtil.CHECKOUT_CLICK);
                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                    HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.MENU_CHECKOUT, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                navigateToOrderReview(v);
            }
        });
        location = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        SharedPrefUtil.putBoolean(ApplicationConstants.SHOULD_REFRESH_FROM_CHAT, false);

        tvNoVendorFound.setVisibility(View.GONE);

        ((GlobalActivity) getActivity()).runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    if (Build.VERSION.SDK_INT >= 26) {
                        if (!isAdded() || isHidden() || (!((GlobalActivity) activity).isRunning() && !getActivity().isInPictureInPictureMode())) {
                            return;
                        }
                    }
                    else {
                        if (!isAdded() || isHidden() || !((GlobalActivity) activity).isRunning()) {
                            return;
                        }
                    }
                    updateOrderTracker();
                    startUpdateTimer();
                }
                catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        };

        startOrderTracker();


        installListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                if (installState.installStatus() == InstallStatus.DOWNLOADED) {

                    if (AppUtils.getConfig(activity).isDirect_soft_update()) {
                        MainApplication.bus.post(new DownloadDone());
                    }
                }
            }
        };
        findCartPosition();

        if (AppUtils.getConfig(getActivity().getApplicationContext()).isDirect_soft_update()) {
            checkForPlayStoreUpdate();
        }

        return view;
    }

    public void findCartPosition(){
        for (int i = 0; i < ((GlobalActivity)activity).bottomNavBar.getChildCount(); i++) {
            BottomNavigationItemView v = (BottomNavigationItemView) ((GlobalActivity)activity).bottomNavBar.getChildAt(i);
            Log.d("nav", v.getItemKey());
            if (v.getItemKey().equals(ApplicationConstants.CART)) {
                cartPositionInBottomBar = i;
                break;
            }
        }
    }

    public void refreshLogic(boolean exitPip) {

        String url = AppUtils.getConfigUrlForWatermark();

        if (AppUtils.isFlavorAllowed())
            ((GlobalActivity) getActivity()).initialiseWatermarkCalls(activity, url);
        else
            ((GlobalActivity) getActivity()).onAllApiCallSuccess("");
    }

    public void afterWatermarkHandling(){
        try {

            if (getActivity() != null) {
                SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false);
            }

            Calendar calendar = DateTimeUtil.adjustCalender(getActivity());
            long currentOccasionId = 0;
            for (int i = 0; i < ((GlobalActivity) getActivity()).occasionChooserAdapter.getOccassionResponse().size(); i++) {
                if (calendar.getTimeInMillis() >
                        DateTimeUtil.getTodaysTimeFromString(((GlobalActivity) getActivity()).occasionChooserAdapter.getOccassionResponse().get(i).startTime)
                        &&
                        calendar.getTimeInMillis() <
                                DateTimeUtil.getTodaysTimeFromString(((GlobalActivity) getActivity()).occasionChooserAdapter.getOccassionResponse().get(i).endTime)) {
                    currentOccasionId = ((GlobalActivity) getActivity()).occasionChooserAdapter.getOccassionResponse().get(i).id;
                    break;
                }
            }
            if (ocassionId != currentOccasionId && MainApplication.isCartCreated()) {
                showCartClearPopUp();
                refreshLayout.setRefreshing(false);
            } else {
                ((GlobalActivity) getActivity()).getOccassionList();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            refreshLayout.setRefreshing(false);
        }
    }

    private void showCartClearPopUp() {
        FreeOrderErrorHandleDialog orderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(new Order(),
                "Your cart will get cleared",
                new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                        mainApplication.clearOrder();
                        MainApplication.bus.post(new OrderClear());
                        addItemsToCart(-1);
                        ((GlobalActivity) getActivity()).getOccassionList();
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

    private void navigateToOrderReview(View view) {
        LogoutTask.updateTime();
//        Intent intent = new Intent(activity, OrderReviewActivity.class);
//        Intent intent = new Intent(activity, OrderReviewAndPaymentActivity.class);
        Intent intent = new Intent(activity, BookmarkPaymentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
        intent.putExtra("anim", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(fragmentAttachListener!=null){
            fragmentAttachListener.onAttach();
        }

    }

    public void setUpCart() {

//        Vendor vendor = mainApplication.getCart().getVendor();
//        if (vendor != null && activity != null) {
//            if (vendor.getDeliveryCharge() > 0
//                    || vendor.getConatainerCharge() > 0
//                    || vendor.getServiceTax() > 0
//                    || vendor.getTax() > 0
//                    || vendor.getCgst() > 0
//                    || vendor.getSgst() > 0
//                    || AppUtils.getConfig(activity).isConvenience_charge_applicable()) {
//                tvExtraCharges.setVisibility(View.VISIBLE);
//            } else {
//                tvExtraCharges.setVisibility(View.GONE);
//            }
//        }
        int cartQty = mainApplication.getTotalOrderCount();
        //double cardAmount = mainApplication.getTotalOrderAmount();
        addItemsToCart(cartQty);



    }

    private void addItemsToCart(int quantity){
        //pass negative number in quantity to clear cart
        if (cartPositionInBottomBar>-1){
            BottomNavigationItemView cartView = (BottomNavigationItemView) ((GlobalActivity) activity).bottomNavBar.getChildAt(cartPositionInBottomBar);
            if (quantity <= 0) {
                cartView.hideBadge();
            } else {
                cartView.showBadge(String.valueOf(quantity));
            }
        }
    }


    @Subscribe
    public void onOrderClearEvent(OrderClear orderClear) {
        flFloatingContainer.setVisibility(View.GONE);
    }

    public void startOrderTracker(){
        if(((GlobalActivity) getActivity()).runnable != null){
            final Handler handler = new Handler();
            handler.postDelayed(((GlobalActivity) getActivity()).runnable, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.bus.register(this);
        verifyLocationChange();
        setUpCart();

        startOrderTracker();

        if (orderFeedAdpater != null)
            orderFeedAdpater.onOrderItemChanged();

        openQR = false;

        try {
            if (SharedPrefUtil.getInt(ApplicationConstants.SSO_LOGIN_LOCATION_ASKED, 0) == 1) {
                ((GlobalActivity) activity).spLocation.performClick();
            }
            if (SharedPrefUtil.getBoolean(ApplicationConstants.SHOULD_REFRESH_FROM_CHAT, false)) {
                SharedPrefUtil.putBoolean(ApplicationConstants.SHOULD_REFRESH_FROM_CHAT, false);
                //For refreshing OrderDetail After Coming Back From Chat
//                getOrderDetail();
                updateOrderTracker();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        ((GlobalActivity) getActivity()).appUpdateManager.registerListener(installListener);

        try {
            ((GlobalActivity) getActivity()).appUpdateManager
                    .getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {

                @Override
                public void onSuccess(AppUpdateInfo appUpdateInfo) {

                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        if (AppUtils.getConfig(activity).isDirect_soft_update()) {
                            MainApplication.bus.post(new DownloadDone());
                        }
                    }
                }
            });
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        endTimer();

    }

    private void verifyLocationChange() {
        long newLocationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        String newLocationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "");
        if (locationId != newLocationId) {

            // clearing cart
            MainApplication mainApplication = (MainApplication) activity.getApplication();
            mainApplication.clearOrder();
            MainApplication.bus.post(new OrderClear());

            locationId = newLocationId;
            location = newLocationName;
            Activity activity = getActivity();
            if (activity != null) {
                GlobalActivity globalActivity = (GlobalActivity) activity;
                globalActivity.getOccassionList();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.bus.unregister(this);

        ((GlobalActivity) getActivity()).appUpdateManager.unregisterListener(installListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activateSlotHandler!=null){
            activateSlotHandler.removeCallbacksAndMessages(null);
        }
    }

    private void getRecommendedItems() {
        baseView.setVisibility(View.GONE);
        shimmerView.setVisibility(View.VISIBLE);
        shimmerView.startShimmer();
        if (ocassionId == 0 || locationId == 0)
            return;
        String url = UrlConstant.GET_HOME_BANNER_API_V2;
        SimpleHttpAgent<HomeBannerResponse> recommendationHttpAgent = new SimpleHttpAgent<>(getActivity(),
                url,
                responseObject -> {

                    baseBanners = new ArrayList<>();
                    popupBanner = new ArrayList<>();
                    if (responseObject != null) {

                        if (getActivity() == null) {
                            if (MainApplication.appContext != null) {
                                Intent intent = new Intent(MainApplication.appContext, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                MainApplication.appContext.startActivity(intent);
                                return;
                            }
                        }

                        baseBanners.addAll(responseObject.getHomeBanner().getHomeBannerItemResponse().getHomeBannerItems());
                        try {
                            index=0;
                            popupBanner.clear();

                            for(Object hbi : baseBanners){
                                banner = (HomeBannerItem) hbi;
                                if(banner.getType().equalsIgnoreCase("banner_interstitial")){
                                    popupBanner.add(banner);
                                }
                            }

                            if (popupBanner.size() > 0) {
                                banner = (HomeBannerItem) popupBanner.get(0);
                                if (banner.getIs_one_time() == 1) {
                                    String key = banner.getName();
                                    String date = DateFormat.getDateInstance().format(new Date());
                                    banners_map = SharedPrefUtil.getMap(ApplicationConstants.INTERSTITIAL_BANNER_ID);
                                    String value = banners_map.get(key);
                                    if (!date.equalsIgnoreCase(value)) {
                                        if(!isFeedbackDialogTOBeShown){
                                            showWastageDialog();
                                        }else{
                                            isWastagePopupToBeShown = true;
                                        }
                                    }

                                } else {
                                    if(!isFeedbackDialogTOBeShown){
                                        showWastageDialog();
                                    }else{
                                        isWastagePopupToBeShown = true;
                                    }
                                }

                                baseBanners.removeAll(popupBanner);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        baseBanners = new ArrayList<>();
                    }

                    if (AppUtils.getConfig(activity).isOffer_banners_available()) {
                        getOffers();
                    } else {
                        getVendorFromServerAndUpdateView();
                    }


                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                AppUtils.HbLog("banner_error", error);
                getVendorFromServerAndUpdateView();
            }
        }, HomeBannerResponse.class);

        HashMap<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("location_id", locationId);
        requestPayload.put("occasion_id", ocassionId);
        recommendationHttpAgent.post(requestPayload, new HashMap<String, JsonSerializer>(), tagForApiRequest);
    }

    private void updateOrderTracker(){

        String url = UrlConstant.GET_RECENT_ORDERS;

        SimpleHttpAgent<RecentOrdersResponse> recentOrdersHttpAgent = new SimpleHttpAgent<>(getActivity(),
                url,
                responseObject -> {

                    if(responseObject != null){

                        ArrayList<Order> closedOrderList = responseObject.getRecentOrders().getLastClosedOrders().getLastClosedOrdersList();

                        if(closedOrderList.size() > 0){

                            updateIgnoreList(closedOrderList);

                            int userEmployeeType = SharedPrefUtil.getInt(ApplicationConstants.PREF_USER_EMP_TYPE_ID, 0);

                            if (AppUtils.getConfig(getActivity()).isShould_take_review_on_start() && AppUtils.getConfig(getActivity()).employee_allowed_for_review(userEmployeeType)) {


                                if (getActivity() != null && ((GlobalActivity) getActivity()).emailActivationDialog != null) {
                                    if (((GlobalActivity) getActivity()).emailActivationDialog.getDialog() != null && !((GlobalActivity) getActivity()).emailActivationDialog.getDialog().isShowing()) {
                                        askUserForFeedback(closedOrderList);
                                    }

                                }
                                else {
                                    askUserForFeedback(closedOrderList);
                                }
                            }
                        }
                        else{
                            updateIgnoreList(new ArrayList<Order>());
                        }

                        updateRecentOrders(responseObject.getRecentOrders());
                    }
                    else{
                        AppUtils.HbLog("recent_orders_error", "recent orders response is null");
                        orderTracker.setVisibility(View.GONE);
                    }

                },
                (errorCode, error, errorResponse) -> {

                    if(error != null && !error.trim().isEmpty()){
                        AppUtils.HbLog("recent_orders_error", error);
                        AppUtils.showToast(error, true, 0);
                    }
                    orderTracker.setVisibility(View.GONE);

                }, RecentOrdersResponse.class);

        recentOrdersHttpAgent.get(tagForApiRequest);
    }


    private void updateIgnoreList(ArrayList<Order> orders){
        Set<Long> idSet = new HashSet<Long>(SharedPrefUtil.getLongArrayList(ApplicationConstants.IGNORE_FEEDBACK_ON_SKIP_LIST));

        ArrayList<Long> ignoreUpdatedList = new ArrayList<Long>();

        for(Order order : orders){

            if(idSet.contains(order.getId())){
                ignoreUpdatedList.add(order.getId());
            }
        }

        SharedPrefUtil.putLongArrayList(ApplicationConstants.IGNORE_FEEDBACK_ON_SKIP_LIST, ignoreUpdatedList);

    }

    private void updateRecentOrders(RecentOrders recentOrders) {

        try {
            if (orderTrackerHandler != null) {
                orderTrackerHandler.removeCallbacks(orderTrackerRunnable);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        ArrayList<Order> slotBookingOrders = new ArrayList<>();
        ArrayList<OrderProduct> sortedList = getSortedOrderItemsList(recentOrders.getLastOpenOrders().getLastOpenOrdersList(),slotBookingOrders);

        if (sortedList.size() > 0) {
            orderTracker.setVisibility(View.VISIBLE);
            updateOrderTrackerUI(sortedList);
        } else {

            orderTracker.setVisibility(View.GONE);


            try {

                ((GlobalActivity) getActivity()).pictureInPictureRequired = false;
                if (slotBookingOrders.isEmpty()) {
                    ((GlobalActivity) getActivity()).handler.removeCallbacks(((GlobalActivity) getActivity()).runnable);
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    if (getActivity().isInPictureInPictureMode()) {
                        getActivity().finish();
                    }
                }

            } catch (Exception exp) {
                exp.printStackTrace();
            }

        }
        handleSlotBookingOrder(slotBookingOrders,recentOrders, 0 );
    }
    private void handleSlotBookingOrder(ArrayList<Order> slotBookingOrders,RecentOrders recentOrders, final int attemptCount){


        activateSlotRunnable = () -> {
            if (orderFeedAdpater!=null) {
                if (recentOrders.getLastOpenOrders().getLastOpenOrdersList()!=null && !recentOrders.getLastOpenOrders().getLastOpenOrdersList().isEmpty()) {

                    if (!slotBookingOrders.isEmpty()) {
                        boolean orderFound = false;
                        for (Order slotBookingOrder : slotBookingOrders) {
                            if (slotBookingOrder.getLocationId() == locationId && slotBookingOrder.getOccasionId() == MainApplication.selectedOcassionId) {
                                orderFeedAdpater.activateSlotBookingView(slotBookingOrder);
                                orderFound = true;
                                break;
                            }
                        }
                        if (!orderFound) {
                            orderFeedAdpater.deActivateSlotBookingView();
                        }
                    } else {
                        orderFeedAdpater.deActivateSlotBookingView();
                    }
                } else{
                    orderFeedAdpater.deActivateSlotBookingView();
                }
            } else {
                if (attemptCount<= max_retries) {
                    handleSlotBookingOrder(slotBookingOrders, recentOrders, attemptCount + 1);
                }
            }

        };
        if (orderFeedAdpater!=null){
            activateSlotHandler.post(activateSlotRunnable);
        } else {
            activateSlotHandler.postDelayed(activateSlotRunnable, 1000);
        }
    }

    private ArrayList<OrderProduct> getSortedOrderItemsList(ArrayList<Order> orders, ArrayList<Order> receiveSlotBookingOrders){
        ArrayList<OrderProduct> products = new ArrayList<OrderProduct>();

        idOrderHashmap.clear();

        HashMap<String, Integer> orderStatePriority = getOrderStateValues();

        for(Order order : orders){
            if (order.vendor.vendor.isSlotBookingVendor()){
                receiveSlotBookingOrders.add(order);
            }
            else if (shouldAddToTracker(order)) {

                for (OrderProduct product : order.getProducts()) {
                    product.setOrderId(order.getOrderId());
                    products.add(product);
                }
                idOrderHashmap.put(order.getOrderId(), order);
            }

        }

        ArrayList<OrderProduct> finalProducts = new ArrayList<OrderProduct>();

        for(OrderProduct product : products){

            if(orderStatePriority.get(product.getStatus()) != null){
                boolean isProductAdded = false;
                int i = 0;
                while(i < finalProducts.size()){
                    if(orderStatePriority.get(product.getStatus()) > orderStatePriority.get(finalProducts.get(i).getStatus())){
                        finalProducts.add(i, product);
                        isProductAdded = true;
                        break;
                    }
                    i++;
                }
                if(!isProductAdded){
                    finalProducts.add(product);
                }
            }

        }

        return finalProducts;
    }
    private boolean shouldAddToTracker(Order order){
        if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.HANDED_OVER)){
            return false;
        }
        else if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING)){
            long preOrderTime = order.getPreOrderDeliveryTime() * 1000;
            long currentTime = DateTimeUtil.adjustCalender(getActivity()).getTimeInMillis();
            return ((preOrderTime > currentTime) && ((preOrderTime - currentTime) <= AppUtils.getConfig(getActivity()).getPre_order_tracker_visible_time()));
        }
        else{
            return true;
        }
    }

    private HashMap<String, Integer> getOrderStateValues(){
        HashMap<String, Integer> values = new HashMap<>();
        values.put(OrderUtil.PROCESSED, 6);
        values.put(OrderUtil.CONFIRMED, 5);
        values.put(OrderUtil.NEW, 4);
        values.put(OrderUtil.PRE_ORDER, 3);
        values.put(OrderUtil.APPROVAL_PENDING, 2);
        values.put(OrderUtil.DELIVERED, 1);
        return values;
    }

    private void updateOrderTrackerUI(ArrayList<OrderProduct> products){

        try{
            orderTracker.setOnClickListener(null);
            trackerItems = products;

            orderTrackerHandler = new Handler();
            orderTrackerRunnable = new Runnable() {
                @Override
                public void run() {

                    if (activity != null) {

                        if (!((GlobalActivity) activity).isRunning()) {

                            if (products.size() > 1) {
                                orderTrackerHandler.postDelayed(this, AppUtils.getConfig(activity).getOrder_tracker_auto_swipe_time());
                            }
                            return;

                        }

                        try {

                            if (vpItemName.getCurrentItem() >= products.size() - 1) {
                                trackerDirection = -1;
                            }
                            else if (vpItemName.getCurrentItem() <= 0) {
                                trackerDirection = 1;
                            }

                            swipeToItem(vpItemName.getCurrentItem() + trackerDirection);

                            if (products.size() > 1) {
                                orderTrackerHandler.postDelayed(this, AppUtils.getConfig(activity).getOrder_tracker_auto_swipe_time());
                            }
                        }
                        catch (Exception exp) {
                            exp.printStackTrace();
                        }

                    }
                }
            };

            orderTrackerHandler.postDelayed(orderTrackerRunnable, AppUtils.getConfig(activity).getOrder_tracker_auto_swipe_time());

            if(vpItemName.getAdapter() == null){
                orderTrackerItemAdapter = new OrderTrackerItemAdapter(activity, products);
                vpItemName.setAdapter(orderTrackerItemAdapter);
                vpItemName.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        updateOrderTrackerStatus(position);
                        currentOrderItem = trackerItems.get(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
            else{
                orderTrackerItemAdapter.changeProducts(products);
                swipeToItem(0);
            }
            updateOrderTrackerStatus(0);
            currentOrderItem = trackerItems.get(0);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void swipeToItem(int position){
        new Handler().post(() -> vpItemName.setCurrentItem(position, true));
    }

    private void updateOrderTrackerStatus(int position){

        try {
            String orderId = trackerItems.get(position).getOrderId();
            Order currentOrder = idOrderHashmap.get(orderId);
            itemStatusView.setItemStatus(trackerItems.get(position), currentOrder.isSpaceBookingOrder());
            tvOrderId.setText(orderId);

            if(currentOrder.isSpaceBookingOrder()){
                tvAccepted.setText("Confirmed");
                tvReady.setText("In Progress");
                tvDelivered.setText("Fulfilled");
            }
            else{
                tvAccepted.setText("Accepted");
                tvReady.setText("Ready");
                tvDelivered.setText("Delivered");
            }

            if(trackerItems.get(position).getStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER) || currentOrder.isSpaceBookingOrder() || (AppUtils.isSocialDistancingActive(currentOrder.getLocation()) && !currentOrder.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY))){

                btQr.setText("View Details");
                btQr.setBackgroundResource(R.drawable.button_collect_order_enabled);
                btQr.setEnabled(true);
                btQr.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, OrderDetailNewActivity.class);
                    intent.putExtra(ApplicationConstants.BOOKING_ID, currentOrder.getId());
                    startActivity(intent);
                });

            }
            else{
                if (AppUtils.getConfig(activity).isHide_qrcode(currentOrder.getLocation()))
                    btQr.setText("Collect Order");
                else
                    btQr.setText(" View QR ");

                if(AppUtils.isKitchenSystemEnabled(currentOrder)){
                    if(trackerItems.get(position).getStatus().equalsIgnoreCase(OrderUtil.NEW) || trackerItems.get(position).getStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)){
                        btQr.setBackgroundResource(R.drawable.button_collect_order);
                        btQr.setEnabled(false);
                        btQr.setOnClickListener(null);
                    }
                    else{
                        btQr.setBackgroundResource(R.drawable.button_collect_order_enabled);
                        btQr.setEnabled(true);
                        btQr.setOnClickListener(v -> openQrActivity(currentOrder));
                    }
                }
                else{
                    btQr.setBackgroundResource(R.drawable.button_collect_order_enabled);
                    btQr.setEnabled(true);

                    if(AppUtils.getConfig(activity).isHide_qrcode(currentOrder.getLocation())){

                        if(trackerItems.get(position).getStatus().equalsIgnoreCase(OrderUtil.NEW) || trackerItems.get(position).getStatus().equalsIgnoreCase(OrderUtil.CONFIRMED))
                            btQr.setOnClickListener(v -> showPopUp());
                        else
                            btQr.setOnClickListener(v -> openQrActivity(currentOrder));

                    }
                    else{
                        btQr.setOnClickListener(v -> openQrActivity(currentOrder));
                    }
                }
            }

            if(position > 0){
                btLeft.setVisibility(View.VISIBLE);
                btLeft.setOnClickListener(v -> {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            swipeToItem(position - 1);
                        }
                    });
                });
            }
            else{
                btLeft.setVisibility(View.INVISIBLE);
            }

            if(position < (trackerItems.size() - 1)){
                btRight.setVisibility(View.VISIBLE);
                btRight.setOnClickListener(v -> {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            swipeToItem(position + 1);
                        }
                    });
                });
            }
            else{
                btRight.setVisibility(View.INVISIBLE);
            }

            handlePipOrderStatus(trackerItems.get(position));

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void handlePipOrderStatus(OrderProduct orderProduct) {

        if (getActivity() != null && orderProduct.getStatus() != null) {

            boolean pipModeEnabled = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                pipModeEnabled = getActivity().isInPictureInPictureMode();
            }

            if (pipModeEnabled) {

                if (orderProduct.getStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)) {
                    tvAccepted.setVisibility(View.VISIBLE);
                    tvReady.setVisibility(View.INVISIBLE);
                    tvDelivered.setVisibility(View.INVISIBLE);

                }
                else if (orderProduct.getStatus().equalsIgnoreCase(OrderUtil.PROCESSED)) {

                    tvAccepted.setVisibility(View.INVISIBLE);
                    tvReady.setVisibility(View.VISIBLE);
                    tvDelivered.setVisibility(View.INVISIBLE);

                }
                else if (orderProduct.getStatus().equalsIgnoreCase(OrderUtil.DELIVERED)) {

                    tvAccepted.setVisibility(View.INVISIBLE);
                    tvReady.setVisibility(View.INVISIBLE);
                    tvDelivered.setVisibility(View.VISIBLE);

                }
            }
            else {
                tvAccepted.setVisibility(View.VISIBLE);
                tvReady.setVisibility(View.VISIBLE);
                tvDelivered.setVisibility(View.VISIBLE);
            }

        }
    }

    public void openQrActivity(Order currentOrder){

        if (!openQR) {
            Intent intent = new Intent(activity, OrderQrActivity.class);
            intent.putExtra("order", currentOrder);

            activity.startActivity(intent);

            openQR = true;
        }
    }

    private com.bumptech.glide.request.target.SimpleTarget target = new com.bumptech.glide.request.target.SimpleTarget<Drawable>() {

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                banner = (HomeBannerItem) popupBanner.get(0);
                if(wastageDialog!=null){
                    if(wastageDialog.isVisible()) {
                        return;
                    }
                }
                wastageDialog = WastagePopUpFragment.newInstance(resource, activity, banner.getLink(), banner.getName(), new WastagePopUpFragment.OnFragmentInteractionListener(){

                    @Override
                    public void onNegativeInteraction() {
                        if (banner.getIs_one_time() == 1) {
                            String key = banner.getName();
                            String date = DateFormat.getDateInstance().format(new Date());
                            banners_map = SharedPrefUtil.getMap(ApplicationConstants.INTERSTITIAL_BANNER_ID);
                            String value = banners_map.get(key);
                            if (!date.equalsIgnoreCase(value)) {
                                banners_map.put(key, date);
                                SharedPrefUtil.putMap(ApplicationConstants.INTERSTITIAL_BANNER_ID, banners_map);
                            }

                            //delete
                            try {
                                bannerKey.clear();
                                Iterator<Map.Entry<String, String>> it = banners_map.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry<String, String> pair = it.next();
                                    if (!pair.getValue().equals(date)) {
                                        bannerKey.add(pair.getKey());
                                    }
                                }

                                for(int i = 0; i< bannerKey.size(); i++){
                                    banners_map.remove(bannerKey.get(i));
                                }
                                SharedPrefUtil.putMap(ApplicationConstants.INTERSTITIAL_BANNER_ID, banners_map);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(wastageDialog, "wastage dialog")
                        .commitAllowingStateLoss();
        }

        @Override
        public void onLoadFailed(Drawable errorDrawable) {
            Log.e("load_eroor","image not loaded");
        }
    };

    public void showWastageDialog(){
        if(getActivity()!=null) {
            ImageHandling.loadBackgroundImageInViewWithCallback(((HomeBannerItem) popupBanner.get(0)).getImage(), getActivity(), target);
        }
    }

    private void askUserForFeedback(ArrayList<Order> closedOrderList){

        if ((GlobalActivity) getActivity() == null || ((GlobalActivity) getActivity()).deskRefRequired) {
            return;
        }

        if(AppUtils.getConfig(activity).is_feedback_skip_allowed() && isFeedbackShown){
            return;
        }


        for(Order order: closedOrderList){

            if (isFeedbackEligibleToShow(order)) {

                isFeedbackShown = true;
                isFeedbackDialogTOBeShown = true;
                orderForFeedback = order;
                showFeedbackDialog(order);
                break;
            }
        }

        if(!isFeedbackShown){
            isFeedbackDialogTOBeShown = false;
            showWastagePopup();
        }


    }

    void showWastagePopup(){
        if(isWastagePopupToBeShown){
            isWastagePopupToBeShown = false;
            showWastageDialog();
        }
    }

    public void showNextEligibleFeedback(boolean fromMendatory){

        if(!fromMendatory && isFeedbackShown){
            return;
        }
        String url = UrlConstant.GET_RECENT_ORDERS;

        SimpleHttpAgent<RecentOrdersResponse> recentOrdersHttpAgent = new SimpleHttpAgent<>(getActivity(),
                url,
                responseObject -> {

                    if(responseObject != null){

                        ArrayList<Order> closedOrderList = responseObject.getRecentOrders().getLastClosedOrders().getLastClosedOrdersList();

                        if(closedOrderList.size() > 0){
                            askUserForFeedback(closedOrderList);
                        }
                        else{
                            isFeedbackDialogTOBeShown = false;
                            showWastagePopup();
                        }

                    }
                    else{
                        AppUtils.HbLog("recent_orders_error", "recent orders response is null");
                        isFeedbackDialogTOBeShown = false;
                        showWastagePopup();
                    }

                },
                (errorCode, error, errorResponse) -> {

                    if(error != null && !error.trim().isEmpty()){
                        AppUtils.HbLog("recent_orders_error", error);
                        AppUtils.showToast(error, true, 0);
                    }


                    isFeedbackDialogTOBeShown = false;
                    showWastagePopup();

                }, RecentOrdersResponse.class);

        recentOrdersHttpAgent.get(tagForApiRequest);
    }

    private void showFeedbackDialog(Order order){

        feedbackFragment = FeedbackFragment.newInstance(order.getId(), "food", order.getVendorId(), order.getVendorName(),
                order.getProductItemList(), ApplicationConstants.HISTORY_TYPE_ORDER, order,new FeedbackFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction() {
                        if (AppUtils.getConfig(activity).is_feedback_skip_allowed()){
                            isFeedbackDialogTOBeShown = false;
                            showWastagePopup();
                        }
                        else {
                            showNextEligibleFeedback(true);
                        }
                    }

                    @Override
                    public void dismissPopup() {
                        isFeedbackDialogTOBeShown = false;
                        showWastagePopup();
                    }
                });


        if (AppUtils.getConfig(activity).is_feedback_skip_allowed())
            feedbackFragment.setCancelable(true);
        else
            feedbackFragment.setCancelable(false);

        if (((GlobalActivity) getActivity()).dialogFragment != null) {

            if (!((GlobalActivity) getActivity()).dialogFragment.isVisible()) {
                replaceGlobalDialog(feedbackFragment);
            }
        }
        else {
            replaceGlobalDialog(feedbackFragment);
        }
    }

    private boolean isFeedbackEligibleToShow(Order order){

        if((order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.FULFILLED)) && (order.rating == null || order.rating.trim().isEmpty())){

            if (AppUtils.getConfig(getContext()).isIgnore_feedback_on_skip() && isFeedbackIgnore(Long.valueOf(order.getId()))) {
                return false;
            }

            if (getActivity() != null && order != null && isResumed()) {

                if ((AppUtils.getConfig(getActivity()).getOrder_feedback_delay() != 0) && ((DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis() - order.getDeliveredAt() * 1000) < AppUtils.getConfig(getActivity()).getOrder_feedback_delay())) {
                    return false;
                }

                if (AppUtils.getConfig(getActivity()).getOrder_feedback_time_limit() != 0 && ((DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis() - order.getDeliveredAt() * 1000) > AppUtils.getConfig(getActivity()).getOrder_feedback_time_limit())) {
                    return false;
                }

                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }



    private boolean isFeedbackIgnore(Long currentOrderId){

        ArrayList<Long> ignoreList = SharedPrefUtil.getLongArrayList(ApplicationConstants.IGNORE_FEEDBACK_ON_SKIP_LIST);
        for(Long orderId : ignoreList){
            if(orderId.equals(currentOrderId)){
                return true;
            }
        }
        return false;
    }

    private void replaceGlobalDialog(FeedbackFragment feedbackFragment) {
        if (feedbackFragment != null) {
            ((GlobalActivity) getActivity()).dialogFragment = feedbackFragment;
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction()
                    .add(feedbackFragment, "feedback")
                    .commitAllowingStateLoss();
        }
    }


    private void checkForPlayStoreUpdate() {

        ((GlobalActivity) getActivity()).rlAppUpdate.setVisibility(View.GONE);

        try {
            ((GlobalActivity) getActivity()).appUpdateManager
                    .getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {

                @Override
                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                    try {
                        if (getActivity() != null && getActivity().getApplicationContext() != null) {

                            long lastUpdateTime = SharedPrefUtil.getLong(ApplicationConstants.DIRECT_SOFT_UPDATE_TIME, 0);
                            long dayDiff = Math.abs(System.currentTimeMillis() - lastUpdateTime);

                            if (!AppUtils.isCafeApp() && appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                try {
                                    ((GlobalActivity) getActivity()).appUpdateInfo = appUpdateInfo;
                                    ((GlobalActivity) getActivity()).rlAppUpdate.setVisibility(View.VISIBLE);
//                                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getUpdatePopupShow(), null, getActivity());
//
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (!AppUtils.isCafeApp() && appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
//                                HashMap<String, Object> map = new HashMap<>();
//                                map.put(CleverTapEvent.PropertiesNames.getMode(), "Auto");
//                                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getInstallUpdate(), map, getActivity());

                                if (!AppUtils.getConfig(getActivity()).isAuto_update()) {
                                    ((GlobalActivity) getActivity()).setAppDownloadView();

                                } else {
                                    ((GlobalActivity) getActivity()).appUpdateManager.completeUpdate();
                                }
                            } else if (!AppUtils.isCafeApp() && appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                ((GlobalActivity) getActivity()).rlAppUpdate.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }



    private void startUpdateTimer() {

        ((GlobalActivity) getActivity()).pictureInPictureRequired = true;
        try {
            if (((GlobalActivity) getActivity()).handler != null) {
                ((GlobalActivity) getActivity()).handler.removeCallbacks(((GlobalActivity) getActivity()).runnable);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        try {

            long totalTime = System.currentTimeMillis() - ((GlobalActivity) getActivity()).activityStartTime;
            if (AppUtils.getConfig(getActivity()).getOrder_tracker_refresh_time() != 0 && totalTime < 30 * 60 * 1000) {
                ((GlobalActivity) getActivity()).handler = new Handler();
                ((GlobalActivity) getActivity()).handler.postDelayed(((GlobalActivity) getActivity()).runnable, AppUtils.getConfig(getActivity()).getOrder_tracker_refresh_time());
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void getOffers() {
        String url = UrlConstant.GET_OFFER_BANNER;
        SimpleHttpAgent<HomeBannerItemResponse> recommendationHttpAgent = new SimpleHttpAgent<>(getActivity(),
                url, new ResponseListener<HomeBannerItemResponse>() {
            @Override
            public void response(HomeBannerItemResponse responseObject) {
                if (responseObject != null) {
                    baseBanners.addAll(responseObject.getHomeBannerItems());

                    getVendorFromServerAndUpdateView();
                }
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                AppUtils.HbLog("banner_error", error);
                getVendorFromServerAndUpdateView();
            }
        }, HomeBannerItemResponse.class);

        recommendationHttpAgent.get(tagForApiRequest);
    }


    private void getVendorFromServerAndUpdateView() {

        if (getActivity() == null || activity == null)
            return;

        if (ocassionId == 0) {
            saveVendorsToDb(new ArrayList<Vendor>());
            return;
        }

        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        String url = UrlConstant.LIST_VENDORS_V3 + "?locationId=" + locationId;

        HashMap<String, String> objectIds = new HashMap<>();
        objectIds.put(ApplicationConstants.OBJECT_ID_1, locationId + "");
        objectIds.put(ApplicationConstants.OBJECT_ID_2, "");

        DataHandlerExtras dHExtras = new DataHandlerExtras();
        dHExtras.setTag(tagForApiRequest);

        DataHandler<AllVendorResponse> userSimpleHttpAgent = new DataHandler<AllVendorResponse>(
                getActivity(),
                url,
                new ResponseListener<AllVendorResponse>() {
                    @Override
                    public void response(AllVendorResponse responseObject) {

                        if(SharedPrefUtil.getBoolean(ApplicationConstants.WATERMARK_FIREBASE_COUNT_SWITCH, true)){

                            // sending data
                            try {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put(CleverTapEvent.PropertiesNames.getOffline_calls(), String.valueOf(SharedPrefUtil.getInt(ApplicationConstants.WATERMARK_OFFLINE_CALLS, 1)));
                                map.put(CleverTapEvent.PropertiesNames.getOnline_calls(), String.valueOf(SharedPrefUtil.getInt(ApplicationConstants.WATERMARK_ONLINE_CALLS, 1)));
                                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getWatermark_calls(), map, activity);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            // reset calls
                            SharedPrefUtil.putBoolean(ApplicationConstants.WATERMARK_FIREBASE_COUNT_SWITCH, false);
                            SharedPrefUtil.putInt(ApplicationConstants.WATERMARK_ONLINE_CALLS, 0);
                            SharedPrefUtil.putInt(ApplicationConstants.WATERMARK_OFFLINE_CALLS, 0);

                        }

                        GlobalActivity globalActivity = (GlobalActivity) getActivity();
                        if (globalActivity != null && globalActivity.isRunning()) {
                        }
                        if (responseObject != null && responseObject.getVendors() != null && responseObject.getVendors().size() > 0) {

                            saveVendorsToDb(responseObject.getVendors());

                        } else {
                            if (globalActivity != null) {

                                saveVendorsToDb(new ArrayList<Vendor>());
                                showNoVendorError(() -> getVendorFromServerAndUpdateView());
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        Activity activity = getActivity();
                        if (activity != null) {

                            SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false);

                            if (activity instanceof GlobalActivity) {
                                GlobalActivity globalActivity = (GlobalActivity) activity;
                                if (globalActivity.isRunning()) {
                                    globalActivity.showError("Unable to find Food Partners", "retry", new ErrorSnackBarActionListener() {
                                        @Override
                                        public void action() {
                                            getVendorFromServerAndUpdateView();
                                        }
                                    });
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }
                },
                AllVendorResponse.class,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras
        );
    }

    private void saveVendorsToDb(ArrayList<Vendor> vendors){

        if (!DbHandler.isStarted())
            DbHandler.start(getContext());

        DbHandler dbHandler = DbHandler.getDbHandler(getContext());
        boolean vendorStored = dbHandler.createVendors(vendors);

        if(vendors.size()>0) {
            updateCurrentVendorsFromServer();
        }else {
            setVendorsAndUpdateUi();
        }
    }
    private void showNoVendorError(ErrorSnackBarActionListener listener){
        if (getActivity()!=null) {
            GlobalActivity globalActivity = (GlobalActivity) getActivity();
            globalActivity.showError(globalActivity.getResources().getString(R.string.no_vendor_found), "retry", listener);
        }
    }

    private void updateCurrentVendorsFromServer(){
        String url = UrlConstant.CURRENT_VENDORS_V3 + "?location_id=" + locationId + "&occasion_id=" + ocassionId;
        SimpleHttpAgent<CurrentVendorResponse> venSimpleHttpAgent = new SimpleHttpAgent<CurrentVendorResponse>(
                getActivity(),
                url,
                new ResponseListener<CurrentVendorResponse>() {
                    @Override
                    public void response(CurrentVendorResponse responseObject) {
                        GlobalActivity globalActivity = (GlobalActivity) getActivity();

                        if (responseObject != null && responseObject.getVendors() != null && responseObject.getVendors().size() > 0)
                        {

                            if (globalActivity != null) {
                                SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, true);
                            }

                            updateCurrentVendorsToDb(responseObject.getVendors());
                        } else {
                            if (globalActivity != null) {

                                SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false);

                                updateCurrentVendorsToDb(new ArrayList<Vendor>());
                                showNoVendorError(() -> updateCurrentVendorsFromServer());
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        shimmerView.stopShimmer();
                        shimmerView.setVisibility(View.GONE);
                        Activity activity = getActivity();
                        if (activity != null) {

                            SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false);

                            if (activity instanceof GlobalActivity) {
                                GlobalActivity globalActivity = (GlobalActivity) activity;
                                if (globalActivity.isRunning()) {
                                    globalActivity.showError("Unable to find current Food Partner", "retry", new ErrorSnackBarActionListener() {
                                        @Override
                                        public void action() {
                                            updateCurrentVendorsFromServer();
                                        }
                                    });
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }
                },
                CurrentVendorResponse.class
        );
        venSimpleHttpAgent.get(tagForApiRequest);
    }

    private void updateCurrentVendorsToDb(ArrayList<Vendor> vendors){

        if (!DbHandler.isStarted())
            DbHandler.start(getContext());

        DbHandler dbHandler = DbHandler.getDbHandler(getContext());

        for(Vendor vendor : vendors){
            Vendor vendorFromDb = dbHandler.getVendorFor(vendor.getId());
              if(vendorFromDb != null){
                  vendorFromDb.setAvgOrderTime(vendor.getAvgOrderTime());
                  vendorFromDb.setQueuedOrders(vendor.getQueuedOrders());
                  vendorFromDb.setStartTime(vendor.getStartTime());
                  vendorFromDb.setRating(vendor.getRating());
                  vendorFromDb.setEndTime(vendor.getEndTime());
                  vendorFromDb.setActive(1);
                  dbHandler.createVendor(vendorFromDb);
              }
          }

        setVendorsAndUpdateUi();

        if (AppUtils.getConfig(getContext()).isExpress_checkout()) {
            fetchBookmarkMenu();
        }else{
            hideExpressCheckoutViews(true,true);
        }
        refreshLayout.setRefreshing(false);
    }



    private void fetchBookmarkMenu() {
        String url = UrlConstant.BOOKMARK_FETCH + "?occasionId=" + ocassionId + "&locationId=" + locationId;
        //setBookmarkMenu(new BookMarkMenuData());
        SimpleHttpAgent<BookMarkMenuResponse> menuResponseSimpleHttpAgent = new SimpleHttpAgent<BookMarkMenuResponse>(
                getActivity(),
                url,
                new ResponseListener<BookMarkMenuResponse>() {
                    @Override
                    public void response(BookMarkMenuResponse responseObject) {
                        if (responseObject != null && responseObject.getBookMarkMenus() != null) {
                            setBookmarkMenu(responseObject.getBookMarkMenus());
                        }else{
                            hideExpressCheckoutViews(true,true);
                        }
                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                hideExpressCheckoutViews(true,true);
            }
        }, BookMarkMenuResponse.class);

        menuResponseSimpleHttpAgent.get(tagForApiRequest);
    }

    private void setBookmarkMenu(final BookMarkMenuData bookMarkMenuData) {

        if (bookMarkMenuData.getUserBookmarks() != null && bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus() != null && bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus().size() > 0) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(BookMarkMenu.class);
                    realm.copyToRealmOrUpdate(bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus());
                }
            });

        } else {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(BookMarkMenu.class);
                }
            });
        }

        if (bookMarkMenuData.getTrendingMenu() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus().size() > 0) {

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(TrendingMenuItem.class);
                    realm.copyToRealmOrUpdate(bookMarkMenuData.getTrendingMenu().getTrendingMenus());
                }
            });
        } else {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(TrendingMenuItem.class);
                }
            });
        }
        setBookmarkTrendingItems(bookMarkMenuData);
    }
    private void setBookmarkTrendingItems(BookMarkMenuData bookMarkMenuData) {
        if (getActivity()!=null) {
            bookmarkProducts = new ArrayList<>();
            bookmarkProducts.add("bookmark_image_label");
            trendingProducts = new ArrayList<>();
            trendingProducts.add("trending_image_label");
            List<Vendor> realmVendors = AppUtils.getAllActiveVendor(getActivity());
            HashMap<Long, String> vendorMap = new HashMap<>();
            vendors = new ArrayList<>();
            HashMap<Long, Vendor> vendorHashMap = new HashMap<>();

            for (Vendor vendor : realmVendors) {
                vendorMap.put(vendor.getId(), vendor.getVendorName());
                vendorHashMap.put(vendor.getId(), vendor);
            }

            if (bookMarkMenuData.getUserBookmarks() != null && bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus() != null && bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus().size() > 0) {

                final List<BookMarkMenu> bookMarkMenus = bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus();

                for (BookMarkMenu bookMarkMenu : bookMarkMenus) {
                    if (vendorMap.get(bookMarkMenu.getVendorId()) != null) {
                        if (vendorHashMap.get(bookMarkMenu.getVendorId())!=null
                                &&((vendorHashMap.get(bookMarkMenu.getVendorId()).isVendingMachine()
                                && AppUtils.getConfig(getContext()).isVendingMachineEnabled())
                            || !vendorHashMap.get(bookMarkMenu.getVendorId()).isVendingMachine())) {
                            bookMarkMenu.setVendorName(vendorMap.get(bookMarkMenu.getVendorId()));
                            Product itemToAdd = bookMarkMenu.copy();
                            itemToAdd.setRecommendationType(ApplicationConstants.RecommendationType.BOOKMARK);
                            bookmarkProducts.add(itemToAdd);
                            vendors.add(vendorHashMap.get(bookMarkMenu.getVendorId()));
                        }
                    }
                }
                if (orderFeedAdpater!=null) {
                    if (bookmarkProducts.size() > 1) {
                        orderFeedAdpater.setBookmarkItems(bookmarkProducts, vendors);
                    } else {
                        hideExpressCheckoutViews(true,false);
                    }
                }
            } else{
                hideExpressCheckoutViews(true, false);
            }

            if (bookMarkMenuData.getTrendingMenu() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus().size() > 0) {
                vendors = new ArrayList<>();
                final List<TrendingMenuItem> trendingMenuItems = bookMarkMenuData.getTrendingMenu().getTrendingMenus();
                for (TrendingMenuItem bookMarkMenu : trendingMenuItems) {
                    if (vendorMap.get(bookMarkMenu.getVendorId()) != null) {
                        if (vendorHashMap.get(bookMarkMenu.getVendorId())!=null
                                &&((vendorHashMap.get(bookMarkMenu.getVendorId()).isVendingMachine()
                                && AppUtils.getConfig(getContext()).isVendingMachineEnabled())
                                || !vendorHashMap.get(bookMarkMenu.getVendorId()).isVendingMachine())) {
                            bookMarkMenu.setVendorName(vendorMap.get(bookMarkMenu.getVendorId()));
                            bookMarkMenu.setTrendingItem(true);
                            Product itemToAdd = bookMarkMenu.copy();
                            itemToAdd.setRecommendationType(ApplicationConstants.RecommendationType.BOOKMARK_TRENDING);
                            trendingProducts.add(itemToAdd);
                            vendors.add(vendorHashMap.get(bookMarkMenu.getVendorId()));
                        }
                    }
                }
                if (orderFeedAdpater!=null) {
                    if (trendingProducts.size() > 1) {
                        orderFeedAdpater.setTrendingItems(trendingProducts,vendors);
                    } else {
                        hideExpressCheckoutViews(false,true);
                    }
                }

            }else{
                hideExpressCheckoutViews(false,true);
            }
            onBoardingHandler = new Handler();
            onBoardingHandler.postDelayed(() -> startOnboarding(),1000);
        }
    }
    private void hideExpressCheckoutViews(boolean hideFav, boolean hideTrending){
        if (orderFeedAdpater!=null && orderFeedAdpater.vendorHeaderItemHolder!=null
                && orderFeedAdpater.vendorHeaderItemHolder.layoutTrending!=null
                && orderFeedAdpater.vendorHeaderItemHolder.layoutFavourites!=null) {
            if (hideFav){
                orderFeedAdpater.vendorHeaderItemHolder.layoutFavourites.setVisibility(View.GONE);
            }
            if (hideTrending) {
                orderFeedAdpater.vendorHeaderItemHolder.layoutTrending.setVisibility(View.GONE);
            }

        }
        if (hideFav && hideTrending) {
            startOnboarding();
        }
    }

    private void setVendorsAndUpdateUi() {

        if (!DbHandler.isStarted())
            DbHandler.start(getContext());

        DbHandler dbHandler = DbHandler.getDbHandler(getContext());
        List<Vendor> vendorResults = dbHandler.getActiveVendorList();
        createVendorList(vendorResults);

        try {
            if (ocassionId != 0 && ((MainApplication) (getActivity().getApplication())).getCart() != null && ((MainApplication) (getActivity().getApplication())).getCart().getVendor() != null) {
                Vendor vendor = AppUtils.getVendorById(getActivity(),((MainApplication) (getActivity().getApplication())).getCart().getVendor().getId());
                if (vendor == null) {
                    ErrorPopFragment orderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(
                            "Oops! Selected Vendor Is Not Available Right Now. Please Select Other Vendor.","OK",true,ApplicationConstants.GENERAL_ERROR,
                            new ErrorPopFragment.OnFragmentInteractionListener() {
                                @Override
                                public void onPositiveInteraction() {
                                    MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                                    mainApplication.clearOrder();
                                    MainApplication.bus.post(new OrderClear());
                                    addItemsToCart(-1);
                                }

                                @Override
                                public void onNegativeInteraction() {

                                }
                            });

                    orderErrorHandleDialog.setCancelable(false);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(orderErrorHandleDialog, "cart_clear")
                            .commitAllowingStateLoss();
                }
            }
        } catch (Exception exp) {
        }
    }


    private void createVendorList(List<Vendor> vendorResult) {

        if (getActivity() == null || activity == null)
            return;

        if (vendorResult.size() > 0) {
            tvNoVendorFound.setVisibility(View.GONE);
        }
        else {
            tvNoVendorFound.setVisibility(View.VISIBLE);
            hideExpressCheckoutViews(true,true);
        }

        try {
            baseView.setVisibility(View.VISIBLE);
            shimmerView.stopShimmer();
            shimmerView.setVisibility(View.GONE);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        boolean isVendingMachineAvailable = false;
        ArrayList<Vendor> vendingMachineVendor = new ArrayList<>();
        ArrayList<Vendor> normalVendor = new ArrayList<>();
        Vendor slotBookingVendor = null;
        for(Vendor vendor : vendorResult){
            if(vendor.isVendingMachine()){
                if(AppUtils.getConfig(getActivity()).isVendingMachineEnabled()) {
                    vendingMachineVendor.add(vendor);
                    isVendingMachineAvailable = true;
                }
            }else{
                if (vendor.isSlotBookingVendor()){
                    slotBookingVendor = vendor;
                } else {
                    normalVendor.add(vendor);
                }
            }
        }
        ArrayList<Object> vendorList = new ArrayList<>();
        vendorList.add(normalVendor);
        vendorList.add(vendingMachineVendor);
        if (normalVendor.isEmpty() && vendingMachineVendor.isEmpty()){
            showNoVendorError(() -> getVendorFromServerAndUpdateView());
        }

        if (orderFeedAdpater == null) {
            orderFeedAdpater = new OrderFeedAdpater(tagForApiRequest,(GlobalActivity) activity,this,
                    vendorList,slotBookingVendor, this, ocassionId, baseBanners, location, getChildFragmentManager(), isVendingMachineAvailable, new OrderFeedAdpater.OnVendorHeaderClick() {
                @Override
                public void onClick(int pos) {
                    ((LinearLayoutManager) rvOrderFeed.getLayoutManager()).scrollToPositionWithOffset(2,pos*(-1));
                }
            });


            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2) {
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0) {
                        return 2;
                    } else if (position > vendorResult.size() + 2) {
                        return 2;
                    } else if (position > 1) {
                        return 1;
                    } else {
                        return 2;
                    }
                }
            });
            CustomNonScrollingLayoutManager customNonScrollingLayoutManager = new CustomNonScrollingLayoutManager(getActivity());

            rvOrderFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvOrderFeed.setAdapter(orderFeedAdpater);
//            rvOrderFeed.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                    rvOrderFeed.removeOnLayoutChangeListener(this);
//                    startOnboarding();
//
//                }
//            });
        } else {
            orderFeedAdpater.baseBanners = baseBanners;
            orderFeedAdpater.replaceVendors(vendorList,isVendingMachineAvailable);
            orderFeedAdpater.setOcassionId(ocassionId);
            orderFeedAdpater.setSlotBookingVendor(slotBookingVendor);
            orderFeedAdpater.notifyDataSetChanged();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showCancelCartProduct(Vendor vendor, Product product, boolean isBuffet) {
        CartCancelDialog cartCancelDialog = CartCancelDialog.newInstance(vendor, product, this, isBuffet);
        cartCancelDialog.setCancelable(false);
        cartCancelDialog.show(getActivity().getSupportFragmentManager(), "cart_Cancel");
    }

    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        if (cartItemAddedEvent.getOrderProduct().isExpressCheckout()) {
            navigateToOrderReviewView();
        } else if (orderFeedAdpater != null) {
            if (orderFeedAdpater.recommendationPagerAdapter != null)
                orderFeedAdpater.recommendationPagerAdapter.notifyDataSetChanged();
        }
        setUpCart();
    }

    @Subscribe
    public void downloadDone(DownloadDone downloadDone) {
        ((GlobalActivity) getActivity()).setAppDownloadView();
    }

    @Subscribe
    public void RemoveProductFromCart(RemoveProductFromCart removeProductFromCart) {
        setUpCart();
    }


    @Override
    public void onFragmentInteraction(Vendor vendor, Product product, boolean isBuffet) {
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        mainApplication.clearOrder();
        AppUtils.HbLog("ranjeet","ranjeet");
        addItemsToCart(-1);
        mainApplication.addProduct(product.clone(), vendor.clone(), new OrderProduct().copy(product), ocassionId);
        int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
        setUpCart();
        if (activity instanceof AddToCardLisenter) {
            Product productClone = product.clone();
            productClone.quantity = orderQty;
            ((AddToCardLisenter) activity).addToCart(vendor.clone(), productClone);
        }
        if (isBuffet) {
            navigateToOrderReviewView();
        }
        orderFeedAdpater.recommendationPagerAdapter.notifyDataSetChanged();
        orderFeedAdpater.onOrderItemChanged();
    }

    private void navigateToOrderReviewView() {
    }

    @Override
    public void validateAndAddProduct(Vendor vendor, Product product, boolean isBuffet) {
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        if (!mainApplication.isVendorValid(vendor.getId())) {
            showCancelCartProduct(vendor, product, isBuffet);
        }
    }

    public void updateViewWithSelectedOcassion(long occasionId) {
        try {
            if (activity == null) {
                activity = getActivity();
            }
        } catch (Exception exp) {
        }

        if (activity == null) {
            return;
        }

        if (occasionId == 0) {
            saveVendorsToDb(new ArrayList<Vendor>());
        }
        ocassionId = occasionId;
        getRecommendedItems();
        updateOrderTracker();
    }

//    private void fetchBookmarkMenu() {
//        if (getActivity()!=null) {
//            String url = UrlConstant.BOOKMARK_FETCH + "?occasionId=" + ocassionId + "&locationId=" + locationId;
//
//            SimpleHttpAgent<BookMarkMenuResponse> menuResponseSimpleHttpAgent = new SimpleHttpAgent<BookMarkMenuResponse>(getActivity(), url, new ResponseListener<BookMarkMenuResponse>() {
//                @Override
//                public void response(BookMarkMenuResponse responseObject) {
//                    if (responseObject != null && responseObject.getBookMarkMenus() != null) {
//                        setBookmarkMenu(responseObject.getBookMarkMenus());
//                    } else {
//                        // no bookmark found
//                    }
//                }
//            }, new ContextErrorListener() {
//                @Override
//                public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
//
//                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
//                        //no net
//
//                    } else {
//                        if (error != null && !error.equals("")) {
//                            //showErrorPopFragment(error, ApplicationConstants.SOME_WRONG);
//                        } else {
//                            //showErrorPopFragment(ApplicationConstants.SOME_WRONG, ApplicationConstants.SOME_WRONG);
//                        }
//                    }
//
//                }
//            }, BookMarkMenuResponse.class);
//
//            menuResponseSimpleHttpAgent.get();
//        }
//    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }



    private void generatePayload() {
        try {
            if(getActivity() == null){
                return;
            }
            SimplFingerprint.init(getContext().getApplicationContext(),"", "");
            SimplFingerprint.getInstance().generateFingerprint(
                    new SimplFingerprintListener() {
                        @Override
                        public void fingerprintData(String payload) {
                            if(getActivity() == null){
                                return;
                            }
                            try{
                                payload = URLEncoder.encode(payload, "UTF-8");
                            }catch (Exception exp){
                                exp.printStackTrace();
                            }
                            SharedPrefUtil.putString(ApplicationConstants.PAYLOAD, payload);
                        }
                    }
            );
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }


    public void startOnboarding(){


        if (!AppUtils.getConfig(getActivity()).isCoach_mark_visible()) {
            return;
        }

        if(((GlobalActivity) getActivity()).isTutorialShowing){
            return;
        }

        try {

            boolean tutorial_location = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_LOCATION, true);
            boolean tutorial_occassion = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_OCCASSION, true);
            boolean tutorial_swipe = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_SWIPE, true);
            boolean tutorial_bottom = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.BOOKMARK_IN_NAV, true);


            ArrayList<SimpleTarget> targets = new ArrayList();

            if (tutorial_location) {
                int[] oneLocation = new int[2];
                ((GlobalActivity) getActivity()).spLocation.getLocationInWindow(oneLocation);
                int oneX = oneLocation[0];
                int oneY = oneLocation[1];
                int height = ((GlobalActivity) getActivity()).spLocation.getHeight() + 10;
                int width = ((GlobalActivity) getActivity()).spLocation.getWidth() + 10;
                SimpleTarget simpleTarget1 = new SimpleTarget.Builder(getActivity())
                        .setPoint(oneX, oneY)
                        .setShape(new RoundedRectangle(height, width, 5))
                        .setTitle(AppUtils.getConfig(getActivity()).getTutorial_text()[0])
                        .setDuration(250)
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                            @Override
                            public void onStarted(SimpleTarget target) {
                            }

                            @Override
                            public void onEnded(SimpleTarget target) {
                                SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_LOCATION, false);
                            }
                        })
                        .build();

                targets.add(simpleTarget1);
            }

            if (tutorial_occassion) {
                int[] oneLocation = new int[2];
                ((GlobalActivity) getActivity()).tvOccasion.getLocationInWindow(oneLocation);
                int oneX = oneLocation[0];
                int oneY = oneLocation[1];
                int height = ((GlobalActivity) getActivity()).tvOccasion.getHeight() + 10;
                int width = ((GlobalActivity) getActivity()).tvOccasion.getWidth() + 10;
                SimpleTarget simpleTarget2 = new SimpleTarget.Builder(getActivity())
                        .setPoint(oneX, oneY)
                        .setDuration(250)
                        .setShape(new RoundedRectangle(height, width, 5))
                        .setTitle(AppUtils.getConfig(getActivity()).getTutorial_text()[1])
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                            @Override
                            public void onStarted(SimpleTarget target) {
                            }

                            @Override
                            public void onEnded(SimpleTarget target) {
                                SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_OCCASSION, false);
                            }
                        })
                        .build();

                targets.add(simpleTarget2);
            }
            VendorHeaderItemHolder holder = (VendorHeaderItemHolder) rvOrderFeed.findViewHolderForAdapterPosition(1);
            if (tutorial_swipe) {

                if (holder.layoutFavourites.getVisibility() == View.VISIBLE || holder.layoutTrending.getVisibility() == View.VISIBLE) {
                    int[] oneLocation = new int[2];
                    int height ,width, oneX, oneY;
                    if (holder.layoutFavourites.getVisibility() == View.VISIBLE && holder.layoutTrending.getVisibility() == View.VISIBLE ){
                        holder.layoutFavourites.getLocationInWindow(oneLocation);
                        oneY = Math.max(oneLocation[1] - 10,0);
                        oneX = Math.max(oneLocation[0] - 10, 0);
                        height = holder.layoutFavourites.getHeight()+holder.layoutFavourites.getHeight() + 50;
                        width = holder.layoutFavourites.getWidth() + 20;
                    }
                    else if (holder.layoutFavourites.getVisibility() == View.VISIBLE){
                        holder.layoutFavourites.getLocationInWindow(oneLocation);
                        oneY = Math.max(oneLocation[1] - 10,0);
                        oneX = Math.max(oneLocation[0] - 10, 0);
                        height = holder.layoutFavourites.getHeight() + 20;
                        width = holder.layoutFavourites.getWidth() + 20;
                    } else{
                        holder.layoutTrending.getLocationInWindow(oneLocation);
                        oneY = Math.max(oneLocation[1] - 10,0);
                        oneX = Math.max(oneLocation[0] - 10, 0);
                        height = holder.layoutTrending.getHeight() + 20;
                        width = holder.layoutTrending.getWidth() + 20;
                    }
                    targets.add(getTutorialTarget(oneX,oneY,height,width));
                } else {
                    SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_SWIPE, false);
                }
            }

            if (tutorial_bottom) {

                try {
                    int pos = -1;
                    for (int i = 0; i < ((GlobalActivity)activity).bottomNavBar.getChildCount(); i++) {
                        BottomNavigationItemView v = (BottomNavigationItemView) ((GlobalActivity)activity).bottomNavBar.getChildAt(i);
                        Log.d("nav", v.getItemKey());
                        if (v.getItemKey().equals(ApplicationConstants.BOOKMARK)) {
                            pos = i;
                            break;
                        }
                    }

                    if (pos != -1) {
                        int[] oneLocation = new int[2];
                        BottomNavigationItemView holders = (BottomNavigationItemView) ((GlobalActivity)activity).bottomNavBar.getChildAt(pos);
                        holders.getLogoImageView().getLocationInWindow(oneLocation);
                        int oneX = (int) (oneLocation[0] + holders.getLogoImageView().getWidth() / 2f);
                        int oneY = (int) (oneLocation[1] + holders.getLogoImageView().getHeight() / 2f);
                        SimpleTarget simpleTarget1 = new SimpleTarget.Builder(activity)
                                .setPoint(oneX, oneY)
                                .setDuration(250)
                                .setShape(new Circle((float) (1.25 * holders.getLogoImageView().getHeight() / 2f)))
                                .setTitle(AppUtils.getConfig(activity).getTutorial_text()[7])
                                .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                                    @Override
                                    public void onStarted(SimpleTarget target) {
                                    }

                                    @Override
                                    public void onEnded(SimpleTarget target) {
                                        SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.BOOKMARK_IN_NAV, false);
                                    }
                                }).build();
                        targets.add(simpleTarget1);

                    }else{
                        SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.BOOKMARK_IN_NAV, false);

                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                    SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.BOOKMARK_IN_NAV, false);
                }
            }

            if (targets.size() > 0) {

                Spotlight.with(getActivity())
                        .setOverlayColor(R.color.transBGG)
                        .setDuration(100L)
                        .setAnimation(new DecelerateInterpolator(1f))
                        .setTargets(targets.toArray(new SimpleTarget[targets.size()]))
                        .setClosedOnTouchedOutside(true)
                        .setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                            @Override
                            public void onStarted() {
                                ((GlobalActivity) getActivity()).isTutorialShowing = true;
                            }

                            @Override
                            public void onEnded() {
                                ((GlobalActivity) getActivity()).isTutorialShowing = false;
                            }
                        })
                        .start();
            }
        } catch (Exception exp) {
            exp.printStackTrace();

            SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_LOCATION, false);
            SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_SWIPE, false);
            SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_SWIPE, false);
        }
    }
    private SimpleTarget getTutorialTarget(int oneX,int oneY,int height,int width){
        return new SimpleTarget.Builder(getActivity())
                .setPoint(oneX, oneY)
                .setDuration(250)
                .setShape(new RoundedRectangle(height, width, 5))
                .setTitle(AppUtils.getConfig(getActivity()).getTutorial_text()[2])
                .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                    @Override
                    public void onStarted(SimpleTarget target) {
                    }

                    @Override
                    public void onEnded(SimpleTarget target) {
                        SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_SWIPE, false);
                    }
                })
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (AppUtils.getConfig(getContext().getApplicationContext()).isDirect_soft_update()) {
            if (requestCode == 0) {
                SharedPrefUtil.putLong(ApplicationConstants.DIRECT_SOFT_UPDATE_TIME, System.currentTimeMillis());

                HashMap<String, Object> map = new HashMap<>();

                if (resultCode == Activity.RESULT_OK) {
                    AppUtils.showToast(AppUtils.getConfig(getContext().getApplicationContext()).getDirect_soft_update_before_download(), true, 1);
                    map.put(CleverTapEvent.PropertiesNames.getStatus(), "Accept");

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    map.put(CleverTapEvent.PropertiesNames.getStatus(), "Canceled");
                } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                    AppUtils.showToast(AppUtils.getConfig(getContext().getApplicationContext()).getDirect_soft_update_failed(), true, 0);
                    map.put(CleverTapEvent.PropertiesNames.getStatus(), "Failed");
                }
                ((GlobalActivity) getActivity()).rlAppUpdate.setVisibility(View.GONE);
//                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getStatusPopupShow(), map, getActivity());
            }
        }
    }

    private void showPopUp() {
        errorHandleDialog = GenericPopUpFragment.newInstance("You will be able to collect the order \n only when the order is ready", "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
                errorHandleDialog.dismiss();
            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        errorHandleDialog.setCancelable(false);
        if (getActivity() != null) {
            errorHandleDialog.show(getActivity().getSupportFragmentManager(), "order processed error");
        }
    }

    public void endTimer(){
        if (orderFeedAdpater!=null && orderFeedAdpater.getCountDownTimer()!= null){
            orderFeedAdpater.getCountDownTimer().cancel();
        }
    }

    @Override
    public void onOrderPlaced() {
        startOrderTracker();
    }
}
