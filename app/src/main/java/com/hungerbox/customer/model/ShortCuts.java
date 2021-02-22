package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShortCuts  implements Serializable, Comparable<NavItemModel> {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("key")
    String key;
    @SerializedName("label")
    String name;

    @Override
    public int compareTo(NavItemModel o) {
        return 0;
    }
}

