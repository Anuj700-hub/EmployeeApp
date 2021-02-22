package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 12/11/17.
 */

public class WalletType {

    long id;
    @SerializedName("name")
    String name;
    @SerializedName("display_name")
    String displayName;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    @Override
    public String toString() {
        if (displayName == null)
            displayName = "";
        return displayName;
    }
}
