package com.hungerbox.customer.offline.listenersOffline;

import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;

public interface OrderReviewProductRefreshListenerOffline {

    void refreshOrderList(OrderProductOffline orderProduct, int position, boolean shouldRefresh);
}

