package com.hungerbox.customer.model;

public class DataHandlerExtras {

    private String tag;

    public String getTag() {
        if(this.tag == null)
            return "";
        else
            return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
