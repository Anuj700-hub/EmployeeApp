package com.hungerbox.customer.order.listeners;

import com.hungerbox.customer.model.OrderProduct;

/**
 * Created by peeyush on 6/7/16.
 */
public interface OrderReviewProductRefreshListener {

    void refreshOrderList(OrderProduct orderProduct, int position, boolean shouldRefresh);
}
