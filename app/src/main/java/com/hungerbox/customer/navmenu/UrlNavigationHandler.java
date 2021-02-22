package com.hungerbox.customer.navmenu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.booking.EventsBaseActivity;
import com.hungerbox.customer.booking.GenericBannerItemActivity;
import com.hungerbox.customer.booking.MeetingBaseActivty;
import com.hungerbox.customer.contest.activity.ContestActivity;
import com.hungerbox.customer.contest.activity.ContestDetailActivity;
import com.hungerbox.customer.health.HealthHomeActivity;
import com.hungerbox.customer.marketing.OfferActivity;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.OcassionReposne;
import com.hungerbox.customer.model.ServerTime;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.mvvm.view.MyAccountActivity;
import com.hungerbox.customer.navmenu.activity.BookMarkActivity;
import com.hungerbox.customer.navmenu.activity.HistoryActivity;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.HelpActivity;
import com.hungerbox.customer.order.activity.MenuActivity;
import com.hungerbox.customer.order.activity.NotificationHomeActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.UrlNavigationConstatnt;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.hungerbox.customer.util.ApplicationConstants.ACTION;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.CATEGORY_ID;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.ITEM_ID;
import static com.hungerbox.customer.util.ApplicationConstants.NotificationsConstants.LOCATION_ID;

/**
 * Created by sandipanmitra on 1/2/18.
 */

public class UrlNavigationHandler {

    Context context;
    MainApplication mainApplication;

    public UrlNavigationHandler(Context context) {
        this.context = context;
        mainApplication = (MainApplication) context.getApplicationContext();
    }

    public Intent getUrlNavPath(String url) {
        Intent intent = null;
        if (Patterns.WEB_URL.matcher(url).matches()) {
            intent = new Intent(context, GenericBannerItemActivity.class);
            intent.putExtra(ApplicationConstants.BANNER_URL, url);
            return intent;
        } else
            if (url.contains(UrlNavigationConstatnt.EVENT_URL)) {
            //TODO event
            intent = new Intent(context, EventsBaseActivity.class);
            intent.putExtra(ApplicationConstants.EVENT_URL, url);
        } else if (url.contains(UrlNavigationConstatnt.MEETING_ROOM)) {
            //TODO event
            intent = new Intent(context, MeetingBaseActivty.class);
            intent.putExtra(ApplicationConstants.MEETING_URL, url);
        } else if (url.contains(UrlNavigationConstatnt.FOOD_MENU)) {
            //TODO food
            try {
                url = url.replace("#/", "");
                Uri uri = Uri.parse(url);
                long vendorId = Long.parseLong(url.split("/")[2]);
                long occasionId = Long.parseLong(uri.getQueryParameter("occasionId"));
                long locationId = Long.parseLong(uri.getQueryParameter("locationId"));
                intent = new Intent(context, MenuActivity.class);
                intent.putExtra(ApplicationConstants.OCASSION_ID, occasionId);
                intent.putExtra(ApplicationConstants.VENDOR_ID, vendorId);
                intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);
                intent.putExtra(ApplicationConstants.VENDOR_NAME, "");
                intent.putExtra(ApplicationConstants.LOCATION, "");
                return intent;
            } catch (Exception e) {
                intent = new Intent(context, GenericBannerItemActivity.class);
                intent.putExtra(ApplicationConstants.BANNER_URL, url);
            }
        } else if (url.contains(UrlNavigationConstatnt.CATEGORY)){
            return menuIntent(url,false);

        }
        else if (url.contains(UrlNavigationConstatnt.ITEM)){
            return menuIntent(url,true);
        }
        else if (url.contains(UrlNavigationConstatnt.HEALTH_URL)) {
            intent = new Intent(context, HealthHomeActivity.class);
            intent.putExtra("goToDetails", true);
            return intent;
        } else if (url.contains(UrlNavigationConstatnt.OFFER)) {
            intent = new Intent(context, OfferActivity.class);
            intent.putExtra(ApplicationConstants.BANNER_URL, url);
        } else if (url.contains(UrlNavigationConstatnt.LIST_CAMPAIGNS)) {
            intent = new Intent(context, ContestActivity.class);
            intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),"Banner");
            return intent;
        } else if (url.contains(UrlNavigationConstatnt.CAMPAIGN_DETAILS)) {

            try {
                int contestId = Integer.parseInt(url.split("/")[1]);
                intent = new Intent(context, ContestDetailActivity.class);
                intent.putExtra(ApplicationConstants.CAMPAIGN_ID, contestId);
                intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),"Banner");
            }catch (Exception e){
                intent =  new Intent(context,ContestActivity.class);
            }

            return intent;

        } else if (url.contains(UrlNavigationConstatnt.NONE)) {
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (url.contains(ApplicationConstants.Navigation.HISTORY_SCREEN)) {
            intent = new Intent(context, HistoryActivity.class);
        }
        else if (url.contains(ApplicationConstants.Navigation.PAYMENT_SCREEN)) {

            long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
            intent = new Intent(context, PaymentActivity.class);
            intent.putExtra(ApplicationConstants.FROM_NAVBAR, true);
            intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);
        }
        else if (url.contains(ApplicationConstants.Navigation.BOOKMARK_SCREEN)) {
            intent = new Intent(context, BookMarkActivity.class);
            intent.putExtra(ApplicationConstants.EXPRESS_CHECKOUT_ACTION, ApplicationConstants.BOOKMARK_FROM_NAVBAR);
        }
        else if (url.contains(ApplicationConstants.Navigation.CONTEST_SCREEN)) {
            intent = new Intent(context, ContestActivity.class);
        }
        else if (url.contains(ApplicationConstants.Navigation.ACCOUNT_SCREEN)) {
            intent = new Intent(context, MyAccountActivity.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
        }
        else if (url.contains(ApplicationConstants.Navigation.HELP_SCREEN)) {
            intent = new Intent(context, HelpActivity.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
            intent.putExtra("Header", "HELP");
        }
        else if (url.contains(ApplicationConstants.Navigation.CART_SCREEN)) {
            try{
                if (mainApplication.getCart().getOrderProducts().size() > 0) {
                    intent = new Intent(context, BookmarkPaymentActivity.class);
                    intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
                } else {
                    intent = new Intent(context,GlobalActivity.class);
                }
            }catch (Exception exp){
                exp.printStackTrace();
                intent = new Intent(context,GlobalActivity.class);
            }
        }
        else {
            intent = new Intent(context, GenericBannerItemActivity.class);
            intent.putExtra(ApplicationConstants.BANNER_URL, url);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ApplicationConstants.FROM_NOTIFICATION, true);
        return intent;
    }

    private Intent menuIntent(String url, boolean isItemNotification){
        try{
            url = url.replace("#/", "");
            Uri uri = Uri.parse(url);
            long vendorId = Long.parseLong(url.split("/")[2]);
            long occasionId = Long.parseLong(uri.getQueryParameter("occasionId"));
            long locationId = Long.parseLong(uri.getQueryParameter("locationId"));
            String action = uri.getQueryParameter("action");
            long categoryId = -1;
            long itemId = -1;

            if (isItemNotification){
                itemId = Long.parseLong(uri.getQueryParameter("itemId"));
            } else{
                categoryId = Long.parseLong(uri.getQueryParameter("categoryId"));
            }

            Intent intent = new Intent(context, MenuActivity.class);
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
            return intent;
        } catch (Exception e){
            Intent navigationIntent = new Intent(context, GlobalActivity.class);
            navigationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return navigationIntent;
        }
    }


    public static void getVendorList(Context context, long locationId, long occasionId, long vendorId, VendorCollection vendorCollection){
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

//    private static void findVendor(Context context, final ArrayList<Vendor> vendors, long vendorId, VendorLookUp vendorLookUp) {
//
//        Realm realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
//
//        realm.executeTransactionAsync(new Realm.Transaction() {
//                                          @Override
//                                          public void execute(Realm realm) {
//                                              realm.delete(Vendor.class);
//                                              for (Vendor vendor : vendors) {
//                                                  final long currentTime = DateTimeUtil.adjustCalender(context).getTimeInMillis();
//                                                  long vendorStartTime = DateTimeUtil.getTodaysTimeFromString(vendor.getStartTime());
//                                                  final long vendorEndTime = DateTimeUtil.getTodaysTimeFromString(vendor.getEndTime());
//
//                                                  if (currentTime < vendorStartTime) {
//                                                      vendor.setActive(0);
//                                                  } else if (currentTime > vendorEndTime) {
//                                                      vendor.setActive(0);
//                                                  } else {
//                                                      vendor.setActive(1);
//                                                  }
//                                              }
//
//                                              realm.copyToRealmOrUpdate(vendors);
//                                          }
//                                      },
//                new Realm.Transaction.OnSuccess() {
//                    @Override
//                    public void onSuccess() {
//
//                        try {
//                            Vendor vendor = realm.where(Vendor.class).equalTo("id", vendorId).findFirst();
//                            if (vendor == null) {
//                                vendorLookUp.onVendorNotFound();
//                            } else{
//                                vendorLookUp.onVendorFound();
//                            }
//                        } catch (Exception exp) {
//                            vendorLookUp.onVendorNotFound();
//                        }
//                    }
//                });
//    }


    private void getServerTime(ServerTimeCallBack callBack) {
        String url = UrlConstant.SERVER_TIME;
        SimpleHttpAgent<ServerTime> timeSimpleHttpAgent = new SimpleHttpAgent<ServerTime>(
                context,
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
