package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.util.ApplicationConstants;

import java.io.Serializable;
import java.util.TreeSet;

public class OrderVendorOffline implements Serializable {
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
}
