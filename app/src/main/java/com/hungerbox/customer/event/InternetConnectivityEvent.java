package com.hungerbox.customer.event;

/**
 * Created by peeyush on 21/6/16.
 */
public class InternetConnectivityEvent {

    public boolean isConnected;

    public InternetConnectivityEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
