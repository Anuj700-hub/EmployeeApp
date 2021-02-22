package com.hungerbox.customer.offline.listenersOffline;

import com.hungerbox.customer.offline.modelOffline.ProductOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;

public interface AddToCardLisenterOffline {
    void addToCart(VendorOffline vendor, ProductOffline product);

    void removeFromCart(VendorOffline vendor, ProductOffline product);
}
