package com.hungerbox.customer.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.hungerbox.customer.R
import kotlinx.android.synthetic.main.activity_chatbot_screen.*
import java.util.*

class ChatBoatScreen : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot_screen)
        SharedPrefUtil.putBoolean(ApplicationConstants.SHOULD_REFRESH_FROM_CHAT, true)

//        try {
//            var source = intent.getStringExtra(CleverTapEvent.PropertiesNames.source)
//            if (source == null){
//                source = "NA"
//            }
//
//            val map = HashMap<String, Any>()
//            map.put(CleverTapEvent.PropertiesNames.source,source)
//            map.put(CleverTapEvent.PropertiesNames.item_name,"Order Cancellation")
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.chat,map,applicationContext)
//
//        }catch ( e : Exception){
//            e.printStackTrace()
//        }


        header.text = intent.getStringExtra("header")
        iv_back.setOnClickListener { finish() }
        initWebView()
    }


    private fun initWebView() {
        val settings = wv_chatbot.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = false
        settings.domStorageEnabled = true
        settings.allowFileAccess = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        settings.pluginState = WebSettings.PluginState.ON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wv_chatbot.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            wv_chatbot.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        wv_chatbot.webChromeClient = (object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress > 95) {
                    pb_loader.setVisibility(View.GONE)
                }
            }
        })
        var url = intent.getStringExtra("url")
        wv_chatbot.loadUrl(url)
    }
}