package com.hungerbox.customer.navmenu.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.BookingHistory;
import com.hungerbox.customer.model.NavItemModel;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.navmenu.adapter.HistoryPagerAdapter;
import com.hungerbox.customer.navmenu.fragment.HistoryFragment;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.order.fragment.FeedbackFragment;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends ParentActivity implements HistoryFragment.OnListFragmentInteractionListener {

    public ImageView ivLogo, ivOcassions, ivSearch;
    ArrayList<String> tabs = new ArrayList<>();
    private Toolbar toolbar;
    private TextView tvTitle;
    private ViewPager vpHistory;
    private TabLayout historyTabLayout;
    private HistoryPagerAdapter historyPagerAdapter;
    private TextView spLocation;
    private boolean fromShortcut;
    private String value,vendor_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_history);
        restoreFromSavedInstance(savedInstanceState);
        toolbar = findViewById(R.id.tb_history);
        setSupportActionBar(toolbar);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        vpHistory = findViewById(R.id.vp_history_pager);
        historyTabLayout = findViewById(R.id.tl_history);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
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
        vendor_type = getIntent().getStringExtra("vendor_type");


//        try{
//            if(fromShortcut){
//                HashMap<String,Object> map = new HashMap<>();
//                if(value != null && value.equals("last_order")){
//                    map.put(CleverTapEvent.PropertiesNames.getScreen_name(),"Order History Detail");
//                }else{
//                    map.put(CleverTapEvent.PropertiesNames.getScreen_name(),"Order History");
//                }
//                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getShortcut(),map,getApplicationContext());
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        Config config = AppUtils.getConfig(this);
        tabs.add(ApplicationConstants.HISTORY_TITLE_FOOD);

        ArrayList<NavItemModel> navBars = config.getNavbars();

        if (navBars.contains(new NavItemModel(ApplicationConstants.SHARED_ECONOMY, "")))
            tabs.add(ApplicationConstants.HISTORY_TITLE_SHARED_ECONOMY);

        if (navBars.contains(new NavItemModel(ApplicationConstants.BOOK_EVENTS, "")))
            tabs.add(ApplicationConstants.HISTORY_TITLE_EVENT);

        if (navBars.contains(new NavItemModel(ApplicationConstants.GUEST_ORDER, "")))
            tabs.add(ApplicationConstants.GUEST_ORDER);

        if(isOfflineOrderAvailable()){
            tabs.add(ApplicationConstants.HISTORY_TITLE_FOOD_OFFLINE);
        }

        if(config.getSpace_management()!=null){
            tabs.add(ApplicationConstants.CURRENT_BOOKING);
        }

        if (tabs.size()>1){
            historyTabLayout.setVisibility(View.VISIBLE);
        }
        else {
            historyTabLayout.setVisibility(View.GONE);
        }

        historyPagerAdapter = new HistoryPagerAdapter(getSupportFragmentManager(), HistoryActivity.this, tabs, value);
        vpHistory.setAdapter(historyPagerAdapter);
        vpHistory.setCurrentItem(0);
        vpHistory.setOffscreenPageLimit(3);
        historyTabLayout.setupWithViewPager(vpHistory);
        LogoutTask.updateTime();
    }

    private boolean isOfflineOrderAvailable(){

        try{
            DbHandler db = DbHandler.getDbHandler(this);
            List<Order> ordersList = db.getAllOrder();
            for (Order order : ordersList) {
                if ((System.currentTimeMillis() - order.getCreatedAt() * 1000) > ApplicationConstants.THIRTY_MIN_MILLIS_OFFLINE) {
                    DbHandler.getDbHandler(this).deleteOrder(order);
                }
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }



        try {
            DbHandler db = DbHandler.getDbHandler(this);
            List<OrderOffline> orderOfflines = db.getAllOrderOffline();
            ArrayList<OrderOffline> newList = new ArrayList<>();
            if(orderOfflines!=null && orderOfflines.size()>0){
                for (OrderOffline orderOffline : orderOfflines) {
                    if ((System.currentTimeMillis() - orderOffline.getCreatedAt() * 1000) > ApplicationConstants.THIRTY_MIN_MILLIS_OFFLINE) {
                        DbHandler.getDbHandler(this).deleteOrderOffline(orderOffline);
                    } else {
                        newList.add(orderOffline);
                    }
                }
                if(newList!=null && newList.size()>0){
                    return true;
                }else{
                    return false;
                }

            }else{
                return false;
            }


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public void onListFragmentInteraction(BookingHistory item, String eventType, Order order) {
        FeedbackFragment fragment;
        LogoutTask.updateTime();
        if(eventType.contains("order") || eventType.contains(ApplicationConstants.CURRENT_BOOKING)){
             fragment = FeedbackFragment.newInstance(order.getId(),"food",order.getVendorId(),order.getVendorName(),
                    order.getProductItemList(), eventType, order,new FeedbackFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onFragmentInteraction() {
                            historyPagerAdapter.updateFragments();
                        }

                         @Override
                         public void dismissPopup() {

                         }
                     });
        } else {
            fragment = FeedbackFragment.newInstance(item.bookingId, "booking", 0,"",
                    item.getName(), eventType, order,new FeedbackFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onFragmentInteraction() {
                            historyPagerAdapter.updateFragments();
                        }

                        @Override
                        public void dismissPopup() {

                        }
                    });
        }

        fragment.setCancelable(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, "feedback")
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if(fromShortcut){
            Intent intent = AppUtils.getHomeNavigationIntent(HistoryActivity.this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        super.onBackPressed();
    }
}
