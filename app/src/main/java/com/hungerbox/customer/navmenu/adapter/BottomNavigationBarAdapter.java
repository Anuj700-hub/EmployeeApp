package com.hungerbox.customer.navmenu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.BluetoothViolationActivity;
import com.hungerbox.customer.bluetooth.NearbyDeviceActivity;
import com.hungerbox.customer.bluetooth.Util;
import com.hungerbox.customer.booking.EventsBaseActivity;
import com.hungerbox.customer.booking.MeetingBaseActivty;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.contest.activity.ContestActivity;
import com.hungerbox.customer.health.HealthHomeActivity;
import com.hungerbox.customer.marketing.RedeemPointsActivity;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.HbEvent;
import com.hungerbox.customer.model.NavItemModel;
import com.hungerbox.customer.model.NavigationItem;
import com.hungerbox.customer.mvvm.view.MyAccountActivity;
import com.hungerbox.customer.navmenu.UrlNavigationHandler;
import com.hungerbox.customer.navmenu.activity.AboutUsActivity;
import com.hungerbox.customer.navmenu.activity.BookMarkActivity;
import com.hungerbox.customer.navmenu.activity.HistoryActivity;
import com.hungerbox.customer.navmenu.activity.Link;
import com.hungerbox.customer.navmenu.activity.MoreNavigation;
import com.hungerbox.customer.occupancy.OccupancyActivity;
import com.hungerbox.customer.order.activity.AddGuestActivity;
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.GuestOrderActivity;
import com.hungerbox.customer.order.activity.HelpActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.order.adapter.BottomNavigationItemView;
import com.hungerbox.customer.pockets.PocketsBaseActivity;
import com.hungerbox.customer.spaceBooking.SpaceBookingDashboard;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.tasks.VendorEventStoreTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class BottomNavigationBarAdapter {
    Activity activity;
    Config config;
    LinearLayout bottomNavBarLayout;
    ArrayList<NavigationItem> navigationItems;
    public List<NavigationItem> navItemsNormal;
    public List<NavigationItem> navItemsMore;
    public GlobalActivity.MoreClick moreClickListener;

    public BottomNavigationBarAdapter(Activity activity, LinearLayout bottomNavBarLayout) {
        this.activity = activity;
        this.config = AppUtils.getConfig(activity);
        this.bottomNavBarLayout = bottomNavBarLayout;
        init();
    }

    public BottomNavigationBarAdapter(Activity activity, LinearLayout bottomNavBarLayout, GlobalActivity.MoreClick moreClickListener) {
        this.activity = activity;
        this.config = AppUtils.getConfig(activity);
        this.bottomNavBarLayout = bottomNavBarLayout;
        this.moreClickListener = moreClickListener;
        init();
    }

    private void init(){
        navigationItems = new ArrayList<>();
        navItemsNormal = new ArrayList<>();
        navItemsMore = new ArrayList<>();
        int maxLength = 5;
        List<NavItemModel> navItemModels = new ArrayList<>();

        for (NavItemModel navItemModel :config.getBottombars() ){

            switch (navItemModel.getKey()) {
                case ApplicationConstants.HOME:
                case ApplicationConstants.CART:
                case Util.BT_DEVICES:
                case Util.VIOLATION_SUMMARY:
                case ApplicationConstants.ORDER_FOOD:
                case ApplicationConstants.HEALTH_TRACKER:
                case ApplicationConstants.SHARED_ECONOMY:
                case ApplicationConstants.HISTORY:
                case ApplicationConstants.BOOKMARK:
                case ApplicationConstants.CAMPAIGN:
                case ApplicationConstants.BOOK_EVENTS:
                case ApplicationConstants.PAYMENTS:
                case ApplicationConstants.POCKETS:
                case ApplicationConstants.MY_ACCOUNT:
                case ApplicationConstants.LOGOUT:
                case ApplicationConstants.SIMPL:
                case ApplicationConstants.GUEST_ADD:
                case ApplicationConstants.GUEST_ORDER:
                case ApplicationConstants.REDEEM_POINTS:
                case ApplicationConstants.HELP:
                case ApplicationConstants.LINK:
                case ApplicationConstants.DEEPLINK:
                case ApplicationConstants.ABOUT_US:
                case ApplicationConstants.CONTACT_US:
                case ApplicationConstants.FAQ:
                case ApplicationConstants.OCCUPANCY:
                case ApplicationConstants.TAndC:
                case ApplicationConstants.SPACE_MANAGEMENT:
                    if(navItemModel.getIsMore()){
                        maxLength = 4;
                    }
                    navItemModels.add(navItemModel);

            }
        }

        //Collections.sort(navItemModels);
        for (int i = 0; i < navItemModels.size(); i++) {
            NavItemModel model = navItemModels.get(i);
            NavigationItem nav = getnavigationItemModel(model, i);
            if(nav != null){
                navigationItems.add(nav);
                if (model.getIsMore() || navItemsNormal.size()>=maxLength){
                    navItemsMore.add(nav);
                } else{
                    navItemsNormal.add(nav);
                }
            }

        }

        for (int i=0; i<navItemsNormal.size(); i++){
            addNavItems(navItemsNormal.get(i));
        }


        if(AppUtils.getConfig(activity).isShow_my_account() || navItemsMore.size() > 0){
            addNavItems(new NavigationItem().setId(5)
                    .setKey("More")
                    .setText("More")
                    .setNew("")
                    .setLocalImage(R.drawable.more_icon_selector));
        }
    }

    public void addNavItems(NavigationItem navigationItem){
        BottomNavigationItemView bottomNavigationItemView = new BottomNavigationItemView(activity,navigationItem.getKey());
        if (navigationItem.getIcon()!=null && !navigationItem.getIcon().trim().equalsIgnoreCase("")){
            ImageHandling.loadRemoteImage(navigationItem.getIcon(),bottomNavigationItemView.getLogoImageView(),navigationItem.getLocalImage(),navigationItem.getLocalImage(),activity);
        } else {
            bottomNavigationItemView.setLogo(navigationItem.getLocalImage());
        }
        bottomNavigationItemView.setName(navigationItem.getText());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        layoutParams.setMargins(10,5,10,5);
        bottomNavigationItemView.setLayoutParams(layoutParams);
        bottomNavigationItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectAll();
                navigateTo(navigationItem);
                bottomNavigationItemView.setSelected(true);
                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getNav_item_name(),navigationItem.getText());
                    map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID,0));
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getNav_item_click(),map,activity);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        bottomNavBarLayout.addView(bottomNavigationItemView);
    }

    private void unSelectAll(){
        for (int i = 0; i < bottomNavBarLayout.getChildCount(); i++) {
            BottomNavigationItemView v = (BottomNavigationItemView) ((GlobalActivity)activity).bottomNavBar.getChildAt(i);
            v.setSelected(false);
        }
    }

    public NavigationItem getnavigationItemModel(NavItemModel navItem, int position) {

        switch (navItem.getKey()) {
            case ApplicationConstants.HOME:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.home_icon_selector);
            case ApplicationConstants.CART:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_cart_unselected);
            case Util.BT_DEVICES:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.bt_icon);
            case Util.VIOLATION_SUMMARY:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_violation_unselected);
            case ApplicationConstants.ORDER_FOOD:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_order_food_unselected);
//                        .setLocalImage(R.drawable.orderfood);
            case ApplicationConstants.HEALTH_TRACKER:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_befit_unselected);
//                        .setLocalImage(R.drawable.healthtracker);
            case ApplicationConstants.SHARED_ECONOMY:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.sharedeconomy);
            case ApplicationConstants.HISTORY:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_history_unselected);
            case ApplicationConstants.BOOKMARK:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_bookmark_unselected);
            case ApplicationConstants.CAMPAIGN:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_contest_unselected);
            case ApplicationConstants.BOOK_EVENTS:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_book_event_unselected);
            case ApplicationConstants.PAYMENTS:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_payment_unselected);
            case ApplicationConstants.POCKETS:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_payment_unselected);
            case ApplicationConstants.MY_ACCOUNT:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.account);
            case ApplicationConstants.LOGOUT:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_logout_unselected);
            case ApplicationConstants.SIMPL:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_simpl_unselected);

            case ApplicationConstants.GUEST_ADD:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setTinted(true)
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_add_guest_unselected);
//                        .setLocalImage(R.drawable.ic_group_add_black_24dp);
            case ApplicationConstants.GUEST_ORDER:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setTinted(true)
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_guest_order_unselected);
//                        .setLocalImage(R.drawable.ic_restaurant_black_24dp);
            case ApplicationConstants.REDEEM_POINTS:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setTinted(true)
                        .setTintColor(R.color.warm_grey)
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_redeem_unselected);

            case ApplicationConstants.HELP:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_help_unselected);

            case ApplicationConstants.LINK:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setLocalImage(R.drawable.ic_link_unselected)
                        .setNew(navItem.isNew())
                        .setUrl(navItem.getUrl());

            case ApplicationConstants.DEEPLINK:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setLocalImage(R.drawable.maskicon)
                        .setNew(navItem.isNew())
                        .setUrl(navItem.getUrl())
                        .setIcon(navItem.getIcon());

            case ApplicationConstants.ABOUT_US:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_about_us_unselected);
            case ApplicationConstants.CONTACT_US:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_contact_us_unselected);
            case ApplicationConstants.FAQ:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_faq_unselected);
            case ApplicationConstants.OCCUPANCY:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.occupancy_icon_selector);
            case ApplicationConstants.TAndC:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_tnc_unselected);
            case ApplicationConstants.SPACE_MANAGEMENT:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.booking_icon_selector);
        }


        return null;
    }

    public void navigateTo(NavigationItem navigationItem) {

        if (navigationItem.getKey().equalsIgnoreCase(ApplicationConstants.MORE)){
            if (moreClickListener!=null){
                moreClickListener.onClick();
                return;
            }
        }
        if (navigationItem.getKey().equalsIgnoreCase(ApplicationConstants.HOME) && activity instanceof GlobalActivity){
            ((GlobalActivity)activity).sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        Intent intent = null;
        if (navigationItem.getKey() == null || navigationItem.getKey().isEmpty()) {
            return;
        }

        try {
            JSONObject mixpanelValues = new JSONObject();
            mixpanelValues.put(EventUtil.MixpanelEvent.NavigationItemClick.ITEM_NAME, navigationItem.getKey());
            if (activity instanceof EventsBaseActivity) {
                mixpanelValues.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Events");
            } else {
                mixpanelValues.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
            }

            HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.NavigationItemClick.EVENT_NAME, mixpanelValues);


            Bundle bundle = new Bundle();
            if (activity instanceof EventsBaseActivity) {
                bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Events");
                bundle.putString(EventUtil.MixpanelEvent.NavigationItemClick.ITEM_NAME, navigationItem.getKey());
            } else {
                bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                bundle.putString(EventUtil.MixpanelEvent.NavigationItemClick.ITEM_NAME, navigationItem.getKey());
            }
            EventUtil.FbEventLog(activity, EventUtil.MixpanelEvent.NavigationItemClick.EVENT_NAME, bundle);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        switch (navigationItem.getKey()) {
            case ApplicationConstants.HOME:
                addEvent(EventUtil.NAV_HOME);
                intent = AppUtils.getHomeNavigationIntent(activity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            case ApplicationConstants.CART:
                intent = new Intent(activity, BookmarkPaymentActivity.class);
                break;
            case Util.BT_DEVICES:
                intent = new Intent(activity, NearbyDeviceActivity.class);
                break;
            case Util.VIOLATION_SUMMARY:
                intent = new Intent(activity, BluetoothViolationActivity.class);
                break;
            case ApplicationConstants.MORE:
                intent = new Intent(activity, MoreNavigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            case ApplicationConstants.ORDER_FOOD:
                addEvent(EventUtil.NAV_ORDER_LIST);
                intent = new Intent(activity, GlobalActivity.class);
                break;
            case ApplicationConstants.HEALTH_TRACKER:
                addEvent(EventUtil.NAV_RECAHRGE);
                intent = new Intent(activity, HealthHomeActivity.class);
                break;
            case ApplicationConstants.SHARED_ECONOMY:
                addEvent(EventUtil.NAV_MY_PROFILE);
                intent = new Intent(activity, MeetingBaseActivty.class);
                break;
            case ApplicationConstants.BOOK_EVENTS:
                addEvent(EventUtil.NAV_SETTINGS);
                intent = new Intent(activity, EventsBaseActivity.class);
                break;
            case ApplicationConstants.HISTORY:
                addEvent(EventUtil.NAV_SETTINGS);
                intent = new Intent(activity, HistoryActivity.class);
                break;
            case ApplicationConstants.BOOKMARK:
                addEvent(EventUtil.NAV_SETTINGS);
                intent = new Intent(activity, BookMarkActivity.class);
                intent.putExtra(ApplicationConstants.EXPRESS_CHECKOUT_ACTION,ApplicationConstants.BOOKMARK_FROM_NAVBAR);
                intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),"Nav");
                break;
            case ApplicationConstants.CAMPAIGN:
                addEvent(EventUtil.NAV_SETTINGS);
                intent = new Intent(activity, ContestActivity.class);
                intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),"Nav");
                break;
            case ApplicationConstants.PAYMENTS:
                addEvent(EventUtil.NAV_SETTINGS);
                long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
                intent = new Intent(activity, PaymentActivity.class);
                intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Navigation Drawer");
                intent.putExtra(ApplicationConstants.FROM_NAVBAR, true);
                intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);
                break;
            case ApplicationConstants.POCKETS:
                intent = new Intent(activity, PocketsBaseActivity.class);
                break;
            case ApplicationConstants.MY_ACCOUNT:
                addEvent(EventUtil.NAV_SETTINGS);
                intent = new Intent(activity, MyAccountActivity.class);
                break;
            case ApplicationConstants.LOGOUT:
                addEvent(EventUtil.NAV_LOGOUT);
                if (activity != null && GlobalActivity.class.isInstance(activity))
                    ((GlobalActivity) activity).showLogoutPopUp();
                break;
            case ApplicationConstants.GUEST_ADD:
                intent = new Intent(activity, AddGuestActivity.class);
                break;
            case ApplicationConstants.GUEST_ORDER:
                intent = new Intent(activity, GuestOrderActivity.class);
                break;
            case ApplicationConstants.REDEEM_POINTS:
                EventUtil.FbEventLog(activity, EventUtil.NAV_BAR_REDEEM_CLICK, "RedeemPointsActivity");
                intent = new Intent(activity, RedeemPointsActivity.class);
                break;
            case ApplicationConstants.HELP:
                intent = new Intent(activity, HelpActivity.class);
                intent.putExtra("Header", navigationItem.getText());
                break;
            case ApplicationConstants.LINK:
                intent = new Intent(activity, Link.class);
                intent.putExtra("url", navigationItem.getUrl());
                intent.putExtra("title", navigationItem.getText());
                break;
            case ApplicationConstants.DEEPLINK:
                UrlNavigationHandler urlNavigationHandler = new UrlNavigationHandler(activity);
                if (navigationItem.getUrl()!=null && !navigationItem.getUrl().trim().equalsIgnoreCase("")) {
                    intent = urlNavigationHandler.getUrlNavPath(navigationItem.getUrl());
                    intent.putExtra("url", navigationItem.getUrl());
                    intent.putExtra("title", navigationItem.getText());
                }
                break;
            case ApplicationConstants.OCCUPANCY:
                intent = new Intent(activity, OccupancyActivity.class);
                break;
            case ApplicationConstants.ABOUT_US:
            case ApplicationConstants.CONTACT_US:
            case ApplicationConstants.FAQ:
            case ApplicationConstants.TAndC:
                addEvent(EventUtil.NAV_SETTINGS);
                intent = new Intent(activity, AboutUsActivity.class);
                intent.putExtra("key", navigationItem.getKey());
                intent.putExtra("label", navigationItem.getText());
                break;
            case ApplicationConstants.SPACE_MANAGEMENT:
                intent = new Intent(activity, SpaceBookingDashboard.class);
                break;
        }

        if (intent != null) {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }


        switch (navigationItem.getKey()) {
            case ApplicationConstants.HOME:
                break;
            case ApplicationConstants.ORDER_FOOD:
                activity.finish();
                break;
        }

    }

    private void addEvent(String event) {
        AppEvent appEvent = new AppEvent();
        HbEvent hbEvent = new HbEvent();
        hbEvent.updateTime(Calendar.getInstance().getTimeInMillis());
        appEvent.setVendorEventRegistrable(hbEvent).setEventName(event);
        EventUtil.updateCafeEventData(activity, appEvent, hbEvent);
        VendorEventStoreTask.addEventToQueue(activity, appEvent);
    }
}
