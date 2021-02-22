package com.hungerbox.customer.prelogin.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.ResetPassword;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.LoginActivity;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;

import java.util.HashMap;

public class ForgotPasswordActivity extends ParentActivity {

    LinearLayout llPasswordSentContainer;
    Button btPositive, btBack;
    ImageView ivBack;
    EditText etEmail;
    TextInputLayout tilEmail;

    private String[] loginMethods;
    private long companyId;
    private String loginHint = "";
    private LinearLayout llLogin;
    RelativeLayout rlProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forgot_password_dialog);
        btPositive = findViewById(R.id.bt_positive);
        ivBack = findViewById(R.id.iv_back);
        btBack = findViewById(R.id.bt_back);
        etEmail = findViewById(R.id.et_email);
        tilEmail = findViewById(R.id.til_email);
        llPasswordSentContainer = findViewById(R.id.ll_password_sent_container);
        loginMethods = AppUtils.getConfig(this).getLogin_methods();
        companyId = AppUtils.getConfig(this).getCompany_id();
        llLogin = findViewById(R.id.ll_login);
        rlProgress = findViewById(R.id.rl_progress);


        for (int i = 0; i < loginMethods.length; ++i) {
            loginHint += loginMethods[i];
            if (i + 1 != loginMethods.length)
                loginHint += "/";
        }

        tilEmail.setHint("Enter " + loginHint);

        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
        ViewCompat.setBackgroundTintList(etEmail, colorStateList);
        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LogoutTask.updateTime();
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    sendPassword();
                }
                return false;
            }
        });

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPassword();
            }
        });


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LogoutTask.updateTime();
    }

    private void sendPassword() {
        LogoutTask.updateTime();
        if (etEmail.getText() == null || etEmail.getText().toString().trim().isEmpty()) {
            tilEmail.setError("please enter your " + loginHint);
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.vibrate);
            tilEmail.startAnimation(shake);
            return;
        }

        sendEmailToServer(etEmail.getText().toString().trim());

    }

    private void sendEmailToServer(String email) {
        String url = UrlConstant.RESET_PASSWORD;
        showProgress();
        btPositive.setEnabled(false);
        AppUtils.hideKeyboard(ForgotPasswordActivity.this,null);
        SimpleHttpAgent<Object> resetPasswordHttpAgent = new SimpleHttpAgent<Object>(
                this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        llPasswordSentContainer.setVisibility(View.VISIBLE);
                        btPositive.setVisibility(View.GONE);
                        llLogin.setVisibility(View.GONE);
                        hideProgress();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        btPositive.setEnabled(true);
                        hideProgress();
                        if (error == null || error.equals("")) {
                            AppUtils.showToast(error, true, 0);
                        } else {
                            AppUtils.showToast("Some error occured.", true, 0);
                        }
                    }
                },
                Object.class
        );
        resetPasswordHttpAgent.post(new ResetPassword().setEmail(email).setCompanyId(companyId), new HashMap<String, JsonSerializer>());
    }

    @Override
    protected void onResume() {
        super.onResume();
        btPositive.setEnabled(true);
    }

    private void showProgress() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        rlProgress.setVisibility(View.GONE);
    }
}
