package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class TrayList extends RealmObject {

    @SerializedName("tray_names")
    RealmList<String> trayNamesList;

    public RealmList<String> getTrayNamesList() {
        if (trayNamesList == null)
            return new RealmList<>();
        return trayNamesList;
    }

    public void setTrayNamesList(RealmList<String> trayNamesList) {
        this.trayNamesList = trayNamesList;
    }

}
