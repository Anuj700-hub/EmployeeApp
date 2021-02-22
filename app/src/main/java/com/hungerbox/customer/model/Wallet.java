package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by peeyush on 8/10/16.
 */
public class Wallet implements Serializable {
    /*
    {

    "amount":"275.00",
    "name":"Company Wallet",
    "display_name":"Snacks Wallet",
    "priority":1,
    "occasion_id":6,
    "employee_can_recharge":0,
    "convenience_fee":0

},
     */
    @SerializedName("amount")
    double amount;
    @SerializedName("name")
    String name;
    @SerializedName("display_name")
    String displayName;
    @SerializedName("priority")
    int priority;
    @SerializedName("occasion_id")
    long occasionId;
    @SerializedName("convenience_fee")
    double convenienceFee;
    @SerializedName("employee_can_recharge")
    int employeeCanRecharge = 0;
    @SerializedName("starting_amount")
    int defaultAmount;
    @SerializedName("show_reverse")
    int showReverse = 0;

    public boolean isEmployeeCanRecharge() {
        if (employeeCanRecharge == 0)
            return false;
        else
            return true;
    }

    public void setEmployeeCanRecharge(boolean employeeCanRecharge) {
        if (employeeCanRecharge)
            this.employeeCanRecharge = 1;
        else
            this.employeeCanRecharge = 0;
    }

    public double getConvenienceFee() {
        return convenienceFee;
    }

    public void setConvenienceFee(double convenienceFee) {
        this.convenienceFee = convenienceFee;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAmountString() {
        return String.format("\u20B9 %.2f", getAmount());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getOccasionId() {
        return occasionId;
    }

    public void setOccasionId(long occasionId) {
        this.occasionId = occasionId;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(int defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public int getShowReverse() {
        return showReverse;
    }

    public void setShowReverse(int showReverse) {
        this.showReverse = showReverse;
    }

    public boolean isShowReverse(){
        return this.showReverse==1;
    }

    public double getDisplayAmount(boolean isDisplayOnly) {
        double displayAmount = 0;
        if(isShowReverse() && isDisplayOnly){
            displayAmount = getDefaultAmount()-getAmount();
        }else{
            displayAmount = getAmount();
        }
        return displayAmount;
    }
}
