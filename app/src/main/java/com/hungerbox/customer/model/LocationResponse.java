package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by peeyush on 20/8/16.
 */
public class LocationResponse {

    @SerializedName("data")
    public ArrayList<Location> locations;
}
