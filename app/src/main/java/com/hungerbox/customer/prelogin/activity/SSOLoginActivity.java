package com.hungerbox.customer.prelogin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

public class SSOLoginActivity extends ParentActivity {

    private WebView wvSSOLogin;
    private ProgressBar pbLoader;
    private long companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssologin);
        wvSSOLogin = findViewById(R.id.wv_sso_login);
        pbLoader = findViewById(R.id.pb_loader);

        companyId = getIntent().getLongExtra(ApplicationConstants.COMPANY_ID, 0);

        initWebView();
        getSSOUrl(companyId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void getSSOUrl(long companyId) {
        pbLoader.setVisibility(View.VISIBLE);
        String url = UrlConstant.SSO_LOGIN.replace("{cid}", "" + companyId);
        loadUrl(url);
    }

    private void loadUrl(String url) {
        pbLoader.setVisibility(View.VISIBLE);
        wvSSOLogin.loadUrl(url);
    }

    private void initWebView() {
        final WebSettings settings = wvSSOLogin.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wvSSOLogin.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            wvSSOLogin.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        wvSSOLogin.clearCache(true);
        clearCookies(this);
        wvSSOLogin.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 95) {
                    pbLoader.setVisibility(View.GONE);
                }
            }
        });
        wvSSOLogin.addJavascriptInterface(new WebViewInterFace(this), "Android");
        wvSSOLogin.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }


    public void performLoginBack(String token) {

        SharedPrefUtil.putString(ApplicationConstants.PREF_AUTH_TOKEN, token);
        Intent intent = new Intent();
        intent.putExtra(ApplicationConstants.PREF_AUTH_TOKEN, token);
        setResult(RESULT_OK, intent);
        finish();
    }


    class WebViewInterFace {
        Context mContext;

        public WebViewInterFace(SSOLoginActivity baseActivity) {
            this.mContext = baseActivity;
        }

        @JavascriptInterface
        public void openAppWithToken(String token) {
            performLoginBack(token);
        }
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
