package com.hungerbox.customer.navmenu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.BluetoothViolationActivity;
import com.hungerbox.customer.bluetooth.NearbyDeviceActivity;
import com.hungerbox.customer.bluetooth.Util;
import com.hungerbox.customer.booking.EventsBaseActivity;
import com.hungerbox.customer.booking.MeetingBaseActivty;
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
import com.hungerbox.customer.navmenu.adapter.viewholder.NavigationDrawerItemViwHolder;
import com.hungerbox.customer.occupancy.OccupancyActivity;
import com.hungerbox.customer.order.activity.AddGuestActivity;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.GuestOrderActivity;
import com.hungerbox.customer.order.activity.HelpActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.pockets.PocketsBaseActivity;
import com.hungerbox.customer.spaceBooking.SpaceBookingDashboard;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.FontCache;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.tasks.VendorEventStoreTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;



public class MoreNavigationAdapter extends RecyclerView.Adapter<NavigationDrawerItemViwHolder> {

    Activity activity;
    LayoutInflater layoutInflater;
    public ArrayList<NavigationItem> navigationItems = new ArrayList<>();

    public MoreNavigationAdapter(Activity activity, ArrayList<NavigationItem> navigationItems) {
        this.activity = activity;
        this.navigationItems = navigationItems;
        layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public NavigationDrawerItemViwHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NavigationDrawerItemViwHolder(layoutInflater.inflate(R.layout.navigation_drawer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NavigationDrawerItemViwHolder holder, final int position) {
        final NavigationItem navigationItem = navigationItems.get(position);
        if (navigationItem!=null) {

            holder.tvNavItem.setText(Html.fromHtml(navigationItem.getText()));
            if (navigationItem.getLocalImage() != 0) {
                Drawable drawable = ContextCompat.getDrawable(activity, navigationItem.getLocalImage());
                if (navigationItem.isTinted()) {
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(activity, navigationItem.getTintColor()));
                    drawable.mutate();
                }

                holder.ivNavIcon.setImageDrawable(drawable);

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventUtil.FbEventLog(activity, EventUtil.NAV_BAR_CLICK, navigationItem.getText());
                    try {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getNav_item_name(), navigationItem.getText());
                        map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID,0));
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getNav_item_click(), map, activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    navigateTo(navigationItem);
                }
            });

            if (navigationItem.isNew() != null && !navigationItem.isNew().trim().equals("")) {
                holder.ivNewIndicator.setVisibility(View.VISIBLE);
                Typeface customFont = FontCache.getTypeface("comic.ttf", activity);
                holder.ivNewIndicator.setTypeface(customFont);
                holder.ivNewIndicator.setText(navigationItem.isNew());
            } else {
                holder.ivNewIndicator.setVisibility(View.GONE);
            }
        }
    }


    private void navigateTo(NavigationItem navigationItem) {

        if (activity instanceof GlobalActivity){
            ((GlobalActivity)activity).toggleBottomSheet();
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
            case ApplicationConstants.ORDER_FOOD:
                addEvent(EventUtil.NAV_ORDER_LIST);
                intent = new Intent(activity, GlobalActivity.class);
                break;
            case ApplicationConstants.HEALTH_TRACKER:
                addEvent(EventUtil.NAV_RECAHRGE);
                intent = new Intent(activity, HealthHomeActivity.class);
                break;
            case Util.BT_DEVICES:
                intent = new Intent(activity, NearbyDeviceActivity.class);
                break;
            case Util.VIOLATION_SUMMARY:
                intent = new Intent(activity, BluetoothViolationActivity.class);
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
                intent = urlNavigationHandler.getUrlNavPath(navigationItem.getUrl());
                intent.putExtra("url", navigationItem.getUrl());
                intent.putExtra("title", navigationItem.getText());
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

        if (intent != null)
            activity.startActivity(intent);


        switch (navigationItem.getKey()) {
            case ApplicationConstants.HOME:
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

    @Override
    public int getItemCount() {
        return navigationItems.size();
    }

    public NavigationItem getnavigationItemModel(NavItemModel navItem, int position) {

        switch (navItem.getKey()) {
            case ApplicationConstants.HOME:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_home_unselected);
            case ApplicationConstants.MORE:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_more_unselected);
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
        }


        return null;
    }
}

