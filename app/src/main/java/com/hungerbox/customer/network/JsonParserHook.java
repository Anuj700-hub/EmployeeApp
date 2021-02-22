package com.hungerbox.customer.network;

/**
 * Created by dheeraj on 21/12/15.
 */
public interface JsonParserHook<A> {

    A overRideJsonParsing(String jsonStr);
}