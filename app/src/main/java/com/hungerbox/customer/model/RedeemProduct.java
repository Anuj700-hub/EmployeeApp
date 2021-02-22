package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manas on 6/4/18.
 * <p>
 * {
 * "product_id": 35283,
 * "product": "Test Menu",
 * "price": "1.00",
 * "vendor_id": 191,
 * "vendor_name": "Pizza Thunderbird"
 * }
 */

public class RedeemProduct {
    @SerializedName("product_id")
    long productId;
    String product;
    double price;
    @SerializedName("vendor_name")
    String vendorName;
    @SerializedName("vendor_id")
    long vendorId;
    @SerializedName("image")
    String vendorImage;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getVendorImage() {
        return vendorImage;
    }

    public void setVendorImage(String vendorImage) {
        this.vendorImage = vendorImage;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }
}
