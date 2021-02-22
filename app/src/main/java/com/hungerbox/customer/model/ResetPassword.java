package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 10/10/16.
 */
public class ResetPassword {

    @SerializedName("email")
    String email;
    @SerializedName("company_id")
    long companyId;

    public long getCompanyId() {
        return companyId;
    }

    public ResetPassword setCompanyId(long companyId) {
        this.companyId = companyId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ResetPassword setEmail(String email) {
        this.email = email;
        return this;
    }
}
