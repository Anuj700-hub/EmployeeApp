package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderUsersReponseOffline implements Serializable {

    @SerializedName("data")
    public ArrayList<OrderUserOffline> users;

    public ArrayList<OrderUserOffline> getUsers() {
        if (users == null)
            users = new ArrayList<>();
        return users;
    }

    public void setUsers(ArrayList<OrderUserOffline> users) {
        this.users = users;
    }
}
