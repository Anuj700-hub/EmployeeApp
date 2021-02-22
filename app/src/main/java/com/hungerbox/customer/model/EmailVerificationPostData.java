package com.hungerbox.customer.model;

import java.io.Serializable;

public class EmailVerificationPostData implements Serializable {
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
