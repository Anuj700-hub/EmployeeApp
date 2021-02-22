package com.hungerbox.customer.prelogin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;

public class PreLaunchActivity extends ParentActivity {

    private TextView tvPreLaunchText;
    private Button btnExploreApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_launch);
        tvPreLaunchText = findViewById(R.id.tv_pre_launch_text);
        btnExploreApp = findViewById(R.id.btn_explore_app);
        clickListeners();
    }

    private void clickListeners() {
        btnExploreApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigateToNextScreen();
            }
        });
    }

    private void NavigateToNextScreen() {
        Intent intent = AppUtils.getHomeNavigationIntent(this);
        startActivity(intent);
        finish();
    }
}
