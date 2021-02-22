package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by manas on 3/5/17.
 */

public class MenuRecommendationResponse {
    @SerializedName("data")
    public ArrayList<Product> productsArray;

}
