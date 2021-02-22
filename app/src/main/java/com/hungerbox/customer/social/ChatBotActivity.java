package com.hungerbox.customer.social;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.hungerbox.customer.R;

public class ChatBotActivity extends AppCompatActivity {

    WebView wvChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        ImageView ivBack = findViewById(R.id.iv_back);
        wvChat = findViewById(R.id.wv_chat_bot);

        wvChat.loadUrl("file:///android_asset/chatbot.html");

        final WebSettings settings = wvChat.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        wvChat.clearCache(true);
        wvChat.setWebChromeClient(new WebChromeClient());

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
