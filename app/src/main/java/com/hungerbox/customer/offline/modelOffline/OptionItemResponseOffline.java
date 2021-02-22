package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OptionItemResponseOffline {
    @SerializedName("data")
    ArrayList<OptionItemOffline> menuOptionsItems;

    public ArrayList<OptionItemOffline> getMenuOptionsItems() {
        return menuOptionsItems;
    }

    public void setMenuOptionsItems(ArrayList<OptionItemOffline> menuOptionsItems) {
        this.menuOptionsItems = menuOptionsItems;
    }
}
