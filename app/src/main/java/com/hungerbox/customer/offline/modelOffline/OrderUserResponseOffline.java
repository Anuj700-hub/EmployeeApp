package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderUserResponseOffline implements Serializable {
    static final long serialVersionUID = 1L;

    @SerializedName("data")
    public OrderUserOffline user;
}
