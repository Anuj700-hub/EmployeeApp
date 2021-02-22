package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by peeyush on 7/7/16.
 */
public class OrderUser implements Serializable {
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
            email = "";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        if (userName == null)
            userName = "";
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        if(name==null)
            return "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
