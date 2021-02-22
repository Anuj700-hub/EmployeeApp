package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by manas on 20/8/17.
 */
public class LocationSingle implements Serializable {

    @SerializedName("data")
    public Location location;
}
