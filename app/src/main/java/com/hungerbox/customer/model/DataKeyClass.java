package com.hungerbox.customer.model;

import com.j256.ormlite.field.DatabaseField;

public class DataKeyClass {


    private String apiKey = "";

    private String object1Id = "";

    private String object2Id = "";

    private String object3Id = "";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getObject1Id() {
        return object1Id;
    }

    public void setObject1Id(String object1Id) {
        this.object1Id = object1Id;
    }

    public String getObject2Id() {
        return object2Id;
    }

    public void setObject2Id(String object2Id) {
        this.object2Id = object2Id;
    }

    public String getObject3Id() {
        return object3Id;
    }

    public void setObject3Id(String object3Id) {
        this.object3Id = object3Id;
    }
}
