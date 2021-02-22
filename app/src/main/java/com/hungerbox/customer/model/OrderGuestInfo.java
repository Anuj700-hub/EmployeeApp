package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by peeyush on 7/7/16.
 */
public class OrderGuestInfo implements Serializable {
    static final long serialVersionUID = 1L;


    @SerializedName("affiliation")
    public String affiliation;


    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    private String name;
    private String email;
    @SerializedName("phone_no")
    private String phoneNo;
    private boolean notification = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public OrderGuestInfo(){

    }

    public OrderGuestInfo(String name, String email, String phoneNo,boolean notification){
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.notification = notification;
    }

}
