package com.hungerbox.customer.model;

/**
 * Created by sandipanmitra on 4/6/18.
 */

public class PaymentMethodHeader {
    String header;


    public PaymentMethodHeader() {
        header = "";
    }

    public PaymentMethodHeader(String header) {
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
