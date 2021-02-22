package com.hungerbox.customer.model;

/**
 * Created by peeyush on 28/6/16.
 */
public class UserAdd {

    long id;
    String userName;
    String password;
    String authToken;
    double balance;
    boolean isSelected;


    public UserAdd(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public UserAdd setId(long id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserAdd setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserAdd setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getAuthToken() {
        return authToken;
    }

    public UserAdd setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }

    public double getBalance() {
        return balance;
    }

    public UserAdd setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public UserAdd setSelected(boolean selected) {
        isSelected = selected;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserAdd) {
            UserAdd other = (UserAdd) obj;
            return id == other.id;
        }
        return false;
    }
}
