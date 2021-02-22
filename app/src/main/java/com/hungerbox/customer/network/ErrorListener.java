package com.hungerbox.customer.network;

import com.android.volley.Response;

/**
 * Created by superprofs on 17/5/16.
 */
public abstract class ErrorListener implements Response.ErrorListener {
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
