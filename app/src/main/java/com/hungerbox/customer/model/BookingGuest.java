package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BookingGuest {

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

    public BookingGuest(String name, String email, String phoneNo){
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
    }
}
