package com.hungerbox.customer.prelogin.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.EmailVerification;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.RegistrationUser;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.hungerbox.customer.network.UrlConstant.IS_EMAIL_VALID;

public class EmailActivationRequired extends ParentActivity {
    RegistrationUser registrationUser;
    private Button btnResendLink , btnChangeEmail;
    private Timer autorefresh;
    final int resendReactDelay = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_activation);
        registrationUser =(RegistrationUser) getIntent().getSerializableExtra("reg_user");
        btnResendLink = (Button) findViewById(R.id.btn_resend_email);
        btnChangeEmail = (Button) findViewById(R.id.btn_change_email);
        clickListeners();
        autorefresh = new Timer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(registrationUser != null){
            checkIfEmailValid();
            startTimer();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    private void startTimer() {
        autorefresh = new Timer();
        autorefresh.schedule(new TimerTask() {
            @Override
            public void run() {
                checkIfEmailValid();
            }
        },5000,10000);
    }



    private void stopTimer() {
        autorefresh.cancel();
        autorefresh.purge();
    }

    private void checkIfEmailValid() {

        String url = IS_EMAIL_VALID+registrationUser.getId();

        SimpleHttpAgent<EmailVerification> emailVerificationHttpAgent = new SimpleHttpAgent<EmailVerification>(
                this,
                url,
                new ResponseListener<EmailVerification>() {
                    @Override
                    public void response(EmailVerification responseObject) {
                        if(responseObject.getData().isSuccess()){
                            registrationUser.setEmailActivated(1);
                            SharedPrefUtil.remove(ApplicationConstants.REGISTRATERED_USER);
                            Intent intent = new Intent(EmailActivationRequired.this, AutoLoginActivity.class);
                            intent.putExtra(ApplicationConstants.USER_REGISTRATION_OBJ, registrationUser);
                            startActivity(intent);
                            finish();
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION ||
                                errorCode == ContextErrorListener.TIMED_OUT) {
                            Snackbar.make(btnResendLink,"Please check your Internet Connection.",Snackbar.LENGTH_SHORT).show();
                        }else{
                            Snackbar.make(btnResendLink,error,Snackbar.LENGTH_SHORT).show();
                        }
                        AppUtils.HbLog("Email",error);
                    }
                },
                EmailVerification.class
        );
        emailVerificationHttpAgent.get();

    }

    private void clickListeners() {
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailActivationRequired.this,SignUpActivityBasic.class);
                long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
                String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "");
                HashMap<String, Long> qrCodeMap = AppUtils.getConfig(EmailActivationRequired.this).getRegistration_qr_hashs();
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
                intent.putExtra(ApplicationConstants.LOCATION_ID,locationId);
                intent.putExtra(ApplicationConstants.LOCATION_NAME,locationName);
                intent.putExtra(ApplicationConstants.QR_CODE,qrCode);
                startActivity(intent);
                finish();

            }
        });

        btnResendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isClickable()){
                    return;
                }
                v.setClickable(false);
                btnResendLink.setClickable(false);
                v.postDelayed(() -> {
                    btnResendLink.setClickable(true);
                    v.setClickable(true);
                },resendReactDelay);

                String url = UrlConstant.RESEND_EMAIL_VERIFICATION_TOKEN+"?request_id="+registrationUser.getId();

                SimpleHttpAgent<EmailVerification> resendEmailHttpAgent = new SimpleHttpAgent<EmailVerification>(
                        EmailActivationRequired.this,
                        url,
                        new ResponseListener<EmailVerification>() {
                            @Override
                            public void response(EmailVerification responseObject) {
                                Snackbar.make(btnResendLink,"Verification email sent successfully.",Snackbar.LENGTH_SHORT).show();
//                                AppUtils.HbLog("Email",responseObject.getEmployeeId());

                            }
                        },
                        new ContextErrorListener() {
                            @Override
                            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                                Snackbar.make(btnResendLink,"Verification email not sent.",Snackbar.LENGTH_SHORT).show();
                                AppUtils.HbLog("Email",error);

                            }
                        },
                        EmailVerification.class
                );
                resendEmailHttpAgent.post(registrationUser,new HashMap<String, JsonSerializer>());
            }
        });
    }



}
