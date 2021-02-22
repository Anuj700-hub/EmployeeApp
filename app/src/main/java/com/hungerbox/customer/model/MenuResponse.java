package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by peeyush on 2/7/16.
 */
public class MenuResponse {


    @SerializedName("menu")
    public LinkedHashMap<String, ArrayList<Product>> menu;
}
