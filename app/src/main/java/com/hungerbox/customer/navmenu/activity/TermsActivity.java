package com.hungerbox.customer.navmenu.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;

public class TermsActivity extends ParentActivity {

    WebView wvContent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        wvContent = findViewById(R.id.wv_content);
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.loadUrl(UrlConstant.TERMS);
        toolbar = findViewById(R.id.tb_global);
        TextView tvTitle;
        tvTitle = findViewById(R.id.tv_toolbar_title);
        tvTitle.setText("Terms and Condition");
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
