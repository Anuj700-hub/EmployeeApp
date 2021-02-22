package com.hungerbox.customer.ksnetwork;

public interface KSResponseListener<T> {

    void response(T responseObject);
}
