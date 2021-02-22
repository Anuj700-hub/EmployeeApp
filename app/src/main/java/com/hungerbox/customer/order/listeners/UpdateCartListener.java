package com.hungerbox.customer.order.listeners;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 12/8/17.
 */

public interface UpdateCartListener {
    void onOrderItemChanged();

    void onBannerAdded(ArrayList<Object> banner);
}
