package com.hungerbox.customer.config;

import java.io.Serializable;

public class ConfigLogin_auth implements Serializable {
    private static final long serialVersionUID = -1613720253677610610L;
    private boolean oauth;
    private boolean signup_enabled;

    public boolean getOauth() {
        return this.oauth;
    }

    public void setOauth(boolean oauth) {
        this.oauth = oauth;
    }

    public boolean getSignup_enabled() {
        return this.signup_enabled;
    }

    public void setSignup_enabled(boolean signup_enabled) {
        this.signup_enabled = signup_enabled;
    }
}
