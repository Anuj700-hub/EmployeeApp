package com.hungerbox.customer.order.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.juspay.juspaysafe.BrowserCallback;
import in.juspay.juspaysafe.BrowserParams;
import in.juspay.juspaysafe.JuspaySafeBrowser;

public class JPWebViewActivity extends ParentActivity {

    public static final int SUCCESS = 1010;
    public static final int FAILURE = 1011;
    //    JuspayWebView wvJP;
    String paymentUrl, returnUrl;
    private boolean shown = false;
    private String transactionId;
    private String amountId;
    private String walletName;
    private PaymentMethod paymentMethodToLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpweb_view);
//        wvJP = (JuspayWebView) findViewById(R.id.wv_jp);

        paymentUrl = getIntent().getStringExtra(ApplicationConstants.PAYMENT_URL);
        returnUrl = getIntent().getStringExtra(ApplicationConstants.RETURN_URL);
        transactionId = getIntent().getStringExtra("transactionId");
        amountId = getIntent().getStringExtra("amountId");
        walletName = getIntent().getStringExtra("walletName");
        paymentMethodToLink = (PaymentMethod) getIntent().getSerializableExtra(ApplicationConstants.PAYMENT_METHOD);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        loadUrl();
    }

    @Override
    public void onBackPressed() {
        if (!shown)
            showBackPressed();
    }

    private void showBackPressed() {
        shown = true;
        GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance("Do you want to cancel the current transaction",
                "Go Back", "Cancel",
                new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        shown = false;
                        goBackAndRefresh(false);

                    }

                    @Override
                    public void onNegativeInteraction() {
                        shown = false;
                    }
                });

        genericPopUpFragment.show(getSupportFragmentManager(), "jpre_cancel");
    }

    public void loadUrl() {

        BrowserCallback callBack = new BrowserCallback() {

            @Override
            public void onPageStarted(WebView view, String url) {
                super.onPageStarted(view, url);
            }

            @Override
            public void onTransactionAborted(JSONObject paymentDetails) {
                JuspaySafeBrowser.exit();
                AppUtils.showToast("Transaction cancelled.", false, 0);
                goBackAndRefresh(false);

            }

            @Override
            public void onWebViewReady(WebView webView) {
                AppUtils.HbLog("JP WV", "onWebViewReady: url " + webView.getUrl());
            }

            @Override
            public void endUrlReached(WebView view, JSONObject sessionInfo) {
                Pattern success = Pattern.compile(UrlConstant.PAYMENT_RESPONSE_REGEX_SUCCESS);

                Matcher successMatcher = null;
                try {
                    successMatcher = success.matcher(sessionInfo.getString("url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JuspaySafeBrowser.exit();

                // Required action performed
                AppUtils.showToast("Transaction completed", false, 1);
                // Payment step has been completed by the user. Take the user to the next step.
                if (successMatcher != null && successMatcher.matches())
                    goBackAndRefresh(true);
                else
                    goBackAndRefresh(false);

            }
        };

        BrowserParams browserParams = new BrowserParams();
        browserParams.setMerchantId(UrlConstant.JUSPAY_MERCHANT_ID);
        browserParams.setTransactionId(transactionId);
        browserParams.setOrderId("");
        browserParams.setClientId("hungerbox");
        browserParams.setUrl(paymentUrl);
        browserParams.setAmount(amountId);
        JuspaySafeBrowser.setEndUrls(new String[]{UrlConstant.PAYMENT_RESPONSE_REGEX_SUCCESS, UrlConstant.PAYMENT_RESPONSE_REGEX_FAILURE});
        JuspaySafeBrowser.start(this, browserParams, callBack);

    }

    private void goBackAndRefresh(boolean status) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("walletName", walletName);
        if (status) {
            paymentMethodToLink.getPaymentDetails()
                    .setCurretBalance(paymentMethodToLink.getPaymentDetails().getCurretBalance()
                            + Double.parseDouble(amountId));
            resultIntent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink);
            setResult(JPWebViewActivity.SUCCESS, resultIntent);
        } else {
            resultIntent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink);
            setResult(JPWebViewActivity.FAILURE, resultIntent);
        }
        goback();
    }

    private void goback() {
        finish();
    }
}
