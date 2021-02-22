package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Manas Dadheech on 11/22/16.
 */

public class Guests {

    @SerializedName("name")
    String name;

    ;
    @SerializedName("valid_from")
    long validFrom;
    @SerializedName("valid_till")
    long validTill;
    @SerializedName("status")
    String status;

    public Guests() {
    }

    public Guests(String name, long validFrom, long validTill) {
        this.name = name;
        this.validFrom = validFrom;
        this.validTill = validTill;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public long getValidTill() {
        return validTill;
    }

    public void setValidTill(long validTill) {
        this.validTill = validTill;
    }
}
