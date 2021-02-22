package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by peeyush on 4/8/16.
 */
public class UserSettingsResponse implements Serializable {
    static final long serialVersionUID = 1L;

    @SerializedName("data")
    public UserSettings userSettings;
}
