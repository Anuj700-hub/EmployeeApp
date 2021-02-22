package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.util.ApplicationConstants;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * Created by peeyush on 7/7/16.
 */
public class OrderVendor implements Serializable {
    static final long serialVersionUID = 1L;

    public long id;
    @SerializedName("vendor_email")
    public String email;
    @SerializedName("name")
    public String userName;
    @SerializedName("description")
    public String desc;
    @SerializedName("is_buffet")
    public int buffet;
    @SerializedName("type")
    public String type;
    @SerializedName("couch_locations")
    public TreeSet<Long> couchLocations = new TreeSet<>();
    @SerializedName("take_away_available")
    public int takeAwayAvailable = 0;
    @SerializedName("vendor_image")
    public String image;
    @SerializedName("customer_gst")
    public double customerGst;
    @SerializedName("is_kitchen_system_enabled")
    private int isKitchenSystemEnabled = 0;

    public int getIsKitchenSystemEnabled() {
        return isKitchenSystemEnabled;
    }

    public void setIsKitchenSystemEnabled(int isKitchenSystemEnabled) {
        this.isKitchenSystemEnabled = isKitchenSystemEnabled;
    }

    public double getCustomerGst() {
        return customerGst;
    }

    public void setCustomerGst(double customerGst) {
        this.customerGst = customerGst;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        if (email == null)
            email = "";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        if (userName == null)
            userName = "";
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getBuffet() {
        return buffet;
    }

    public boolean isBuffet() {
        return buffet == 1;
    }

    public void setBuffet(int buffet) {
        this.buffet = buffet;
    }

    public boolean isRestaurant() {
        return type != null && type.equalsIgnoreCase(ApplicationConstants.VENDOR_TYPE_RESTAURANT);
    }

    public boolean isVendingMachine(){
        if(type == null){
            return false;
        }
        return type.equalsIgnoreCase(ApplicationConstants.VENDING_MACHINE);
    }


    public TreeSet<Long> getCouchLocations() {
        if (couchLocations == null)
            couchLocations = new TreeSet<>();
        return couchLocations;
    }

    public void setCouchLocations(TreeSet<Long> couchLocations) {
        this.couchLocations = couchLocations;
    }

    public boolean isTakeAwayAvailable() {
        return takeAwayAvailable == 1;
    }

    public int getTakeAwayAvailable() {
        return takeAwayAvailable;
    }

    public void setTakeAwayAvailable(int takeAwayAvailable) {
        this.takeAwayAvailable = takeAwayAvailable;
    }

    public boolean isSlotBookingVendor(){
        if (type!=null && type.equalsIgnoreCase(ApplicationConstants.SLOT_BOOKING)){
            return true;
        } else{
            return false;
        }
    }

    public boolean isSpaceBookingVendor(){
        if(type == null)
            return false;
        return type.equalsIgnoreCase(ApplicationConstants.SPACE_BOOKING);
    }
}
