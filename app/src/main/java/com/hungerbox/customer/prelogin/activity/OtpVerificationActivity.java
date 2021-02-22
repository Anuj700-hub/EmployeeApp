package com.hungerbox.customer.prelogin.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.material.snackbar.Snackbar;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.OTPUser;
import com.hungerbox.customer.model.RegistrationUser;
import com.hungerbox.customer.model.ResendOtpResponse;
import com.hungerbox.customer.model.WalletOtpVerification;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.receiver.SmsListener;
import com.hungerbox.customer.util.AppSignatureHelper;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SmsRetrieverReceiver;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpVerificationActivity extends ParentActivity {

    public static final int SMS_CONSENT_REQUEST = 2;
    OTPUser otpUser;
    private TextView tvOtpTitle;
    private Button btnSubmitOtp;
    private ProgressBar pbOtp;
    private String receivedOtp;
    private boolean isRegistrationContinued = false;
    private RegistrationUser registrationUser;
    private boolean forResult;
    private WalletOtpVerification verificationCode;
    private LinearLayout llSimplHelpText;
    private LinearLayout enterCodeBox;
    private String walletCode = "";
    private String walletName = "";
    private LinearLayout lazypayHelperText;
    private CardView helperTextContainer;
    private TextView tvLazypayTnc, header, subHeader, content,tbtitle,tvResend;
    private String otpRegex;
    private SmsRetrieverReceiver smsRetreiverReceiver;
    private EditText etOtp1,etOtp2,etOtp3,etOtp4,etOtp5,etOtp6;
    int otpLength;
    IntentFilter intentFilter;
    ImageView ivBack;
    String employeeId, companyId;
    public static final int SIMPL_ACTIVITY = 400, PAYMENT_ACTIVITY = 401, SIGN_UP_ACTIVITY = 402, SEND_OTP_ACTIVITY = 403;
    private final String TAG = "sms consent";
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

        setContentView(R.layout.activity_otp_verification);
        AppUtils.setupUI(findViewById(R.id.parentOtpVerificationActivity), OtpVerificationActivity.this);
        intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter);
        SmsRetriever.getClient(this).startSmsUserConsent(null);
        Intent recievedOtpUserIntent = getIntent();
        otpUser = (OTPUser) recievedOtpUserIntent.getSerializableExtra(ApplicationConstants.OTP_USER);
        isRegistrationContinued = recievedOtpUserIntent.getBooleanExtra(ApplicationConstants.USER_REGISTRATION_CONT, false);
        //for resendOtp()
        registrationUser = (RegistrationUser) recievedOtpUserIntent.getSerializableExtra(ApplicationConstants.USER_REGISTRATION_OBJ);
        forResult = recievedOtpUserIntent.getBooleanExtra("forResult", false);
        verificationCode = (WalletOtpVerification) recievedOtpUserIntent.getSerializableExtra("verification_code");
        walletCode = recievedOtpUserIntent.getStringExtra("wallet");
        walletName = recievedOtpUserIntent.getStringExtra("wallet_name");
        otpRegex = recievedOtpUserIntent.getStringExtra("otp_regex");
        employeeId = recievedOtpUserIntent.getStringExtra("employee_id");
        companyId = recievedOtpUserIntent.getStringExtra("company_id");
        initUI();
        setTvOtpTitle();
        clickListeners();
        handleOTPMethodCalling(recievedOtpUserIntent);


    }

    private void setTvOtpTitle(){
        if (otpUser != null)
            tvOtpTitle.setText(tvOtpTitle.getText().toString() + " " + otpUser.getMobileNumber());
        else if (isRegistrationContinued)
            tvOtpTitle.setText(tvOtpTitle.getText().toString() + " " + registrationUser.getMobileNum());
        else if (verificationCode == null){
            tvOtpTitle.setText("Verify your phone number to link ");
        }
        else
            tvOtpTitle.setText("Verify your phone number :" + verificationCode.getPhoneNumber() + "\n to link " + walletName);
    }

    private void handleOTPMethodCalling(Intent receivedIntent){
        int callingActivity = receivedIntent.getIntExtra("name", 0);
        if (callingActivity!=0){
          switch (callingActivity){
              case SIMPL_ACTIVITY :
              case SIGN_UP_ACTIVITY :{
                  resendOtp(false);
              }
              break;
              case PAYMENT_ACTIVITY : {
                 linkUserPayment();
              }
              break;
              case SEND_OTP_ACTIVITY : {
                  sendOtp();
              } break;
          }
        }
    }


    public void messageReceived(String messageText) {
        try {

            if (messageText == null) {
                return;
            }
            if (forResult) {
                Log.d(TAG, "messageReceived: case forResult");
                if (messageText.contains("simpl")) {
                    Log.d(TAG, "messageReceived: contains simpl");
                    Pattern p;
                    if (otpRegex != null && !otpRegex.equals("")) {
                        p = Pattern.compile(otpRegex);
                    } else {
                        p = Pattern.compile("(\\d{4})");
                        etOtp5.setVisibility(View.GONE);
                        etOtp6.setVisibility(View.GONE);
                    }
                    Matcher m = p.matcher(messageText);
                    if (m.find()) {
                        receivedOtp = m.group(0);
                        if (receivedOtp.length() == 4) {
                            etOtp6.setVisibility(View.GONE);
                            etOtp5.setVisibility(View.GONE);
                        } else if (receivedOtp.length() == 5) {
                            etOtp6.setVisibility(View.GONE);
                        }
//                            etOtp.setText(receivedOtp);
                        Log.d(TAG, "messageReceived: OTP extracted " + receivedOtp);
                        setOtp(receivedOtp);
                        btnSubmitOtp.performClick();
                    }
                } else {
                    Pattern p;
                    if (otpRegex != null && !otpRegex.equals("")) {
                        p = Pattern.compile(otpRegex);
                    } else {
                        p = Pattern.compile("(\\d{4,6})");
                    }
                    Matcher m = p.matcher(messageText);
                    if (m.find()) {
                        receivedOtp = m.group(0);
                        adjustOtpLength(receivedOtp.length());
                        Log.d(TAG, "messageReceived: OTP extracted " + receivedOtp);
                        setOtp(receivedOtp);
                        btnSubmitOtp.performClick();
                        SmsRetrieverReceiver.stopListener();
                    }
                }
            } else {
                if (messageText.toLowerCase().contains("otp")) {
                    Pattern p;
                    if (otpRegex != null && !otpRegex.equals("")) {
                        p = Pattern.compile(otpRegex);
                    } else {
//                        if (messageText.toLowerCase().contains("lazypay") || messageText.toLowerCase().contains("simpl")){
//                            p = Pattern.compile("(\\d{4})");
//                        }else {
//                            p = Pattern.compile("(\\d{6})");
//                        }
                        p = Pattern.compile("(\\d{4,6})");
                    }
                    Matcher m = p.matcher(messageText);
                    if (m.find()) {
                        receivedOtp = m.group(0);
                        if (receivedOtp.length() == 4) {
                            etOtp6.setVisibility(View.GONE);
                            etOtp5.setVisibility(View.GONE);
                        } else if (receivedOtp.length() == 5) {
                            etOtp6.setVisibility(View.GONE);
                        }
                        //etOtp.setText(receivedOtp);
                        Log.d(TAG, "messageReceived: OTP extracted " + receivedOtp);
                        setOtp(receivedOtp);
                        btnSubmitOtp.performClick();
                        SmsRetrieverReceiver.stopListener();
                    }
                }
            }
        } catch (Exception e){
            Log.d("sms consent", "error in sms auto read");
            e.printStackTrace();
        }
    }
    private void submitOtpToServer() {
        SimpleHttpAgent<Object> validateOtp = new SimpleHttpAgent<Object>(OtpVerificationActivity.this,
                UrlConstant.VALIDATE_OTP, new ResponseListener<Object>() {
            @Override
            public void response(Object responseObject) {
                pbOtp.setVisibility(View.GONE);
                btnSubmitOtp.setEnabled(true);
                Intent goToPasswordReset = new Intent(OtpVerificationActivity.this, OTPPasswordSetActivity.class);
                goToPasswordReset.putExtra(ApplicationConstants.OTP_USER, otpUser);
                startActivity(goToPasswordReset);
                finish();
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                pbOtp.setVisibility(View.GONE);
                btnSubmitOtp.setEnabled(true);

                ErrorPopFragment errorPopUp = ErrorPopFragment.Companion
                        .newInstance(error, "Back",true,ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment
                                .OnFragmentInteractionListener() {
                            @Override
                            public void onPositiveInteraction() {

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
        }, Object.class);

        if (getOtp().length() == 6) {
            otpUser.setOtp(getOtp());
            pbOtp.setVisibility(View.VISIBLE);
            otpUser.setCompanyId(AppUtils.getConfig(OtpVerificationActivity.this).getCompany_id());
            validateOtp.post(otpUser, new HashMap<String, JsonSerializer>());
        } else {
            AppUtils.showToast("Please enter a valid OTP", true, 0);

            btnSubmitOtp.setEnabled(true);
        }
    }

    private void clickListeners() {

   /*     tvTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;

                if (event.getRawX() <= (tvTitle.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                    onBackPressed();
                    return true;
                }

                return false;
            }
        });*/

        btnSubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp =getOtp();
                Log.d("otp",otp);
                if (otp.length() > 3 && otp.length() < 7) {
                    btnSubmitOtp.setEnabled(false);
                    pbOtp.setVisibility(View.GONE);
                    if (isRegistrationContinued) {
                        continueWithregistration();
                    } else if (forResult) {
                        verifyOtp();
                    } else {
                        submitOtpToServer();
                    }
                } else {
                    AppUtils.showToast("Please enter a valid OTP", true, 0);
                }
            }


        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResendClick();

            }
        });

       etOtp1.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if (charSequence.length()==1){
                   etOtp2.requestFocus(View.FOCUS_RIGHT);
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       etOtp2.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if (charSequence.length()==1){
                   etOtp3.requestFocus(View.FOCUS_RIGHT);
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       etOtp3.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if (charSequence.length()==1){
                   etOtp4.requestFocus(View.FOCUS_RIGHT);
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       etOtp4.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if (charSequence.length()==1){
                   etOtp5.requestFocus(View.FOCUS_RIGHT);
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       etOtp5.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if (charSequence.length()==1){
                   etOtp6.requestFocus(View.FOCUS_RIGHT);
               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });

        etOtp2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_DEL && etOtp2.getText().toString().length()==0 && keyEvent.getAction()==KeyEvent.ACTION_UP) {
                    //this is for backspace
                    etOtp1.requestFocus(View.FOCUS_LEFT);
                    return true;
                }
                return false;
            }
        });
        etOtp3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_DEL && etOtp3.getText().toString().length()==0&& keyEvent.getAction()==KeyEvent.ACTION_UP) {
                    //this is for backspace
                    etOtp2.requestFocus(View.FOCUS_LEFT);
                    return true;
                }
                return false;
            }
        });
        etOtp4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_DEL && etOtp4.getText().toString().length()==0&& keyEvent.getAction()==KeyEvent.ACTION_UP) {
                    //this is for backspace
                    etOtp3.requestFocus(View.FOCUS_LEFT);
                    return true;
                }
                return false;
            }
        });
        etOtp5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_DEL && etOtp5.getText().toString().length()==0&& keyEvent.getAction()==KeyEvent.ACTION_UP) {
                    //this is for backspace
                    etOtp4.requestFocus(View.FOCUS_LEFT);
                    return true;
                }
                return false;
            }
        });
       etOtp6.setOnKeyListener(new View.OnKeyListener() {
           @Override
           public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
               if(keyCode == KeyEvent.KEYCODE_DEL && etOtp6.getText().toString().length()==0&& keyEvent.getAction()==KeyEvent.ACTION_UP) {
                   //this is for backspace
                   etOtp5.requestFocus(View.FOCUS_LEFT);
                   return true;
               }
               return false;
           }
       });


    }
    private void onResendClick(){
        if (smsVerificationReceiver!=null && intentFilter!=null) {
            stopSmsListener();
            startSmsListener();
        }
        tvResend.setVisibility(View.GONE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tvResend.setVisibility(View.VISIBLE);
                etOtp1.setSelection(0);
                pbOtp.setVisibility(View.VISIBLE);
                if (isRegistrationContinued) {
                    resendOtp(true);
                } else if (forResult) {
                    linkUserPayment();
                } else {
                    sendOtp();
                }
            }
        };
        new Handler().postDelayed(runnable,800);
    }
    private void verifyOtp() {
        String url = UrlConstant.SIMPL_VALIDATE + "?walletCode=" + walletCode;
        SimpleHttpAgent<WalletOtpVerification> simpleHttpAgent = new SimpleHttpAgent<WalletOtpVerification>(
                this,
                url,
                new ResponseListener<WalletOtpVerification>() {
                    @Override
                    public void response(WalletOtpVerification responseObject) {
                        pbOtp.setVisibility(View.GONE);
                        Snackbar.make(etOtp1, "Wallet Linked Successfully", Snackbar.LENGTH_SHORT);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("wallet", walletCode);
                        resultIntent.putExtra("wallet_name", walletName);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbOtp.setVisibility(View.GONE);
                        btnSubmitOtp.setEnabled(true);
                        String errorMessage;
                        if (errorResponse != null) {
                            if (errorResponse.message.equalsIgnoreCase("zero_click_otp_does_not_match"))
                                errorMessage = "Incorrect Otp";
                            else
                                errorMessage = errorResponse.message + "";
                            ErrorPopFragment errorPopUp = ErrorPopFragment.Companion.newInstance(errorMessage, "Back",ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment
                                            .OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {

                                        }

                                        @Override
                                        public void onNegativeInteraction() {

                                        }
                                    });
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(errorPopUp, "linkUser")
                                    .commitAllowingStateLoss();
                        }
                    }
                },
                WalletOtpVerification.class
        );

        pbOtp.setVisibility(View.VISIBLE);
        verificationCode.setOtp(getOtp());
        simpleHttpAgent.post(verificationCode, new HashMap<String, JsonSerializer>());
    }


    private void linkUserPayment() {
        Log.d(TAG, "linkUserPayment: initiate");

        String url = UrlConstant.SIMPL_INITIATE + "?walletCode=" + walletCode;
        SimpleHttpAgent<WalletOtpVerification> userWalletsSimpleHttpAgent = new SimpleHttpAgent<>(
                OtpVerificationActivity.this,
                url,
                new ResponseListener<WalletOtpVerification>() {
                    @Override
                    public void response(WalletOtpVerification responseObject) {
                        Log.d(TAG, "linkUserPayment: otp sent");
                        pbOtp.setVisibility(View.GONE);
                        verificationCode = responseObject;
                        forResult = true;
                        setTvOtpTitle();

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error,
                                            ErrorResponse errorResponse) {
                        pbOtp.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION
                                || errorCode == ContextErrorListener.TIMED_OUT) {
                            ErrorPopFragment errorPopUp = ErrorPopFragment.Companion
                                    .newInstance(ApplicationConstants.NO_NET_STRING, "Back",ApplicationConstants.NO_INTERNET_IMAGE, new ErrorPopFragment
                                            .OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {
                                            goBackToPayment();
                                        }

                                        @Override
                                        public void onNegativeInteraction() {
                                            goBackToPayment();
                                        }
                                    });
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(errorPopUp, "linkUser")
                                    .commitAllowingStateLoss();
                        }else{
                            ErrorPopFragment errorPopUp = ErrorPopFragment.Companion.newInstance(errorResponse.message, "Back",true,ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment
                                    .OnFragmentInteractionListener() {
                                @Override
                                public void onPositiveInteraction() {
                                    goBackToPayment();
                                }

                                @Override
                                public void onNegativeInteraction() {
                                    goBackToPayment();
                                }
                            });
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(errorPopUp, "linkUser")
                                    .commitAllowingStateLoss();
                        }
                    }
                },
                WalletOtpVerification.class
        );

        setOtp("");
        pbOtp.setVisibility(View.VISIBLE);

        userWalletsSimpleHttpAgent.get();

    }

    void goBackToPayment(){
        Intent data = new Intent();
        setResult(RESULT_CANCELED,data);
        finish();
    }

    private String getOtp(){
        String otp ;
        if (otpLength==5){
            otp= etOtp1.getText().toString().trim()+
                    etOtp2.getText().toString().trim()+
                    etOtp3.getText().toString().trim()+
                    etOtp4.getText().toString().trim()+
                    etOtp5.getText().toString().trim();
        }
        else if (otpLength==4){
          otp  = etOtp1.getText().toString().trim()+
                    etOtp2.getText().toString().trim()+
                    etOtp3.getText().toString().trim()+
                    etOtp4.getText().toString().trim();

        }
        else{
            otp = etOtp1.getText().toString().trim()+
                    etOtp2.getText().toString().trim()+
                    etOtp3.getText().toString().trim()+
                    etOtp4.getText().toString().trim()+
                    etOtp5.getText().toString().trim()+
                    etOtp6.getText().toString().trim();
        }

        return otp;
    }

    private void setOtp(String otp){
        if (otp.equals("")){
            etOtp6.setText("");
            etOtp5.setText("");
            etOtp4.setText("");
            etOtp3.setText("");
            etOtp2.setText("");
            etOtp1.setText("");

        }else {
            if (otp.length() == 6) {
                etOtp6.setText(String.valueOf(otp.charAt(5)));
                etOtp5.setText(String.valueOf(otp.charAt(4)));
                etOtp4.setText(String.valueOf(otp.charAt(3)));
                etOtp3.setText(String.valueOf(otp.charAt(2)));
                etOtp2.setText(String.valueOf(otp.charAt(1)));
                etOtp1.setText(String.valueOf(otp.charAt(0)));
            } else if (otp.length() == 5) {
                etOtp5.setText(String.valueOf(otp.charAt(4)));
                etOtp4.setText(String.valueOf(otp.charAt(3)));
                etOtp3.setText(String.valueOf(otp.charAt(2)));
                etOtp2.setText(String.valueOf(otp.charAt(1)));
                etOtp1.setText(String.valueOf(otp.charAt(0)));
            } else {
                etOtp4.setText(String.valueOf(otp.charAt(3)));
                etOtp3.setText(String.valueOf(otp.charAt(2)));
                etOtp2.setText(String.valueOf(otp.charAt(1)));
                etOtp1.setText(String.valueOf(otp.charAt(0)));
            }
        }

    }

    private void continueWithregistration() {
        registrationUser.setOtp(getOtp());
        //registrationUser.setOtp(etOtp.getText().toString().trim());

        String url = UrlConstant.SIGN_UP_OTP_VALIDATE;
        SimpleHttpAgent<Object> simpleHttpAgent = new SimpleHttpAgent<Object>(
                this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        pbOtp.setVisibility(View.GONE);
                        btnSubmitOtp.setEnabled(true);

                        proceedWithLogin();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbOtp.setVisibility(View.GONE);
                        btnSubmitOtp.setEnabled(true);

                        if (errorResponse != null) {
                            ErrorPopFragment errorPopUp = ErrorPopFragment.Companion
                                    .newInstance(errorResponse.message, "Back",true,ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment
                                            .OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {

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

    private void sendOtp() {
        Log.d(TAG, "sendOtp: initiate");
        if (employeeId != null && companyId != null) {

            String url = UrlConstant.SEND_OTP;

            SimpleHttpAgent<OTPUser> otpUserSimpleHttpAgent = new SimpleHttpAgent<OTPUser>(
                    this,
                    url,
                    new ResponseListener<OTPUser>() {
                        @Override
                        public void response(OTPUser responseObject) {
                            Log.d(TAG, "sendOtp: otp sent");
                            tvOtpTitle.setText("Verify your phone number :" + responseObject.getMobileNumber());
                            pbOtp.setVisibility(View.GONE);
                            otpUser = responseObject;

                        }
                    },
                    new ContextErrorListener() {
                        @Override
                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                            pbOtp.setVisibility(View.GONE);
                            if (errorResponse != null) {
                                ErrorPopFragment errorPopUp = ErrorPopFragment.Companion
                                        .newInstance(errorResponse.message, "Back", true, ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment
                                                .OnFragmentInteractionListener() {
                                            @Override
                                            public void onPositiveInteraction() {
                                                sendBackToSendOtpActivity();
                                            }

                                            @Override
                                            public void onNegativeInteraction() {
                                                sendBackToSendOtpActivity();
                                            }
                                        });
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .add(errorPopUp, "Resend Otp")
                                        .commit();
                            }
                        }
                    },
                    OTPUser.class
            );


            setOtp("");
            pbOtp.setVisibility(View.VISIBLE);

            TreeMap<String, String> userMap = new TreeMap<>();
            userMap.put("employeeId", employeeId);
            userMap.put("companyId", companyId);

            otpUserSimpleHttpAgent.post(userMap, new HashMap<String, JsonSerializer>());
        }

    }

    private void sendBackToSendOtpActivity(){
        Intent intent = new Intent(OtpVerificationActivity.this, SendOtpActivity.class);
        startActivity(intent);
        finish();
    }

    private void resendOtp(boolean resend) {
        Log.d(TAG, "resendOtp: initiate");

        String url = UrlConstant.REGISTRATION_RESEND_OTP;
        if (resend && registrationUser.getEmail()!=null && AppUtils.getConfig(this).isOtp_over_email()){
            registrationUser.setOtpMedium("both");
        }

        SimpleHttpAgent<ResendOtpResponse> simpleHttpAgent = new SimpleHttpAgent<ResendOtpResponse>(
                this,
                url,
                new ResponseListener<ResendOtpResponse>() {
                    @Override
                    public void response(ResendOtpResponse responseObject) {
                        Log.d(TAG, "resendOtp: otp sent");
                        pbOtp.setVisibility(View.GONE);
                        if (resend && responseObject!=null && responseObject.getResendOtpData()!=null
                                && responseObject.getResendOtpData().getMessage()!=null && !responseObject.getResendOtpData().getMessage().equals("")){
                            tvOtpTitle.setText(responseObject.getResendOtpData().getMessage());
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbOtp.setVisibility(View.GONE);
                        if (errorResponse != null) {
                            ErrorPopFragment errorPopUp = ErrorPopFragment.Companion
                                    .newInstance(errorResponse.message, "Back",ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment
                                            .OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {

                                        }

                                        @Override
                                        public void onNegativeInteraction() {

                                        }
                                    });
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(errorPopUp, "Resend Otp")
                                    .commit();
                        }
                    }
                },
                ResendOtpResponse.class
        );

        setOtp("");
        pbOtp.setVisibility(View.VISIBLE);

        simpleHttpAgent.post(registrationUser, new HashMap<String, JsonSerializer>());
    }

    private void proceedWithLogin() {

        Intent intent = new Intent(this, AutoLoginActivity.class);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_OBJ, registrationUser);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setError("You cancelled otp linking.");
        finish();
    }

    private void setError(String error) {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.ERROR_MESSAGE, error);
        setResult(Activity.RESULT_CANCELED, intent);
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
        tbtitle = findViewById(R.id.tb_title);
        header = findViewById(R.id.header);
        subHeader = findViewById(R.id.subHeader);
        content = findViewById(R.id.content);
        tvOtpTitle = findViewById(R.id.tv_otp_verification);
        btnSubmitOtp = findViewById(R.id.btn_submit_otp);
        pbOtp = findViewById(R.id.pb_otp_verification);
        llSimplHelpText = findViewById(R.id.ll_simpl_help_text);
        //tvTitle = findViewById(R.id.tv_otp_title);
        enterCodeBox = findViewById(R.id.enterCodeBox);
        helperTextContainer = findViewById(R.id.cv_change_password);
        etOtp1 = findViewById(R.id.et_otp1);
        etOtp2 = findViewById(R.id.et_otp2);
        etOtp3 = findViewById(R.id.et_otp3);
        etOtp4 = findViewById(R.id.et_otp4);
        etOtp5 = findViewById(R.id.et_otp5);
        etOtp6 = findViewById(R.id.et_otp6);
        tbtitle.setText("OTP Verification");
        ivBack = findViewById(R.id.iv_back);
        tvResend = findViewById(R.id.tv_resend);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if(otpRegex!=null && !otpRegex.equals("")){
            String res = otpRegex.substring(otpRegex.indexOf("{")+1,otpRegex.indexOf("}"));
            String[] strs = res.split(",");
            int max=0;
            for (String s: strs){
                if (Integer.parseInt(s)>max){
                    max = Integer.parseInt(s);
                }
            }
            otpLength=max;
        }
        else {
            otpLength=6;
        }
        adjustOtpLength(otpLength);

        if (verificationCode == null) {
            helperTextContainer.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) enterCodeBox.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            enterCodeBox.setLayoutParams(params);
        } else if (walletCode.equalsIgnoreCase("simpl")) {
            header.setText("With Simpl, Pay once in 15 days");
            subHeader.setText("How it works");
            content.setText("Simpl creates a tab for you; every Hungerbox order gets added to the tab.\n\nOn 1st and 15th Simpl sends you a consolidated bill of all your spends.\n\nYou can clear it using any online payment method.");
        } else if (walletCode.equalsIgnoreCase("lazypay")) {
            header.setText("Top features of LazyPay Credit");
            subHeader.setText("-Zero Cost Credit (0% interest)\n-No sign-up needed\n-Place your order with just an OTP\n-Pay us back within the due date- we will remind you");
            content.setText("Simpl creates a tab for you; every Hungerbox order gets added to the tab.\n\nOn 1st and 15th Simpl sends you a consolidated bill of all your spends.\n\nYou can clear it using any online payment method.");
            content.setClickable(true);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "By proceeding you agree to the <a href='https://lazypay.in/tnc'> T&C of LazyPay </a>";
            content.setText(Html.fromHtml(text));
        }else if (walletCode.equalsIgnoreCase("PhonePeUpi")) {
            header.setVisibility(View.GONE);
            subHeader.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            helperTextContainer.setVisibility(View.GONE);
            header.setText("Top features of Phone Pay");
            subHeader.setText("-Zero Cost Credit (0% interest)\\n-No sign-up needed\\n-Place your order with just an OTP\\n-Pay us back within the due date- we will remind you");
            content.setText("Simpl creates a tab for you; every Hungerbox order gets added to the tab.\\n\\nOn 1st and 15th Simpl sends you a consolidated bill of all your spends.\\n\\nYou can clear it using any online payment method.");
            content.setClickable(true);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "By proceeding you agree to the <a href='https://lazypay.in/tnc'> T&C of LazyPay </a>";
            content.setText(Html.fromHtml(text));
        }
        else {
            helperTextContainer.setVisibility(View.GONE);
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
                    Log.d(TAG, "Message received onActivityResult: "+ message);
                    messageReceived(message);
                    // Extract one-time code from the message and complete verification
                    // `sms` contains the entire text of the SMS message, so you will need
                    // to parse the string.
//                    String oneTimeCode = parseOneTimeCode(message); // define this function

                    // send one time code to the server
                } else {
                    // Consent canceled, handle the error ...
                    Log.d(TAG, "onActivityResult: Consent Cancelled");
                }
                break;
        }
    }

    private void hideOtpBox(){
        try {
            etOtp1.setVisibility(View.GONE);
            etOtp2.setVisibility(View.GONE);
            etOtp3.setVisibility(View.GONE);
            etOtp4.setVisibility(View.GONE);
            etOtp5.setVisibility(View.GONE);
            etOtp6.setVisibility(View.GONE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void showOtpBox(){
        try {
            etOtp1.setVisibility(View.VISIBLE);
            etOtp2.setVisibility(View.VISIBLE);
            etOtp3.setVisibility(View.VISIBLE);
            etOtp4.setVisibility(View.VISIBLE);
            etOtp5.setVisibility(View.VISIBLE);
            etOtp6.setVisibility(View.VISIBLE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void adjustOtpLength(int otpLength){
        try {
            showOtpBox();
            if (otpLength == 5){
                etOtp6.setVisibility(View.GONE);
            } else if (otpLength == 4){
                etOtp6.setVisibility(View.GONE);
                etOtp5.setVisibility(View.GONE);
            }

        } catch (Exception e){
            e.printStackTrace();
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

    private void startSmsListener(){
        try {
            registerReceiver(smsRetreiverReceiver,intentFilter);
            SmsRetriever.getClient(this).startSmsUserConsent(null);
        }catch (Exception exp){
        }
    }
}
