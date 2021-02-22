package com.hungerbox.customer.prelogin.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FCMDevice;
import com.hungerbox.customer.model.HbEvent;
import com.hungerbox.customer.model.OTPUser;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.model.serializer.UserSerializer;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.tasks.VendorEventStoreTask;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.FatLiDialog;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;


public class OTPPasswordSetActivity extends ParentActivity {

    Button btSubmit;
    TextInputLayout tilPassword, tilPasswordVerify;
    TextInputEditText tetPassword, tetPasswordVerify;
    OTPUser otpUser;
    boolean resumed;
    User user;
    long companyId;
    private ImageView ivBack;
    RelativeLayout rlProgress;
    TextView tbTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otppassword_set);
        AppUtils.setupUI(findViewById(R.id.parentOtpPasswordSetActivity), OTPPasswordSetActivity.this);
        tilPassword = findViewById(R.id.til_password);
        tilPasswordVerify = findViewById(R.id.til_password_again);
        tetPassword = findViewById(R.id.tet_password);
        tetPasswordVerify = findViewById(R.id.tet_password_again);
        ivBack = findViewById(R.id.iv_back);
        rlProgress = findViewById(R.id.rl_progress);

        btSubmit = findViewById(R.id.bt_submit);
        tbTitle = findViewById(R.id.tb_title);

        otpUser = (OTPUser) getIntent().getSerializableExtra(ApplicationConstants.OTP_USER);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndSubmit();
            }
        });


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        companyId = AppUtils.getConfig(this).getCompany_id();
        tbTitle.setText("Set new password");
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
        ViewCompat.setBackgroundTintList(tetPassword, colorStateList);
        tetPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                ViewCompat.setBackgroundTintList(tetPassword, colorStateList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tetPasswordVerify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                ViewCompat.setBackgroundTintList(tetPasswordVerify, colorStateList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        resumed = false;
    }

    private void verifyAndSubmit() {
        String password = (tetPassword.getText() == null) ? "" : tetPassword.getText().toString();
        String passwordVerify = (tetPasswordVerify.getText() == null) ? "" : tetPasswordVerify.getText().toString();

        if (password.isEmpty()) {
            tilPassword.setError("Please enter password");
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate);
            tilPassword.startAnimation(shake);
            return;
        }
        if (password.length() < 8) {
            tilPassword.setError("Please enter atleast 8 characters");
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate);
            tilPassword.startAnimation(shake);
            return;
        }

        if (passwordVerify.isEmpty()) {
            tilPasswordVerify.setError("Please enter the password again here");
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate);
            tilPasswordVerify.startAnimation(shake);
            return;
        }
        if (passwordVerify.length() < 8) {
            tilPasswordVerify.setError("Please enter atleast 8 characters");
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate);
            tilPasswordVerify.startAnimation(shake);
            return;
        }

        if (!password.equals(passwordVerify)) {
            tilPasswordVerify.setError("Password enter do not match. Please enter again");
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate);
            tilPasswordVerify.startAnimation(shake);
            return;
        }

        submitPassword(password, passwordVerify);
    }

    private void submitPassword(final String password, String passwordVerify) {
        btSubmit.setEnabled(false);
        String url = UrlConstant.OTP_RESET_PASSWORD;

        SimpleHttpAgent<Object> otpPasswordSetAgent = new SimpleHttpAgent<Object>(
                this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        btSubmit.setEnabled(true);
                        performUserLogin(otpUser.getEmployeeId(), password, null);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        btSubmit.setEnabled(true);
                        showErrorOnResetPassword(error);
                    }
                },
                Object.class
        );

        TreeMap<String, String> otpPasswordVerify = new TreeMap<>();
        otpPasswordVerify.put("employeeId", otpUser.getEmployeeId());
        otpPasswordVerify.put("companyId", String.valueOf(companyId));
        otpPasswordVerify.put("password", password);
        otpPasswordVerify.put("password_confirmation", passwordVerify);
        otpPasswordVerify.put("otp", otpUser.getOtp());
        otpPasswordSetAgent.post(otpPasswordVerify, new HashMap<String, JsonSerializer>());
    }

    private void showErrorOnResetPassword(String error) {
        ErrorPopFragment genericPopUpFragment = ErrorPopFragment.Companion.newInstance(error, "RETRY",true,ApplicationConstants.GENERAL_ERROR,
                new ErrorPopFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        verifyAndSubmit();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "error")
                .commitAllowingStateLoss();
    }

    private void performUserLogin(String userName, String password, final String tagId) {

        EventUtil.FbEventLog(this, EventUtil.MixpanelEvent.RESET_PASSWORD, "");

        HBMixpanel.getInstance().addEvent(this, EventUtil.MixpanelEvent.RESET_PASSWORD);

        showProgress();
        String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, null);
        if (authToken != null) {
            return;
        }
        SimpleHttpAgent<User> userSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                UrlConstant.LOGIN_URL,
                new ResponseListener<User>() {
                    @Override
                    public void response(User responseObject) {
                        LogoutTask.updateTime();
                        if (responseObject != null) {
                            storeAndMoveToMainList(responseObject);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        LogoutTask.updateTime();
                        hideProgress();
                        if (tagId != null)
                            showWrongCardError();
                        else
                            showWrongLoginError();

//                        storeAndMoveToMainList(new User().setId(1).setUserName("peeyush").setAuthToken("test_auth"));
                    }
                },
                User.class
        );

        HashMap<String, JsonSerializer> jsonSerializerHashMap = new HashMap<>();
        jsonSerializerHashMap.put("io.realm.UserRealmProxy", new UserSerializer());
        userSimpleHttpAgent.post(new User().setPassword(password).setUserName(userName).setTagId(tagId).setCompanyId(companyId), jsonSerializerHashMap);

    }

    private void showProgress() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        rlProgress.setVisibility(View.GONE);
    }


    private void showLoginError(String message) {
        showErrorDialog(message);
    }

    private void showWrongLoginError() {
        showErrorDialog("Your login id or password is not correct. Please retry again");
    }

    private void showWrongCardError() {
        showErrorDialog("The card is not attached to the user. Please visit the helpdesk at your cafeteria");
    }

    private void showErrorDialog(String errorMessage) {
        ErrorPopFragment fatLiDialog = ErrorPopFragment.Companion.newInstance(errorMessage, "OK",true, ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {

            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        if (resumed) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(fatLiDialog, "error")
                    .commitAllowingStateLoss();
        }
    }

    private void startLogoutTimer() {
        if (AppUtils.getConfig(this).isAuto_logout())
            LogoutTask.getInstance(getApplicationContext()).startTimer();
    }

    private void NavigateToNextScreen(User user) {
        watermarkHandeling();
    }

    private void NavigateToNextActivity() {
        Intent intent = AppUtils.getHomeNavigationIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void watermarkHandeling(){
        String url = AppUtils.getConfigUrlForWatermark();

        if(AppUtils.isFlavorAllowed())
            initialiseWatermarkCalls(OTPPasswordSetActivity.this, url);
        else
            onAllApiCallSuccess("");
    }

    @Override
    public void onAllApiCallSuccess(String configUrl){
        NavigateToNextActivity();
    }

    private void storeAndMoveToMainList(final User responseObject) {
        startLogoutTimer();
        user = responseObject;
        SharedPrefUtil.putString(ApplicationConstants.PREF_AUTH_TOKEN, user.getAuthToken());
        SharedPrefUtil.putString(ApplicationConstants.PREF_REFRESH_TOKEN, user.getRefreshToken());
        SharedPrefUtil.putLong(ApplicationConstants.PREF_AUTH_EXPIRY,AppUtils.getAuthExpiry(user.getTokenExpiry(),this));
        SharedPrefUtil.putLong(ApplicationConstants.PREF_USER_ID, user.getId());
        SharedPrefUtil.putString(ApplicationConstants.PREF_USER_NAME, user.getUserName());
        SharedPrefUtil.putString(ApplicationConstants.PREF_USER_EMAIL, user.getEmpEmail());
        SharedPrefUtil.putString(ApplicationConstants.PREF_NAME, user.getName());
        SharedPrefUtil.putInt(ApplicationConstants.PREF_USER_EMP_TYPE_ID, user.getEmployeeTypeId());

        MainApplication mainApplication = (MainApplication) getApplication();
        mainApplication.clearOrder();
        if (user != null)
            getUserDetailsFromServer();
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
                                SharedPrefUtil.putInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, responseObject.getUser().getDeskOrderingEnabled());

                                AppUtils.logUser(responseObject.user.getUserName());
                                NavigateToNextScreen(responseObject.user);
                                logLoginEvent();
                            } else {
                                SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
                                errorInLogin(500, "Unable to fetch your cafeteria");
                            }
                        }
                        postDeviceData();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        errorInLogin(errorCode, "Unable to get your details from server");
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


    private void logLoginEvent() {
        if (user != null) {
            AppEvent appEvent = new AppEvent();
            HbEvent hbEvent = new HbEvent();
            hbEvent.updateTime(Calendar.getInstance().getTimeInMillis());
            appEvent.setVendorEventRegistrable(hbEvent).setEventName(EventUtil.LOGIN).setLocationId(user.getLocationId());
            EventUtil.updateCafeEventData(getApplicationContext(), appEvent, hbEvent);
            VendorEventStoreTask.addEventToQueue(this, appEvent);
        }
    }

    private void errorInLogin(int errorCode, String error) {
        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
            LogoutTask.updateTime();
            hideProgress();
            showLoginError(error);
        } else {
            LogoutTask.updateTime();
            hideProgress();
            showLoginError(error);
            tetPassword.setText("");
            tetPasswordVerify.setText("");
        }
    }
}
