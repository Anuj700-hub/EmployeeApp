package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 6/9/16.
 */
public class ChangePassword {

    @SerializedName("user_id")
    public long userId;
    @SerializedName("old_password")
    public String oldPassword;
    @SerializedName("password")
    public String password;
    @SerializedName("password_confirmation")
    public String passwordConfirmation;
    @SerializedName("reset_required")
    public boolean resetRequired;

    public boolean isResetRequired() {
        return resetRequired;
    }

    public ChangePassword setResetRequired(boolean resetRequired) {
        this.resetRequired = resetRequired;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public ChangePassword setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public ChangePassword setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ChangePassword setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public ChangePassword setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
        return this;
    }
}
