package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class DeskReferenceSetting
{
    @SerializedName("desk_reference")
    private String deskReference=null;

    public String getDeskReference() {
        return deskReference;
    }

    public void setDeskReference(String deskReference) {
        this.deskReference = deskReference;
    }
}
