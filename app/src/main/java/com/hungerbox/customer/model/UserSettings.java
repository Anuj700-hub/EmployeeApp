package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 8/9/16.
 */
public class UserSettings {
    @SerializedName("email_notification")
    int email;
    @SerializedName("sms_notification")
    int sms;
    @SerializedName("app_notification")
    int appNotification;
    @SerializedName("general_notification")
    int generalNotification;
    @SerializedName("chat_head")
    int chatHeadEnabled;

    public boolean isChatHeadEnabled() {
        if (chatHeadEnabled == 1)
            return true;
        else
            return false;
    }

    public void setChatHeadEnabled(boolean chatHeadEnabled) {
        if (chatHeadEnabled)
            this.chatHeadEnabled = 1;
        else
            this.chatHeadEnabled = 0;
    }

    public boolean isEmail() {
        if (email == 1)
            return true;
        else
            return false;
    }

    public void setEmail(boolean email) {
        if (email)
            this.email = 1;
        else
            this.email = 0;
    }

    public boolean isSms() {
        if (sms == 1)
            return true;
        else
            return false;
    }

    public void setSms(boolean sms) {
        if (sms)
            this.sms = 1;
        else
            this.sms = 0;
    }

    public boolean isAppNotification() {
        if (appNotification == 1)
            return true;
        else
            return false;
    }

    public void setAppNotification(boolean appNotification) {
        if (appNotification)
            this.appNotification = 1;
        else
            this.appNotification = 0;
    }

    public boolean isGeneralNotification() {
        if (generalNotification == 1)
            return true;
        else
            return false;
    }

    public void setGeneralNotification(boolean generalNotification) {
        if (generalNotification)
            this.generalNotification = 1;
        else
            this.generalNotification = 0;
    }
}
