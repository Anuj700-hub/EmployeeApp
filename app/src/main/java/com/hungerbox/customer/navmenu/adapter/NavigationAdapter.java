package com.hungerbox.customer.navmenu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
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
import com.hungerbox.customer.navmenu.DrawerOpenCloseListener;
import com.hungerbox.customer.navmenu.activity.BookMarkActivity;
import com.hungerbox.customer.navmenu.activity.HistoryActivity;
import com.hungerbox.customer.navmenu.activity.Link;
import com.hungerbox.customer.navmenu.adapter.viewholder.NavigationDrawerItemViwHolder;
import com.hungerbox.customer.order.activity.AddGuestActivity;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.GuestOrderActivity;
import com.hungerbox.customer.order.activity.HelpActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.pockets.PocketsBaseActivity;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by peeyush on 22/6/16.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationDrawerItemViwHolder> {

    Activity activity;
    LayoutInflater layoutInflater;
    DrawerOpenCloseListener drawerOpenCloseListener;
    public ArrayList<NavigationItem> navigationItems = new ArrayList<>();
    Config config;

    public NavigationAdapter(Activity activty, DrawerOpenCloseListener drawerOpenCloseListener, Config config) {
        this.activity = activty;
        this.drawerOpenCloseListener = drawerOpenCloseListener;
        layoutInflater = LayoutInflater.from(activty);
        this.config = config;
        populateNaviagtionDrawer();
    }

    private void populateNaviagtionDrawer() {
        List<NavItemModel> navItemModels = config.getNavbars();
        Collections.sort(navItemModels);
        for (int i = 0; i < config.getNavbars().size(); i++) {

            NavigationItem nav = getnavigationItemModel(navItemModels.get(i), i);
            if(nav != null){
                navigationItems.add(nav);
            }
        }
    }

    @Override
    public NavigationDrawerItemViwHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NavigationDrawerItemViwHolder(layoutInflater.inflate(R.layout.navigation_drawer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NavigationDrawerItemViwHolder holder, final int position) {
        final NavigationItem navigationItem = navigationItems.get(position);

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
                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getNav_item_name(),navigationItem.getText());
                    map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID,0));
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getNav_item_click(),map,activity);
                }catch (Exception e){
                    e.printStackTrace();
                }

                drawerOpenCloseListener.closeDrawer();
                navigateTo(navigationItem);
            }
        });

        if (navigationItem.isNew() != null && !navigationItem.isNew().trim().equals("")) {
            holder.ivNewIndicator.setVisibility(View.VISIBLE);
            Typeface customFont = FontCache.getTypeface("comic.ttf",activity);
            holder.ivNewIndicator.setTypeface(customFont);
            holder.ivNewIndicator.setText(navigationItem.isNew());
        }
        else{
            holder.ivNewIndicator.setVisibility(View.GONE);
        }
    }


    private void navigateTo(NavigationItem navigationItem) {
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
                        .setLocalImage(R.drawable.account);
            case ApplicationConstants.ORDER_FOOD:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.orderfood);
            case ApplicationConstants.HEALTH_TRACKER:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.healthtracker);
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
                        .setLocalImage(R.drawable.history);
            case ApplicationConstants.BOOKMARK:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.bookmark);
            case ApplicationConstants.CAMPAIGN:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.contest_1);
            case ApplicationConstants.BOOK_EVENTS:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.bookevents);
            case ApplicationConstants.PAYMENTS:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.payment);
            case ApplicationConstants.POCKETS:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.payments);
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
                        .setLocalImage(R.drawable.logout);
            case ApplicationConstants.SIMPL:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.simpl);

            case ApplicationConstants.GUEST_ADD:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setTinted(true)
                        .setTintColor(R.color.warm_grey)
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_group_add_black_24dp);
            case ApplicationConstants.GUEST_ORDER:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setTinted(true)
                        .setTintColor(R.color.warm_grey)
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.ic_restaurant_black_24dp);
            case ApplicationConstants.REDEEM_POINTS:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setTinted(true)
                        .setTintColor(R.color.warm_grey)
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.cricket_ball);

            case ApplicationConstants.HELP:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setNew(navItem.isNew())
                        .setLocalImage(R.drawable.help);

            case ApplicationConstants.LINK:
                return new NavigationItem().setId(position)
                        .setKey(navItem.getKey())
                        .setText(navItem.getName())
                        .setLocalImage(R.drawable.ic_web)
                        .setNew(navItem.isNew())
                        .setUrl(navItem.getUrl());
        }


        return null;
    }
}
