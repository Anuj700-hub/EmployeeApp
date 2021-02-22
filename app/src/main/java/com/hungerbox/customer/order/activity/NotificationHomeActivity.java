package com.hungerbox.customer.order.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.booking.GenericBannerItemActivity;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.contest.activity.ContestActivity;
import com.hungerbox.customer.contest.activity.ContestDetailActivity;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.OcassionReposne;
import com.hungerbox.customer.model.ServerTime;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.mvvm.view.MyAccountActivity;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.navmenu.UrlNavigationHandler;
import com.hungerbox.customer.navmenu.activity.BookMarkActivity;
import com.hungerbox.customer.navmenu.activity.HistoryActivity;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.NotificationUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.UrlNavigationConstatnt;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.hungerbox.customer.util.ApplicationConstants.ACTION;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.CATEGORY_ID;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.ITEM_ID;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.LOCATION_ID;

public class NotificationHomeActivity extends ParentActivity {
    Intent inputIntent;
    MainApplication mainApplication;
    Context context;

    long vendorId = -1;
    long occasionId = -1;
    long locationId = -1;
    long campaignId = -1;
    String action = "";
    long itemId = -1;
    long categoryId = -1;
    boolean containsLink = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_home);
        inputIntent = getIntent();
        mainApplication = (MainApplication) getApplicationContext();
        context = this;
        navigateToRequested();
    }

    private void navigateToRequested(){

        Bundle extras = null;
        boolean isDeeplink = false;
        String urlData ="";
        try{
            String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
            Config config = ((MainApplication) getApplication()).getConfig();
            extras = inputIntent.getExtras();
            isDeeplink = extras.containsKey(ApplicationConstants.NotificationsConstants.DEEPLINK_SCREEN);
            urlData = extras.getString("route","");
            if (!urlData.equalsIgnoreCase("")){
                containsLink = true;
            }
            if (authToken == null || authToken.isEmpty()) {
                isDeeplink = false;
            }else if(config == null || config.getCompany_id() == -1){
                isDeeplink = false;
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }


        if (containsLink){
            extractUrlParameter(urlData);
            sortIntents(ApplicationConstants.Navigation.MENU_SCREEN,extras);
        }
        else if (isDeeplink){
            sortIntents(extras.getString(ApplicationConstants.NotificationsConstants.DEEPLINK_SCREEN),extras);
        }
        else{
            navigateToMainScreen();
        }
    }

    private void extractUrlParameter(String urlData){
        urlData = urlData.replace("#/", "");
        Uri uri = Uri.parse(urlData);
        try {
            vendorId = Long.parseLong(urlData.split("/")[2]);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            occasionId = Long.parseLong(uri.getQueryParameter("occasion_id"));
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            locationId = Long.parseLong(uri.getQueryParameter("location_id"));
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            campaignId = Long.parseLong(uri.getQueryParameter(ApplicationConstants.CAMPAIGN_ID));
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            itemId = Long.parseLong(uri.getQueryParameter(ITEM_ID));
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            categoryId = Long.parseLong(uri.getQueryParameter(CATEGORY_ID));
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            action = uri.getQueryParameter(ApplicationConstants.NotificationsConstants.ACTION);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sortIntents(String screen, Bundle extras){
        Intent intent;
        switch (screen) {
            case ApplicationConstants.Navigation.HISTORY_SCREEN: {
                intent = new Intent(context, HistoryActivity.class);
                navigateToScreen(intent);
                break;
            }
            case ApplicationConstants.Navigation.PAYMENT_SCREEN: {
                if (locationId == -1) {
                    locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
                }
                intent = new Intent(context, PaymentActivity.class);
                intent.putExtra(ApplicationConstants.FROM_NAVBAR, true);
                intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);
                navigateToScreen(intent);
                break;
            }
            case ApplicationConstants.Navigation.BOOKMARK_SCREEN: {
                intent = new Intent(context, BookMarkActivity.class);
                intent.putExtra(ApplicationConstants.EXPRESS_CHECKOUT_ACTION, ApplicationConstants.BOOKMARK_FROM_NAVBAR);
                navigateToScreen(intent);
                break;
            }
            case ApplicationConstants.Navigation.CONTEST_SCREEN: {
                intent = new Intent(context, ContestActivity.class);
                navigateToScreen(intent);
                break;
            }
            case ApplicationConstants.Navigation.ACCOUNT_SCREEN: {
                intent = new Intent(context, MyAccountActivity.class);
                intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
                intent.putExtra(ApplicationConstants.FROM_NOTIFICATION, true);
                navigateToScreen(intent);
                break;
            }
            case ApplicationConstants.Navigation.HELP_SCREEN: {
                intent = new Intent(context, HelpActivity.class);
                intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
                intent.putExtra(ApplicationConstants.FROM_NOTIFICATION, true);
                intent.putExtra("Header", "HELP");
                navigateToScreen(intent);
                break;
            }

            case ApplicationConstants.Navigation.CONTEST_DETAIL_SCREEN: {

                int campaignId = -1;
                try{
                    campaignId = Integer.valueOf(extras.getString(ApplicationConstants.CAMPAIGN_ID, null));
                }catch (Exception exp){
                    exp.printStackTrace();
                }

                if(campaignId != -1){
                    intent = new Intent(context, ContestDetailActivity.class);
                    intent.putExtra(ApplicationConstants.CAMPAIGN_ID, campaignId);
                    intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
                    intent.putExtra(ApplicationConstants.FROM_NOTIFICATION, true);
                    navigateToScreen(intent);
                }else{
                    intent = new Intent(context, ContestActivity.class);
                    navigateToScreen(intent);
                }

                break;
            }

            case ApplicationConstants.Navigation.CART_SCREEN: {
                try{
                    if (mainApplication.getCart().getOrderProducts().size() > 0) {
                        intent = new Intent(this, BookmarkPaymentActivity.class);
                        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
                        navigateToScreen(intent);
                    } else {
                        navigateToMainScreen();
                    }
                }catch (Exception exp){
                    exp.printStackTrace();
                    navigateToMainScreen();
                }
            }
            break;

            case ApplicationConstants.Navigation.MENU_SCREEN:

                handleMenuNavigation(extras);
                break;

            default:
                navigateToMainScreen();
                break;
        }
    }
    private void navigateToScreen(Intent intent){
        finish();
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
        intent.putExtra(ApplicationConstants.FROM_NOTIFICATION, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void navigateToMainScreen() {
        Intent navigationIntent = new Intent(this, MainActivity.class);
        navigationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(navigationIntent);
    }

    private void navigateToMenuScreen(long vendorId, long occasionId,long locationId, long itemId, long categoryId, String action){
        finish();
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
        intent.putExtra(LOCATION_ID, locationId);
        intent.putExtra(ApplicationConstants.OCASSION_ID, occasionId);
        intent.putExtra(ApplicationConstants.VENDOR_ID, vendorId);
        intent.putExtra(ApplicationConstants.NotificationsConstants.ACTION, action);
        intent.putExtra(ApplicationConstants.FROM_NOTIFICATION, true);
        if (itemId>-1){
            intent.putExtra(ITEM_ID,itemId);
        }
        if (categoryId>-1){
            intent.putExtra(CATEGORY_ID,categoryId);
        }
        startActivity(intent);
    }

    private void handleMenuNavigation(Bundle extras) {
        try{
            if ((extras.containsKey(ApplicationConstants.NotificationsConstants.OCCASION_ID)
                    && extras.containsKey(ApplicationConstants.NotificationsConstants.VENDOR_ID))
                 ||(occasionId>-1 && vendorId>-1)
            ) { Long lVendorId ;
                Long lOccasionId ;
                Long lLocationId ;
                Long lItemId ;
                Long lCategoryId ;
                String lAction ;
                if (containsLink){
                    lVendorId = vendorId;
                    lOccasionId = occasionId;
                    lLocationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
                    lItemId = itemId;
                    lCategoryId = categoryId;
                    lAction = action;
                } else {
                    lVendorId= Long.parseLong(extras.getString("vendor_id", "-1"));
                    lOccasionId= Long.parseLong(extras.getString("occasion_id", "-1"));
                    lLocationId= SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
                    lItemId= Long.parseLong(extras.getString(ITEM_ID, "-1"));
                    lCategoryId= Long.parseLong(extras.getString(CATEGORY_ID, "-1"));
                    lAction = extras.getString(ACTION, "");
                }

                VendorLookUp vendorLookUp = new VendorLookUp() {
                    @Override
                    public void onVendorFound() {
                        navigateToMenuScreen(lVendorId,lOccasionId,lLocationId, lItemId, lCategoryId, lAction);
                    }

                    @Override
                    public void onVendorNotFound() {
                        AppUtils.showToast("Vendor not found", true, 0);
                        navigateToMainScreen();
                    }
                };

                getServerTime(new ServerTimeCallBack() {
                    @Override
                    public void onTimeDifferenceFound() {

                        getVendorList(NotificationHomeActivity.this, locationId, occasionId, vendorId, new VendorCollection() {
                            @Override
                            public void getVendorList(ArrayList<Vendor> vendors) {
                                findVendor(NotificationHomeActivity.this, vendors, vendorId, vendorLookUp);
                            }

                            @Override
                            public void onNullResponse() {
                                vendorLookUp.onVendorNotFound();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if(error == null || error.equals("")){
                            AppUtils.showToast("Some error occured.", true, 0);
                        }else{
                            AppUtils.showToast(error, true, 0);
                        }
                        navigateToMainScreen();
                    }
                });
            } else{
                navigateToMainScreen();
            }
        }catch (Exception exp){
            exp.printStackTrace();
            navigateToMainScreen();
        }
    }

    public static void getVendorList(Context context, long locationId, long occasionId, long vendorId,VendorCollection vendorCollection){
        String url = UrlConstant.GET_VENDOR_LIST + "?locationId=" + locationId + "&occasionId=" + occasionId;

        SimpleHttpAgent<OcassionReposne> venSimpleHttpAgent = new SimpleHttpAgent<>(
                context,
                url,
                new ResponseListener<OcassionReposne>() {
                    @Override
                    public void response(OcassionReposne responseObject) {

                        if (responseObject != null && responseObject.getOcassions() != null && responseObject.getOcassions().size() > 0 && responseObject.getOcassions().get(0).vendors.vendors.size() > 0) {

                            if(context!=null){
                                SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE,true);
                            }
                            vendorCollection.getVendorList(responseObject.getOcassions().get(0).vendors.vendors);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (context != null) {
                            SharedPrefUtil.putBoolean(ApplicationConstants.IS_VENDORS_AVAILABLE,false);
                            vendorCollection.onNullResponse();
                        }
                    }
                },
                OcassionReposne.class
        );
        venSimpleHttpAgent.get();
    }
    private static void findVendor(Context context, final ArrayList<Vendor> vendors, long vendorId, VendorLookUp vendorLookUp) {
        for (Vendor vendor : vendors) {
            final long currentTime = DateTimeUtil.adjustCalender(context).getTimeInMillis();
            long vendorStartTime = DateTimeUtil.getTodaysTimeFromString(vendor.getStartTime());
            final long vendorEndTime = DateTimeUtil.getTodaysTimeFromString(vendor.getEndTime());
            if (currentTime < vendorStartTime) {
                vendor.setActive(0);
            } else if (currentTime > vendorEndTime) {
                vendor.setActive(0);
            } else {
                vendor.setActive(1);
            }
        }

        if (!DbHandler.isStarted())
            DbHandler.start(context);
        DbHandler dbHandler = DbHandler.getDbHandler(context);
        boolean vendorStored = dbHandler.createVendors(vendors);

        try {
            Vendor vendor = AppUtils.getVendorById(context, vendorId);
            if (vendor == null) {
                vendorLookUp.onVendorNotFound();
            } else {
                vendorLookUp.onVendorFound();
            }
        } catch (Exception exp) {
            vendorLookUp.onVendorNotFound();
        }
    }


    private void getServerTime(ServerTimeCallBack callBack) {
        String url = UrlConstant.SERVER_TIME;
        SimpleHttpAgent<ServerTime> timeSimpleHttpAgent = new SimpleHttpAgent<ServerTime>(
                this,
                url,
                new ResponseListener<ServerTime>() {
                    @Override
                    public void response(ServerTime responseObject) {
                        ServerTime serverTime = responseObject;
                        serverTime.calculateDifference();
                        SharedPrefUtil.putLong(ApplicationConstants.TIME_DIFFERENCE, serverTime.difference);
                        callBack.onTimeDifferenceFound();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        callBack.onError(error);
                    }
                },
                ServerTime.class
        );
        timeSimpleHttpAgent.getWithoutHeader();
    }


    interface VendorCollection{
        void getVendorList(ArrayList<Vendor> vendors);
        void onNullResponse();
    }
    interface VendorLookUp{
        void onVendorFound();
        void onVendorNotFound();
    }
    interface ServerTimeCallBack{
        void onTimeDifferenceFound();
        void onError(String error);
    }
}
