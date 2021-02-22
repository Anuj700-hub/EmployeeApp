package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocationSingleOffline
        implements Serializable {

    @SerializedName("data")
    public LocationOffline location;
}

