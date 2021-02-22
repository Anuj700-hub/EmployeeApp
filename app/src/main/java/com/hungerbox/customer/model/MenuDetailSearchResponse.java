package com.hungerbox.customer.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuDetailSearchResponse {

    @SerializedName("data")
    @Expose
    private List<Product> productList = null;

    public List<Product> getData() {
        return productList;
    }

    public void setData(List<Product> productList) {
        this.productList = productList;
    }

}
