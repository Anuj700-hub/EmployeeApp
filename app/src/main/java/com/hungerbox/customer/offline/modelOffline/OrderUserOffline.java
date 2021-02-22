package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderUserOffline implements Serializable {
    static final long serialVersionUID = 1L;

    public long id;
    @SerializedName("email")
    public String email;
    @SerializedName("username")
    public String userName;
    @SerializedName("name")
    public String name;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        if (email == null)
            email = "hola";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        if (userName == null)
            userName = "hola";
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
