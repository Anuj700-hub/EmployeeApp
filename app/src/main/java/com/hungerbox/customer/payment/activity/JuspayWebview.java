package com.hungerbox.customer.payment.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JuspayWebview extends ParentActivity {

    private WebView juspayWebview;
    private ProgressBar pbLoader;
    private String url, orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juspay_webview);
        juspayWebview = findViewById(R.id.juspay_webview);
        pbLoader = findViewById(R.id.pb_loader);

        url = getIntent().getStringExtra(ApplicationConstants.URL);
        if(url.contains("hungerbox.com")){
            url = url+"&access_token="+SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
        }
        orderId = getIntent().getStringExtra(ApplicationConstants.ORDER_ID);

        initWebView();
        loadUrl(url);
    }

    @Override
    public void onBackPressed() {

        AlertDialog al = new AlertDialog.Builder(this).setTitle("Cancel Transaction?").setMessage("This would cancel your ongoing transaction.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        al.show();

    }

    private void loadUrl(String url) {
        pbLoader.setVisibility(View.VISIBLE);
        juspayWebview.loadUrl(url);
    }

    private void initWebView() {
        final WebSettings settings = juspayWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            juspayWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            juspayWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        juspayWebview.clearCache(true);
        clearCookies(this);
        juspayWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 95) {
                    pbLoader.setVisibility(View.GONE);
                }
            }
        });

        juspayWebview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Pattern payment = Pattern.compile(UrlConstant.PAYMENT_RESPONSE_REGEX);
                Matcher matcher = payment.matcher(url);
                if(matcher.matches()){
                    returnOrderId();
                    return true;
                }
                return false;
            }
        });
    }


    public void returnOrderId() {

        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.ORDER_ID, orderId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @SuppressWarnings("deprecation")
    public void clearCookies(Context context)
    {

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

