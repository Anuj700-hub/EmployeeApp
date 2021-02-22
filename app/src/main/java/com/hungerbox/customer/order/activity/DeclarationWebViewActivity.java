package com.hungerbox.customer.order.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;

public class DeclarationWebViewActivity extends AppCompatActivity {

    TextView header;
    ImageView ivBack;
    WebView webView;
    String url;

    ProgressBar pbLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declaration_web_view);


        ivBack = findViewById(R.id.iv_back);
        header = findViewById(R.id.header);
        webView = findViewById(R.id.wv_lowes);
        pbLoader = findViewById(R.id.pb_loader);

        header.setText("Declaration Form");
        url = getIntent().getStringExtra("url");

        ivBack.setOnClickListener(v -> finish());
        initWebView();


    }

    private void initWebView(){

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        settings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.addJavascriptInterface(new WebViewInterFace(this), "Android");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, getHeadersForDeclarationWebView());
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 95) {
                    pbLoader.setVisibility(View.GONE);
                }
            }
        });

        webView.loadUrl(url,getHeadersForDeclarationWebView());
    }

    class WebViewInterFace {
        Context mContext;

        public WebViewInterFace(DeclarationWebViewActivity baseActivity) {
            this.mContext = baseActivity;
        }

        @JavascriptInterface
        public void CloseWebViewHandler() {
            finish();
        }
    }

    private HashMap<String,String> getHeadersForDeclarationWebView(){
        HashMap<String,String> headerMap = new HashMap();
        headerMap.put("Authorization","Bearer "+ SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN,""));
        return headerMap;
    }

}
