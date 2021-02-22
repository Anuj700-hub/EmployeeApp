package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Reward implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_unlocked")
    @Expose
    private Integer isUnlocked;
    @SerializedName("campaign_id")
    @Expose
    private Integer campaignId;
    @SerializedName("campaign_title")
    @Expose
    private String campaignTitle;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private Integer value;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("voucher_code")
    @Expose
    private String voucherCode;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("expiry_time")
    @Expose
    private String expiryTime;
    @SerializedName("voucher_type")
    @Expose
    private String voucherType;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("terms_and_conditions")
    @Expose
    private String termsAndConditions;
    @SerializedName("required_task_count")
    @Expose
    private Integer requiredTaskCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getIsUnlocked() {
        return isUnlocked==1;
    }

    public void setIsUnlocked(Integer isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignTitle() {
        return campaignTitle;
    }

    public void setCampaignTitle(String campaignTitle) {
        this.campaignTitle = campaignTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public Integer getRequiredTaskCount() {
        return requiredTaskCount;
    }

    public void setRequiredTaskCount(Integer requiredTaskCount) {
        this.requiredTaskCount = requiredTaskCount;
    }

    public boolean isVoucher(){
        if (type!=null && !type.isEmpty() && type.equalsIgnoreCase("voucher")){
            return true;
        }
        else{
            return false;
        }
    }
}
