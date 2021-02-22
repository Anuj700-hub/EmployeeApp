package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sandipanmitra on 2/22/17.
 */
public class OrderUsersReponse implements Serializable {

    @SerializedName("data")
    public ArrayList<OrderUser> users;

    public ArrayList<OrderUser> getUsers() {
        if (users == null)
            users = new ArrayList<>();
        return users;
    }

    public void setUsers(ArrayList<OrderUser> users) {
        this.users = users;
    }
}
