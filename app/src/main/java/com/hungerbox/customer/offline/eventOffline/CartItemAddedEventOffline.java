package com.hungerbox.customer.offline.eventOffline;

import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;

public class CartItemAddedEventOffline {

    OrderProductOffline orderProduct;

    public CartItemAddedEventOffline(OrderProductOffline orderProduct) {
        this.orderProduct = orderProduct;
    }

    public OrderProductOffline getOrderProduct() {
        return orderProduct;
    }

    public void setOrderProduct(OrderProductOffline orderProduct) {
        this.orderProduct = orderProduct;
    }
}


