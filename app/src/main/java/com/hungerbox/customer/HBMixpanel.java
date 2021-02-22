package com.hungerbox.customer;

import android.content.Context;

import com.hungerbox.customer.model.User;

import org.json.JSONObject;

public class HBMixpanel {

    private static HBMixpanel instance;

    public static HBMixpanel getInstance() {

        if (instance == null) {
            instance = new HBMixpanel();
        }

        return instance;
    }

    public void addEvent(Context context, String eventName) {
        /*try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.track(eventName);
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void addEvent(Context context, String eventName, JSONObject subEventName) {
       /* try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.track(eventName, subEventName);
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void addPeople(Context context, String userId, String userName, String email, String locationId, String phoneNumber) {

        /*try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.getPeople().identify(userId);
            mixpanel.getPeople().set(EventUtil.MixpanelEvent.PeopleProperties.Name, userName);
            mixpanel.getPeople().set(EventUtil.MixpanelEvent.PeopleProperties.EMAIL_ID, email);
            mixpanel.getPeople().set(EventUtil.MixpanelEvent.PeopleProperties.LOCATION_ID, locationId);
            mixpanel.getPeople().set(EventUtil.MixpanelEvent.PeopleProperties.MOBILE_NUMBER, phoneNumber);
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void identifyMixpanel(Context context, String userId) {
       /* try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.identify(userId);
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void flushMixpanel(Context context) {
        /*try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.flush();
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void registerSuperProperties(Context context, JSONObject event) {
      /*  try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.registerSuperProperties(event);
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void registerSuperPropertiesOnce(Context context, JSONObject event) {
        /*try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.registerSuperPropertiesOnce(event);
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void increment(Context context, String eventName, double value) {
       /* try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.getPeople().increment(eventName, value);
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void timeTrack(Context context, String eventName) {
       /* try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.timeEvent(eventName);
        } catch (Exception exp) {
            exp.printStackTrace();
        }*/
    }

    public void logUser(Context context, User user) {

        /*try{
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
            mixpanel.getPeople().identify(user.getId() + "");
            mixpanel.getPeople().set("name", user.getName());
            mixpanel.getPeople().set("email", user.getEmail());
            mixpanel.getPeople().set("location", user.getLocationName());
            mixpanel.getPeople().set("phone", user.getPhoneNumber());
        }catch (Exception exp){
            exp.printStackTrace();
        }*/
    }
}
