package com.hungerbox.customer.order.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.OTPUser;
import com.hungerbox.customer.model.RegistrationUser;
import com.hungerbox.customer.model.WalletOtpVerification;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.AutoLoginActivity;
import com.hungerbox.customer.prelogin.activity.OtpVerificationActivity;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.receiver.SmsListener;
import com.hungerbox.customer.util.AppSignatureHelper;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.SmsRetrieverReceiver;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hungerbox.customer.prelogin.activity.OtpVerificationActivity.SMS_CONSENT_REQUEST;

public class SimplOtpVerificationActivity extends ParentActivity {

    OTPUser otpUser;
    private TextView tvOtpTitle,tvSimplAmount,tvSimplError,tvLinkedMobile;
    private EditText etOtp;
    private Button btnSubmitSimplOtp,btSendOtp;
    private ProgressBar pbOtp;
    private String receivedOtp;
    private boolean isRegistrationContinued = false;
    private RegistrationUser registrationUser;
    private WalletOtpVerification verificationCode;
    private Button btnResendOtp;
    private TextView btnSkipSimpl;
    private LinearLayout llSimpleOtp,llTextContainer,llSimplAmount;
    private Runnable runnable;
    private Handler handler;
    private SmsRetrieverReceiver smsRetreiverReceiver;
    private ImageView ivBack;
    private Config.RegistrationWallet registrationWallet;
    private TextView title, desc, desc1, desc2, desc3;
    private ImageView logo, logo1, logo2, logo3;
    IntentFilter intentFilter;
    private String otpRegex;

    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get consent intent
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            // Handle the exception ...
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Time out occurred, handle the error.
                        break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpl_otp_verification);
        Intent recievedOtpUserIntent = getIntent();
        startSmsListener();
        otpUser = (OTPUser) recievedOtpUserIntent.getSerializableExtra(ApplicationConstants.OTP_USER);
        isRegistrationContinued = recievedOtpUserIntent.getBooleanExtra(ApplicationConstants.USER_REGISTRATION_CONT, false);
        registrationWallet = (Config.RegistrationWallet) recievedOtpUserIntent.getSerializableExtra(ApplicationConstants.USER_REGISTRATION_WALLET);
        registrationUser = (RegistrationUser) recievedOtpUserIntent.getSerializableExtra(ApplicationConstants.USER_REGISTRATION_OBJ);
        verificationCode = (WalletOtpVerification) recievedOtpUserIntent.getSerializableExtra("verification_code");
        ivBack = findViewById(R.id.iv_back);
        llTextContainer = findViewById(R.id.ll_text_container);
        tvSimplAmount = findViewById(R.id.tv_simpl_amount);
        tvSimplError = findViewById(R.id.tv_simpl_error);
        llSimplAmount = findViewById(R.id.ll_simpl_amount);
        tvLinkedMobile = findViewById(R.id.tv_linked_mobile);
        btSendOtp = findViewById(R.id.bt_send_otp);

        initUI();
        if (otpUser != null)
            tvOtpTitle.setText(tvOtpTitle.getText().toString() + " " + otpUser.getMobileNumber());
        else if (isRegistrationContinued)
            tvOtpTitle.setText(tvOtpTitle.getText().toString() + " " + registrationUser.getMobileNum());
        else
            tvOtpTitle.setText("Verify your phone number :" + verificationCode.getPhoneNumber());
        clickListeners();
    }

    private void clickListeners() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResendClick();
            }
        });

        btnSubmitSimplOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etOtp.getText().toString().length() > 3 && etOtp.getText().toString().length() < 7) {
                    pbOtp.setVisibility(View.GONE);
                    validateSimplOtp();
                    btnSubmitSimplOtp.setEnabled(false);
                } else {
                    AppUtils.showToast("Please enter a valid OTP", true, 0);
                }
            }

        });

        btnSkipSimpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRegistrationOtp();

            }
        });

        btSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateSimplRegistration(registrationUser);
            }
        });
    }

    private void onResendClick(){
        if (smsVerificationReceiver!=null && intentFilter!=null) {
            stopSmsListener();
            startSmsListener();
        }
        btnResendOtp.setVisibility(View.GONE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                btnResendOtp.setVisibility(View.VISIBLE);
                initiateSimplRegistration(registrationUser);
            }
        };
        new Handler().postDelayed(runnable,800);
    }

    private void validateSimplOtp() {
        registrationUser.setOtp(etOtp.getText().toString().trim());
        registrationUser.setVerificationId(verificationCode.getVerificationId());

        String url = UrlConstant.WALLET_VALIDATE_NEW+"?walletCode="+registrationWallet.getWallet_code();
        SimpleHttpAgent<Object> simpleHttpAgent = new SimpleHttpAgent<Object>(
                this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        pbOtp.setVisibility(View.GONE);
                        btnSubmitSimplOtp.setEnabled(true);
                        proceedWithLogin();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbOtp.setVisibility(View.GONE);
                        btnSubmitSimplOtp.setEnabled(true);

                        if (errorResponse != null) {
                            GenericPopUpFragment errorPopUp = GenericPopUpFragment
                                    .newInstance(errorResponse.message, "Resend", new GenericPopUpFragment
                                            .OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {
                                            initiateSimplRegistration(registrationUser);
                                        }

                                        @Override
                                        public void onNegativeInteraction() {

                                        }
                                    });
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(errorPopUp, "logout")
                                    .commitAllowingStateLoss();
                        }
                    }
                },
                Object.class
        );

        pbOtp.setVisibility(View.VISIBLE);

        simpleHttpAgent.post(registrationUser, new HashMap<String, JsonSerializer>());
    }

    private void getRegistrationOtp() {
        stopSmsListener();
        Intent intent = new Intent(SimplOtpVerificationActivity.this, OtpVerificationActivity.class);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_CONT, true);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_OBJ, registrationUser);
        intent.putExtra("name",OtpVerificationActivity.SIMPL_ACTIVITY);
        startActivity(intent);
    }

    private void startSmsListener(){
        try {
            intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
            registerReceiver(smsVerificationReceiver, intentFilter);
            SmsRetriever.getClient(this).startSmsUserConsent(null);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initiateSimplRegistration(final RegistrationUser registrationUser) {

        String payload = SharedPrefUtil.getString(ApplicationConstants.PAYLOAD, null);

        if(payload != null && !AppUtils.getConfig(this).isStop_simple_payload() && registrationWallet.getWallet_code().equalsIgnoreCase("simpl")){
            registrationUser.setSimplPayload(payload);
        }

        String url = UrlConstant.WALLET_OTP_SIGN_UP+"?walletCode="+registrationWallet.getWallet_code();


        SimpleHttpAgent<WalletOtpVerification> userWalletsSimpleHttpAgent = new SimpleHttpAgent<>(
                SimplOtpVerificationActivity.this,
                url,
                new ResponseListener<WalletOtpVerification>() {
                    @Override
                    public void response(WalletOtpVerification responseObject) {
                        pbOtp.setVisibility(View.GONE);
                        btSendOtp.setVisibility(View.GONE);
                        llSimpleOtp.setVisibility(View.VISIBLE);
                        verificationCode = responseObject;
                        otpRegex = responseObject.getOtpRegex();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error,
                                            ErrorResponse errorResponse) {
                        pbOtp.setVisibility(View.GONE);
                        btnSkipSimpl.performClick();
                        if (errorResponse != null && errorResponse.getMessage() != null) {
                            AppUtils.showToast(errorResponse.getMessage(), true, 0);
                        } else {
                            AppUtils.showToast("Something went wrong.", true, 0);
                        }
                    }
                },
                WalletOtpVerification.class
        );

        etOtp.setText("");
        pbOtp.setVisibility(View.VISIBLE);

        userWalletsSimpleHttpAgent.post(registrationUser, new HashMap<String, JsonSerializer>());
    }

    private void proceedWithLogin() {
        Intent intent = new Intent(this, AutoLoginActivity.class);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_OBJ, registrationUser);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        try {
            handler.removeCallbacks(runnable);
        }catch (Exception e){}
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initUI() {
        title = findViewById(R.id.tv_otp_title);
        btnSubmitSimplOtp = findViewById(R.id.btn_submit_simpl);
        logo = findViewById(R.id.logo);
        desc = findViewById(R.id.desc);
        logo1 = findViewById(R.id.logo1);
        desc1 = findViewById(R.id.desc1);
        logo2 = findViewById(R.id.logo2);
        desc2 = findViewById(R.id.desc2);
        logo3 = findViewById(R.id.logo3);
        desc3 = findViewById(R.id.desc3);

        title.setText(registrationWallet.getWallet_name());

        if(registrationWallet.getDesc() == null || registrationWallet.getDesc().equals("")){
            desc.setVisibility(View.GONE);
        }else{
            desc.setText(registrationWallet.getDesc());
        }

        if(registrationWallet.getDesc1() == null || registrationWallet.getDesc1().equals("")){
            findViewById(R.id.desc1_container).setVisibility(View.GONE);
        }else{
            desc1.setText(registrationWallet.getDesc1());
        }

        if(registrationWallet.getDesc2() == null || registrationWallet.getDesc2().equals("")){
            findViewById(R.id.desc2_container).setVisibility(View.GONE);
        }else{
            desc2.setText(registrationWallet.getDesc2());
        }

        if(registrationWallet.getDesc3() == null || registrationWallet.getDesc3().equals("")){
            findViewById(R.id.desc3_container).setVisibility(View.GONE);
        }else{
            desc3.setText(registrationWallet.getDesc3());
        }

        ImageHandling.loadRemoteImage(registrationWallet.getLogo(), logo,-1,-1, this);

        ImageHandling.loadRemoteImage(registrationWallet.getLogo1(), logo1,-1,-1, this);

        ImageHandling.loadRemoteImage(registrationWallet.getLogo2(), logo2,-1,-1, this);

        ImageHandling.loadRemoteImage(registrationWallet.getLogo3(), logo3,-1,-1, this);

        btnSubmitSimplOtp.setText("Activate "+registrationWallet.getWallet_name());

        tvOtpTitle = findViewById(R.id.tv_otp_verification);
        etOtp = findViewById(R.id.et_otp_verification);
        pbOtp = findViewById(R.id.pb_otp_verification);
        btnResendOtp = findViewById(R.id.btn_resend_simpl);
        btnResendOtp.setVisibility(View.VISIBLE);
        btnSkipSimpl = findViewById(R.id.btn_skip);
        llSimpleOtp = findViewById(R.id.ll_simpl_otp);


        llTextContainer.setVisibility(View.VISIBLE);
        runnable = new Runnable() {
            @Override
            public void run() {
                btnResendOtp.setEnabled(true);
            }
        };

        handler = new Handler();
        handler.postDelayed(runnable, 10000);
    }

    public void messageReceived(String messageText) {
        try {
            if (messageText != null && !messageText.equals("")) {
                Pattern p;
                if (otpRegex != null && !otpRegex.equals("")) {
                    p = Pattern.compile(otpRegex);
                } else {
                    if (messageText.toLowerCase().contains("simpl") || messageText.toLowerCase().contains("lazypay")) {
                        p = Pattern.compile("(\\d{4})");
                    } else {
                        p = Pattern.compile("(\\d{6})");
                    }
                }
                Matcher m = p.matcher(messageText);
                if (m.find()) {

                    receivedOtp = m.group(0);
                    etOtp.setText(receivedOtp);
                    etOtp.setSelection(receivedOtp.length());
                }
            }
        } catch (Exception e){
            Log.d("sms consent", "error in sms auto read");
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // ...
            case SMS_CONSENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    // Get SMS message content
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                    messageReceived(message);
                } else {
                    // Consent canceled, handle the error ...
                }
                break;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        stopSmsListener();
    }

    @Override
    public void finish() {
        super.finish();
        stopSmsListener();
    }

    private void stopSmsListener(){
        try {
            unregisterReceiver(smsRetreiverReceiver);
            SmsRetrieverReceiver.stopListener();
        }catch (Exception exp){
        }
    }



}
