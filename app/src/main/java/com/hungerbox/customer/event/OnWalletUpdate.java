package com.hungerbox.customer.event;

import com.hungerbox.customer.model.User;

/**
 * Created by ranjeet on 13/11/17.
 */

public class OnWalletUpdate {
    public User user;

    public OnWalletUpdate(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
