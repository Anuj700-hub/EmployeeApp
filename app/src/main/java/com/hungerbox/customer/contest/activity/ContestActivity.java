package com.hungerbox.customer.contest.activity;

import android.content.Intent;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.contest.adapter.ContestPagerAdapter;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;

import java.util.ArrayList;

public class ContestActivity extends ParentActivity {

    private ImageView ivBack;
    private ContestPagerAdapter contestPagerAdapter;
    private TabLayout contestLayout;
    private ViewPager vpContest;
    ArrayList<String> tabs = new ArrayList<>();
    public static int STATUSSUCCESS = 1221;
    public static int REQCODEORDER = 2222;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);
        ivBack = findViewById(R.id.iv_back);
        contestLayout = findViewById(R.id.tl_contest);
        vpContest = findViewById(R.id.vp_contest_pager);

        tabs.add(ApplicationConstants.CONTEST_TITLE_CAMPAIGN);
//        tabs.add(ApplicationConstants.CONTEST_TITLE_OFFERS);
//        tabs.add(ApplicationConstants.CONTEST_TITLE_INBOX);

        String source = getIntent().getStringExtra(CleverTapEvent.PropertiesNames.getSource());
        if(source==null){
            source = "NA";
        }

        contestPagerAdapter = new ContestPagerAdapter(getSupportFragmentManager(),ContestActivity.this,tabs,source);
        vpContest.setAdapter(contestPagerAdapter);
        vpContest.setCurrentItem(0);
        vpContest.setOffscreenPageLimit(3);
        contestLayout.setupWithViewPager(vpContest);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQCODEORDER && resultCode == STATUSSUCCESS){
            onBackPressed();
            finish();
        }
    }
}
