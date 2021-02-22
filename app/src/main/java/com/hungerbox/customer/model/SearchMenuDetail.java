package com.hungerbox.customer.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchMenuDetail {

    @SerializedName("menu_ids")
    @Expose
    private ArrayList<Long> menuIds = null;
    @SerializedName("occasionId")
    @Expose
    private long occasionId;
    @SerializedName("locationId")
    @Expose
    private long locationId;

    public SearchMenuDetail(ArrayList<Long> menuIds, long occasionId, long locationId) {
        this.menuIds = menuIds;
        this.occasionId = occasionId;
        this.locationId = locationId;
    }

    public ArrayList<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(ArrayList<Long> menuIds) {
        this.menuIds = menuIds;
    }

    public long getOccasionId() {
        return occasionId;
    }

    public void setOccasionId(long occasionId) {
        this.occasionId = occasionId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

}