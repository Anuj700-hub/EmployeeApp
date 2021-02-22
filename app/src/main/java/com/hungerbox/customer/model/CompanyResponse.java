package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 20/8/16.
 */
public class CompanyResponse {

    public static String api_key = "location";

    @SerializedName("data")
    public Company companyResponse;
}
