package com.hungerbox.customer.order.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;

public class PaytmRechargeActivity extends ParentActivity {
    WebView webView;
    Double amount = -1.0,externalWalletBalance = 0.0;

    private PaymentMethod paymentMethodToLink;
    public static final int SUCCESS = 1010;
    public static final int FAILURE = 1011;
    private RelativeLayout rechargeView;
    private ImageView backButton;
    private TextView currentBalance;
    private TextView minimumAmount;
    private EditText amountBox;
    private Button button200,button500,button1000;
    private Button addMoney;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_recharge);


        webView = findViewById(R.id.wv_paytm);
        rechargeView = findViewById(R.id.rl_recharge);
        backButton = findViewById(R.id.iv_back);
        currentBalance = findViewById(R.id.currentBalance);
        minimumAmount = findViewById(R.id.remainingAmount);
        amountBox = findViewById(R.id.amountBox);
        button200 = findViewById(R.id.recharge_200);
        button500 = findViewById(R.id.recharge_500);
        button1000 = findViewById(R.id.recharge_1000);
        addMoney = findViewById(R.id.buttonRecharge);
        amount = getIntent().getDoubleExtra("amount",-1.0);
        externalWalletBalance = getIntent().getDoubleExtra(ApplicationConstants.CURRENT_EXTRENAL_WALLET_BALANCE,0.0);


        paymentMethodToLink = (PaymentMethod) getIntent().getSerializableExtra(ApplicationConstants.PAYMENT_METHOD);

        //recharge view
        webView.setVisibility(View.GONE);
        rechargeView.setVisibility(View.VISIBLE);
        currentBalance.setText(externalWalletBalance.toString());
        amountBox.setText(getStringFromDouble(amount));
        minimumAmount.setText(getStringFromDouble(amount));
        setButtonListeners();



        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (amount>-1.0){
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    System.out.println(url);
                    if (url.matches(".+#/status/[a-zA-Z]+/success.*")){
                        goBackAndRefresh(true);
                        //AppUtils.showToast("Recharge Successful",true,1);
                        return true;

                    } else if (url.matches(".+#/status/[a-zA-Z]+/error.*")){
//                        AppUtils.showToast("Recharge Failed",true,0);
                        goBackAndRefresh(false);
                        return true;
                    }

                    return false;
                }


            });
        }
    }

    private void goBackAndRefresh(boolean status){
        //restart logout timer
        LogoutTask.updateTime();
        LogoutTask.getInstance(PaytmRechargeActivity.this).startTimer();

        Intent resultIntent = new Intent();
        if(status) {
            setResult(SUCCESS, resultIntent);
            paymentMethodToLink.setAmount(Double.parseDouble(amountBox.getText().toString())+externalWalletBalance);
            resultIntent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink);
        }else{
            resultIntent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink);
            setResult(FAILURE, resultIntent);
        }

        finish();

    }

    private void setButtonListeners(){

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        button200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAmount(200);
            }
        });
        button500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAmount(500);
            }
        });
        button1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAmount(1000);
            }
        });
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidAmount() && amount>-1.0){
                    proceedToRecharge();
                }
                else{
                    AppUtils.showToast("Minimum amount is "+getStringFromDouble(amount),true, 0);
                }
            }
        });
    }

    private void proceedToRecharge(){
        //stop auto logout timer
        if (AppUtils.getConfig(getApplicationContext()).isAuto_logout()) {
            LogoutTask.updateTime();
            LogoutTask.getInstance(getApplicationContext()).stopTimer();
        }
        rechargeView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        HashMap<String,String> headerMap = new HashMap();
        String url = UrlConstant.PAYTM_DIRECT_RECHARGE+"amount="+amountBox.getText().toString();
        headerMap.put("Authorization","Bearer "+ SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN,""));
        webView.loadUrl(url,headerMap);
    }

    private void addAmount(int amountToAdd){
        try{
            Double currentAmount = Double.parseDouble(amountBox.getText().toString());
            currentAmount += amountToAdd;
            amountBox.setText(getStringFromDouble(currentAmount));
        } catch (Exception e){
            e.printStackTrace();
            amountBox.setText(String.valueOf(amountToAdd));
        }
    }

    private boolean isValidAmount(){
        try {
            if (Double.parseDouble(amountBox.getText().toString())<amount){
                return false;
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void onBackPressed() {
        goBackAndRefresh(false);
    }

    private String getStringFromDouble(Double input){
        Double rounded = Math.ceil(input);
        String roundedString = rounded.toString();
        return roundedString;
    }
}
