package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 25/8/16.
 */
public class LocationUpdate {

    @SerializedName("location_id")
    public long locationId;
    @SerializedName("location_Name")
    public String locationName;

    public long getLocationId() {
        return locationId;
    }

    public LocationUpdate setLocationId(long locationId) {
        this.locationId = locationId;
        return this;
    }

    public String getLocationName() {
        return locationName;
    }

    public LocationUpdate setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }
}
