package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 4/5/18.
 */

public class PaymentMethodMethod implements Serializable {
    @SerializedName("id")
    long id;
    @SerializedName("name")
    String name;
    @SerializedName("method")
    String method;
    @SerializedName("method_type")
    String methodType;
    @SerializedName("priority")
    int priority;

    boolean selected = false;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        if (name == null)
            name = getMethod();
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PaymentMethodMethod) {
            PaymentMethodMethod paymentMethodMethod = (PaymentMethodMethod) obj;
            return paymentMethodMethod.id == id;
        }
        return false;
    }
}
