package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sandipanmitra on 9/22/17.
 */

public class OrderPaymentData implements Serializable {
    @SerializedName("action")
    String action;
    @SerializedName("method")
    String method;
    @SerializedName("inputs")
    HashMap<String, String> paymentPayload = new HashMap<String, String>();
    long orderId;
    String orderRef;
    transient ZetaCertificate zetaCertificate = new ZetaCertificate();


    public String getAction() {
        if (action == null)
            action = "";
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setMethods(String methods) {
        this.method = methods;
    }

    public HashMap<String, String> getPaymentPayload() {
        return paymentPayload;
    }

    public void setPaymentPayload(HashMap<String, String> paymentPayload) {
        this.paymentPayload = paymentPayload;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public ZetaCertificate getZetaCertificate() {
        return zetaCertificate;
    }

    public void setZetaCertificate(ZetaCertificate zetaCertificate) {
        this.zetaCertificate = zetaCertificate;
    }
}
