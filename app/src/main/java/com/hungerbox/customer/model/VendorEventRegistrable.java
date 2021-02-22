package com.hungerbox.customer.model;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 3/7/17.
 */

public interface VendorEventRegistrable extends Serializable {

    void updateTime(long time);

    long getLocalTime();
}
