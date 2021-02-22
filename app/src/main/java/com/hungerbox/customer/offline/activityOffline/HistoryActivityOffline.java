package com.hungerbox.customer.offline.activityOffline;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.BookingHistory;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.navmenu.fragment.HistoryFragment;
import com.hungerbox.customer.offline.adapterOffline.HistoryPagerAdapterOffline;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivityOffline extends ParentActivity implements HistoryFragment.OnListFragmentInteractionListener {

    public ImageView ivLogo, ivOcassions, ivSearch;
    ArrayList<String> tabs = new ArrayList<>();
    private Toolbar toolbar;
    private TextView tvTitle;
    private ViewPager vpHistory;
    private TabLayout historyTabLayout;
    private HistoryPagerAdapterOffline historyPagerAdapter;
    private TextView spLocation;
    private boolean fromShortcut;
    private String value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_offline);
        toolbar = findViewById(R.id.tb_history);
        setSupportActionBar(toolbar);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        vpHistory = findViewById(R.id.vp_history_pager);
        historyTabLayout = findViewById(R.id.tl_history);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("History");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fromShortcut = getIntent().getBooleanExtra("fromShortcut", false);
        value = getIntent().getStringExtra("value");


//        try {
//            if (fromShortcut) {
//                HashMap<String, Object> map = new HashMap<>();
//                if (value != null && value.equals("last_order")) {
//                    map.put(CleverTapEvent.PropertiesNames.getScreen_name(), "Order History Detail");
//                } else {
//                    map.put(CleverTapEvent.PropertiesNames.getScreen_name(), "Order History");
//                }
//                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getShortcut(), map, getApplicationContext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        tabs.add(ApplicationConstants.HISTORY_TITLE_FOOD);
        tabs.add(ApplicationConstants.HISTORY_TITLE_FOOD_OFFLINE);

        historyPagerAdapter = new HistoryPagerAdapterOffline(getSupportFragmentManager(), HistoryActivityOffline.this, tabs, value);
        vpHistory.setAdapter(historyPagerAdapter);
        vpHistory.setCurrentItem(0);
        vpHistory.setOffscreenPageLimit(3);
        historyTabLayout.setupWithViewPager(vpHistory);
        LogoutTask.updateTime();
    }

    @Override
    public void onListFragmentInteraction(BookingHistory item, String eventType, Order order) {
    }
}
