package com.hungerbox.customer.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.SharedPrefUtil;

public class BluetoothInformationActivity extends AppCompatActivity {

    TextView tvInfoText;
    Button btOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_bluetooth_information);

        tvInfoText = findViewById(R.id.tv_information);
        btOk = findViewById(R.id.bt_ok);

        if (SharedPrefUtil.getString(Util.BT_INFO_TEXT) != null && !SharedPrefUtil.getString(Util.BT_INFO_TEXT).equals("")) {
            String btInfoText = SharedPrefUtil.getString(Util.BT_INFO_TEXT);
            tvInfoText.setText(Html.fromHtml(btInfoText));
        } else {
            finish();
        }

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //do nothing -- let the user press ok button
    }
}

