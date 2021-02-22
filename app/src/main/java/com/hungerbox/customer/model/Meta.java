package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by manas on 24/1/17.
 */
public class Meta {
    @SerializedName("scopes")
    public HashMap<String, String> scopes;

    public HashMap<String, String> getScopes() {
        if(scopes == null)
            return new HashMap<>();
        return scopes;
    }

    public void setScopes(HashMap<String, String> scopes) {
        this.scopes = scopes;
    }
}
