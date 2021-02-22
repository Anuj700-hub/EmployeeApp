package com.hungerbox.customer.network;

/**
 * Created by peeyushpathak on 05/12/15.
 */
public interface ResponseListener<T> {

    void response(T responseObject);
}
