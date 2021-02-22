package com.hungerbox.customer.model;

/**
 * Created by ranjeet on 17,January,2019
 */

/**
 * Created by sandipanmitra on 4/6/18.
 */

public class MoreOptionHeader {
    String header;


    public MoreOptionHeader() {
        header = "";
    }

    public MoreOptionHeader(String header) {
        if (header == null)
            this.header = "";
        else
            this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
