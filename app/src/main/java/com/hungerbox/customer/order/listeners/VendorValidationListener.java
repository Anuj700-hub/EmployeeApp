package com.hungerbox.customer.order.listeners;

import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;

/**
 * Created by peeyush on 4/7/16.
 */
public interface VendorValidationListener {
    void validateAndAddProduct(Vendor vendor, Product product, boolean isBuffet);
}
