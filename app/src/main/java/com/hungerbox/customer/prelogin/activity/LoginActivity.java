package com.hungerbox.customer.prelogin.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonSerializer;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FCMDevice;
import com.hungerbox.customer.model.HbEvent;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserCardCheck;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.model.serializer.UserSerializer;
import com.hungerbox.customer.navmenu.fragment.PasswordChangeDialog;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.fragment.EmployeeIdPopUpFragment;
import com.hungerbox.customer.prelogin.fragment.ForgotPasswordActivity;
import com.hungerbox.customer.prelogin.fragment.ResetPasswordLogoutDialog;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CardUtil;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.tasks.VendorEventStoreTask;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.FatLiDialog;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LoginActivity extends ParentActivity {

    private static final int WIDTH = 200;
    protected NfcAdapter mNfcAdapter;
    ImageView ivBack;
    EditText etUserName, etPassword;
    TextInputLayout tilUsername, tilPassword;
    Button btLogin, btOtpLogin;
    TextView btForgotPassword, tbTitle, tbHeader;
    CoordinatorLayout clMainLayout;
    LinearLayout llUserAppContainer;
    String[] loginMethods;
    Realm realm;
    long companyId;
    boolean resumed = false;
    User user;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private boolean isNFCLogin = false;
    private boolean isLocationGiven = false;
    RelativeLayout rlProgress;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String loginHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        AppUtils.setupUI(findViewById(R.id.cl_login), LoginActivity.this);
        ivBack = findViewById(R.id.iv_back);
        tbTitle = findViewById(R.id.tb_title);
        tbHeader = findViewById(R.id.tb_header);
        clMainLayout = findViewById(R.id.cl_login);
        etUserName = findViewById(R.id.et_user_name);
        etPassword = findViewById(R.id.et_password);
        tilUsername = findViewById(R.id.til_user_name);
        tilPassword = findViewById(R.id.til_password);
        btLogin = findViewById(R.id.bt_login);
        btOtpLogin = findViewById(R.id.bt_otp_login);
        btForgotPassword = findViewById(R.id.bt_reset_password);
        llUserAppContainer = findViewById(R.id.ll_user_app_container);
        rlProgress = findViewById(R.id.rl_progress);
        etUserName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if(BuildConfig.FLAVOR.equals("cafe")){
            etUserName.setSaveEnabled(false);
            etPassword.setSaveEnabled(false);
        }
        tbTitle.setText("Hello!");
        tbHeader.setText("Glad to see you again..");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etUserName.setText("");
        etPassword.setText("");

        loginMethods = AppUtils.getConfig(this).getLogin_methods();

//        if (AppUtils.getConfig(this) == null || AppUtils.getConfig(this).getCompany_id() == -1) {
//
//            AppUtils.HbLog("getHomeNavigationIntent","LoginActivity - GetHomeNavigation");
//
//            Intent intent = AppUtils.getHomeNavigationIntent(this);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//            return;
//        }
   /*     if (AppUtils.getConfig(this).getLogin_auth().getSignup_enabled() && !AppUtils.isCafeApp()) {
            btSignUp.setVisibility(View.VISIBLE);
        } else {
            btSignUp.setVisibility(View.GONE);
        }*/

        loginHint = "";

        for (int i = 0; i < loginMethods.length; ++i) {
            loginHint += loginMethods[i];
            if (i + 1 != loginMethods.length)
                loginHint += "/";
        }

        tilUsername.setHint(loginHint);

        if(!AppUtils.isCafeApp()){
            etUserName.setText(SharedPrefUtil.getString(ApplicationConstants.PREF_USER_NAME, ""));
        }

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndPerformLogin();
            }
        });

        btForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordPopup();
                try {
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getCompanyId(),AppUtils.getConfig(getApplicationContext()).getCompany_id());
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getForgot_password_click(), map, LoginActivity.this);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        });

        btOtpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToOtpActivity();
            }
        });
     /*   btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSignUpActivity();
            }
        });*/

        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());


        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                ViewCompat.setBackgroundTintList(etUserName, colorStateList);
                if (s != null && s.length() > 1) {
                    tilUsername.setError("");
                }
                String newText = s.toString();
                if (s.toString().contains("\n")) {
                    newText = newText.replace("\n", "");
                    etUserName.setText(newText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    tilPassword.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {
                if (actionid == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) && (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etPassword.getApplicationWindowToken(), 0);
                    validateAndPerformLogin();
                    return true;
                }
                return false;
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                    tilPassword.setPasswordVisibilityToggleTintList(colorStateList);
                } else {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black_40));
                    tilPassword.setPasswordVisibilityToggleTintList(colorStateList);
                }
            }
        });

        etUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {
                if (actionid == EditorInfo.IME_ACTION_GO || keyEvent == null || keyEvent.getAction() == KeyEvent.ACTION_UP
                        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    etPassword.requestFocus(View.FOCUS_DOWN);
                    return true;
                }
                return false;
            }
        });

        companyId = AppUtils.getConfig(this).getCompany_id();


       // etUserName.clearFocus();
       // etPassword.clearFocus();
        //  loadCompanyLogo();
        LogoutTask.getInstance(getApplicationContext()).stopTimer();
        if(AppUtils.isCafeApp())
            setupNfcAdapter();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



    }

    private void navigateToSignUpActivity() {
        Intent intent;
        if (BuildConfig.APPLICATION_ID.contains("cafe")) {
            intent = new Intent(this, SignUpActivityBasic.class);
            long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
            String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "");
            HashMap<String, Long> qrCodeMap = AppUtils.getConfig(this).getRegistration_qr_hashs();
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
            intent = new Intent(LoginActivity.this, SignUpActivityBasic.class);
        } else {
            intent = new Intent(this, QRScannerActivity.class);
        }


        startActivity(intent);
    }

    private void navigateToOtpActivity() {
        Intent intent = new Intent(this, SendOtpActivity.class);
        startActivity(intent);
        finish();
    }

    private void showForgotPasswordPopup() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void setupNfcAdapter() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            AppUtils.HbLog("hb", "nfc disabled");
        } else {
            AppUtils.HbLog("hb", "nfc enabled");
        }

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[]{CardUtil.newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true)});
    }

    private void enableNfcForeground() {
        if (AppUtils.getConfig(this).isCard_login()) {
            if (mNfcAdapter != null) {
//                if (!mNfcAdapter.isEnabled()) {
////                showWirelessSettingsDialog();
//                }
                mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
                mNfcAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
            }
        }
    }

    private void disableNfcForeground() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
            mNfcAdapter.disableForegroundNdefPush(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppUtils.isCafeApp())
            enableNfcForeground();
        crossfade();
        resumed = true;
        btLogin.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(AppUtils.isCafeApp())
            disableNfcForeground();
        resumed = false;
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
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                long tagId = CardUtil.dumpTagData(tag);
                if (tagId > 0) {
                    isNFCLogin = true;
                    performUserLogin("", "", Long.toString(tagId));

                }
//                byte[] payload =.getBytes();
//                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
//                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
//                msgs = new NdefMessage[] { msg };
            }
        }
    }

//    private void startQRScanner() {
//        new IntentIntegrator(this).initiateScan();
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            getTagIdAndLogin(intent);
        }
    }


    private void validateAndPerformLogin() {
        LogoutTask.updateTime();
        if (etUserName.getText() == null || etUserName.getText().toString().isEmpty()) {
            tilUsername.setError("Please enter " + loginHint);
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.vibrate);
            tilUsername.startAnimation(shake);

        }
        else if (etPassword.getText() == null || etPassword.getText().toString().isEmpty()) {
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.vibrate);
            tilPassword.startAnimation(shake);
            tilPassword.setError("Please enter password");
        } else {
            etPassword.clearFocus();
            etUserName.clearFocus();
            performUserLogin(etUserName.getText().toString(), etPassword.getText().toString(), null);
        }
    }

    private void showProgress() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        rlProgress.setVisibility(View.GONE);
    }

    private void performUserLogin(String userName, String password, final String tagId) {
        btLogin.setEnabled(false);
        showProgress();
        String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, null);

        AppUtils.hideKeyboard(LoginActivity.this,null);
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
//                        Snackbar.make(clMainLayout, error, Snackbar.LENGTH_LONG).show();
                        if (tagId != null)
                            showWrongCardError();
                        else
                            showWrongLoginError(error);

                        btLogin.setEnabled(true);
//                        storeAndMoveToMainList(new User().setId(1).setUserName("peeyush").setAuthToken("test_auth"));
                    }
                },
                User.class
        );

        HashMap<String, JsonSerializer> jsonSerializerHashMap = new HashMap<>();
        jsonSerializerHashMap.put("io.realm.UserRealmProxy", new UserSerializer());
        userSimpleHttpAgent.post(new User().setPassword(password).setUserName(userName).setTagId(tagId).setCompanyId(companyId), new HashMap<String, JsonSerializer>());

    }

    private void showLoginError(String message,String imageType) {
        showErrorDialog(message,imageType);
    }

    private void showWrongLoginError(String error) {
        showErrorDialog(error,ApplicationConstants.GENERAL_ERROR);
    }

    private void showWrongCardError() {
        showErrorDialog("The card is not attached to the user. Please visit the helpdesk at your cafeteria",ApplicationConstants.GENERAL_ERROR);
    }

    private void showErrorDialog(String errorMessage,String imageType) {
        ErrorPopFragment fatLiDialog = ErrorPopFragment.Companion.newInstance(errorMessage, "Back", true, imageType, new ErrorPopFragment.OnFragmentInteractionListener() {
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
                    .commit();
        }
    }

    private void storeAndMoveToMainList(final User responseObject) {
        startLogoutTimer();
        realm.executeTransactionAsync(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              user = responseObject;

                                              SharedPrefUtil.putString(ApplicationConstants.PREF_AUTH_TOKEN, user.getAuthToken());
                                              SharedPrefUtil.putString(ApplicationConstants.PREF_REFRESH_TOKEN, user.getRefreshToken());
                                              SharedPrefUtil.putLong(ApplicationConstants.PREF_AUTH_EXPIRY,AppUtils.getAuthExpiry(user.getTokenExpiry(),LoginActivity.this));
                                              SharedPrefUtil.putLong(ApplicationConstants.PREF_USER_ID, user.getId());
                                              SharedPrefUtil.putString(ApplicationConstants.PREF_USER_NAME, user.getUserName());
                                              SharedPrefUtil.putString(ApplicationConstants.PREF_USER_EMAIL, user.getEmpEmail());
                                              SharedPrefUtil.putString(ApplicationConstants.PREF_NAME, user.getName());
                                              SharedPrefUtil.putInt(ApplicationConstants.PREF_USER_EMP_TYPE_ID, user.getEmployeeTypeId());
                                          }
                                      },
                new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        MainApplication mainApplication = (MainApplication) getApplication();
                        mainApplication.clearOrder();
                        if (user != null)
                            getUserDetailsFromServer();
                    }
                });
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
                            hideProgress();

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

                                AppUtils.setupUser("" + user.getId(), user.getEmail(), user.getName());
                                AppUtils.logUser(responseObject.user.getUserName());
                                EventUtil.logUser(LoginActivity.this, responseObject.user);
                                if (responseObject.user.isCardCheck() &&
                                        isNFCLogin && AppUtils.isCafeApp()
                                        && !responseObject.meta.scopes.containsKey("guest")) {
                                    showEmployeeIdConfirmationDialog(responseObject.user);
                                } else if (responseObject.user.isResetRequired() &&
                                        !AppUtils.getConfig(getApplicationContext()).is_location_fixed())
                                    showResetPasswordAndNavigateToNextScreen(responseObject.user);
                                else {
                                    if (isLocationGiven)
                                        navigateToNextScreen(responseObject.user);
                                    else {
                                        showLocationCheckDialog(responseObject.user);
                                    }
                                }
                                logLoginEvent();
                            } else {
                                SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
                                errorInLogin(500, "Unable to fetch your cafeteria");
                            }
                        }
                        AppUtils.postDeviceData(LoginActivity.this);
                    }
                },
                (errorCode, error, errorResponse) -> errorInLogin(errorCode, "Unable to get your details from server"),
                UserReposne.class
        );
        userSimpleHttpAgent.get();
    }

    private void showLocationCheckDialog(final User user) {
        navigateToNextScreen(user);
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

    private void showEmployeeIdConfirmationDialog(final User user) {
        if (!resumed)
            return;

        String title = "Please enter ";
        boolean containsMobile = false;
        boolean containsEmail = false;
        if (loginMethods.length > 0) {

            for (String loginMethod : loginMethods) {
                if (loginMethod.equalsIgnoreCase(ApplicationConstants.EMAIL_TEXT)) {
                    containsEmail = true;
                } else if (loginMethod.equalsIgnoreCase(ApplicationConstants.MOBILE_NO_TEXT_1)
                        || loginMethod.equalsIgnoreCase(ApplicationConstants.MOBILE_NO_TEXT_2)
                        || loginMethod.equalsIgnoreCase(ApplicationConstants.MOBILE_NO_TEXT_3)) {
                    containsMobile = true;
                }
            }

            if (containsMobile) {
                title += "Mobile number";
            } else if (containsEmail) {
                title += "email id";
            } else {
                title += "employee ID";
            }
        } else {
            title += "employee ID";
        }

        final boolean finalContainsMobile = containsMobile;
        final boolean finalContainsEmail = containsEmail;
        EmployeeIdPopUpFragment empIdFragment = EmployeeIdPopUpFragment.newInstance(title, "Confirm",
                new EmployeeIdPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction(String empId) {
//                        if(finalContainsMobile && user.getPhoneNumber().equalsIgnoreCase(empId)){
//                            disableCardCheckAndNavigate(user);
//                        }
//                        else if(finalContainsEmail && user.getEmail().equalsIgnoreCase(empId)){
//                            disableCardCheckAndNavigate(user);
//                        }
//                        else

                        if (user.getUserName().equalsIgnoreCase(empId)) {
                            disableCardCheckAndNavigate(user);
                        } else {
                            String failedText = getString(R.string.employee_id_did_not_match);
                            if (finalContainsMobile) {
                                failedText = failedText.replace("{1}", "Mobile number");
                            } else if (finalContainsEmail) {
                                failedText = failedText.replace("{1}", "Email id");
                            } else {
                                failedText = failedText.replace("{1}", "Employee ID");
                            }
                            ErrorPopFragment logout = ErrorPopFragment.Companion.newInstance(
                                    failedText, "Try Again", true,ApplicationConstants.GENERAL_ERROR,
                                    new ErrorPopFragment.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {
                                            showEmployeeIdConfirmationDialog(user);

                                        }

                                        @Override
                                        public void onNegativeInteraction() {
                                            doLogout();
                                        }
                                    });
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(logout, "logout")
                                    .commit();
                        }

                    }
                });
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(empIdFragment, "emp_id_confirm")
                .commitAllowingStateLoss();
    }

    private void disableCardCheckAndNavigate(User user) {
        long userId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
        String url = UrlConstant.CHANGE_EMPLOYEE_DETAILS + userId;
        SimpleHttpAgent<Object> cardCheckUpdateHttpAgent = new SimpleHttpAgent<>(
                LoginActivity.this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        AppUtils.HbLog("card_Check", "success");
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        AppUtils.HbLog("card_Check", "fail");
                    }
                },
                Object.class
        );

        cardCheckUpdateHttpAgent.put(new UserCardCheck().setCardCheck(0), new HashMap<String, JsonSerializer>());
        checkLocationAndNavigateToNextScreen(user);
    }

    private void checkLocationAndNavigateToNextScreen(User user) {
        if (isLocationGiven)
            navigateToNextScreen(user);
        else
            showLocationCheckDialog(user);
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
        AppUtils.doLogoutServer(LoginActivity.this.getApplicationContext());

        SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
        SharedPrefUtil.remove(ApplicationConstants.PREF_USER_ID);

        Intent intent = new Intent(LoginActivity.this, HBWelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void errorInLogin(int errorCode, String error) {
        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
            LogoutTask.updateTime();
            Snackbar.make(clMainLayout, error, Snackbar.LENGTH_LONG).show();
            btLogin.setEnabled(true);
            hideProgress();
            showLoginError(error,ApplicationConstants.NO_INTERNET_IMAGE);
        } else {
            LogoutTask.updateTime();
            Snackbar.make(clMainLayout, error, Snackbar.LENGTH_LONG).show();
            btLogin.setEnabled(true);
            hideProgress();
            showLoginError(error,ApplicationConstants.GENERAL_ERROR);
            etPassword.setText("");
            etUserName.setText("");
        }
    }

    private void startLogoutTimer() {
        if (AppUtils.getConfig(this).isAuto_logout())
            LogoutTask.getInstance(getApplicationContext()).startTimer();
    }

    private void navigateToNextScreen(User user) {
        showProgress();
        watermarkHandeling();
    }

    private void navigateToNextActivity() {
        MainApplication.clearCart();
        Calendar calendar = Calendar.getInstance();
        String launchDate = AppUtils.getConfig(LoginActivity.this).getLaunch_date();
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
            initialiseWatermarkCalls(LoginActivity.this, url);
        else
            onAllApiCallSuccess("");
    }

    @Override
    public void onAllApiCallSuccess(String configUrl){
        hideProgress();
        navigateToNextActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                AppUtils.showToast("Cancelled", false, 0);
            } else {
                AppUtils.showToast("Scanned: " + result.getContents(), false, 1);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }


    private void crossfade() {

//        if (!AppUtils.isCafeApp()) {
//        TODO is cafe app
        if (true) {
            return;
        }
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        llUserAppContainer.setAlpha(1f);
        llUserAppContainer.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.

        TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.3f);
        mAnimation.setDuration(10000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        llUserAppContainer.setAnimation(mAnimation);

//        llUserAppContainer.animate()
//                .translationYBy(10)
//                .setStartDelay(500)
//                .setDuration(500)
//                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
//        mLoadingView.animate()
//                .alpha(0f)
//                .setDuration(mShortAnimationDuration)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mLoadingView.setVisibility(View.GONE);
//                    }
//                });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

}
