package com.hungerbox.customer.order;

import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;

/**
 * Created by peeyush on 30/6/16.
 */
public interface AddToCardLisenter {
    void addToCart(Vendor vendor, Product product);

    void removeFromCart(Vendor vendor, Product product);

}
