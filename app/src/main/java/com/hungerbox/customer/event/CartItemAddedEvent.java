package com.hungerbox.customer.event;

import com.hungerbox.customer.model.OrderProduct;

/**
 * Created by manas on 28/10/16.
 */

public class CartItemAddedEvent {

    OrderProduct orderProduct;

    public CartItemAddedEvent(OrderProduct orderProduct) {
        this.orderProduct = orderProduct;
    }

    public OrderProduct getOrderProduct() {
        return orderProduct;
    }

    public void setOrderProduct(OrderProduct orderProduct) {
        this.orderProduct = orderProduct;
    }
}
