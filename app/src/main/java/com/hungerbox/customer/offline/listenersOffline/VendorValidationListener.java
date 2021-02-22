package com.hungerbox.customer.offline.listenersOffline;

import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;

public interface VendorValidationListener {
    void validateAndAddProduct(VendorOffline vendor, ProductOffline product, boolean isBuffet);

}
