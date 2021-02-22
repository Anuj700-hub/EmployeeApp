package com.hungerbox.customer.order.activity;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.BluetoothDeviceCheckActivity;
import com.hungerbox.customer.bluetooth.Model.Violation;
import com.hungerbox.customer.bluetooth.Util;
import com.hungerbox.customer.bluetooth.ViolationLogManager;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.BannerRefreshEvent;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.NewOrderEvent;
import com.hungerbox.customer.event.OccasionChangeEvent;
import com.hungerbox.customer.event.OnWalletUpdate;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.CompanyResponse;
import com.hungerbox.customer.model.DataHandlerExtras;
import com.hungerbox.customer.model.DateTime;
import com.hungerbox.customer.model.EmailVerificationPostData;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.ListWallet;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.NavigationItem;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.OcassionReposne;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.model.PrivateKeyUpdate;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.WalletBreakupItem;
import com.hungerbox.customer.model.WalletResponse;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.mvvm.view.MyAccountActivity;
import com.hungerbox.customer.navmenu.DrawerOpenCloseListener;
import com.hungerbox.customer.navmenu.activity.BookMarkActivity;
import com.hungerbox.customer.navmenu.activity.RechargeActivity;
import com.hungerbox.customer.navmenu.adapter.BottomNavigationBarAdapter;
import com.hungerbox.customer.navmenu.adapter.MoreNavigationAdapter;
import com.hungerbox.customer.navmenu.fragment.WalletBreakupFragment;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.DataHandler;
import com.hungerbox.customer.network.NetworkUtil;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.offline.activityOffline.HungerBoxOffline;
import com.hungerbox.customer.order.adapter.BottomNavigationItemView;
import com.hungerbox.customer.order.adapter.OccasionChooserAdapter;
import com.hungerbox.customer.order.fragment.DeskReferenceChangeDialog;
import com.hungerbox.customer.order.fragment.EmailActivationDialog;
import com.hungerbox.customer.order.fragment.FeedbackFragment;
import com.hungerbox.customer.order.fragment.FreeOrderErrorHandleDialog;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.fragment.OrderFeedFragment;
import com.hungerbox.customer.order.listeners.ErrorSnackBarActionListener;
import com.hungerbox.customer.order.listeners.FragmentAttachListener;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.prelogin.activity.HBWelcomeActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CardUtil;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.GenericPopUpFragment;
import com.squareup.otto.Subscribe;
import com.takusemba.spotlight.Spotlight;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static com.hungerbox.customer.util.ApplicationConstants.DECLARATION_FORM_WEBVIEW;


public class GlobalActivity extends ParentActivity implements DrawerOpenCloseListener, FragmentAttachListener {

    public final static int SCREEN_REQUEST_CODE = 5234;
    public final static int LOCATION_CHANGE_REQUEST = 5232;
    public OcassionReposne ocassionReposne = new OcassionReposne();
    public ImageView ivLogo, ivOcassions, ivSearch;
    public TextView tvOccasion;
    public TextView tvTitle;
    public TextView tvWallet;
    public OccasionChooserAdapter occasionChooserAdapter;
    public TextView spLocation;
    public Handler handler;
    public Runnable runnable;
    public long activityStartTime = 0;
    protected FrameLayout rlBaseContainer;
    protected CoordinatorLayout clBaseContainer;
    protected Fragment fragment;
    protected Toolbar toolbar;
    public ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    protected TextView tvVendorName;
    public boolean pictureInPictureRequired = false;
    public long selectedOcasionId;
    private boolean isRunning = false, isRunningPip = false;
    private long locationId;
    Handler doubleBackExitHandler, feedbackHandler;
    private RecyclerView rvOccasions;
    private boolean doubleBackToExitPressedOnce;
    private Order currentOrder;
    public static DisplayMetrics displayMetrics;
    public DialogFragment dialogFragment;
    public boolean deskRefRequired = false;
    public EmailActivationDialog emailActivationDialog;
    public LinearLayout bottomNavBar;
    public ConstraintLayout parentCl;
    public LinearLayout bottomSheetLayout;
    public ConstraintLayout clBottomSheet;
    public BottomSheetBehavior sheetBehavior;
    //public Button btnBottomSheet;
    public WalletResponse allWallets;
    BottomNavigationBarAdapter bottomNavigationBarAdapter;
    TextView userNameTv,versionNameTv,viewAccountTv;
    View backgroundView;
    BottomNavigationItemView moreButton = null;
    BottomNavigationItemView homeButton = null;
    boolean moreAvailable = false, homeAvailable = false,isAppDownloadComplete = false;
    private int cartPositionInBottomBar =-1;
    private ArrayList<Ocassion> currentOccasionsList = new ArrayList<>();
    private ArrayList<Ocassion> activeOccasionList;
    public static User user;
    public TextView tvAppUpdate,tvUpdateAvailable;
    public RelativeLayout rlAppUpdate;
    public AppUpdateInfo appUpdateInfo;
    public ArrayList<WalletBreakupItem> walletList = new ArrayList<>();
    public double totalWalletAmount = 0;
    private boolean rechargeAllowed = false;
    public boolean isTutorialShowing = false;
    private Cart carts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        displayMetrics = getResources().getDisplayMetrics();
        restoreFromSavedInstance(savedInstanceState);
        AppUtils.sendCrashlyticsLogs();

        setApiTag(String.valueOf(System.currentTimeMillis()));

        activityStartTime = System.currentTimeMillis();
        setContentView(R.layout.activity_global_new);
        toolbar = findViewById(R.id.tb_global);
        spLocation = findViewById(R.id.tv_location);
        rvOccasions = findViewById(R.id.rv_occasions);
        rvOccasions.setVisibility(View.GONE);
        clBaseContainer = findViewById(R.id.cl_base);
        tvAppUpdate = findViewById(R.id.tv_update);
        rlAppUpdate = findViewById(R.id.ll_app_update);
        tvUpdateAvailable = findViewById(R.id.tv_update_available);

        tvTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        tvWallet = toolbar.findViewById(R.id.tv_wallet);
        rlBaseContainer = findViewById(R.id.rl_base_container);
        mDrawerLayout = findViewById(R.id.dl_base_drawer);
        tvVendorName = findViewById(R.id.tv_vendor_name);

        ivLogo = findViewById(R.id.logo);
        ivOcassions = findViewById(R.id.iv_ocassion);
        tvOccasion = findViewById(R.id.tv_occasion);
        ivSearch = findViewById(R.id.iv_search);

        //mGesture = new GestureDetector(getApplicationContext(), mOnGesture);
        backgroundView = findViewById(R.id.transparent_view);
        backgroundView.setVisibility(View.GONE);

//        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getApp_start(), null, getApplicationContext());

        // check user email verification status
        // if email is not verified , move to email activation screen
        long locationIdPermanent = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID_PERMANENT, -1);

        if (savedInstanceState == null &&locationIdPermanent != -1) {
            SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID, locationIdPermanent);
            SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME, SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME_PERMANENT, "India T, BLR"));
        }
        SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false);

        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 1);
        if (!AppUtils.getConfig(this).is_location_fixed()) {
            spLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventUtil.FbEventLog(getApplicationContext(), EventUtil.HOME_LOCATION_CLICK, EventUtil.SCREEN_HOME);

//                    try {
//                        HashMap map = new HashMap<>();
//                        map.put(CleverTapEvent.PropertiesNames.getScreen_name(), "Home");
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getLocation_click(), map, getApplicationContext());
//                    } catch (Exception exp) {
//                        exp.printStackTrace();
//                    }

                    String source = "Home";
                    try {
                        source = getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
                        if (source == null) {
                            source = "Home";
                        }
                        JSONObject jo = new JSONObject();
                        jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                        HBMixpanel.getInstance().addEvent(getApplicationContext(), EventUtil.MixpanelEvent.LOCATION_CLICK, jo);
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                    if (AppUtils.getConfig(getApplicationContext()).isShow_location()) {
                        Intent locationIntent = new Intent(getApplicationContext(), LocationChangeActivity.class);
                        locationIntent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                        startActivityForResult(locationIntent, LOCATION_CHANGE_REQUEST);
                    }
                }
            });
        } else{
            spLocation.setCompoundDrawables(null,null,null,null);
        }

        tvAppUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(isAppDownloadComplete && appUpdateManager!=null){
                        appUpdateManager.completeUpdate();
                    }else {
                        if (appUpdateInfo != null) {
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.FLEXIBLE,
                                    GlobalActivity.this, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvOccasion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                try {
//                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getOcassion_click(), null, getApplicationContext());
//                } catch (Exception exp) {
//                    exp.printStackTrace();
//                }

                EventUtil.FbEventLog(getApplicationContext(), EventUtil.HOME_OCCASION_CLICK, EventUtil.SCREEN_HOME);

                HBMixpanel.getInstance().addEvent(getApplicationContext(), EventUtil.MixpanelEvent.CLICK_OCCASION);

                if (rvOccasions.getVisibility() == View.GONE) {
                    if (ocassionReposne != null && ocassionReposne.getOcassions().size() > 0)
                        hide_Show_Fade_Anim(rvOccasions, View.VISIBLE);
                    else
                        AppUtils.showToast("No occasions found.",true,2);
                } else
                    hide_Show_Fade_Anim(rvOccasions, View.GONE);
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HashMap map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getSearch_click(), map, GlobalActivity.this);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                EventUtil.FbEventLog(getApplicationContext(), EventUtil.HOME_CLICK_SEARCH_VENDOR, EventUtil.SCREEN_HOME);
                HBMixpanel.getInstance().addEvent(getApplicationContext(), EventUtil.MixpanelEvent.VENDOR_SERACH_CLICK);
//                Intent intent = new Intent(getApplicationContext(), VendorSearchActivity.class);
                Intent intent = new Intent(getApplicationContext(), GlobalSearchActivity.class);
                intent.putExtra(ApplicationConstants.OCASSION_ID, selectedOcasionId);
                String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
                intent.putExtra(ApplicationConstants.LOCATION_NAME, locationName);
                startActivity(intent);
            }
        });


        setSupportActionBar(toolbar);
        tvTitle.setVisibility(View.INVISIBLE);
        spLocation.setVisibility(View.GONE);
        spLocation.setSelected(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(null);

        if (AppUtils.getConfig(getApplicationContext()).isShow_location()) {
            spLocation.setVisibility(View.VISIBLE);
        } else {
            spLocation.setVisibility(View.INVISIBLE);
        }

        createBaseContainer();

        String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        spLocation.setText(locationName);
        //getOccassionList();
        if(!SharedPrefUtil.getBoolean(ApplicationConstants.IS_GUEST_USER))
            checkForPrivateKey();

//        mShake = new ShakeListener(getApplicationContext());
//        mShake.setOnShakeListener(new ShakeListener.OnShakeListener() {
//            @Override
//            public void onShake() {
//                navigateToBookMark("Shake");
//            }
//        });
        initialiseBottomSheet();
        initialiseTimeStamp();
        checkForBTAllTime();
        Util.sendViolationLog(this);

        if(getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            if(extras.get("REQUEST_FOR").equals("PROXIMITY_FEATURE")){
                Intent intent = new Intent(this, BluetoothDeviceCheckActivity.class);
                startActivity(intent);
            }
        }
    }

    public void checkForBTAllTime(){
        if(AppUtils.getConfig(this).getBluetooth_distancing() != null) {
            ViolationLogManager.Companion.removeViolationsBeforeGivenDays(this.getApplicationContext(), AppUtils.getConfig(this.getApplicationContext()).getBluetooth_distancing().getContact_tracing_api_max_days());
        }

        if(Util.isBluetoothDistancingActive(this, null) && ViolationLogManager.Companion.shouldEnableBluetoothForAllDay(this.getApplicationContext())){


            AppUtils.settingBTServiceParameters(this.getApplicationContext());

            String message = AppUtils.checkBTPermissions(this.getApplicationContext());
            if(message.isEmpty()){
                Intent intent = new Intent(this, BluetoothDeviceCheckActivity.class);
                startActivityForResult(intent, ViolationLogManager.BLE_ALL_TIME_REQUEST);
                overridePendingTransition(0, 0);
            }
            else{
                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    map.put(CleverTapEvent.PropertiesNames.getSource(),"BLE_Service");
                    map.put(CleverTapEvent.PropertiesNames.getBle_service_stopped_reason(),message);
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getBle_service_stopped(),map,getApplicationContext());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Util.stopDeviceTracking(this.getApplicationContext());
                SharedPrefUtil.putBoolean(ApplicationConstants.BLE_PROXIMITY_ENABLED, false);
                AppUtils.sendNotificationForDeepLink(this.getApplicationContext(), message, ApplicationConstants.Navigation.ACCOUNT_SCREEN);
            }
        }
        else{
            if(Util.isBluetoothDistancingActive(this, null) && ViolationLogManager.Companion.isBluetoothAllDayEnabled(this.getApplicationContext())){
                Util.stopDeviceTracking(this.getApplicationContext());
            }
        }
    }


    public void setAppDownloadView(){
        isAppDownloadComplete = true;

        rlAppUpdate.setVisibility(View.VISIBLE);
        tvUpdateAvailable.setText(AppUtils.getConfig(getApplicationContext()).getDirect_soft_update_after_download());
        tvAppUpdate.setText("   Install   ");

    }

    public void initialiseTimeStamp(){
        if(SharedPrefUtil.getLong(ApplicationConstants.TIME_DIFFERENCE, 0) == 0){
            String url = UrlConstant.SERVER_TIME;
            SimpleHttpAgent<DateTime> timeSimpleHttpAgent = new SimpleHttpAgent<DateTime>(
                    this,
                    url,
                    new ResponseListener<DateTime>() {
                        @Override
                        public void response(DateTime responseObject) {
                            DateTime serverTime = responseObject;
                            long timeDifference = 0;

                            try {
                                timeDifference = Calendar.getInstance().getTimeInMillis() - serverTime.now * 1000;
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }

                            if (serverTime.now == 0) {
                                timeDifference = 0;
                            }
                            SharedPrefUtil.putLong(ApplicationConstants.TIME_DIFFERENCE, timeDifference);
                        }
                    },
                    new ContextErrorListener() {
                        @Override
                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                            // On Error
                        }
                    },
                    DateTime.class
            );
            timeSimpleHttpAgent.getWithoutHeader(getApiTag());
        }
    }
    public void initialiseBottomSheet(){


        userNameTv = findViewById(R.id.tv_username);
        versionNameTv = findViewById(R.id.tv_version);
        viewAccountTv = findViewById(R.id.tv_account);
        bottomNavBar = findViewById(R.id.ll_bottom_nav);
        initiateBottomNav();
        //btnBottomSheet = findViewById(R.id.bt_toggle);
        bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        clBottomSheet = findViewById(R.id.cl_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        if (!AppUtils.getConfig(this).isShow_my_account()){
            viewAccountTv.setVisibility(View.GONE);
        }

        if(SharedPrefUtil.getBoolean(ApplicationConstants.IS_GUEST_USER)){
            bottomNavBar.setVisibility(View.GONE);
            toolbar.setNavigationIcon(R.drawable.ic_accent_back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutPopUp();
                }
            });
        }




        try {
            for (int i = 0; i < bottomNavBar.getChildCount(); i++) {
                BottomNavigationItemView v = (BottomNavigationItemView) bottomNavBar.getChildAt(i);
                if (v.getItemKey().equals(ApplicationConstants.HOME)) {
                    homeButton = (BottomNavigationItemView) bottomNavBar.getChildAt(i);
                }
                if (v.getItemKey().equals(ApplicationConstants.CART)){
                    cartPositionInBottomBar = i;
                }
                if (v.getItemKey().equals(ApplicationConstants.MORE)) {
                    moreButton = (BottomNavigationItemView) bottomNavBar.getChildAt(i);
                    break;
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        }
        if (moreButton!=null && moreButton.getItemKey().equals(ApplicationConstants.MORE)) {
            moreAvailable = true;
        }
        if (homeButton!=null && homeButton.getItemKey().equals(ApplicationConstants.HOME)) {
            homeAvailable = true;
        }


        if (homeAvailable){
            homeButton.setSelected(true);
        }

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        if (moreAvailable){
                            moreButton.setSelected(true);
                        }
                        if (homeAvailable){
                            homeButton.setSelected(false);
                        }
                        backgroundView.setVisibility(View.VISIBLE);
                        backgroundView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_fade_in));
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        if (moreAvailable){
                            moreButton.setSelected(false);
                        }
                        if (homeAvailable){
                            homeButton.setSelected(true);
                        }
                        backgroundView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_fade_out));
                        backgroundView.setVisibility(View.GONE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:{
                    }
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:{
                    }
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        setUpRv();
        clBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("");
            }
        });
        backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        userNameTv.setText(SharedPrefUtil.getString(ApplicationConstants.PREF_NAME));
        viewAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
                try {
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getMy_account_click(), null, GlobalActivity.this);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(GlobalActivity.this, MyAccountActivity.class);
                startActivity(intent);

            }
        });

    }

    private void addItemsToCart(int quantity){
        //pass negative number in quantity to clear cart
        if (cartPositionInBottomBar>-1){
            BottomNavigationItemView cartView = (BottomNavigationItemView)bottomNavBar.getChildAt(cartPositionInBottomBar);
            if (quantity <= 0) {
                cartView.hideBadge();
            } else {
                cartView.showBadge(String.valueOf(quantity));
            }
        }
    }
    public void setUpRv(){
        RecyclerView recyclerView = findViewById(R.id.rv_activity_list);
        recyclerView.setAdapter(new MoreNavigationAdapter(this, (ArrayList<NavigationItem>) bottomNavigationBarAdapter.navItemsMore));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            btnBottomSheet.setText("Expand sheet");
        }
    }

    private void initiateBottomNav(){
        bottomNavigationBarAdapter = new BottomNavigationBarAdapter(this, bottomNavBar, new MoreClick() {
            @Override
            public void onClick() {
                toggleBottomSheet();
            }
        });
        versionNameTv.setText("v" + BuildConfig.VERSION_NAME);
    }


    private void getUserDetailsFromServer() {

        String url = UrlConstant.USER_DETAIL;
        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        if (responseObject != null) {
                            long locationId = responseObject.user.getLocationId();
                            String locationName = responseObject.user.getLocationName();
                            if (!DbHandler.isStarted())
                                DbHandler.start(getApplicationContext());
                            long companyId = SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, 0);
                            if (companyId == responseObject.user.getCompanyId()) {
                                if (!AppUtils.isCafeApp()) {
                                    SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID, locationId);
                                    SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID_PERMANENT, locationId);
                                    SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME_PERMANENT, locationName);
                                    SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME, locationName);
                                }
                                SharedPrefUtil.putLong(ApplicationConstants.PREF_USER_ID, responseObject.user.getId());
                                SharedPrefUtil.putString(ApplicationConstants.PREF_USER_NAME, responseObject.user.getUserName());
                                SharedPrefUtil.putString(ApplicationConstants.PREF_USER_EMAIL, responseObject.user.getEmpEmail());
                                SharedPrefUtil.putString(ApplicationConstants.PREF_NAME, responseObject.user.getName());
                                SharedPrefUtil.putInt(ApplicationConstants.PREF_USER_EMP_TYPE_ID, responseObject.user.getEmployeeTypeId());
                                AppUtils.setupUser("" + responseObject.user.getId(), responseObject.user.getEmail(), responseObject.user.getName());
                                AppUtils.logUser(responseObject.user.getUserName());
                            } else {
                                SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
                                AppUtils.showToast("Unable to get your details from server", true, 0);
                            }
                        } else {

                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                        AppUtils.showToast("Unable to get your details from server", true, 0);
                    }
                },
                UserReposne.class
        );
        userSimpleHttpAgent.get(getApiTag());
    }

    private void checkForPrivateKey(){
        String certificate = SharedPrefUtil.getString(ApplicationConstants.OFFLINE_CERTIFICATE,"");

        String privateKey = SharedPrefUtil.getString(ApplicationConstants.OFFLINE_PRIVATE_KEY,"");

        if(certificate==null || certificate.isEmpty() || privateKey == null || privateKey.isEmpty() || isKeyExpired()){

            ArrayList<String> keyList = AppUtils.getCertificate();
            if(keyList == null)
                return;

            PrivateKeyUpdate privateKeyUpdate = new PrivateKeyUpdate();
            privateKeyUpdate.setPublic_key(keyList.get(0));

            String url = UrlConstant.KEY_REFRESH;

            SimpleHttpAgent<PrivateKeyUpdate> simpleHttpAgent = new SimpleHttpAgent<PrivateKeyUpdate>(this, url,
                    new ResponseListener<PrivateKeyUpdate>() {
                        @Override
                        public void response(PrivateKeyUpdate responseObject) {
                            if(responseObject!=null) {
                                try {
                                    if (responseObject.getCertificate() != null && !responseObject.getCertificate().isEmpty()) {
                                        SharedPrefUtil.putString(ApplicationConstants.OFFLINE_CERTIFICATE, responseObject.getCertificate());
                                        SharedPrefUtil.putString(ApplicationConstants.OFFLINE_PRIVATE_KEY, keyList.get(1));
                                        MainApplication.setHungerBoxOfflineNull();
                                    }
                                } catch (Exception exp) {
                                    exp.printStackTrace();
                                }
                            }
                        }
                    }, new ContextErrorListener() {
                @Override
                public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        cleverTapEvent("Error code "+errorCode+" , No Internet");
                    } else {
                        cleverTapEvent("Error code "+errorCode);
                    }
                }
            },PrivateKeyUpdate.class);

            simpleHttpAgent.post(privateKeyUpdate,new HashMap<>(), getApiTag());

        }
    }

    private void cleverTapEvent(String errorMessage){
//        try{
//            HashMap<String,Object> place_order_map = new HashMap<>();
//            place_order_map.put(CleverTapEvent.PropertiesNames.getSource(),"GlobalActivity");
//            place_order_map.put(CleverTapEvent.PropertiesNames.getMessage(),errorMessage==null?"":errorMessage);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getCertificate_error(),place_order_map,GlobalActivity.this);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
    }


    private boolean isKeyExpired(){
        try {
            MainApplication mainApplication = (MainApplication) getApplication();
            HungerBoxOffline.EmployeeApp employeeApp = mainApplication.getHungerBoxOffline();

            Calendar calendar =  Calendar.getInstance();


            int random = new Random().nextInt(60*24);
            return (employeeApp.getCertExpiresAt().getTime() - calendar.getTimeInMillis()) < (172800000 + random*60*1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkDrawOverlayPermission();
        String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        spLocation.setText(locationName);
        LogoutTask.updateTime();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        isRunningPip = false;
    }


    @Override
    public void onBackPressed() {
        if(SharedPrefUtil.getBoolean(ApplicationConstants.IS_GUEST_USER)) {
            showLogoutPopUp();
        }
        else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ){
            doubleBackToExitPressedOnce = false;
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            showBackPressedMessage();
        }
    }

    protected void showBackPressedMessage() {

        View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Spotlight.with(GlobalActivity.this).closeSpotlight();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        };
        if (doubleBackToExitPressedOnce) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                finishAfterTransition();
            else
                super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        AppUtils.showToast("Press back again to exit", true, 2);

        doubleBackExitHandler = new Handler();
        doubleBackExitHandler.postDelayed(() -> doubleBackToExitPressedOnce = false, 10000);
    }

    public void showLogoutPopUp() {
        String message = "Are you sure you want to Logout?";
        GenericPopUpFragment fragment = GenericPopUpFragment.newInstance(message, "Yes",
                new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        AppUtils.doLogoutServer(GlobalActivity.this.getApplicationContext());
                        SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
                        SharedPrefUtil.remove(ApplicationConstants.PREF_USER_ID);
                        Intent intent = new Intent(GlobalActivity.this, HBWelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });
        fragment.setCancelable(true);
        dialogFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, "logout")
                .commit();
    }

    public void getUserDetailsFromServer(final boolean fromNav) {

        String url = UrlConstant.CURRENT_USER_V3;
        HashMap<String , String> objectIds = new HashMap<>();
        objectIds.put(ApplicationConstants.OBJECT_ID_1,SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID,0)+"");
        objectIds.put(ApplicationConstants.OBJECT_ID_2,"");

        DataHandlerExtras dHExtras = new DataHandlerExtras();
        dHExtras.setTag(getApiTag());

        DataHandler<UserReposne> userSimpleHttpAgent = new DataHandler<UserReposne>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        if (responseObject != null) {
                            user = responseObject.getUser();
                            SharedPrefUtil.getString(ApplicationConstants.USER_PHONE, "");
                            SharedPrefUtil.putString(ApplicationConstants.USER_PHONE, responseObject.user.getPhoneNumber());

                            if (!fromNav) {
                                //checkDeskRefAndShowDialog(responseObject.getUser());
                                checkUserEmailStats(responseObject.getUser());
                            }

                            try {
                                JSONObject mixPanelValues = new JSONObject();
                                mixPanelValues.put(EventUtil.MixpanelEvent.SuperProperties.MOBILE_NUMBER, responseObject.user.getPhoneNumber());
                                mixPanelValues.put(EventUtil.MixpanelEvent.SuperProperties.EMAIL_ID, responseObject.user.getEmpEmail());
                                HBMixpanel.getInstance().registerSuperProperties(GlobalActivity.this, mixPanelValues);

                                HBMixpanel.getInstance().addPeople(GlobalActivity.this, String.valueOf(responseObject.user.getId()), responseObject.user.getName(), responseObject.user.getEmpEmail(),
                                        String.valueOf(responseObject.user.getLocationId()), responseObject.user.getPhoneNumber());

                            } catch (Exception exp) {

                            }

                            try {
                                CleverTapEvent.pushUserProfile(responseObject.user, getApplicationContext());
                            } catch (Exception e) {

                            }
                            MainApplication.bus.post(new OnWalletUpdate(responseObject.user));

                            if(responseObject.getUser().getDeclarationFromLink() != null){
                                openDeclarationWebview(responseObject.getUser().getDeclarationFromLink());
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION ||
                                errorCode == ContextErrorListener.TIMED_OUT) {
                            showNoNetFragment(new RetryListener() {
                                @Override
                                public void onRetry() {
                                    getUserDetailsFromServer(false);
                                }
                            });
                        }
                    }
                },
                UserReposne.class,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras
        );
    }
    public void getUserPaymentDetails() {

        String url = UrlConstant.WALLET_LIST_V2 + "?occasionId=" + selectedOcasionId +
                    "&locationId=" + locationId + "&transactionAmount=" + 0 +
                    "&vendorId=" + 0 + "&showMswipe=" + 0 + "&showPineLabs=" + 0;


        SimpleHttpAgent<ListWallet> userWalletsSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<ListWallet>() {
                    @Override
                    public void response(ListWallet responseObject) {

                        if (responseObject != null) {
                            calculateAndShowWallet(responseObject);
                        }else{
                            tvWallet.setVisibility(View.GONE);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        tvWallet.setVisibility(View.GONE);
                    }
                },
                ListWallet.class
        );

        userWalletsSimpleHttpAgent.post("", new HashMap<String, JsonSerializer>(), getApiTag());
    }
    private void calculateAndShowWallet(ListWallet listWallet){
        try {
            if (listWallet != null) {
                ArrayList<WalletBreakupItem> walletList = new ArrayList<>();
                double balance = 0;
                rechargeAllowed = false;
                for (PaymentMethod paymentMethod : listWallet.getPaymentMethods()){
                    if (paymentMethod.getWalletSource().equalsIgnoreCase("internal")){
                        if(paymentMethod.isInternal() && paymentMethod.employeeCanRecharge()){
                            rechargeAllowed = true;
                        }
                        balance += paymentMethod.getAmount();
                        walletList.add(new WalletBreakupItem(paymentMethod.getDisplayName(),Double.toString(paymentMethod.getAmount())));
                    }
                }
                if (walletList.size() > 0) {
                    tvWallet.setVisibility(View.VISIBLE);
                    tvWallet.setText("Rs." + balance);
                } else {
                    tvWallet.setVisibility(View.GONE);
                }
                tvWallet.setOnClickListener(v -> showWalletBreakPopUp(walletList,rechargeAllowed));
            }else{
                tvWallet.setVisibility(View.GONE);
            }
        } catch (Exception e){
            e.printStackTrace();
            tvWallet.setVisibility(View.GONE);
        }
    }

    private void showWalletBreakPopUp(ArrayList<WalletBreakupItem> walletList,boolean rechargeAllowed){
        if(walletList.size() > 0){
            WalletBreakupFragment wbFragment = WalletBreakupFragment.Companion.newInstance(walletList,rechargeAllowed);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(wbFragment, "wallet_breakup")
                    .commitAllowingStateLoss();
        }
    }

    private void openDeclarationWebview(String declarationUrl){
        Intent intent = new Intent(this, DeclarationWebViewActivity.class);
        intent.putExtra("url", declarationUrl);
        startActivityForResult(intent, DECLARATION_FORM_WEBVIEW);
    }

    private void checkUserEmailStats(User user){

        if (user.getEmailVerifiedflag() == 1){
            if (user.getUpdatedEmail()==null || user.getUpdatedEmail().equals("")){
                showEmailActDialog(null,AppUtils.getConfig(this).getEmail_verification_message(),"SUBMIT",null,user);
            } else{
                showEmailActDialog(user.getUpdatedEmail(),AppUtils.getConfig(this).getResend_email_message(),"RESEND",null,user);
            }
        } else{
            checkDeskRefAndShowDialog(user);
        }

    }

    private void checkDeskRefAndShowDialog(User user) {
        int deskOrderingEnabled = SharedPrefUtil.getInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, 0);
        if (deskOrderingEnabled == 1) {

            String deskReference = user.getDeskReference();
            if (deskReference == null || deskReference.trim().isEmpty()) {
                deskRefRequired = true;
                showRefChangeDialog();
            }

        }
        if (user.getEmailVerifiedflag() == 1) {
            checkAndShowFeedbackDialog();
        }
    }

    private void checkAndShowFeedbackDialog(){

        if (fragment instanceof OrderFeedFragment){
            if (((OrderFeedFragment)fragment).isFeedbackDialogTOBeShown){


                feedbackHandler = new Handler();
                feedbackHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (emailActivationDialog!=null) {

                            if (!emailActivationDialog.isVisible()) {
                                ((OrderFeedFragment) fragment).showNextEligibleFeedback(false);
                            }
                        }
                        else{
                            ((OrderFeedFragment) fragment).showNextEligibleFeedback(false);
                        }
                    }
                }, 1500);
            }
        }
    }

    private void showRefChangeDialog() {

        deskRefRequired = true;
        DeskReferenceChangeDialog deskReferenceChangeDialog = DeskReferenceChangeDialog.newInstance(null, false, new DeskReferenceChangeDialog.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction(String ref) {
                AppUtils.resetCurrentUserDataClass(GlobalActivity.this.getApplicationContext());
                checkAndShowFeedbackDialog();
            }

            @Override
            public void onNegativeInteraction() {
                checkAndShowFeedbackDialog();
            }

            @Override
            public void onDissmis() {
                deskRefRequired = false;
            }
        });
        deskReferenceChangeDialog.setCancelable(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(deskReferenceChangeDialog, "ref")
                .commitAllowingStateLoss();

    }

    private void showEmailActDialog(String email,String message,String positiveButtonText,String negativeButtonText, final User user) {

        emailActivationDialog = EmailActivationDialog.newInstance(email,message,positiveButtonText,negativeButtonText, new EmailActivationDialog.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction(String email) {
                sendEmailForVerification(email,user);
            }

            @Override
            public void onNegativeInteraction() {
                emailActivationDialog.dismiss();
            }

            @Override
            public void onDismiss() {
                checkDeskRefAndShowDialog(user);
            }
        });
        emailActivationDialog.setCancelable(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(emailActivationDialog, "ref")
                .commitAllowingStateLoss();

    }

    private void dismissFeedbackDialog(){
        try {

            if (dialogFragment != null && dialogFragment instanceof FeedbackFragment) {
                if (dialogFragment.getDialog() != null && dialogFragment.getDialog().isShowing()) {
                    dialogFragment.dismiss();
                    System.out.println("dialog fragment dismissed");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendEmailForVerification(String email, final User user){
        //todo checkDeskRefAndShowDialog(user);

        String url = UrlConstant.SEND_EMAIL_FOR_VERIFICATION;
        EmailVerificationPostData emailVerificationPostData = new EmailVerificationPostData();
        emailVerificationPostData.setEmail(email);
        SimpleHttpAgent<JsonObject> emailPostApi = new SimpleHttpAgent<>(this.getApplicationContext(), url, new ResponseListener<JsonObject>() {
            @Override
            public void response(JsonObject responseObject) {
                if (emailActivationDialog!=null && emailActivationDialog.isVisible()){
                    emailActivationDialog.dismiss();
                }
                AppUtils.showToast(AppUtils.getConfig(GlobalActivity.this).getVerify_email_message(),true,1);
            }
        },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (emailActivationDialog!=null && emailActivationDialog.isVisible()){
                            emailActivationDialog.dismiss();
                        }
                    }
                },JsonObject.class);
        emailPostApi.post(emailVerificationPostData,new HashMap<String, JsonSerializer>(), getApiTag());
    }

    public void showNoNetFragment(RetryListener retryListener) {
        NoNetFragment fragment = NoNetFragment.newInstance(retryListener);
        fragment.setCancelable(true);
        dialogFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, "exit")
                .commit();
    }

    protected void createBaseContainer() {

        if (fragment == null) {
            fragment = OrderFeedFragment.newInstance(this, "Home", "Home", selectedOcasionId,this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.rl_base_container, fragment, "vendor")
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.rl_base_container, fragment, "vendor")
                        .commit();
            }
        }
    }

//    private GestureDetector.OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return false;
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            boolean result = false;
//            try {
//                Display display = getWindowManager().getDefaultDisplay();
//                Point size = new Point();
//                display.getSize(size);
//                int width = size.x;
//                int height = size.y;
//
//                float diffY = e2.getY() - e1.getY();
//                float diffX = e2.getX() - e1.getX();
//
//                double ratioHeight = height * 0.375;
//                double ratioWidth = width * 0.4167;
//
//                if (Math.abs(diffX) > Math.abs(diffY)) {
//                    if (Math.abs(diffX) > ratioWidth && Math.abs(velocityX) > 100 && e1.getY() > ratioHeight) {
//                        if (diffX > 0) {
//                            // onSwipeRight();
//                        } else {
////                            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
////                                return false;
////                            }
//                            navigateToBookMark("Swipe");
//                        }
//                        result = true;
//                    }
//                } else if (Math.abs(diffY) > 300 && Math.abs(velocityY) > 100) {
//                    result = true;
//                }
//            } catch (Exception exception) {
//            }
//            return result;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return false;
//        }
//    };

    public void navigateToBookMark(String source) {

        boolean tutorial_location = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_LOCATION, true);
        boolean tutorial_occassion = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_OCCASSION, true);
        boolean tutorial_swipe = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_SWIPE, true);

        if (((tutorial_location || tutorial_occassion || tutorial_swipe) && AppUtils.getConfig(this).isCoach_mark_visible())) {
            return;
        }
        if (SharedPrefUtil.getBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE, false) &&
                AppUtils.getConfig(GlobalActivity.this).isExpress_checkout() &&
                !SharedPrefUtil.getBoolean(ApplicationConstants.IS_PREORDER_AVAILABLE, false) && !SharedPrefUtil.getBoolean(ApplicationConstants.IS_GUEST_USER)) {

            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getExp_click(), map, getApplicationContext());

            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(GlobalActivity.this, BookMarkActivity.class);
            intent.putExtra(ApplicationConstants.EXPRESS_CHECKOUT_ACTION, ApplicationConstants.BOOKMARK_FROM_MAINACTIVITY);
            intent.putExtra(CleverTapEvent.PropertiesNames.getSource(), source);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right_express, R.anim.exit_to_left_animation);
        }
    }

    public void getOccassionList() {

        pictureInPictureRequired = false;
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception exp) {
        }

        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        String url = UrlConstant.OCCASION_LIST_V3 + "?location_id=" + locationId;

        HashMap<String , String> objectIds = new HashMap<>();
        objectIds.put(ApplicationConstants.OBJECT_ID_1,locationId+"");
        objectIds.put(ApplicationConstants.OBJECT_ID_2,"");

        DataHandlerExtras dHExtras = new DataHandlerExtras();
        dHExtras.setTag(getApiTag());

        DataHandler<OcassionReposne> ocassionReposneSimpleHttpAgent = new DataHandler<OcassionReposne>(
                this,
                url,
                new ResponseListener<OcassionReposne>() {
                    @Override
                    public void response(OcassionReposne responseObject) {
                        if (responseObject != null && responseObject.getOcassions().size() > 0) {

                            setOcassion(responseObject);
                        }
                        else {
                            setOcassionNotAvailable();
                            try {
                                if (fragment instanceof OrderFeedFragment)
                                    ((OrderFeedFragment) fragment).refreshLayout.setRefreshing(false);
                                ((OrderFeedFragment) fragment).shimmerView.stopShimmer();
                                ((OrderFeedFragment) fragment).shimmerView.setVisibility(View.GONE);
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (occasionChooserAdapter == null) {

                            String message = "";
                            String imageType = "";
                            if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                                message = ApplicationConstants.NO_NET_STRING;
                                imageType = ApplicationConstants.NO_INTERNET_IMAGE;
                            } else {
                                message = ApplicationConstants.SOME_WRONG;
                                message = ApplicationConstants.GENERAL_ERROR;
                            }

                            ErrorPopFragment orderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(message, "Retry", true,imageType, new ErrorPopFragment.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {
                                            getOccassionList();
                                        }

                                        @Override
                                        public void onNegativeInteraction() {
                                            finish();
                                        }
                                    });

                            orderErrorHandleDialog.setCancelable(false);
                            dialogFragment = orderErrorHandleDialog;
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(orderErrorHandleDialog, "no_internet")
                                    .commitAllowingStateLoss();
                        } else {

                            String message = "";
                            String imageType = "";
                            if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                                message = ApplicationConstants.NO_NET_STRING;
                                imageType = ApplicationConstants.NO_INTERNET_IMAGE;
                            } else {
                                message = ApplicationConstants.SOME_WRONG;
                                message = ApplicationConstants.GENERAL_ERROR;
                            }

                            ErrorPopFragment orderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(message,
                                    "Retry",true,imageType,
                                    new ErrorPopFragment.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {
                                            getOccassionList();
                                        }

                                        @Override
                                        public void onNegativeInteraction() {
                                        }
                                    });

                            orderErrorHandleDialog.setCancelable(false);
                            dialogFragment = orderErrorHandleDialog;
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(orderErrorHandleDialog, "no_internet")
                                    .commitAllowingStateLoss();

                            try {
                                if (fragment instanceof OrderFeedFragment)
                                    ((OrderFeedFragment) fragment).refreshLayout.setRefreshing(false);
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }

                        }
                    }
                },
                OcassionReposne.class,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras

        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.bus.register(this);

        isRunning = true;
        isRunningPip = true;
        if (homeAvailable && homeButton!=null){
            homeButton.setSelected(true);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.bus.unregister(this);
        isRunning = false;
    }

    @Subscribe
    public void onNewOrderEvent(NewOrderEvent newOrderEvent) {
        currentOrder = newOrderEvent.order;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getAction() != null && intent.getAction().equals(ApplicationConstants.ACTION_OPEN_LOCATION)) {
            String customAction = intent.getStringExtra(ApplicationConstants.ACTION);
            if (customAction != null && !customAction.isEmpty()) {
                switch (customAction) {
                    case ApplicationConstants.ACTION_OPEN_LOCATION:

                        MainApplication mainApplication = (MainApplication) getApplication();
                        mainApplication.clearOrder();
                        MainApplication.bus.post(new OrderClear());

                        openLocationFromAction();
                        break;
                }
            }
        }
    }

    private void openLocationFromAction() {
        spLocation.performClick();
    }

    private void getTagIdAndLogin(Intent intent) {
        if (AppUtils.getConfig(this).isCard_login()) {
            AppUtils.HbLog("Peeyush", "nfc");
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];

                }
            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                long tagId = CardUtil.dumpTagData(tag);
                if (tagId > 0) {
                    AppUtils.HbLog("Hungerbox", Long.toString(tagId));
                }
            }
        }
    }


    public void showError(String msg) {
        Snackbar.make(clBaseContainer, msg, Snackbar.LENGTH_LONG).show();
    }

    public void showError(String msg, String actionMessage, final ErrorSnackBarActionListener errorSnackBarActionListener) {
        Snackbar.make(clBaseContainer, msg, Snackbar.LENGTH_LONG)
                .setAction(actionMessage, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorSnackBarActionListener.action();
                    }
                }).setActionTextColor(getResources().getColor(R.color.white))
                .show();
    }

    @Override
    public void closeDrawer() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT))
            mDrawerLayout.closeDrawers();
    }

    @Override
    public void openDrawer() {
        if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    public void setOcassion(OcassionReposne ocassionReposne) {
        this.ocassionReposne = ocassionReposne;
        setOcassionView(false);
    }

    public void setOcassionNotAvailable() {
        if (fragment instanceof OrderFeedFragment) {
            OrderFeedFragment vendorListFragment = (OrderFeedFragment) fragment;
            vendorListFragment.updateViewWithSelectedOcassion(0);
        }
    }

    private ArrayList <Ocassion> getSortedOccasionsList(ArrayList <Ocassion> occasions){
        ArrayList <Ocassion> normalOccasions  = new ArrayList<>();
        ArrayList <Ocassion> preOrderOccasions = new ArrayList<>();

        for(Ocassion occasion : occasions){
            if(occasion.isPreorder())
                preOrderOccasions.add(occasion);
            else
                normalOccasions.add(occasion);
        }
        normalOccasions.addAll(preOrderOccasions);
        return normalOccasions;
    }

    private void setOcassionView(boolean showDrawer) {

        Calendar calendar = DateTimeUtil.adjustCalender(this);

        int currentOccasionIndex = 0;
        currentOccasionsList = new ArrayList<>();

        for (int i = 0; i < ocassionReposne.getOcassions().size(); i++) {

            if(ocassionReposne.getOcassions().get(i).isPreOrderOnly()){
                if(AppUtils.isTimeInBetween(ocassionReposne.getOcassions().get(i).preorderStartTime, ocassionReposne.getOcassions().get(i).preorderEndTime, calendar)){
                    ocassionReposne.getOcassions().get(i).isPreOrder = true;
                    currentOccasionsList.add(ocassionReposne.getOcassions().get(i));
                }
            }
            else{
                if (AppUtils.isTimeInBetween(ocassionReposne.getOcassions().get(i).startTime, ocassionReposne.getOcassions().get(i).endTime, calendar)) {
                    ocassionReposne.getOcassions().get(i).isPreOrder = false;
                    currentOccasionsList.add(ocassionReposne.getOcassions().get(i));
                }
                else if ((AppUtils.isTimeInBetween(ocassionReposne.getOcassions().get(i).preorderStartTime, ocassionReposne.getOcassions().get(i).preorderEndTime, calendar))){
                    ocassionReposne.getOcassions().get(i).isPreOrder = true;
                    currentOccasionsList.add(ocassionReposne.getOcassions().get(i));
                }
            }
        }


        if (currentOccasionsList.size() > 0) {

            currentOccasionsList = getSortedOccasionsList(currentOccasionsList);

            tvOccasion.setText(currentOccasionsList.get(currentOccasionIndex).name);
            AppUtils.setAlarmFor(DateTimeUtil.getTodaysTimeFromString(currentOccasionsList.get(currentOccasionIndex).endTime), this);

            if (rvOccasions.getAdapter() == null) {
                occasionChooserAdapter = new OccasionChooserAdapter(GlobalActivity.this,
                        currentOccasionsList, currentOccasionsList.get(currentOccasionIndex).id, new OccasionChangeInterface() {
                    @Override
                    public void onOccasionChanged(int currentOccasionIndex, final View view) {

                        if (currentOccasionIndex == -1) {

                            FreeOrderErrorHandleDialog orderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(new Order(),
                                    "Your cart will get cleared if you change the occasion.",
                                    new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveButtonClicked() {
                                            MainApplication mainApplication = (MainApplication) getApplication();
                                            mainApplication.clearOrder();
                                            MainApplication.bus.post(new OrderClear());
                                            view.performClick();
                                            addItemsToCart(-1);
                                        }

                                        @Override
                                        public void onNegativeButtonClicked() {

                                        }
                                    }, "OK", "CANCEL");


                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(orderErrorHandleDialog, "cart_clear")
                                    .commitAllowingStateLoss();
                        } else {
                            HBMixpanel.getInstance().addEvent(GlobalActivity.this, EventUtil.MixpanelEvent.CHANGE_OCCASION);

                            EventUtil.FbEventLog(GlobalActivity.this, EventUtil.HOME_OCCASION_CHANGE, EventUtil.SCREEN_HOME);
                            hide_Show_Fade_Anim(rvOccasions, View.GONE);
                            setSelectedOcassion(currentOccasionsList.get(currentOccasionIndex).id, currentOccasionsList.get(currentOccasionIndex).startTime);
                            tvOccasion.setText(currentOccasionsList.get(currentOccasionIndex).name);
                            MainApplication.addOcassion(currentOccasionsList.get(currentOccasionIndex));
                        }
                    }
                });
                rvOccasions.setLayoutManager(new LinearLayoutManager(GlobalActivity.this, LinearLayoutManager.HORIZONTAL, false));
                rvOccasions.setAdapter(occasionChooserAdapter);
            } else if (rvOccasions.getAdapter() != null && rvOccasions.getAdapter() instanceof OccasionChooserAdapter) {
                OccasionChooserAdapter occasionChooserAdapter = (OccasionChooserAdapter) rvOccasions.getAdapter();
                occasionChooserAdapter.setSelectedOccasion(currentOccasionsList.get(currentOccasionIndex).id);
                ((OccasionChooserAdapter) rvOccasions.getAdapter()).setOcassionArrayList(currentOccasionsList);
                rvOccasions.getAdapter().notifyDataSetChanged();
                rvOccasions.getLayoutManager().smoothScrollToPosition(rvOccasions, null, 0);
            }
            MainApplication.addOcassion(currentOccasionsList.get(currentOccasionIndex));
            handleCart(currentOccasionsList.get(currentOccasionIndex).id);
            setSelectedOcassion(currentOccasionsList.get(currentOccasionIndex).id, currentOccasionsList.get(currentOccasionIndex).startTime);
            getUserDetailsFromServer(false);
        }
        else{
            AppUtils.showToast("There are no current Occasions Available", true, 0);
        }
    }

    private void handleCart(long occassionId) {
        if (MainApplication.isCartCreated() && MainApplication.selectedOcassionId != occassionId) {
            MainApplication mainApplication = (MainApplication) getApplication();
            mainApplication.clearOrder();
            addItemsToCart(-1);
            MainApplication.bus.post(new OrderClear());
        }
    }

    public void setSelectedOcassion(long ocassionId, String startTime) {

        this.selectedOcasionId = ocassionId;
        if(AppUtils.getConfig(this).showWallet_breakup()){
            getUserPaymentDetails();
        }else{
            tvWallet.setVisibility(View.GONE);
        }
        Calendar calendar = DateTimeUtil.adjustCalender(this);
        if (calendar.getTimeInMillis() < DateTimeUtil.getTodaysTimeFromString(startTime))
            SharedPrefUtil.putBoolean(ApplicationConstants.IS_PREORDER_AVAILABLE, true);
        else
            SharedPrefUtil.putBoolean(ApplicationConstants.IS_PREORDER_AVAILABLE, false);

        if (fragment instanceof OrderFeedFragment) {
            OrderFeedFragment orderFeedFragment = (OrderFeedFragment) fragment;
            orderFeedFragment.updateViewWithSelectedOcassion(ocassionId);
        }
    }


    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Subscribe
    public void onOccasionChangeEventHandle(OccasionChangeEvent occasionChangeEvent) {
        AppUtils.HbLog("PEEYUSH", "refresh");
        getOccassionList();
    }

    public void checkDrawOverlayPermission() {
        if (AppUtils.isCafeApp())
            return;
        boolean isChatEnabled = SharedPrefUtil.getBoolean(ApplicationConstants.CHAT_HEAD_SETTING, true);
        boolean permissionAsked = SharedPrefUtil.getBoolean(ApplicationConstants.PERMISSION_ASKED, false);
        isChatEnabled = false;
        if (isChatEnabled) {
            if (permissionAsked)
                return;
            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance("The order head requires screen overlay permission." +
                    "\nPlease confirm for this permission.", "YES", new GenericPopUpFragment.OnFragmentInteractionListener() {
                @Override
                public void onPositiveInteraction() {
                    SharedPrefUtil.putBoolean(ApplicationConstants.PERMISSION_ASKED, true);
                    /** check if we already  have permission to draw over other apps */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.canDrawOverlays(getApplicationContext())) {
                            /** if not construct intent to request permission */
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            /** request permission via start activity for result */
                            startActivityForResult(intent, SCREEN_REQUEST_CODE);
                        }
                    }
                }

                @Override
                public void onNegativeInteraction() {
                    SharedPrefUtil.putBoolean(ApplicationConstants.CHAT_HEAD_SETTING, false);
                    SharedPrefUtil.putBoolean(ApplicationConstants.PERMISSION_ASKED, true);
                }
            });

            genericPopUpFragment.show(getSupportFragmentManager(), "order_head_confirm");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode == DECLARATION_FORM_WEBVIEW){
            ((OrderFeedFragment) fragment).refreshLogic(true);
        }
        else if(requestCode == LOCATION_CHANGE_REQUEST){
            if(resultCode == RESULT_OK) {
                MainApplication mainApplication = (MainApplication) getApplication();
                mainApplication.clearOrder();
                MainApplication.bus.post(new OrderClear());
            }
            checkForBTAllTime();
        }

    }

    public void getLocations() {
        String url = UrlConstant.COMPANY_LOCATION_GET + AppUtils.getConfig(GlobalActivity.this).getCompany_id();

        SimpleHttpAgent<CompanyResponse> locationSimpleHttpAgent = new SimpleHttpAgent<CompanyResponse>(
                GlobalActivity.this,
                url,
                new ResponseListener<CompanyResponse>() {
                    @Override
                    public void response(CompanyResponse responseObject) {

                        if (responseObject == null) {
                            showNoNetFragment(new RetryListener() {
                                @Override
                                public void onRetry() {
                                    getLocations();
                                }
                            });
                        } else {
                            AppUtils.addSubdomain(GlobalActivity.this, responseObject.companyResponse.subdomain);
                            updateSpinnerWithLocations(responseObject.companyResponse.locationResponse.locations);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                CompanyResponse.class
        );

        locationSimpleHttpAgent.get(getApiTag());
    }

    private void updateSpinnerWithLocations(final ArrayList<Location> locations) {
        if (locations == null)
            return;
        for (Location location : locations) {
            if (location.id == locationId) {

                break;
            }
        }


    }

    @Override
    protected void onDestroy() {
        HBMixpanel.getInstance().flushMixpanel(this);
        flushingHandlers();
        super.onDestroy();
    }

    private void hide_Show_Fade_Anim(final View view, int visible) {

        if (visible == View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bounce_in);
            view.startAnimation(animation);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onOccasionChangeEventHandle(BannerRefreshEvent bannerRefreshEvent) {
        getOccassionList();
    }

    @Override
    public void finish() {

        AppUtils.HbLog("app_state", "resume");
        try {

            ((OrderFeedFragment) fragment).orderFeedAdpater.activity = null;
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        super.finish();
    }

    void flushingHandlers(){
        try {
            if (doubleBackExitHandler != null) {
                doubleBackExitHandler.removeCallbacksAndMessages(null);
            }
            if (((OrderFeedFragment)fragment).orderTrackerHandler != null) {
                ((OrderFeedFragment)fragment).orderTrackerHandler.removeCallbacksAndMessages(null);
            }
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            if(feedbackHandler != null){
                feedbackHandler.removeCallbacksAndMessages(null);
            }
            if (((OrderFeedFragment)fragment).onBoardingHandler != null) {
                ((OrderFeedFragment)fragment).onBoardingHandler.removeCallbacksAndMessages(null);
            }
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void onAttach() {
        getOccassionList();
    }

    public interface OccasionChangeInterface {
        void onOccasionChanged(int occasionId, View view);
    }

    @Override
    protected void onUserLeaveHint() {


        try {
            if (dialogFragment != null && dialogFragment.getDialog() != null && dialogFragment.getDialog().isShowing()) {
                return;
            }
            if (emailActivationDialog != null && emailActivationDialog.getDialog() != null && emailActivationDialog.getDialog().isShowing()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (false && AppUtils.getConfig(getApplicationContext()).isPicture_in_picture() && !AppUtils.isCafeApp() && pictureInPictureRequired && Build.VERSION.SDK_INT >= 26 && getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) && !isInPictureInPictureMode() && SharedPrefUtil.getBoolean(ApplicationConstants.PIP_SETTING, true)) {
            rvOccasions.setVisibility(View.GONE);
            enterPictureInPictureMode(new PictureInPictureParams.Builder().build());
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {

        if (isInPictureInPictureMode) {

            findViewById(R.id.ll_bottom_nav).setVisibility(View.GONE);
            findViewById(R.id.cv_toolbar).setVisibility(View.GONE);
            ((OrderFeedFragment) fragment).refreshLayout.setVisibility(View.GONE);
            ((OrderFeedFragment) fragment).orderPipHeader.setVisibility(View.VISIBLE);
            findViewById(R.id.order_status).setVisibility(View.VISIBLE);
            if (fragment instanceof OrderFeedFragment) {
                ((OrderFeedFragment) fragment).qrButtonSection.setVisibility(View.GONE);
                ((OrderFeedFragment) fragment).btLeft.setVisibility(View.INVISIBLE);
                ((OrderFeedFragment) fragment).btLeft.setVisibility(View.INVISIBLE);
            }

            handleOrderStatusViewInPip(true);
        }
        else {
            if (fragment instanceof OrderFeedFragment) {
                if (MainApplication.isCartCreated()) {
                    ((OrderFeedFragment) fragment).qrButtonSection.setVisibility(View.VISIBLE);
                    ((OrderFeedFragment) fragment).btLeft.setVisibility(View.VISIBLE);
                    ((OrderFeedFragment) fragment).btLeft.setVisibility(View.VISIBLE);
                }
            }


            findViewById(R.id.ll_bottom_nav).setVisibility(View.VISIBLE);
            findViewById(R.id.cv_toolbar).setVisibility(View.VISIBLE);
            ((OrderFeedFragment) fragment).refreshLayout.setVisibility(View.VISIBLE);
            ((OrderFeedFragment) fragment).qrButtonSection.setVisibility(View.VISIBLE);
            ((OrderFeedFragment) fragment).orderPipHeader.setVisibility(View.GONE);
            findViewById(R.id.order_status).setVisibility(View.GONE);


            try {
                if (NetworkUtil.isInternetConnected(getApplicationContext())) {
                    ((OrderFeedFragment) fragment).refreshLayout.setRefreshing(true);
                    ((OrderFeedFragment) fragment).refreshLogic(true);
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }

            if (AppUtils.getConfig(this).isHide_qrcode(currentOrder.getLocation())){
                if (fragment instanceof OrderFeedFragment) {
                    ((OrderFeedFragment) fragment).btQr.setText("Collect Order");
                }
            }
            handleOrderStatusViewInPip(false);
        }
    }

    private void handleOrderStatusViewInPip(boolean isPip){

        if (fragment!=null && fragment instanceof OrderFeedFragment) {

            OrderFeedFragment orderFeedFragment = (OrderFeedFragment) fragment;

            if (isPip){

                orderFeedFragment.firstVerticalGuide.setGuidelinePercent(0.18f);
                orderFeedFragment.secondVerticalGuide.setGuidelinePercent(0.5f);
                orderFeedFragment.thirdVerticalGuide.setGuidelinePercent(0.82f);

                if (orderFeedFragment.currentOrderItem != null){

                    if (orderFeedFragment.currentOrderItem.getStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)) {

                        orderFeedFragment.tvAccepted.setVisibility(View.VISIBLE);
                        orderFeedFragment.tvReady.setVisibility(View.INVISIBLE);
                        orderFeedFragment.tvDelivered.setVisibility(View.INVISIBLE);
                    }
                    else if (orderFeedFragment.currentOrderItem.getStatus().equalsIgnoreCase(OrderUtil.PROCESSED)) {

                        orderFeedFragment.tvAccepted.setVisibility(View.INVISIBLE);
                        orderFeedFragment.tvReady.setVisibility(View.VISIBLE);
                        orderFeedFragment.tvDelivered.setVisibility(View.INVISIBLE);
                    }
                    else if (orderFeedFragment.currentOrderItem.getStatus().equalsIgnoreCase(OrderUtil.DELIVERED)) {

                        orderFeedFragment.tvAccepted.setVisibility(View.INVISIBLE);
                        orderFeedFragment.tvReady.setVisibility(View.INVISIBLE);
                        orderFeedFragment.tvDelivered.setVisibility(View.VISIBLE);
                    }
                }
            }
            else{
                orderFeedFragment.firstVerticalGuide.setGuidelinePercent(0.08f);
                orderFeedFragment.secondVerticalGuide.setGuidelinePercent(0.365f);
                orderFeedFragment.thirdVerticalGuide.setGuidelinePercent(0.64f);
                orderFeedFragment.tvAccepted.setVisibility(View.VISIBLE);
                orderFeedFragment.tvReady.setVisibility(View.VISIBLE);
                orderFeedFragment.tvDelivered.setVisibility(View.VISIBLE);
            }

        }

    }
    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        if (isRunning) {
            Intent intent = new Intent(this, BookmarkPaymentActivity.class);
            intent.putExtra(ApplicationConstants.FROM_BOOKMARK, true);
            startActivity(intent);
        }
    }
    public interface MoreClick{
        void onClick();
    }

    @Override
    public void onAllApiCallSuccess(String configUrl){
        ((OrderFeedFragment) fragment).afterWatermarkHandling();
    }
    public interface SlotBookingOrderSuccessListener{
        void onOrderPlaced();
    }
}
