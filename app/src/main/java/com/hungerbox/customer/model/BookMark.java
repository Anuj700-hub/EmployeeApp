package com.hungerbox.customer.model;

/**
 * Created by ranjeet on 18,December,2018
 */
public class BookMark {

    private long menuId;
    private long locationId;
    private long occasionId;

    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getOccasionId() {
        return occasionId;
    }

    public void setOccasionId(long occasionId) {
        this.occasionId = occasionId;
    }
}
