package com.hungerbox.customer.navmenu.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.R;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

public class AboutUsActivity extends ParentActivity {

    WebView wvNavItem;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        wvNavItem = findViewById(R.id.wv_about_us);
        wvNavItem.getSettings().setJavaScriptEnabled(true);

        Bundle bundle = getIntent().getExtras();
        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        tvTitle.setText(bundle.getString("label"));

        String url = "https://"+ AppUtils.getConfig(this).getSubDomain() +".hungerbox.com/views/";

        switch(bundle.getString("key")){
            case ApplicationConstants.ABOUT_US:
                url = url + "about-us.html";
                break;
            case ApplicationConstants.CONTACT_US:
                url = url + "contact-us.html";
                break;
            case ApplicationConstants.FAQ:
                url = url + "faqs.html";
                break;
            case ApplicationConstants.TAndC:
                url = url + "term.html";
                break;
        }
        wvNavItem.loadUrl(url);
        toolbar = findViewById(R.id.tb_global);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
