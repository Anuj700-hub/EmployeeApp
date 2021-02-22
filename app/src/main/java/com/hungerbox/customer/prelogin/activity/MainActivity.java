package com.hungerbox.customer.prelogin.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.logcatviewer.LogcatViewerUtil;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.Vendorevent.Service.VendorEventTimeHandleService;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.CardClearTask;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.VersionViewCloseEvent;
import com.hungerbox.customer.model.Company;
import com.hungerbox.customer.model.DataHandlerExtras;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FCMDevice;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.model.serializer.UserSerializer;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.DataHandler;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.offline.activityOffline.UrlConstantsOffline;
import com.hungerbox.customer.offline.modelOffline.ServerStatus;
import com.hungerbox.customer.order.activity.OrderDetailNewActivity;
import com.hungerbox.customer.prelogin.fragment.VersionFragment;
import com.hungerbox.customer.service.OrderUpdateService;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class MainActivity extends ParentActivity {


    VersionFragment versionFragment;
    private ImageView pb;
    private int COMMON_QRCODE = 1;
    private RelativeLayout commonLayout;
    private Button getStarted;
    private Button goButton;
    private RelativeLayout rlCompanyLogo;
    private ImageView ivCompanyLogo, ivCompanyOnlyLogo;
    private TextView tvCompanyName;
    private EditText edittext;
    private Runnable runnable;
    private Handler handler;
    private boolean status = true,isWaterMarkAvailable = false;

    OkHttpClient client = AppUtils.getstethoOkHttpBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (BuildConfig.BUILD_TYPE.contains("qa") && SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE,"").equals("qa")) {
            if(SharedPrefUtil.getBoolean(ApplicationConstants.LOG_SETTING,false)) {
                LogcatViewerUtil.startLogcatViewer(this, BuildConfig.DEBUG);
            }
        }

        if(BuildConfig.BUILD_TYPE.equalsIgnoreCase("qa") && SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE,"").equalsIgnoreCase("")){

            new Handler().postDelayed(() -> {
                startActivity(new Intent(this,ManualURLActivity.class));
            }, 3000);
        }else{

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                try {
                    ProviderInstaller.installIfNeeded(MainActivity.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    AppUtils.HbLog("TLS", e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    AppUtils.HbLog("TLS", e.getMessage());
                }
            }

            if (!BuildConfig.APPLICATION_ID.contains("common")) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            setApiTag(String.valueOf(System.currentTimeMillis()));

            setContentView(R.layout.activity_main);
            AppUtils.setupUI(findViewById(R.id.mainActivityParent), MainActivity.this);

            commonLayout = findViewById(R.id.common);
            rlCompanyLogo = findViewById(R.id.layout_company_logo);

            ivCompanyLogo = findViewById(R.id.iv_company_logo);
            ivCompanyOnlyLogo = findViewById(R.id.iv_company_only_logo);
            tvCompanyName = findViewById(R.id.tv_company_name);


            final EditText edittext = findViewById(R.id.edQR);
            edittext.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {

                        goButton.performClick();
                        return true;
                    }
                    return false;
                }
            });

            edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        goButton.setVisibility(View.VISIBLE);
                    } else {
                        goButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            goButton = findViewById(R.id.go);

            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edittext.getText().toString() != null && !edittext.getText().toString().equals("")) {
                        getCompanyId(edittext.getText().toString(), false);
                    } else {
                        AppUtils.showToast("Enter Company Code / Company Email.", true, 2);
                    }
                    AppUtils.hideKeyboard(MainActivity.this, edittext);
                }
            });

            getStarted = findViewById(R.id.get_started);

            pb = findViewById(R.id.pb);

            showGif();

            HBMixpanel.getInstance().addEvent(this, EventUtil.MixpanelEvent.APP_LAUNCH);

            String refType = getIntent().getStringExtra("ref_type");
            String refId = getIntent().getStringExtra("ref_id");

            if (refType != null && refId != null && refType.equalsIgnoreCase("order")) {

                Order orderFromNotification = new Order();
                orderFromNotification.setId(Long.parseLong(refId));
                orderFromNotification.setOrderStatus(OrderUtil.NEW);
                Intent intent = new Intent(this, OrderDetailNewActivity.class);
                intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
                intent.putExtra(ApplicationConstants.FROM_NOTIFICATION, true);
                intent.putExtra(ApplicationConstants.BOOKING_ID, orderFromNotification.getId());
                startActivity(intent);
                startOrderNotificationService(Long.valueOf(refId));
                finish();
                return;
            }

            final Config config = ((MainApplication) getApplication()).getConfig();

            if (config != null && config.getCompany_id() != -1) {
                Bundle bundle = new Bundle();
                bundle.putString(EventUtil.MixpanelEvent.SuperProperties.COMPANY_ID, String.valueOf(config.getCompany_id()));
                EventUtil.FbEventLog(this, EventUtil.MixpanelEvent.APP_LAUNCH, bundle);
            } else {
                EventUtil.FbEventLog(this, EventUtil.MixpanelEvent.APP_LAUNCH, "");
            }

            EventUtil.logBaseEvent(this, EventUtil.APP_LAUNCH);

            String applicationId = BuildConfig.APPLICATION_ID;


            getStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
                    intent.putExtra("source", "common");
                    startActivityForResult(intent, COMMON_QRCODE);
                }
            });

            checkAuthTokenValidity();

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    setConfigAndCheckForVersion(config);
                }
            };

            String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");

            if (BuildConfig.APPLICATION_ID.contains("common")) {

                String cId = SharedPrefUtil.getString(ApplicationConstants.PREF_UNIFIED_COMPANY_ID, "");
                if (cId == null || cId.equals("")) {
                    commonLayout.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);

                } else {
                    applicationId = cId;
                    String url = UrlConstant.CONFIG_URL_GET + applicationId + "/android?unified=true";

                    if (config == null || config.getCompany_id() == -1 || authToken == null || authToken.isEmpty()) {
                        watermarkHandling(url);
                    } else {
                        checkServerStatus(url);
                    }
                }
            } else {
                if (!AppUtils.isCafeApp()) {
                    String url = UrlConstant.CONFIG_URL_GET + applicationId + "/android";
                    if (config == null || config.getCompany_id() == -1 || authToken == null || authToken.isEmpty()) {
                        watermarkHandling(url);
                    } else {
                        checkServerStatus(url);
                    }
                } else {
                    startBateryEventService();
                    navigateCustomSetupActivity();
                }
            }
        }
    }

    private void showGif() {

        Glide.with(this).asGif().load(R.drawable.splash_screen).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {

                    }
                });
                return false;
            }
        }).into(pb);

    }

    private void performClick() {
        if (edittext.getText().toString() != null && !edittext.getText().toString().equals("")) {
            getCompanyId(edittext.getText().toString(), false);
        } else {
            AppUtils.showToast("Enter Company Code / Company Email.", true, 2);
        }
        AppUtils.hideKeyboard(MainActivity.this, edittext);
    }

    private void checkAuthTokenValidity() {
        try {
            Log.d("Auth token", "token expires at : " + DateTimeUtil.getDateString12Hour(SharedPrefUtil.getLong(ApplicationConstants.PREF_AUTH_EXPIRY, -1)));
            if (SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN) != null &&
                    !SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN).equalsIgnoreCase("") &&
                    SharedPrefUtil.getString(ApplicationConstants.PREF_REFRESH_TOKEN) != null &&
                    !SharedPrefUtil.getString(ApplicationConstants.PREF_REFRESH_TOKEN).equalsIgnoreCase("") &&
                    SharedPrefUtil.getLong(ApplicationConstants.PREF_AUTH_EXPIRY, -1) != -1
            ) {
                if (DateTimeUtil.adjustCalender(this).getTimeInMillis() >= SharedPrefUtil.getLong(ApplicationConstants.PREF_AUTH_EXPIRY)) {
                    refreshToken();
                }
            }
            //refreshToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshToken() {
        SimpleHttpAgent<User> userSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                UrlConstant.LOGIN_URL,
                new ResponseListener<User>() {
                    @Override
                    public void response(User responseObject) {
                        LogoutTask.updateTime();
                        if (responseObject != null) {
                            try {
//                                HashMap<String,Object> map = new HashMap<>();
//                                map.put(CleverTapEvent.PropertiesNames.getSignin_type(),"sign_in");
//                                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getSignin_click(),map,getApplicationContext());

                                SharedPrefUtil.putString(ApplicationConstants.PREF_AUTH_TOKEN, responseObject.getAuthToken());
                                SharedPrefUtil.putString(ApplicationConstants.PREF_REFRESH_TOKEN, responseObject.getRefreshToken());
                                SharedPrefUtil.putLong(ApplicationConstants.PREF_AUTH_EXPIRY, AppUtils.getAuthExpiry(responseObject.getTokenExpiry(), MainActivity.this));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        LogoutTask.updateTime();
                        SharedPrefUtil.remove(ApplicationConstants.PREF_REFRESH_TOKEN);
                    }
                },
                User.class
        );

        HashMap<String, JsonSerializer> jsonSerializerHashMap = new HashMap<>();
        HashMap<String, Object> input = new HashMap<>();
        input.put("client_id", UrlConstant.CLIENT_ID);
        input.put("client_secret", UrlConstant.CLIENT_SECRET);
        input.put("grant_type", "refresh_token");
        input.put("refresh_token", SharedPrefUtil.getString(ApplicationConstants.PREF_REFRESH_TOKEN));
        jsonSerializerHashMap.put("io.realm.UserRealmProxy", new UserSerializer());
        userSimpleHttpAgent.post(input, new HashMap<String, JsonSerializer>(), getApiTag());
    }

    private void checkServerStatus(final String configUrl) {

        final String url = UrlConstant.CHECK_SERVER_STATUS;
        SimpleHttpAgent<ServerStatus> simpleHttpAgent = new SimpleHttpAgent<ServerStatus>(this
                , url,
                new ResponseListener<ServerStatus>() {
                    @Override
                    public void response(ServerStatus responseObject) {
                        if (responseObject != null) {
                            if (!responseObject.getStatus()) {

                                watermarkHandling(configUrl);
                            } else {

                                UrlConstantsOffline.initializeUrl(responseObject.getNewEndpoint());
                                status = false;
                                navigateToLoginActivity();

                            }
                        } else {
                            watermarkHandling(configUrl);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                        watermarkHandling(configUrl);
                    }
                }, ServerStatus.class);
        simpleHttpAgent.getWithoutHeader(getApiTag());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void manageToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.tv_toolbar_title)).setText("Home");
        findViewById(R.id.iv_ocassion).setVisibility(View.GONE);
        findViewById(R.id.iv_search).setVisibility(View.GONE);
        findViewById(R.id.tv_location).setVisibility(View.GONE);

    }


    private void startBateryEventService() {
        Intent intent = new Intent(this, VendorEventTimeHandleService.class);
        startService(intent);
    }


    private void startOrderNotificationService(long refId) {
        Intent intent = new Intent(this, OrderUpdateService.class);
        intent.putExtra(ApplicationConstants.ORDER_ID, refId);
        startService(intent);
    }

    private void watermarkHandling(final String url) {

        String authTokens = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, null);

        if (authTokens != null && AppUtils.isFlavorAllowed()) {
            initialiseWatermarkCalls(MainActivity.this, url);
        } else {

            if (!DbHandler.isStarted())
                DbHandler.start(getApplicationContext());
            DbHandler.getDbHandler(this).deleteDataTable();

            callConfigFromServer(url);
        }
    }

    private void callConfigFromServer(final String url) {

        HashMap<String, String> objectIds = new HashMap<>();
        if (BuildConfig.APPLICATION_ID.contains("common")) {
            objectIds.put(ApplicationConstants.OBJECT_ID_1, SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, 0) + "");
        } else {
            objectIds.put(ApplicationConstants.OBJECT_ID_1, BuildConfig.APPLICATION_ID + "");
        }
        objectIds.put(ApplicationConstants.OBJECT_ID_2, "android");

        new DataHandler<>(
                this,
                url,
                responseObject -> {

                    try {
                        if (responseObject != null && responseObject.getCompany_id() != -1) {
                            EventUtil.logUser(MainActivity.this, String.valueOf(responseObject.getCompany_id()));
                        }
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }

                    if (responseObject == null) {

                        ErrorPopFragment orderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(
                                "Some error occurred.", "RETRY", true, ApplicationConstants.GENERAL_ERROR,
                                new ErrorPopFragment.OnFragmentInteractionListener() {
                                    @Override
                                    public void onPositiveInteraction() {
                                        callConfigFromServer(url);
                                    }

                                    @Override
                                    public void onNegativeInteraction() {
                                        finish();
                                    }
                                });

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .add(orderErrorHandleDialog, "fetch_config")
                                .commitAllowingStateLoss();
                    } else {
                        setConfigAndCheckForVersion(responseObject);
                    }
                },
                (errorCode, error, errorResponse) -> {


                    String message = "";
                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                        message = "Please check your internet connection.";
                    } else {
                        if(error!=null && error.length()>0){
                            message = error;
                        }else {
                            message = "Some error occurred.";
                        }
                    }

                    ErrorPopFragment orderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(
                            message, "RETRY", true, ApplicationConstants.GENERAL_ERROR,
                            new ErrorPopFragment.OnFragmentInteractionListener() {
                                @Override
                                public void onPositiveInteraction() {
                                    watermarkHandling(url);
                                }

                                @Override
                                public void onNegativeInteraction() {
                                    finish();
                                }
                            });

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(orderErrorHandleDialog, "fetch_config")
                            .commitAllowingStateLoss();
                },
                Config.class,
                objectIds,
                ApplicationConstants.CRUD.GET,
                new DataHandlerExtras()
        );

    }

    private void setConfigAndCheckForVersion(Config config) {
        new ConfigStorehandler(config).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void checkAndShowDialog(String title, String desc, boolean isHard) {

        pb.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        versionFragment = VersionFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("desc", desc);
        bundle.putBoolean("isHard", isHard);
        versionFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .add(versionFragment, "version")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commitAllowingStateLoss();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.bus.unregister(this);
    }

    @Subscribe
    public void versionViewCloseEvent(VersionViewCloseEvent versionViewCloseEvent) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .remove(versionFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();

        pb.setVisibility(View.VISIBLE);
        navigate();
    }

    private void navigate() {
        navigateToLoginActivity();
        AppUtils.postDeviceData(this);
    }

    @Override
    public void onAllApiCallSuccess(String configUrl){
        callConfigFromServer(configUrl);
    }

    private void navigateCustomSetupActivity() {
        Intent intent = new Intent(this, CustomSetupActivity.class);
        startActivity(intent);
        finish();
    }

    private void splashScreenActivity() {

        commonLayout.setVisibility(View.GONE);
        String companyLogoUrl = AppUtils.getConfig(MainActivity.this).getCompany_logo();

        if (AppUtils.getConfig(MainActivity.this).getApp_name() == null || AppUtils.getConfig(MainActivity.this).getApp_name().equals("")) {
            tvCompanyName.setVisibility(View.GONE);

            ivCompanyLogo.setVisibility(View.GONE);
            ivCompanyOnlyLogo.setVisibility(View.VISIBLE);
            if (companyLogoUrl.startsWith("http")) {
                ImageHandling.loadBackgroundImageInViewWithCallback(companyLogoUrl, getApplicationContext(), target);

            } else {
                ivCompanyOnlyLogo.setVisibility(View.GONE);
                navigateToWelcomeActivity();
            }
        } else {
            tvCompanyName.setText(AppUtils.getConfig(MainActivity.this).getApp_name());
            ivCompanyOnlyLogo.setVisibility(View.GONE);
            ivCompanyLogo.setVisibility(View.VISIBLE);
            if (companyLogoUrl.startsWith("http")) {
                ImageHandling.loadBackgroundImageInViewWithCallback(companyLogoUrl, getApplicationContext(), target);
            } else {
                ivCompanyLogo.setVisibility(View.GONE);
                navigateToWelcomeActivity();
            }
        }
    }

    private SimpleTarget target = new SimpleTarget<Drawable>() {

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            pb.setVisibility(View.GONE);
            rlCompanyLogo.setVisibility(View.VISIBLE);

            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_openinig);

            if (ivCompanyLogo.getVisibility() == View.VISIBLE) {
                ivCompanyLogo.setImageDrawable(resource);
                ivCompanyLogo.startAnimation(anim);
            } else {
                ivCompanyOnlyLogo.setImageDrawable(resource);
                ivCompanyOnlyLogo.setAnimation(anim);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigateToWelcomeActivity();
                }
            }, 2000);
        }

        @Override
        public void onLoadFailed(Drawable errorDrawable) {
            navigateToWelcomeActivity();
        }
    };


    private void navigateToLoginActivity() {

        Intent intent;
        String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, null);


        if (authToken != null) {
            if (status) {
                intent = AppUtils.getHomeNavigationIntent(this);
                if(getIntent().getExtras() != null){
                    Bundle extras = getIntent().getExtras();
                    if(extras.get("REQUEST_FOR") != null){
                        intent.putExtra("REQUEST_FOR", extras.getString("REQUEST_FOR"));
                    }
                }
            } else {
                intent = AppUtils.getHomeNavigationIntentOffline(this);
            }

            try{
                HashMap<String,Object> map = new HashMap<>();
                map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getUser_active(),map,getApplicationContext());
            }catch (Exception e){
                e.printStackTrace();
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            splashScreenActivity();
        }
    }

    private void navigateToWelcomeActivity() {

        Intent intent = new Intent(this, HBWelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COMMON_QRCODE) {

            if (resultCode == RESULT_OK) {
                String value = data.getStringExtra("data");
                getCompanyId(value, true);
            }
        }
        else if(requestCode == LogcatViewerUtil.APP_OVERLAY_PERMISSION_REQUEST){
            LogcatViewerUtil.logcatViewerRequestHandler(this, BuildConfig.DEBUG);
        }
    }

    private void getCompanyId(final String companyId, final boolean isScanned) {

        pb.setVisibility(View.VISIBLE);
        commonLayout.setVisibility(View.GONE);

        String url = UrlConstant.COMMON_COMPANY_ID_GET;
        SimpleHttpAgent<Company> getCompanyId = new SimpleHttpAgent<Company>(
                this.getApplicationContext(),
                url,
                new ResponseListener<Company>() {
                    @Override
                    public void response(Company responseObject) {

                        try {
                            if (isScanned) {
                                SharedPrefUtil.putString(ApplicationConstants.REFERRAL_QR_CODE, companyId);
                            }
                            SharedPrefUtil.putString(ApplicationConstants.PREF_UNIFIED_COMPANY_CODE, companyId);
                            SharedPrefUtil.putString(ApplicationConstants.PREF_UNIFIED_COMPANY_ID, responseObject.getId() + "");
                            String url = UrlConstant.CONFIG_URL_GET + responseObject.getId() + "/android?unified=true";
                            SharedPrefUtil.putLong(ApplicationConstants.COMPANY_ID, responseObject.getId());

                            try {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put(CleverTapEvent.PropertiesNames.getCompanyId(), responseObject.getId());
                                if(isScanned){
                                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getScan_qr(), map, getApplicationContext());
                                }else {
                                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getCc_entered(), map, getApplicationContext());
                                }
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }

                            watermarkHandling(url);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                            AppUtils.showToast("Please check the company code.", true, 0);
                            pb.setVisibility(View.GONE);
                            commonLayout.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        AppUtils.showToast("Some error occured.", true, 0);
                        pb.setVisibility(View.GONE);
                        commonLayout.setVisibility(View.VISIBLE);
                    }
                },
                Company.class
        );

        getCompanyId.post(new Company().setCode(companyId), new HashMap<String, JsonSerializer>(), getApiTag());
    }

    @Override
    public void finish() {
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        super.finish();
    }

    class ConfigStorehandler extends AsyncTask<Void, Integer, Config> {
        Config config;

        public ConfigStorehandler(Config config) {
            this.config = config;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPrefUtil.putLong(ApplicationConstants.COMPANY_ID, config.getCompany_id());
            try {
                JSONObject mixPanelValues = new JSONObject();
                mixPanelValues.put(EventUtil.MixpanelEvent.SuperProperties.COMPANY_ID, config.getCompany_id());
                HBMixpanel.getInstance().registerSuperProperties(MainActivity.this, mixPanelValues);

            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }

        @Override
        protected Config doInBackground(Void... voids) {
            MainApplication mainApplication = (MainApplication) getApplication();
            mainApplication.setConfig(config);
            CardClearTask.getInstance(getApplicationContext()).startTimer();
            return config;
        }

        @Override
        protected void onPostExecute(Config config) {
            super.onPostExecute(config);
            int versionCode = BuildConfig.VERSION_CODE;
            long appLaunchTime = SharedPrefUtil.getLong(ApplicationConstants.PREF_LAUNCH_TIME, 0);

            long dayDiff = TimeUnit.MILLISECONDS.toDays(Math.abs(System.currentTimeMillis() - appLaunchTime));
            if (!AppUtils.isCafeApp() && config.getApp_update() != null && config.getApp_update().getHard_version() >= versionCode) {
                checkForPlayStoreUpdate(config.getApp_update().getTitle(), config.getApp_update().getHard_desc(), config.getApp_update().getUpdate_redirect_url(), true);
            } else if (!AppUtils.isCafeApp() && config.getApp_update() != null && config.getApp_update().getSoft_version() >= versionCode && dayDiff >= 2 && !config.isDirect_soft_update()) {
                SharedPrefUtil.putLong(ApplicationConstants.PREF_LAUNCH_TIME, System.currentTimeMillis());
                checkAndShowDialog(config.getApp_update().getTitle(), config.getApp_update().getSoft_desc() ,false);
            } else {

                navigate();
            }
        }
    }

    private void checkForPlayStoreUpdate(String title, String desc, String redirectURL, boolean isHard){
        try {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                    if( appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
                        checkAndShowDialog(title,desc,isHard);
                    }else{
                        navigate();
                    }
                }
            });
        }catch (Exception e){
            navigate();
            e.printStackTrace();
        }
    }

}
