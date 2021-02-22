package com.hungerbox.customer.hbanalytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class  HBAnalytics {

    public static void pushFcmEvents(String fcmId, Context context, String analyticsName){
        if(HBAnalyticsConstants.CLEVERTAP.equalsIgnoreCase(analyticsName)){
            pushFCMCleverTap(fcmId, context);
        }
    }

    private static void pushFCMCleverTap(String fcmId, Context context){
        try{
            CleverTapAPI cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(context);
            if (cleverTapDefaultInstance != null) {
                cleverTapDefaultInstance.pushFcmRegistrationId(fcmId, true);
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }

    public static void pushUserProfile(HashMap<String, Object> user, Context context, String analyticsName){

        if(HBAnalyticsConstants.CLEVERTAP.equalsIgnoreCase(analyticsName)){
            pushUserProfileCleverTap(user,context);
        }
        else if(HBAnalyticsConstants.FIREBASE.equalsIgnoreCase(analyticsName)){
            pushUserProfileFireBase(user,context);
        }
    }

    private static void pushUserProfileCleverTap(HashMap<String, Object> user, Context context){
        CleverTapAPI cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(context);
        if(cleverTapDefaultInstance!=null){

            if(user != null){
                cleverTapDefaultInstance.onUserLogin(user);
            }
        }
    }

    private static void pushUserProfileFireBase(HashMap<String, Object> user, Context context) {
        FirebaseAnalytics mFireBaseAnalytics = FirebaseAnalytics.getInstance(context);
        if (user != null) {
            for (Map.Entry<String, Object> entry : user.entrySet()) {
                mFireBaseAnalytics.setUserProperty(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }


    public static void pushUserEvent(HashMap<String, Object> map, String eventName, Context context, String analyticsName, SharedPreferences sharedPreferences){
        if(HBAnalyticsConstants.CLEVERTAP.equalsIgnoreCase(analyticsName)){
            pushUserEventsCleverTap(map,context,eventName);
        }
        else if(HBAnalyticsConstants.FIREBASE.equalsIgnoreCase(analyticsName)){
            pushUserEventsFireBase(map, context,eventName);
        }
    }


    private static void pushUserEventsCleverTap(HashMap<String, Object> map, Context context, String eventName){
        try {
            CleverTapAPI cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(context);
            if (map != null && cleverTapDefaultInstance!=null) {
                cleverTapDefaultInstance.pushEvent(eventName, map);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void pushUserEventsFireBase(HashMap<String, Object> map, Context context, String eventName){
        try {

            Bundle bundle = new Bundle();
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getValue().getClass() == String.class) {
                        bundle.putString(entry.getKey(), (String) entry.getValue());
                    } else if (entry.getValue().getClass() == Boolean.class) {
                        bundle.putBoolean(entry.getKey(), (boolean) entry.getValue());
                    } else if (entry.getValue().getClass() == Double.class) {
                        bundle.putDouble(entry.getKey(), (Double) entry.getValue());
                    } else if (entry.getValue().getClass() == Long.class) {
                        bundle.putLong(entry.getKey(), (Long) entry.getValue());
                    } else if (entry.getValue().getClass() == int.class || entry.getValue().getClass() == Integer.class) {
                        bundle.putInt(entry.getKey(), (int) entry.getValue());
                    }
                }
            }
            FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
