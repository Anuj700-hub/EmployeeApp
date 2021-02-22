package com.hungerbox.customer.prelogin.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.deviceinfo.DeviceRegister;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.contest.activity.TermsAndCondition;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FCMDevice;
import com.hungerbox.customer.model.GenerateChallenge;
import com.hungerbox.customer.model.GuestAccessToken;
import com.hungerbox.customer.model.HbEvent;
import com.hungerbox.customer.model.OnBoard;
import com.hungerbox.customer.model.RegistrationUser;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.navmenu.fragment.PasswordChangeDialog;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.fragment.OnBoardingPagerAdapter;
import com.hungerbox.customer.prelogin.fragment.ResetPasswordLogoutDialog;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.tasks.VendorEventStoreTask;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HBWelcomeActivity extends LoginActivity {

    private static final int SSO_LOGIN_REQUEST = 777;
    public static String payload;
    private Button btnLogin, btnSSO, btnOtp;
    private Button btnRegister;
    private ImageView ivCompanyLogo, ivCompanyOnlyLogo ,ivPolicyDivider,ivBack,nfc;
    private TextView tvCompanyName,tvPolicy,tvTerms, tvNfcText ,tvNfc,tvPeripheral,tvPeripheralId;
    private View ssoOverlay;
    private RelativeLayout rlNfc,rlBack,rlCafeView;
    private Button btnHideNfc, btGuest;
    private long hbDeviceId = -1;
    private ProgressBar progressBar;

    ViewPager vpOnBoarding;
    ArrayList<OnBoard> onBoards = new ArrayList<>();
    private Handler handler;
    private Runnable runnable;
    private LinearLayout llIndicatorContainer,llGuestView;
    LottieAnimationView lottieNfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.hb_activity_welcome);

        RegistrationUser registrationUser = new Gson().fromJson(SharedPrefUtil.getString(ApplicationConstants.REGISTRATERED_USER, null), RegistrationUser.class);
        if (!AppUtils.isCafeApp() && AppUtils.getConfig(HBWelcomeActivity.this).isSignup_email_activation() && registrationUser != null && registrationUser.getEmailActivated() == 0) {
            Intent intent = new Intent(this, EmailActivationRequired.class);
            intent.putExtra("reg_user", registrationUser);
            startActivity(intent);
            finish();
            return;
        }

        btnLogin = findViewById(R.id.btn_login);
        ssoOverlay = findViewById(R.id.sso_overlay);
        btnSSO = findViewById(R.id.btn_sso);
        btnOtp = findViewById(R.id.btn_otp);
        btnRegister = findViewById(R.id.btn_register);
        ivCompanyLogo = findViewById(R.id.iv_company_logo);
        ivCompanyOnlyLogo = findViewById(R.id.iv_company_only_logo);
        tvCompanyName = findViewById(R.id.tv_company_name);
        String companyLogoUrl = AppUtils.getConfig(HBWelcomeActivity.this).getCompany_logo();
        rlNfc = (RelativeLayout) findViewById(R.id.rl_nfc);
        btnHideNfc = (Button) findViewById(R.id.btn_hide_nfc);
        vpOnBoarding = findViewById(R.id.vp_onboard);
        llIndicatorContainer = findViewById(R.id.ll_onboarding_indicators);
        ivPolicyDivider = findViewById(R.id.iv_policy_divider);
        tvPolicy = findViewById(R.id.tv_policy);
        tvTerms = findViewById(R.id.tv_terms);
        btGuest = findViewById(R.id.bt_guest);
        progressBar = findViewById(R.id.progress_bar);
        tvNfcText = findViewById(R.id.tv_nfc_text);
        tvNfc = findViewById(R.id.tv_nfc);
        lottieNfc = findViewById(R.id.animation_view);
        rlBack = findViewById(R.id.layout_header);
        ivBack = findViewById(R.id.iv_back);
        tvPeripheral = findViewById(R.id.tv_peripheral);
        tvPeripheralId = findViewById(R.id.tv_peripheral_id);
        nfc = findViewById(R.id.nfc);
        llGuestView = findViewById(R.id.ll_guest_view);
        rlCafeView = findViewById(R.id.rl_cafe_view);

        SharedPrefUtil.putBoolean(ApplicationConstants.IS_GUEST_USER,false);
        Config config = AppUtils.getConfig(getApplicationContext());
        if (config == null || config.getCompany_id() == -1) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        setSsoLogout();
        setupOnboardingData();
        initOnBoarding();

        if (AppUtils.getConfig(HBWelcomeActivity.this).getLogin_auth() != null && AppUtils.getConfig(HBWelcomeActivity.this).getLogin_auth().getSignup_enabled()) {
            if (!AppUtils.isCafeApp())
                btnRegister.setVisibility(View.VISIBLE);
        }

        if (AppUtils.isCafeApp()) {
            rlNfc.setVisibility(View.VISIBLE);
            rlBack.setVisibility(View.VISIBLE);
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlNfc.setVisibility(View.VISIBLE);
            }
        });


        if(AppUtils.getConfig(this).isIs_sso_login()){
            if(!AppUtils.isCafeApp()) {
                btnSSO.setVisibility(View.VISIBLE);
            }
            btnSSO.setText(AppUtils.getConfig(this).getSso_login_text());
            if(AppUtils.getConfig(this).isIs_sso_login_only())
            {
                btnOtp.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                if(AppUtils.isCafeApp())
                {
                    btnSSO.setVisibility(View.GONE);
                    rlNfc.setVisibility(View.VISIBLE);
                    btnHideNfc.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tvCompanyName.setTextColor(getResources().getColor(R.color.white,null));
                    }else{
                        tvCompanyName.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }
        }
        else {
            btnRegister.setText("NEW TO HUNGERBOX? SIGN UP ");
        }


        btnHideNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlNfc.setVisibility(View.GONE);
            }
        });


        if (AppUtils.getConfig(HBWelcomeActivity.this).getApp_name() == null || AppUtils.getConfig(HBWelcomeActivity.this).getApp_name().equals("")) {
            tvCompanyName.setVisibility(View.GONE);

            ivCompanyLogo.setVisibility(View.GONE);
            ivCompanyOnlyLogo.setVisibility(View.VISIBLE);
            if (companyLogoUrl.startsWith("http")) {

                ImageHandling.loadRemoteImage(companyLogoUrl, ivCompanyOnlyLogo, -1, -1, getApplicationContext());

            } else {
                ivCompanyOnlyLogo.setVisibility(View.GONE);
            }
        } else {
            tvCompanyName.setText(AppUtils.getConfig(HBWelcomeActivity.this).getApp_name());
            ivCompanyOnlyLogo.setVisibility(View.GONE);
            ivCompanyLogo.setVisibility(View.VISIBLE);
            if (companyLogoUrl.startsWith("http")) {
                ImageHandling.loadRemoteImage(companyLogoUrl, ivCompanyLogo, -1, -1, getApplicationContext());

            } else {
                ivCompanyLogo.setVisibility(View.GONE);
            }
        }


        if (!AppUtils.getConfig(this).isOtp_login()) {
            btnOtp.setVisibility(View.GONE);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getCompanyId(),AppUtils.getConfig(getApplicationContext()).getCompany_id());
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getSignin_click(),map,getApplicationContext());
                }catch (Exception e){
                    e.printStackTrace();
                }

                Intent intent = new Intent(HBWelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getCompanyId(),AppUtils.getConfig(getApplicationContext()).getCompany_id());
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getSign_in_with_otp_click(),map,getApplicationContext());
                }catch (Exception e){
                    e.printStackTrace();
                }

                Intent intent = new Intent(HBWelcomeActivity.this, SendOtpActivity.class);
                startActivity(intent);
            }
        });

        btnSSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getCompanyId(),AppUtils.getConfig(getApplicationContext()).getCompany_id());
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getSso_login_click(),map,getApplicationContext());
                }catch (Exception e){
                    e.printStackTrace();
                }

                ssoOverlay.setVisibility(View.VISIBLE);
                Intent intent = new Intent(HBWelcomeActivity.this, SSOLoginActivity.class);
                intent.putExtra(ApplicationConstants.COMPANY_ID, AppUtils.getConfig(HBWelcomeActivity.this).getCompany_id());
                startActivityForResult(intent, SSO_LOGIN_REQUEST);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                if (BuildConfig.APPLICATION_ID.contains("cafe")) {
                    intent = new Intent(HBWelcomeActivity.this, SignUpActivityBasic.class);
                    long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
                    HashMap<String, Long> qrCodeMap = AppUtils.getConfig(HBWelcomeActivity.this).getRegistration_qr_hashs();
                    String qrCode = null;
                    for (String key : qrCodeMap.keySet()) {
                        long id = qrCodeMap.get(key);
                        if (id == locationId) {
                            intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);
                            intent.putExtra(ApplicationConstants.QR_CODE, key);
                            qrCode = key;
                            break;
                        }
                    }

                    if (qrCode == null) {
                        AppUtils.showToast("Not configured Properly", true, 0);
                        return;
                    }
                } else if (BuildConfig.FLAVOR.equalsIgnoreCase("common")) {
                    intent = new Intent(HBWelcomeActivity.this, SignUpActivityBasic.class);
                } else {
                    intent = new Intent(HBWelcomeActivity.this, QRScannerActivity.class);
                }

                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getCompanyId(),AppUtils.getConfig(getApplicationContext()).getCompany_id());
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getSignup_clicked(),map,getApplicationContext());
                }catch (Exception e){
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });
        ivCompanyLogo.setVisibility(View.GONE);
        ivCompanyOnlyLogo.setVisibility(View.GONE);
        if (AppUtils.isCafeApp()) {
            tvCompanyName.setVisibility(View.VISIBLE);
            tvCompanyName.setTextSize(30);
        } else {
            tvCompanyName.setVisibility(View.GONE);
        }

        if(AppUtils.getConfig(this).getPrivacy_policy()!=null && !AppUtils.getConfig(this).getPrivacy_policy().equals("")){
            tvPolicy.setVisibility(View.VISIBLE);
        }

        if(AppUtils.getConfig(this).getTerms_of_use()!=null && !AppUtils.getConfig(this).getTerms_of_use().equals("")){
            tvTerms.setVisibility(View.VISIBLE);
        }

        if(tvTerms.getVisibility()==View.VISIBLE && tvPolicy.getVisibility()==View.VISIBLE){
            ivPolicyDivider.setVisibility(View.VISIBLE);
        }

        tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),TermsAndCondition.class);
                intent.putExtra(ApplicationConstants.IS_URL,true);
                intent.putExtra(ApplicationConstants.URL_STRING,AppUtils.getConfig(getApplicationContext()).getPrivacy_policy());
                intent.putExtra(ApplicationConstants.HEADER_TITLE,"Privacy Policy");
                startActivity(intent);

            }
        });

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), TermsAndCondition.class);
                intent.putExtra("isUrl",true);
                intent.putExtra("url",AppUtils.getConfig(getApplicationContext()).getTerms_of_use());
                intent.putExtra(ApplicationConstants.HEADER_TITLE,"Terms of Use");
                startActivity(intent);

            }
        });



        hbDeviceId = SharedPrefUtil.getLong(ApplicationConstants.HB_DEVICE_ID);
        if(AppUtils.isGuestApp() && AppUtils.getConfig(this).isCafe_app_guest_ordering() ) {
            if(SharedPrefUtil.getString(ApplicationConstants.PERIPHERAL_DEIVCE_REF)!=null && SharedPrefUtil.getString(ApplicationConstants.PERIPHERAL_DEIVCE_REF).length()>0) {
                tvPeripheral.setText("Device ID : "+SharedPrefUtil.getString(ApplicationConstants.PERIPHERAL_DEIVCE_REF));
                tvPeripheral.setVisibility(View.VISIBLE);
            }
            if(SharedPrefUtil.getString(ApplicationConstants.PERIPHERAL_DEIVCE_ID)!=null && SharedPrefUtil.getString(ApplicationConstants.PERIPHERAL_DEIVCE_ID).length()>0){
                tvPeripheralId.setText("Device Serial No : "+SharedPrefUtil.getString(ApplicationConstants.PERIPHERAL_DEIVCE_ID));
                tvPeripheralId.setVisibility(View.VISIBLE);
            }
            //Guest App Visibility
            llGuestView.setVisibility(View.VISIBLE);
            if(hbDeviceId!=-1) {
                btGuest.setVisibility(View.VISIBLE);
            }

            //Cafe App Visibility
            rlCafeView.setVisibility(View.GONE);
            tvCompanyName.setVisibility(View.GONE);
        }

        btGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChallengeFromServer();
            }
        });
        LogoutTask.getInstance(getApplicationContext()).stopTimer();

        String targetEnv = SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE,"");

        if(!targetEnv.equals("")){
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    private void getChallengeFromServer() {

        progressBar.setVisibility(View.VISIBLE);
        String url = UrlConstant.GENERATE_CHALLENGE + hbDeviceId;

        SimpleHttpAgent<GenerateChallenge> challengeSimpleHttpAgent = new SimpleHttpAgent<GenerateChallenge>(
                this,
                url,
                new ResponseListener<GenerateChallenge>() {
                    @Override
                    public void response(GenerateChallenge responseObject) {
                        if (responseObject != null) {
                            getAccessTokenForGuest(responseObject);
                        }else{
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        progressBar.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            AppUtils.showToast("No Internet Connection", true, 0);
                        } else {
                            if (error != null && !error.equals("")) {
                                AppUtils.showToast(error, false, 0);
                            } else {
                                AppUtils.showToast("Something went wrong", true, 0);
                            }
                        }
                    }
                },
                GenerateChallenge.class
        );

        challengeSimpleHttpAgent.getWithoutHeader();

    }

    private void getAccessTokenForGuest(GenerateChallenge generateChallenge) {

        String challenge_signature = DeviceRegister.getSignChallenge(this, generateChallenge.getChallengeString());

        if (challenge_signature == null) {
            showNullKeyDialog();
            return;
        }
        SharedPrefUtil.putString(ApplicationConstants.PREF_AUTH_TOKEN, "");


        GuestAccessToken guestAccessToken = new GuestAccessToken();
        guestAccessToken.setChallengeSignature(challenge_signature);
        guestAccessToken.setHbDeviceId(generateChallenge.getDeviceId());
        guestAccessToken.setCompanyId(SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID));
        guestAccessToken.setLocationId(SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID));

        String url = UrlConstant.LOGIN_URL;

        SimpleHttpAgent<User> challengeSimpleHttpAgent = new SimpleHttpAgent<User>(
                this,
                url,
                new ResponseListener<User>() {
                    @Override
                    public void response(User responseObject) {
                        if (responseObject != null) {
                            SharedPrefUtil.putString(ApplicationConstants.PREF_AUTH_TOKEN, responseObject.getAuthToken());
                            getUserDetailsFromServer();
                        }else{
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        progressBar.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            AppUtils.showToast("No Internet Connection", true, 0);
                        } else {
                            if (error != null && !error.equals("")) {
                                AppUtils.showToast(error, false, 0);
                            } else {
                                AppUtils.showToast("Something went wrong", true, 0);
                            }
                        }

                    }
                },
                User.class
        );

        challengeSimpleHttpAgent.post(guestAccessToken, new HashMap<>());

    }

    private void startLogoutTimer() {
        if (AppUtils.getConfig(this).isAuto_logout())
            LogoutTask.getInstance(getApplicationContext()).startTimer();
    }

    private void showNullKeyDialog() {
        GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance("Something went wrong(0).Please contact HungerBox Tech Support Team", "Ok", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
                finish();
            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        genericPopUpFragment.setCancelable(false);
        genericPopUpFragment.show(getSupportFragmentManager(), "root_dialog");
    }


    private void initOnBoarding() {
        vpOnBoarding.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (handler != null)
                    handler.removeCallbacks(runnable);

                updateIndicators(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void autoChangePage() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                vpOnBoarding.setCurrentItem((vpOnBoarding.getCurrentItem() + 1) % onBoards.size(), true);
            }
        };
        handler.postDelayed(runnable, AppUtils.getConfig(HBWelcomeActivity.this).getOnboarding_delay());

    }

    @Override
    protected void onDestroy() {
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            super.finishAfterTransition();
        else
            super.onBackPressed();
    }

    private void updateIndicators(int position) {
        int i = 0;
        for (i = 0; i < llIndicatorContainer.getChildCount(); i++) {
            llIndicatorContainer.getChildAt(i).setSelected(false);
        }
        if (position < llIndicatorContainer.getChildCount()) {
            View view = llIndicatorContainer.getChildAt(position);
            view.setSelected(true);
        }
        autoChangePage();

    }

    private void setupOnboardingData() {

        if (AppUtils.getConfig(this).getBranding() != null) {
            if (AppUtils.getConfig(this).getBranding().isWelcome_logo_enabled() && AppUtils.getConfig(this).getBranding().getLogo() != null &&
                    !(AppUtils.getConfig(this).getBranding().getLogo().equalsIgnoreCase(""))) {
                onBoards.add(new OnBoard().setText(AppUtils.getConfig(this).getBranding().getWelcome_text_title()).setHeader(AppUtils.getConfig(this).getBranding().getWelcome_text_desc()).setCompanyLogoUrl(AppUtils.getConfig(this).getBranding().getLogo()));
            }
        }
        if(AppUtils.getConfig(this).getOnboarding_text() != null && AppUtils.getConfig(this).getOnboarding_text().size() == 4){
            onBoards.add(new OnBoard().setText(AppUtils.getConfig(this).getOnboarding_text().get(0).getTitle()).setHeader(AppUtils.getConfig(this).getOnboarding_text().get(0).getDesc()).setIconId(R.drawable.ic_onboarding_new_1));
            onBoards.add(new OnBoard().setText(AppUtils.getConfig(this).getOnboarding_text().get(1).getTitle()).setHeader(AppUtils.getConfig(this).getOnboarding_text().get(1).getDesc()).setIconId(R.drawable.ic_onboarding_new_2));
            onBoards.add(new OnBoard().setText(AppUtils.getConfig(this).getOnboarding_text().get(2).getTitle()).setHeader(AppUtils.getConfig(this).getOnboarding_text().get(2).getDesc()).setIconId(R.drawable.ic_onboarding_new_3));
            onBoards.add(new OnBoard().setText(AppUtils.getConfig(this).getOnboarding_text().get(3).getTitle()).setHeader(AppUtils.getConfig(this).getOnboarding_text().get(3).getDesc()).setIconId(R.drawable.ic_onboarding_new_4));
        }else{
            onBoards.add(new OnBoard().setText(getString(R.string.ob1)).setHeader(getString(R.string.ob_body_1)).setIconId(R.drawable.ic_onboarding_new_1));
            onBoards.add(new OnBoard().setText(getString(R.string.ob2)).setHeader(getString(R.string.ob_body_2)).setIconId(R.drawable.ic_onboarding_new_2));
            onBoards.add(new OnBoard().setText(getString(R.string.ob3)).setHeader(getString(R.string.ob_body_3)).setIconId(R.drawable.ic_onboarding_new_3));
            onBoards.add(new OnBoard().setText(getString(R.string.ob4)).setHeader(getString(R.string.ob_body_4)).setIconId(R.drawable.ic_onboarding_new_4));
        }


        OnBoardingPagerAdapter onBoardingPagerAdapter = new OnBoardingPagerAdapter(getSupportFragmentManager(), onBoards);
        vpOnBoarding.setAdapter(onBoardingPagerAdapter);
        for (int i = 0; i < onBoards.size(); i++) {
            addIndicator();
        }
        updateIndicators(0);

    }

    private void addIndicator() {
        if (HBWelcomeActivity.this != null) {
            LayoutInflater inflater = LayoutInflater.from(HBWelcomeActivity.this);
            View view = inflater.inflate(R.layout.indicator_image, llIndicatorContainer, false);
            llIndicatorContainer.addView(view);
        }
    }

    private void setSsoLogout() {

        try {
            if (AppUtils.getConfig(HBWelcomeActivity.this).getSso_logout_url() == null) {
                return;
            }

            WebView wvSSOLogin = findViewById(R.id.ssoLogout);
            final WebSettings settings = wvSSOLogin.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(false);
            settings.setDomStorageEnabled(true);
            settings.setAllowFileAccess(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            settings.setPluginState(WebSettings.PluginState.ON);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                wvSSOLogin.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                wvSSOLogin.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            wvSSOLogin.clearCache(true);
            wvSSOLogin.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);

                }
            });
            wvSSOLogin.addJavascriptInterface(new WebViewInterFace(HBWelcomeActivity.this), "Android");
            wvSSOLogin.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }
            });
            wvSSOLogin.loadUrl(AppUtils.getConfig(HBWelcomeActivity.this).getSso_logout_url());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }


    private void navigateToNextScreen() {
        Calendar calendar = Calendar.getInstance();
        String launchDate = AppUtils.getConfig(HBWelcomeActivity.this).getLaunch_date();
        Intent intent;
        if (DateTimeUtil.getTodaysCalenderFromDateString(launchDate).after(calendar)) {
            intent = new Intent(this, PreLaunchActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void navigateToNextScreen(boolean fromSSO) {
        MainApplication.clearCart();
        Calendar calendar = Calendar.getInstance();
        String launchDate = AppUtils.getConfig(HBWelcomeActivity.this).getLaunch_date();
        Intent intent;
        if (DateTimeUtil.getTodaysCalenderFromDateString(launchDate).after(calendar)) {
            intent = new Intent(this, PreLaunchActivity.class);
        } else {
            intent = AppUtils.getHomeNavigationIntent(this);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SSO_LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                setSsoLogout();

                if (SharedPrefUtil.getInt(ApplicationConstants.SSO_LOGIN_LOCATION_ASKED, 0) == 0) {
                    SharedPrefUtil.putInt(ApplicationConstants.SSO_LOGIN_LOCATION_ASKED, 1);
                }

                getUserDetailsFromServer();
            } else {
                ssoOverlay.setVisibility(View.GONE);
                AppUtils.showToast("Login Failed", true, 0);
            }
        }
    }

    private void getUserDetailsFromServer() {

        String url = UrlConstant.USER_DETAIL;
        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        progressBar.setVisibility(View.GONE);
                        if (responseObject != null) {
                            long locationId = responseObject.user.getLocationId();
                            String locationName = responseObject.user.getLocationName();
                            if (!DbHandler.isStarted())
                                DbHandler.start(getApplicationContext());

                            long companyId = SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, 0);
                            if (companyId == responseObject.user.getCompanyId()) {

                                if(responseObject.getMeta().getScopes().containsKey(ApplicationConstants.GUEST_ORDERING_SCOPE)){
                                    SharedPrefUtil.putBoolean(ApplicationConstants.IS_GUEST_USER,true);
                                    startLogoutTimer();
                                }

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
                                AppUtils.logUser(responseObject.user.getUserName());
                                EventUtil.logUser(HBWelcomeActivity.this, responseObject.user);
                                if (responseObject.user.isResetRequired() &&
                                        !AppUtils.getConfig(getApplicationContext()).is_location_fixed()) {
                                    showResetPasswordAndNavigateToNextScreen(responseObject.user);
                                    ssoOverlay.setVisibility(View.VISIBLE);
                                } else {
                                    navigateToNextScreen(true);
                                }

                                logLoginEvent(responseObject.user);
                            } else {
                                ssoOverlay.setVisibility(View.VISIBLE);
                                SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
                                AppUtils.showToast("Unable to get your details from server", true, 0);
                            }
                        } else {
                            ssoOverlay.setVisibility(View.VISIBLE);
                        }
                        postDeviceData();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        ssoOverlay.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        AppUtils.showToast("Unable to get your details from server", true, 0);
                    }
                },
                UserReposne.class
        );
        userSimpleHttpAgent.get();
    }


    private void postDeviceData() {
        if (AppUtils.isCafeApp())
            return;

        String token = SharedPrefUtil.getString(ApplicationConstants.PREF_FCM_TOKEN, "");
        String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
        if (!token.isEmpty() && !authToken.isEmpty()) {
            String url = UrlConstant.DEVICE_REGISTER;
            SimpleHttpAgent<Object> fcmDeviceSimpleHttpAgent = new SimpleHttpAgent<>(
                    this.getApplicationContext(),
                    url,
                    new ResponseListener<Object>() {
                        @Override
                        public void response(Object responseObject) {

                        }
                    },
                    new ContextErrorListener() {
                        @Override
                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                        }
                    },
                    Object.class
            );

            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            fcmDeviceSimpleHttpAgent.post(new FCMDevice().setDeviceId(android_id).setFcmId(token), new HashMap<String, JsonSerializer>());
        }
    }

    private void logLoginEvent(User user) {
        if (user != null) {
            AppEvent appEvent = new AppEvent();
            HbEvent hbEvent = new HbEvent();
            hbEvent.updateTime(Calendar.getInstance().getTimeInMillis());
            appEvent.setVendorEventRegistrable(hbEvent).setEventName(EventUtil.LOGIN).setLocationId(user.getLocationId());
            EventUtil.updateCafeEventData(getApplicationContext(), appEvent, hbEvent);
            VendorEventStoreTask.addEventToQueue(this, appEvent);
        }
    }

    private void showResetPasswordAndNavigateToNextScreen(final User user) {


        PasswordChangeDialog fragment = PasswordChangeDialog.newInstance(new PasswordChangeDialog.OnPasswordChangeListener() {
            @Override
            public void onPasswordChanged() {
                showResetPasswordLogoutDialog(user);
            }

            @Override
            public void onCancel() {
                checkLocationAndNavigateToNextScreen(user);
            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, "pass_change")
                .commitAllowingStateLoss();


    }

    private void showResetPasswordLogoutDialog(final User user) {
        ResetPasswordLogoutDialog resetPasswordLogoutDialog = ResetPasswordLogoutDialog
                .newInstance(new ResetPasswordLogoutDialog.OnFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction() {
                        doLogout();
                    }
                });
        resetPasswordLogoutDialog.setCancelable(false);
        resetPasswordLogoutDialog.show(getSupportFragmentManager(), "Logout Dialog");
    }

    private void doLogout() {
        AppUtils.doLogoutServer(HBWelcomeActivity.this.getApplicationContext());
        SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
        SharedPrefUtil.remove(ApplicationConstants.PREF_USER_ID);
        finish();
    }

    private void checkLocationAndNavigateToNextScreen(User user) {
        navigateToNextScreen();
    }

    class WebViewInterFace {
        Context mContext;

        public WebViewInterFace(HBWelcomeActivity baseActivity) {
            this.mContext = baseActivity;
        }

        @JavascriptInterface
        public void openAppWithToken(String token) {

        }
    }

}
