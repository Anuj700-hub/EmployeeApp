package com.hungerbox.customer.payment.activity;

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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.PaymentDetailsResponse;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.receiver.SmsListener;
import com.hungerbox.customer.util.AppSignatureHelper;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SmsRetrieverReceiver;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hungerbox.customer.prelogin.activity.OtpVerificationActivity.SMS_CONSENT_REQUEST;

public class JPOtpverificationActivity extends ParentActivity {

    TextView tvTitle,tbTitle;
    //EditText etOtp;
    EditText etOtp1,etOtp2,etOtp3,etOtp4,etOtp5,etOtp6;
    Button btnSubmit;
    ImageView ivBack;
    ProgressBar pbLoader;
    PaymentMethod paymentMethodToLink;
    User user;
    private TextView btnResend;
    private SmsRetrieverReceiver smsRetreiverReceiver;
    int otpLength;
    IntentFilter intentFilter;
    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
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
        setContentView(R.layout.activity_jpotpverification);
        intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter);
        SmsRetriever.getClient(this).startSmsUserConsent(null);
        tvTitle = findViewById(R.id.tv_title);
       // etOtp = findViewById(R.id.et_otp);
        ivBack = findViewById(R.id.iv_back);
        btnSubmit = findViewById(R.id.btn_submit);
        pbLoader = findViewById(R.id.pb_loader);
        btnResend = findViewById(R.id.btn_resend_otp);
        tbTitle = findViewById(R.id.tb_title);

        etOtp1 = findViewById(R.id.et_otp1);
        etOtp2 = findViewById(R.id.et_otp2);
        etOtp3 = findViewById(R.id.et_otp3);
        etOtp4 = findViewById(R.id.et_otp4);
        etOtp5 = findViewById(R.id.et_otp5);
        etOtp6 = findViewById(R.id.et_otp6);

        pbLoader.setVisibility(View.GONE);
        tbTitle.setText("OTP Verification");

        user = (User) getIntent().getSerializableExtra(ApplicationConstants.OTP_USER);
        paymentMethodToLink = (PaymentMethod) getIntent().getSerializableExtra(ApplicationConstants.PAYMENT_METHOD);


        if(paymentMethodToLink.getOtpRegex()!=null && !paymentMethodToLink.getOtpRegex().equals("")){
            String res = paymentMethodToLink.getOtpRegex().substring(paymentMethodToLink.getOtpRegex().indexOf("{")+1,paymentMethodToLink.getOtpRegex().indexOf("}"));
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
        if (otpLength==5){
            etOtp6.setVisibility(View.GONE);
        }
        else if (otpLength==4){
            etOtp5.setVisibility(View.GONE);
            etOtp6.setVisibility(View.GONE);
        }

        if (user != null)
            tvTitle.setText(String.format("OTP sent to %s", user.getPhoneNumber()));
        else
            tvTitle.setText("OTP sent to your Registered mobile number.");


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSubmit();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResendClick();
            }
        });
/*        etOtp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateAndSubmit();
                    return true;
                }
                return false;
            }
        });*/
        clickListeners();

        setError("You cancelled the otp linking");

        startWalletOtpLinking(paymentMethodToLink);
    }

    private void onResendClick(){
        if (smsVerificationReceiver!=null && intentFilter!=null) {
            stopSmsListener();
            startSmsListener();
        }
        btnResend.setVisibility(View.GONE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                etOtp1.setSelection(0);
                btnResend.setVisibility(View.VISIBLE);
                startWalletOtpLinking(paymentMethodToLink);
            }
        };
        new Handler().postDelayed(runnable,800);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startWalletOtpLinking(final PaymentMethod paymentMethod) {

        String url = UrlConstant.JUSPAY_OTP_INIT;

        pbLoader.setVisibility(View.VISIBLE);
        HashMap<String, Object> reqPayload = new HashMap<>();
        reqPayload.put("walletName", paymentMethod.getPaymentMethod());
        if (paymentMethod.getPaymentDetails() != null && !paymentMethod.getPaymentDetails().getId().isEmpty())
            reqPayload.put("walletId", paymentMethod.getPaymentDetails().getId());

        SimpleHttpAgent<PaymentDetailsResponse> paymentDetailsResponseSimpleHttpAgent = new SimpleHttpAgent<PaymentDetailsResponse>(
                this,
                url,
                new ResponseListener<PaymentDetailsResponse>() {
                    @Override
                    public void response(PaymentDetailsResponse responseObject) {
                        pbLoader.setVisibility(View.GONE);
                        if (responseObject != null) {
                            paymentMethodToLink.setPaymentDetailsResponse(responseObject);
                            //Snackbar.make(etOtp, "OTP Sent Successfully", BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else
                            otpLinkingFailed("Check OTP initiate API");
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbLoader.setVisibility(View.GONE);
                        otpLinkingFailed(error);
                    }
                },
                PaymentDetailsResponse.class
        );

        paymentDetailsResponseSimpleHttpAgent.post(reqPayload, new HashMap<String, JsonSerializer>());
    }

    private void setError(String error) {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.ERROR_MESSAGE, error);
        intent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink);
        setResult(Activity.RESULT_CANCELED, intent);
    }

    private void otpLinkingFailed(String error) {
        setError(error);
        goBack();
    }

    private void otpLinkingSuccess() {
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink);
        setResult(Activity.RESULT_OK, intent);
        goBack();
    }


    private void validateAndSubmit() {
        String orderStr = getOtp();
        if (orderStr.length() >= 4 && orderStr.length() <= 6 && orderStr.matches("[0-9]+")) {
            verifyOtpFromServer();
        }
    }

    private void verifyOtpFromServer() {
        pbLoader.setVisibility(View.VISIBLE);
        String url = UrlConstant.JUSPAY_OTP_VALIDATE;
        final SimpleHttpAgent<PaymentDetailsResponse> otpLinking = new SimpleHttpAgent<PaymentDetailsResponse>(
                this,
                url,
                new ResponseListener<PaymentDetailsResponse>() {
                    @Override
                    public void response(PaymentDetailsResponse responseObject) {
                        paymentMethodToLink.setPaymentDetailsResponse(responseObject);
                        pbLoader.setVisibility(View.GONE);
                        otpLinkingSuccess();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbLoader.setVisibility(View.GONE);
                        //etOtp.setError(error);
                        AppUtils.showToast(error,false,0,false);
                    }
                },
                PaymentDetailsResponse.class
        );

        HashMap<String, Object> reqPayload = new HashMap<>();
        reqPayload.put("walletId", paymentMethodToLink.getPaymentDetails().getId());
        reqPayload.put("otp", getOtp());

        otpLinking.post(reqPayload, new HashMap<String, JsonSerializer>());
    }

    private void goBack() {

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            unregisterReceiver(smsRetreiverReceiver);
        }catch (Exception exp){
        }
        SmsRetrieverReceiver.stopListener();

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopSmsListener();
    }

    public void messageReceived(String messageText){
        try {
            if (messageText != null && !messageText.equals("")) {
                messageText = messageText.toLowerCase();
                if (paymentMethodToLink.getOtpRegex() != null && paymentMethodToLink.getOtpRegex().length() > 0) {
                    Pattern p = Pattern.compile(paymentMethodToLink.getOtpRegex());
                    Matcher m = p.matcher(messageText);
                    if (m.find()) {
                        String receivedOtp = m.group(0);
                        if (receivedOtp.trim().length() == 4) {
                            etOtp6.setVisibility(View.GONE);
                            etOtp5.setVisibility(View.GONE);
                        } else if (receivedOtp.trim().length() == 5) {
                            etOtp6.setVisibility(View.GONE);
                        }
                        setOtp(receivedOtp.trim());
                        //etOtp.setText(receivedOtp.trim());
                        btnSubmit.performClick();
                    }
                }
            }
        } catch (Exception e){
            Log.d("sms consent", "error in sms auto read");
            e.printStackTrace();
        }
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
    private void clickListeners(){
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
                if(keyCode == KeyEvent.KEYCODE_DEL && etOtp3.getText().toString().length()==0 && keyEvent.getAction()==KeyEvent.ACTION_UP) {
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
                if(keyCode == KeyEvent.KEYCODE_DEL && etOtp4.getText().toString().length()==0 && keyEvent.getAction()==KeyEvent.ACTION_UP) {
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
                if(keyCode == KeyEvent.KEYCODE_DEL && etOtp5.getText().toString().length()==0 && keyEvent.getAction()==KeyEvent.ACTION_UP) {
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
                if(keyCode == KeyEvent.KEYCODE_DEL && etOtp6.getText().toString().length()==0 && keyEvent.getAction()==KeyEvent.ACTION_UP) {
                    //this is for backspace
                    etOtp5.requestFocus(View.FOCUS_LEFT);
                    return true;
                }
                return false;
            }
        });
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
