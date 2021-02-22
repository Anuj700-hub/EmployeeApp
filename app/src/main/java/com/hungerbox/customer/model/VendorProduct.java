package com.hungerbox.customer.model;

/**
 * Created by peeyush on 27/6/16.
 */
public class VendorProduct implements Comparable<VendorProduct> {

    public long vendorId;
    public long productId;
    public double price;
    public int count;


    @Override
    public boolean equals(Object o) {
        if (o instanceof VendorProduct) {
            VendorProduct vendorProduct = (VendorProduct) o;
            return vendorId == vendorProduct.vendorId && productId == vendorProduct.productId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        String hashId = "v" + vendorId + "p" + productId;
        return hashId.hashCode();
    }


    @Override
    public int compareTo(VendorProduct another) {
        if (this.equals(another)) {
            return count - another.count;
        } else if (vendorId == another.vendorId) {
            return (int) (productId - another.productId);
        } else {
            return (int) (vendorId - another.vendorId);
        }
    }
}
