package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manas on 17/8/17.
 * <p>
 * "data": {
 * "display_name": "Simpl",
 * "code": "simpl",
 * "show_balance": 1,
 * "linking_required": 1,
 * "otp_required_for_linking": 1,
 * "otp_required_for_checkout": 0,
 * "is_user_linked": true
 * }
 */
public class PaymentData implements Serializable {

    long id;
    @SerializedName("display_name")
    String displayName;
    String code;
    @SerializedName("show_balance")
    int showBalance;
    @SerializedName("linking_required")
    int linkingRequired;
    @SerializedName("otp_required_for_linking")
    int otpRequiredForLinking;
    @SerializedName("otp_required_for_checkout")
    int otpRequiredForCheckout;
    @SerializedName("is_user_linked")
    boolean isUserLinked;
    @SerializedName("default")
    int isDefault;
    @SerializedName("logo")
    String payment_logo;
    private boolean selected;
    @SerializedName("add_money_allowed")
    int addMoneyAllowed = 0;
    @SerializedName("polling_enabled")
    private int pollingEnabled = 0;
    @SerializedName("helper_html")
    private String helper_html = "";

    public String getPayment_logo() {
        return payment_logo;
    }

    public void setPayment_logo(String payment_logo) {
        this.payment_logo = payment_logo;
    }

    public boolean getIsDefault() {
        if (isDefault == 1)
            return true;
        else
            return false;
    }

    public void setIsDefault(boolean isDefault) {
        if (isDefault == true)
            this.isDefault = 1;
        else
            this.isDefault = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCode() {
        if (code == null) {
            code = "";
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getShowBalance() {
        return showBalance;
    }

    public void setShowBalance(int showBalance) {
        this.showBalance = showBalance;
    }

    public int getLinkingRequired() {
        return linkingRequired;
    }

    public boolean isLinkingRequired() {
        return linkingRequired == 1;
    }

    public void setLinkingRequired(int linkingRequired) {
        this.linkingRequired = linkingRequired;
    }

    public int getOtpRequiredForLinking() {
        return otpRequiredForLinking;
    }

    public void setOtpRequiredForLinking(int otpRequiredForLinking) {
        this.otpRequiredForLinking = otpRequiredForLinking;
    }

    public int getOtpRequiredForCheckout() {
        return otpRequiredForCheckout;
    }

    public void setOtpRequiredForCheckout(int otpRequiredForCheckout) {
        this.otpRequiredForCheckout = otpRequiredForCheckout;
    }

    public boolean isUserLinked() {
        return isUserLinked;
    }

    public void setUserLinked(boolean userLinked) {
        isUserLinked = userLinked;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getAddMoneyAllowed() {
        return addMoneyAllowed;
    }

    public void setAddMoneyAllowed(int addMoneyAllowed) {
        this.addMoneyAllowed = addMoneyAllowed;
    }

    public int getPollingEnabled() {
        return pollingEnabled;
    }

    public void setPollingEnabled(int pollingEnabled) {
        this.pollingEnabled = pollingEnabled;
    }

    public String getHelper_html() {
        if(helper_html==null)
            return "";
        return helper_html;
    }

    public void setHelper_html(String helper_html) {
        this.helper_html = helper_html;
    }
}
