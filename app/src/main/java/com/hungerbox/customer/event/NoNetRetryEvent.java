package com.hungerbox.customer.event;

/**
 * Created by peeyush on 15/7/16.
 */
public class NoNetRetryEvent {
    int type;

    public NoNetRetryEvent(int type) {
        this.type = type;
    }
}
