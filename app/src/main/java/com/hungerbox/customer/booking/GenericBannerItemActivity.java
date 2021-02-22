package com.hungerbox.customer.booking;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;

public class GenericBannerItemActivity extends ParentActivity {

    WebView wvGeneric;
    ProgressBar pbLoader;
    String url;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_banner_item);

        url = getIntent().getStringExtra(ApplicationConstants.BANNER_URL);
        if (!url.startsWith("http")) {
            url = UrlConstant.BASE_URL + url;
        }
        wvGeneric = findViewById(R.id.wv_generic);
        toolbar = findViewById(R.id.tb_global);

        findViewById(R.id.iv_search).setVisibility(View.GONE);
        findViewById(R.id.iv_ocassion).setVisibility(View.GONE);
        findViewById(R.id.tv_occasion).setVisibility(View.GONE);

        TextView tvLoaction = findViewById(R.id.tv_location);
        pbLoader = findViewById(R.id.pb_loader);

        tvLoaction.setVisibility(View.INVISIBLE);

        String token = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");

        final WebSettings settings = wvGeneric.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getCacheDir().getPath());
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
        });

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        String cookieString = "webAccessToken=" + token;
        cookieManager.setCookie(UrlConstant.BASE_URL, cookieString);
        CookieSyncManager.getInstance().sync();

        Map<String, String> abc = new HashMap<String, String>();
        abc.put("Cookie", cookieString);

        pbLoader.setVisibility(View.VISIBLE);
        wvGeneric.loadUrl(url);

        TextView tvTitle;
        tvTitle = findViewById(R.id.tv_toolbar_title);
        tvTitle.setText("");
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
        String function = String.format("javascript:updateWebAccessToken('%s', '%s')", token, url);
        wvGeneric.loadUrl(function);
    }
}
