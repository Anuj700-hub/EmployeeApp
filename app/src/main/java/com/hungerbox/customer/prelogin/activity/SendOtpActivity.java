package com.hungerbox.customer.prelogin.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.OTPUser;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppSignatureHelper;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class SendOtpActivity extends ParentActivity {

    Button btSubmit;
    TextInputEditText tetUserName;
    TextInputLayout tilUsername;
    long companyId;
    boolean isResumed = false;
    ImageView ivBack;
    private String[] loginMethods;
    private TextView tvTitle;
    RelativeLayout rlProgress;
    TextView tbHeader,tbTitle;
    private static final int CREDENTIAL_PICKER_REQUEST = 1;
    public static String userPhoneNumber = "";
    private String loginHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);
        AppUtils.setupUI(findViewById(R.id.sendOtpParent), SendOtpActivity.this);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_new_title);
        tetUserName = findViewById(R.id.tet_username);
        tilUsername = findViewById(R.id.til_username);
        btSubmit = findViewById(R.id.bt_submit);
        rlProgress = findViewById(R.id.rl_progress);
        tbHeader = findViewById(R.id.tb_header);
        tbTitle = findViewById(R.id.tb_title);

        tbTitle.setText("Hello!");
        tbHeader.setText("Glad to see you again..");

        loginMethods = AppUtils.getConfig(this).getLogin_methods();
        loginHint = "";

        for (int i = 0; i < loginMethods.length; ++i) {
            loginHint += loginMethods[i].substring(0, 1).toUpperCase() + loginMethods[i].substring(1);
            if (i + 1 != loginMethods.length)
                loginHint += "/";
        }
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvTitle.setText("Enter " + loginHint + " to get OTP on registered number");
        tilUsername.setHint("Enter " + loginHint);

        companyId = AppUtils.getConfig(this).getCompany_id();
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUsernameAndSendOtp();
            }
        });


        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
        ViewCompat.setBackgroundTintList(tetUserName, colorStateList);
        tetUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {
                if (actionid == EditorInfo.IME_ACTION_GO || keyEvent == null || keyEvent.getAction() == KeyEvent.ACTION_UP
                        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    verifyUsernameAndSendOtp();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        navigateToLoginActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        btSubmit.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    private void navigateToLoginActivity() {
        finish();
    }

    private void verifyUsernameAndSendOtp() {

        if (tetUserName.getText() == null || tetUserName.getText().toString().isEmpty()) {
            tilUsername.setError(" Please enter your " + loginHint);
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate);
            tilUsername.startAnimation(shake);
            return;
        }
        startSmsListening();
        sendOtp(tetUserName.getText().toString());
    }

    // Construct a request for phone numbers and show the picker
    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0);
    }

    private void startSmsListening(){
//        Task<Void> task = SmsRetriever.getClient(this).startSmsUserConsent(null);


    }

    private void sendOtp(String userName) {

        btSubmit.setEnabled(false);
        navigateToNextActivity(userName, String.valueOf(companyId));
    }

    private void navigateToNextActivity(String employeeId, String companyId) {
        Intent intent = new Intent(this, OtpVerificationActivity.class);
        intent.putExtra("employee_id",employeeId);
        intent.putExtra("company_id",companyId);
        intent.putExtra("name",OtpVerificationActivity.SEND_OTP_ACTIVITY);
        startActivity(intent);
        finish();
    }

    private void showProgress() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        rlProgress.setVisibility(View.GONE);
    }

    private void showError(String error,String imageType) {
        if (!isResumed)
            return;

        ErrorPopFragment genericPopUpFragment = ErrorPopFragment.Companion.newInstance(error, "RETRY",true,imageType,
                new ErrorPopFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        verifyUsernameAndSendOtp();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREDENTIAL_PICKER_REQUEST:
                // Obtain the phone number from the result
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    String temp = credential.getId();
                    if (temp.length()==13 && temp.contains("+")){
                        userPhoneNumber = temp.substring(3);
                        tetUserName.setText(userPhoneNumber);
                    } else if (temp.length()==11 && temp.charAt(0)=='0'){
                        userPhoneNumber = temp.substring(1);
                        tetUserName.setText(userPhoneNumber);
                    }
//                     userPhoneNumber = credential.getId();  // will need to process phone number string
                }
                break;

        }
    }
}
