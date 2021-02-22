package com.hungerbox.customer.event;

import com.hungerbox.customer.model.Order;

/**
 * Created by manas on 28/10/16.
 */

public class LastOrderDeliveredEvent {
    public Order order;

    public LastOrderDeliveredEvent(Order order) {
        this.order = order;
    }
}
