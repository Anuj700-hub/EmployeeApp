package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by peeyush on 29/9/16.
 */
public class CompaniesReposne {

    @SerializedName("data")
    public ArrayList<Company> companies;
}
