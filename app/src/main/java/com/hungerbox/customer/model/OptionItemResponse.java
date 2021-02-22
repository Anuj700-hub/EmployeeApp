package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by sandipanmitra on 11/7/16.
 */

public class OptionItemResponse extends RealmObject {

    @SerializedName("data")
    RealmList<OptionItem> menuOptionsItems;

    public RealmList<OptionItem> getMenuOptionsItems() {
        return menuOptionsItems;
    }

    public void setMenuOptionsItems(RealmList<OptionItem> menuOptionsItems) {
        this.menuOptionsItems = menuOptionsItems;
    }
}
