package com.hungerbox.customer.order.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.fragment.SimplWebViewFragment;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.receiver.SmsListener;
import com.hungerbox.customer.receiver.SmsReceiver;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderWebPaymentActivity extends ParentActivity {

    WebView paymentWebView;
    ProgressBar pbLoader;
    LinearLayout llPaymentCancelContainer;
    Button btNegative, btPositive;
    String method;
    Map<String, String> postData;
    boolean transactionComplete = false;
    Order order;

    private SimplWebViewFragment.OnRedirectListener mListener;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_web_payment);

        paymentWebView = findViewById(R.id.webview_layout);
        pbLoader = findViewById(R.id.pb_loader);
        llPaymentCancelContainer = findViewById(R.id.ll_back_container);
        btNegative = findViewById(R.id.btn_negative);
        btPositive = findViewById(R.id.btn_positive);

        url = getIntent().getStringExtra(ApplicationConstants.URL);
        method = getIntent().getStringExtra(ApplicationConstants.HTTP_METHOD);
        postData = (Map<String, String>) getIntent().getSerializableExtra(ApplicationConstants.POST_DATA);
        order = (Order) getIntent().getSerializableExtra(ApplicationConstants.ORDER);


        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentCancelNegativeAction();
            }
        });

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentCancelPositiveAction();
            }
        });

        llPaymentCancelContainer.setVisibility(View.GONE);
        setupWebView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateCartTime();
    }

    @Override
    public void onBackPressed() {
        llPaymentCancelContainer.setVisibility(View.VISIBLE);
    }


    private void paymentCancelPositiveAction() {
        goBack(url, false, ApplicationConstants.USER_CANCELLED);
    }

    private void paymentCancelNegativeAction() {
        llPaymentCancelContainer.setVisibility(View.GONE);
    }

    private void updateCartTime() {
        MainApplication.updateCartTime();
    }

    private void setupWebView() {
        paymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    if(request.getUrl().toString().contains("#/status/simpl/success"))
//                    {
//                        mListener.onRedirect(request.getUrl().toString(),true);
//                        dismissAllowingStateLoss();
//                    }else if(url.contains("#/status/simpl/error")){
//                        mListener.onRedirect(request.getUrl().toString(),false);
//                        dismissAllowingStateLoss();
//                    }
//                }
                return super.shouldOverrideUrlLoading(view, request);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.matches(".+#/status/[a-zA-Z]+/success.*")) {
                    transactionComplete = true;
                    goBack(url, true, "");
                    return true;

                } else if (url.matches(".+#/status/[a-zA-Z]+/error.*")) {
                    url = url.replace("#", "");
                    Uri errorUri = Uri.parse(url);
                    String reason = errorUri.getQueryParameter("reason");
                    if (reason != null) {
//                            mListener.onRedirect(url, false, reason);
                        goBack(url, false, reason);
                    } else {
//                            mListener.onRedirect(url, false, "");
                        goBack(url, false, "");
                    }
                    transactionComplete = true;
                    return true;
                } else if (url.contains("/user_cancelled")) {
                    transactionComplete = true;
                    goBack(url, false, "");
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoader.setVisibility(View.GONE);
                paymentWebView.clearHistory();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
//                request.getUrl()
                if (view.getUrl().contains(UrlConstant.ZETA_TRANSACTION_VALIDATION)) {
                    loadWebView();
                } else if (view.getUrl().contains("hungerbox")) {
                    if (mListener != null)
                        mListener.onRedirect(url, false, "");
                }
            }
        });

        WebSettings settings = paymentWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            paymentWebView.setWebContentsDebuggingEnabled(true);
        }
        settings.setJavaScriptEnabled(true);
        paymentWebView.getSettings().setLoadWithOverviewMode(true);
        paymentWebView.getSettings().setBuiltInZoomControls(true);
        paymentWebView.getSettings().setSupportZoom(true);
        clearCookies(OrderWebPaymentActivity.this);
        loadWebView();
    }


    private void loadWebView() {
        if (method.equalsIgnoreCase(ApplicationConstants.REDIRECTION)) {
            paymentWebView.loadUrl(url);
        } else if (method.equalsIgnoreCase(ApplicationConstants.HTTP_GET)) {
            paymentWebView.loadUrl(url);
        } else {
            StringBuilder sb = new StringBuilder();

            sb.append("<html><head></head>");
            sb.append("<body>");
            sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
            for (String key : postData.keySet()) {
                sb.append(String.format("<input name='%s' type='hidden' value='%s' />", key, postData.get(key)));
            }
            sb.append("</form>");
//            sb.append("<script>document.getElementById('form1').submit();</script>");
            sb.append("<script>setTimeout(function(){ document.getElementById('form1').submit(); }, 1000);</script>");
            sb.append("</body></html>");

            String url = UrlConstant.ZETA_TRANSACTION_VALIDATION + "/" + postData.get("order_id");

            paymentWebView.loadUrl(url);
        }
    }


    private void goBack(String url, Boolean success, String reason) {
        //TODO have to write back transition of payment view
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.URL, url);
        intent.putExtra(ApplicationConstants.PAYMENT_STATUS, success);
        intent.putExtra(ApplicationConstants.REASON, reason);
        intent.putExtra(ApplicationConstants.ORDER, order);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    public void updateOtp(String otp) {
        if (paymentWebView != null) {
            paymentWebView.loadUrl(String.format("javascript:try{document.getElementById('otp-box').value=%s;}catch(e){}", otp));
        }
    }


    private void checkForPermission() {
        if (AppUtils.getConfig(this).isOtp_on_order()) {
            int hasContactPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.RECEIVE_SMS);

            if (hasContactPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                        ApplicationConstants.PERMISSION_REQUEST_CODE);
            } else {
                bindSmsReciever();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ApplicationConstants.PERMISSION_REQUEST_CODE:
                // Check if the only required permission has been granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppUtils.HbLog("Permission", "Receive SMS permission has now been granted. Showing result.");
                    bindSmsReciever();
                } else {
                    AppUtils.HbLog("Permission", "Receive SMS permission was NOT granted.");
                }
                break;
        }
    }


    private void bindSmsReciever() {
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                AppUtils.HbLog("Text", messageText);
                {
                    if (messageText.matches(AppUtils.getConfig(getApplicationContext()).getZeta_sms_keyword())) {
                        Pattern p = Pattern.compile("(\\d{4})");
                        Matcher m = p.matcher(messageText);
                        if (m.find()) {
                            String receivedOtp = m.group(0);
                            if (receivedOtp.length() >= 4 && receivedOtp.charAt(0) != '0') {
                                receivedOtp = receivedOtp.length() == 4 ? receivedOtp : String.format("%0" + (4 - receivedOtp.length()) + "d%s", 0, receivedOtp);
                                updateOtp(receivedOtp);
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void clearCookies(Context context)
    {

        if(!AppUtils.isCafeApp()){
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
