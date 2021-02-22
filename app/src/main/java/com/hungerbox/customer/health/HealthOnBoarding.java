package com.hungerbox.customer.health;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OnBoard;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.view.ViewPagerCustomDuration;

import java.util.ArrayList;

public class HealthOnBoarding extends ParentActivity {

    ArrayList<OnBoard> onBoards = new ArrayList<>();
    private Button btnNext;
    private ImageView ivBack;
    private ViewPagerCustomDuration vpOnBoarding;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_onboard);
        vpOnBoarding = findViewById(R.id.vp_onboard);
        try {
            vpOnBoarding.setScrollDurationFactor(6.0);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        ivBack = findViewById(R.id.iv_back);
        btnNext = findViewById(R.id.btn_next);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HealthOnBoarding.this, HealthDetailsActivity.class);
                intent.putExtra("isRegister", true);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        vpOnBoarding.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (handler != null && runnable != null)
                    handler.removeCallbacks(runnable);

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (vpOnBoarding.getCurrentItem() < 4)
                            vpOnBoarding.setCurrentItem(vpOnBoarding.getCurrentItem() + 1, true);
                    }
                };
                handler.postDelayed(runnable, 1500);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupOnboardingData();
    }

    private void setupOnboardingData() {
        onBoards.add(new OnBoard().setImageId(R.drawable.health_onboarding_1).setTextId(R.string.ob1).setIconId(-1).setTitle("Link Devices"));
        onBoards.add(new OnBoard().setImageId(R.drawable.health_onboarding_2).setTextId(R.string.ob1).setIconId(-1).setTitle("Track Calories"));
        onBoards.add(new OnBoard().setImageId(R.drawable.health_onboarding_3).setTextId(R.string.ob1).setIconId(-1).setTitle("Manage Goals"));
        onBoards.add(new OnBoard().setImageId(R.drawable.health_onboarding_4).setTextId(R.string.ob1).setIconId(-1).setTitle("Become A Fitter Version Of You"));
        HealthOnBoardingPagerAdapter onBoardingPagerAdapter = new HealthOnBoardingPagerAdapter(getSupportFragmentManager(), onBoards);
        vpOnBoarding.setAdapter(onBoardingPagerAdapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (vpOnBoarding.getCurrentItem() < 4)
                    vpOnBoarding.setCurrentItem(vpOnBoarding.getCurrentItem() + 1, true);
            }
        };
        handler.postDelayed(runnable, 1500);
    }

    @Override
    public void finish() {

        try {
            if (handler != null && runnable != null)
                handler.removeCallbacks(runnable);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        super.finish();
    }
}
