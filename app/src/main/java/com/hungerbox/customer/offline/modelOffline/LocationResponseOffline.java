package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LocationResponseOffline {

    @SerializedName("data")
    public ArrayList<LocationOffline> locations;
}
