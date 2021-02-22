package com.hungerbox.customer.marketing;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;

public class OfferActivity extends ParentActivity {
    WebView wvGeneric;
    ProgressBar pbLoader;
    String finalUrl;
    private Toolbar toolbar;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_banner_item);

        url = getIntent().getStringExtra(ApplicationConstants.BANNER_URL);
//        if(!url.startsWith("http")){
//            url = UrlConstant.BASE_URL+url;
//        }
        wvGeneric = findViewById(R.id.wv_generic);
        toolbar = findViewById(R.id.tb_global);
        TextView tvLoaction = findViewById(R.id.tv_location);
        pbLoader = findViewById(R.id.pb_loader);
        String token = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
        tvLoaction.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        final WebSettings settings = wvGeneric.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(OfferActivity.this.getCacheDir().getPath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wvGeneric.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            wvGeneric.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        wvGeneric.clearCache(true);
        wvGeneric.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }
        });
        wvGeneric.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String currentUrl) {
                super.onPageFinished(view, url);
                pbLoader.setVisibility(View.GONE);
                if (currentUrl.contains("login") || currentUrl.contains("get-start")) {
                    loadAccessToken();
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.cancel();
                if (error != null && error.toString().length() > 0) {
                    AppUtils.showToast(error.toString(), true, 0);
                }
            }
        });

//        wvGeneric.addJavascriptInterface(new BookingFragment.WebViewInterFace((EventsBaseActivity) getActivity()),"Android");

        CookieSyncManager.createInstance(OfferActivity.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        String cookieString = "webAccessToken=" + token;
        cookieManager.setCookie(UrlConstant.BASE_URL, cookieString);
        CookieSyncManager.getInstance().sync();

        Map<String, String> abc = new HashMap<String, String>();
        abc.put("Cookie", cookieString);
        String loadUrl;
        pbLoader.setVisibility(View.VISIBLE);


        String subdomain = SharedPrefUtil.getString(ApplicationConstants.COMPANY_SUBDOMAIN, "");
        if (subdomain.isEmpty() || subdomain.equalsIgnoreCase("abb")) {
            subdomain = "paladion";
        }

        if (url != null && !url.isEmpty()) {
            loadUrl = "https://" + subdomain + ".hungerbox.com/#/" + url;
//            EventsBaseActivity eventsBaseActivity = (EventsBaseActivity) getActivity();
//            eventsBaseActivity.toggleToolbar(false);
        } else {
            loadUrl = UrlConstant.EVENTS_WEB_VIEW;
            loadUrl = loadUrl.replace("{1}", subdomain);
        }
        if (subdomain.equalsIgnoreCase("qbase")) {
            loadUrl = loadUrl.replace("#/", "ios/#/");
            loadUrl = loadUrl.replace("https", "http");
        }
        finalUrl = loadUrl;
        wvGeneric.loadUrl(loadUrl,
                abc);

        TextView tvTitle;
        tvTitle = findViewById(R.id.tv_toolbar_title);
        ImageView ivOccasion = findViewById(R.id.iv_ocassion);
        ImageView ivSearch = findViewById(R.id.iv_search);
        ivOccasion.setVisibility(View.GONE);
        ivSearch.setVisibility(View.GONE);
        tvTitle.setText(getIntent().getStringExtra("Title"));
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadAccessToken() {
        String token = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
        String function = String.format("javascript:updateWebAccessToken('%s', '%s')", token, finalUrl);
        wvGeneric.loadUrl(function);
    }

}
