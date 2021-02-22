package com.hungerbox.customer.network;

import com.hungerbox.customer.model.NetworkHeaders;

public interface ResponseListenerNew<T> {

    void response(T responseObject, NetworkHeaders networkHeaders);
}
