package com.hungerbox.customer.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.HbEvent;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.util.tasks.VendorEventStoreTask;

import java.util.Calendar;

/**
 * Created by peeyush on 4/10/16.
 */
public class EventUtil {

    public static final String HEALTH_DEVICE_NAME = "Health_Device_Name";
    public static final String HEALTH_DEVICE_CONNECT = "Health_Device_Connect";
    public static final String HEALTH_DISH_ADD = "Health_Dish_Add";
    public static final String LOGIN = "login";
    public static final String OCCASION_CHANGE = "occassion_change";
    public static final String LOCATION_CHANGE = "location_CHANGE";
    public static final String WALLET_CLICK = "wallet_click";
    public static final String MENU_CATEGORY_CLICK = "menu_categrory_click";
    public static final String CART_ADDITION = "cart_added";
    public static final String CART_REMOVAL = "cart_removal";
    public static final String CART_CREATION = "cart_create";
    public static final String ORDER_PLACED = "order_placed";
    public static final String APP_LAUNCH = "app_laucnh";
    public static final String CHECKOUT_CLICK = "checkout_click";
    public static final String NAV_HOME = "nv_home";
    public static final String NAV_ORDER_LIST = "nv_order_list";
    public static final String NAV_RECAHRGE = "nv_recharge";
    public static final String NAV_MY_PROFILE = "nv_my_profile";
    public static final String NAV_SETTINGS = "nv_settings";
    public static final String NAV_LOGOUT = "nv_logout";
    public static final String SCREEN_HOME = "HOME";
    public static final String SCREEN_VENDOR_SEARCH = "VENDOR_SEARCH_SCREEN";
    public static final String SCREEN_MENU = "VENDOR_MENU";
    public static final String SCREEN_CART = "REVIEW_CART";
    public static final String SCREEN_PAYMENT = "PAYMENT_SCREEN";
    public static final String SCREEN_RECHARGE = "SCREEN_RECHARGE";
    public static final String HOME_LOCATION_CLICK = "home_location_click_1_1";
    public static final String HOME_LOCATION_CHANGE = "home_location_change_1_2";
    public static final String HOME_CLICK_SEARCH_VENDOR = "home_click_search_vendor_1_3";
    public static final String HOME_SEARCH_SUCCESS = "home_search_success_1_4";
    public static final String HOME_CHECKOUT_NOITEM = "home_click_checkout_noitem_1_5";
    public static final String HOME_OCCASION_CLICK = "home_occasion_click_1_8";
    public static final String HOME_OCCASION_CHANGE = "home_occasion_change_1_9";
    public static final String HOME_RECOMMENDED_ADDITEM = "home_recommended_additem_1_6";
    public static final String HOME_CHECKOUT_RECOMMENDED = "home_recommended_checkout_1_7";
    public static final String MENU_RECOMMENDED_CLICK = "menu_recommended_click_2_3";
    public static final String CART_RECOMMENDED_ADD = "cart_recommended_add_3_2";
    public static final String MENU_SEARCH_CLICK = "menu_search_click_2_1";
    public static final String MENU_SEARCH_SUCCESS = "menu_search_success_2_2";
    public static final String MENU_CLICK_VEGONLY = "menu_click_vegonly_2_4";
    public static final String MENU_BACK = "menu_click_back_2_8";
    public static final String MENU_ADD_ITEM = "menu_add_item_2_9";
    public static final String MENU_ITEM_INCR = "menu_item_incr_2_10";
    public static final String MENU_ITEM_DECR = "menu_item_decr_2_11";
    public static final String MENU_CHECKOUT = "menu_checkout_2_10";
    public static final String CART_ITEM_EDIT = "cart_item_edit_3_1";
    public static final String CART_CLICK_CHANGE_LOCATION = "cart_click_changelocation_3_3";
    public static final String CART_CHANGE_LOCATION = "cart_change_location_3_4";
    public static final String CART_PLACE_ORDER_CLICK = "cart_place_order_click_3_5";
    public static final String CART_ORDER_PLACED = "cart_order_placed_3_6";
    public static final String CART_ORDER_ERROR = "cart_order_error_3_7";
    public static final String CART_DETAILED_BILL = "cart_detailed_bill_3_8";
    public static final String PAYMENT_RECHARGE_CLICK = "payment_recharge_clicks_4_2";
    public static final String PAYMENT_CLICK_PAY = "payment_click_pay_4_3";
    public static final String RECHARGE_VALUE = "recharge_value_enter_5_1";
    public static final String RECHARGE_200 = "recharge_click_200_5_2_1";
    public static final String RECHARGE_500 = "recharge_click_500_5_2_2";
    public static final String RECHARGE_1000 = "recharge_click_1000_5_2_3";
    public static final String SCREEN_OPEN_HOME = "screen_open_home_1";
    public static final String SCREEN_OPEN_MENU = "screen_open_menu_2";
    public static final String SCREEN_OPEN_CART = "screen_open_cart_3";
    public static final String SCREEN_OPEN_PAYMENT = "screen_open_payment_4";
    public static final String SCREEN_OPEN_RECHARGE = "screen_open_recharge_5";
    public static final String BANNER_BTN_CLICK = "banner_button_click";
    public static final String OFFER_BANNER_BTN_CLICK = "offer_banner_button_click";
    public static final String OFFER_BANNER_INELIGIBILITY_DIALOG = "offer_banner_dialog_ok_click";
    public static final String NAV_BAR_CLICK = "nav_bar_click";
    public static final String NAV_BAR_REDEEM_CLICK = "nav_bar_redeem_click";
    public static final String REDEEM_PRODUCT_FINAL = "redeem_product_final";
    public static final String MIXPANEL_TOKEN = "cf1d239f67d5ca0a16194270ab8761a1";



    public static void FbEventLog(Context context, String event_name, String value) {
//        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
//        HashMap<String,Object>map = new HashMap<>();
//        map.put(FirebaseAnalytics.Param.VALUE,value);
//        LogEvent.pushUserEvent(map,event_name,context,"Firebase",SharedPrefUtil.getSharedPref());
////        Bundle bundle = new Bundle();
////        if (value != null && !value.equals(""))
////            bundle.putString(FirebaseAnalytics.Param.VALUE, value);
////        mFirebaseAnalytics.logEvent(event_name, bundle);
    }

    public static void FbEventLog(Context context, String event_name, Bundle value) {
//        HashMap<String,Object>map = new HashMap<>();
//        map.put(FirebaseAnalytics.Param.VALUE,value);
//        LogEvent.pushUserEvent(map,event_name,context,"Firebase",SharedPrefUtil.getSharedPref());
////        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
////        mFirebaseAnalytics.logEvent(event_name, value);
    }

    public static void logUser(Context context, User user) {
//        if (!BuildConfig.DEBUG) {
//            HashMap<String,Object>user_map = new HashMap<>();
//            user_map.put("user_id",user.getId() + "");
//            user_map.put("name", user.getName());
//            user_map.put("email", user.getEmail());
//            user_map.put("location", user.getLocationName());
//            user_map.put("phone", user.getPhoneNumber());
//            LogEvent.pushUserProfile(user_map,context,"Firebase");
////            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
////            mFirebaseAnalytics.setUserId(user.getId() + "");
////            mFirebaseAnalytics.setUserProperty("name", user.getName());
////            mFirebaseAnalytics.setUserProperty("email", user.getEmail());
////            mFirebaseAnalytics.setUserProperty("location", user.getLocationName());
////            mFirebaseAnalytics.setUserProperty("phone", user.getPhoneNumber());
//        } else {
//            HBMixpanel.getInstance().logUser(context, user);
//        }
    }

    public static void logUser(Context context, String cid) {
//        HashMap<String,Object>user_map = new HashMap<>();
//        user_map.put("company_id", cid);
//        LogEvent.pushUserProfile(user_map,context,"Firebase");
////        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
////        mFirebaseAnalytics.setUserProperty("company_id", cid);
    }

    public static void logBaseEvent(Context context, String event) {
        AppEvent appEvent = new AppEvent();
        HbEvent hbEvent = new HbEvent();
        hbEvent.updateTime(Calendar.getInstance().getTimeInMillis());
        appEvent.setVendorEventRegistrable(hbEvent).setEventName(event);
        EventUtil.updateCafeEventData(context, appEvent, hbEvent);
        VendorEventStoreTask.addEventToQueue(context, appEvent);
    }

    public static void updateCafeEventData(Context context, AppEvent appEvent, HbEvent hbEvent) {
        if (AppUtils.isCafeApp()) {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String address = info.getMacAddress();
            appEvent.setDeviceId(address);
            hbEvent.addDataToMap("is_cafe", String.valueOf(true));
        }
    }

    public static class MixpanelEvent {

        public static final String RESET_PASSWORD = "Reset_Password";
        public static final String LOCATION_CHANGE = "Location_Change";
        public static final String LOCATION_CLICK = "Location_Click";
        public static final String MENU_CATEGORY_CLICK = "Menu_Categrory_Click";
        public static final String APP_LAUNCH = "App_Launch";
        public static final String NAVIGATION_DRAWER_OPEN = "Navigation_Drawer_Open";
        public static final String VENDOR_SERACH_CLICK = "Vendor_Search_Click";
        public static final String SEARCH_SUCCESS = "Vendor_Search_Success";
        public static final String CHECKOUT_NOITEM = "Checkout_No_Item";
        public static final String CLICK_OCCASION = "Occasion_Click";
        public static final String CHANGE_OCCASION = "Occasion_Change";
        public static final String MENU_SEARCH_CLICK = "Menu_Search_Click";
        public static final String MENU_SEARCH_SUCCESS = "Menu_Search_Success";
        public static final String MENU_RECOMMENDED_CLICK = "Menu_Recommended_Click";
        public static final String MENU_CLICK_VEGONLY = "Menu_Click_Vegonly";
        public static final String MENU_CLICK_BACK = "Menu_Click_Back";
        public static final String MENU_ITEM_ADD = "Menu_Item_Add";
        public static final String MENU_ITEM_INCR = "Menu_Item_Incr";
        public static final String MENU_ITEM_DECR = "Menu_Item_Decr";
        public static final String MENU_CHECKOUT = "Menu_Checkout";
        public static final String RECOMMENDED_ADDITEM = "Recommended_AddItem";
        public static final String CHECKOUT_RECOMMENDED = "Checkout_Recommended";
        public static final String CART_RECOMMENDED_ADD = "Cart_Recommended_Add";
        public static final String CART_PLACE_ORDER_CLICK = "Cart_Order_Place_Click";
        public static final String CART_ORDER_PLACED = "Cart_Order_Placed";
        public static final String CART_ORDER_ERROR = "Cart_Order_Error";
        public static final String CART_DETAILED_BILL = "Cart_Detailed_Bill";

        public static final String PAYMENT_RECHARGE_CLICK = "Payment_Recharge_Click";
        public static final String PAYMENT_PAY_CLICK = "Payment_Pay_Click";

        public static final String SCREEN_OPEN_MENU = "Screen_Open_Menu";
        public static final String SCREEN_OPEN_CART = "Screen_Open_Cart";
        public static final String SCREEN_OPEN_PAYMENT = "Screen_Open_Payment";
        public static final String SCREEN_OPEN_RECHARGE = "Screen_Open_Recharge";

        public static final String REDEEM_PRODUCT = "Redeem_Product";

        public static final String VIEW_QR_CODE = "View_QR_Code";
        public static final String PAYMENT_OPTION_SPENT_TIME = "Payment_Option_Spent_Time";

        public static class BannerIneligible {

            public static final String EVENT_NAME = "Banner_Ineligible";
            public static final String BANNER_NAME = "Banner_Name";
        }

        public static class NavigationItemClick {

            public static final String EVENT_NAME = "Navigation_Item_Click";
            public static final String ITEM_NAME = "Item_Name";
        }

        public static class RechargeClick {

            public static final String EVENT_NAME = "Recharge_Click";
            public static final String RECHARGE_AMOUNT = "Recharge_Amount";
        }

        public static class BannerClick {

            public static final String EVENT_NAME = "Banner_Click";
            public static final String BANNER_NAME = "Item_Name";
        }

        public static class SuperProperties {

            public static final String MOBILE_NUMBER = "Mobile_Number";
            public static final String EMAIL_ID = "Email_Id";
            public static final String COMPANY_ID = "Company_Id";
            public static final String USER_RATING = "User_Rating";
        }


        public static class SubProperties {

            public static final String SOURCE = "Source";
            public static final String STATUS = "Status";
            public static final String ERROR = "Error";
        }
    }
}
