package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 6/9/16.
 */
public class UserDefaultLocation {
    @SerializedName("default_location_id")
    long locationId;

    public long getLocationId() {
        return locationId;
    }

    public UserDefaultLocation setLocationId(long locationId) {
        this.locationId = locationId;
        return this;
    }
}
