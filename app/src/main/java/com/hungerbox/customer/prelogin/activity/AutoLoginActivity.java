package com.hungerbox.customer.prelogin.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.HbEvent;
import com.hungerbox.customer.model.RegistrationUser;
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
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.tasks.VendorEventStoreTask;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.FatLiDialog;

import java.util.Calendar;
import java.util.HashMap;


public class AutoLoginActivity extends ParentActivity {

    TextView tvLoginText;
    boolean resumed = false;
    User user;
    RegistrationUser registrationUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_login);
        tvLoginText = findViewById(R.id.tv_login_status);
        registrationUser = (RegistrationUser) getIntent().getSerializableExtra(ApplicationConstants.USER_REGISTRATION_OBJ);
        performUserLogin(registrationUser.getUserName(), registrationUser.getPassword(), null);
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

    private void showProgress(String text) {
        tvLoginText.setText(text);
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

    private void showLoginError(String message) {
        showErrorDialog(message);
    }

    private void showWrongLoginError() {
        showErrorDialog("Your login id or password is not correct. Please retry again");
    }

    private void showWrongCardError() {
        showErrorDialog("The card is not attached to the user. Please visit the helpdesk at your cafeteria");
    }


    private void performUserLogin(String userName, String password, final String tagId) {
        showProgress("Logging you in");
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
//                        hideProgress();
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
        userSimpleHttpAgent.post(new User().setPassword(password).setUserName(userName).setTagId(tagId).setCompanyId(registrationUser.getCompanyId()), jsonSerializerHashMap);

    }


    private void startLogoutTimer() {
        if (AppUtils.getConfig(this).isAuto_logout())
            LogoutTask.getInstance(getApplicationContext()).startTimer();
    }

    private void storeAndMoveToMainList(final User responseObject) {
        showProgress("Setting up your tokens");
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
//            tetPassword.setText("");
//            tetPasswordVerify.setText("");
        }
    }

    private void hideProgress() {
        finish();
    }

    private void getUserDetailsFromServer() {

        showProgress("Loading your details");

        UserReposne.getUserFromServer(this,
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
                                    SharedPrefUtil.putFloat(ApplicationConstants.LOCATION_CAPACITY, responseObject.user.getLocationCapacity());

                                }
                                SharedPrefUtil.putLong(ApplicationConstants.PREF_USER_ID, responseObject.user.getId());
                                SharedPrefUtil.putString(ApplicationConstants.PREF_USER_NAME, responseObject.user.getUserName());
                                SharedPrefUtil.putString(ApplicationConstants.PREF_USER_EMAIL, responseObject.user.getEmpEmail());
                                SharedPrefUtil.putString(ApplicationConstants.PREF_NAME, responseObject.user.getName());
                                SharedPrefUtil.putInt(ApplicationConstants.PREF_USER_EMP_TYPE_ID, responseObject.user.getEmployeeTypeId());
                                SharedPrefUtil.putInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, responseObject.getUser().getDeskOrderingEnabled());
//
                                AppUtils.logUser(responseObject.user.getUserName());
                                NavigateToNextScreen(responseObject.user);
                                logLoginEvent();
                            } else {
                                SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
                                errorInLogin(500, "Unable to fetch your cafeteria");
                            }
                        }
                        AppUtils.postDeviceData(getApplicationContext());
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        errorInLogin(errorCode, "Unable to get your details from server");
                    }
                });
    }

    private void NavigateToNextScreen(User user) {
        watermarkHandeling();
    }

    private void navigateToNextActivity() {
        MainApplication.clearCart();//clear cart on login
        Calendar calendar = Calendar.getInstance();
        String launchDate = AppUtils.getConfig(AutoLoginActivity.this).getLaunch_date();
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

    private void watermarkHandeling(){
        String url = AppUtils.getConfigUrlForWatermark();

        if(AppUtils.isFlavorAllowed())
            initialiseWatermarkCalls(AutoLoginActivity.this, url);
        else
            onAllApiCallSuccess("");
    }

    @Override
    public void onAllApiCallSuccess(String configUrl){
        navigateToNextActivity();
    }
}
