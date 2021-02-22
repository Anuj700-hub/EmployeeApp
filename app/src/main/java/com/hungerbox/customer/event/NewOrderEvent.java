package com.hungerbox.customer.event;

import com.hungerbox.customer.model.Order;

/**
 * Created by peeyush on 21/6/16.
 */
public class NewOrderEvent {

    public Order order;

    public NewOrderEvent(Order order) {
        this.order = order;
    }
}
