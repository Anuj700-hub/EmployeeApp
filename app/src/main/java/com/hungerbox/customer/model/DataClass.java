package com.hungerbox.customer.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import io.realm.annotations.PrimaryKey;

@DatabaseTable(tableName = "data_class")
public class DataClass implements Serializable {

    @PrimaryKey
    @DatabaseField(generatedId = true)
    private long Id;

    @DatabaseField
    private String apiKey = "";

    @DatabaseField
    private String object1Id = "";

    @DatabaseField
    private String object2Id = "";

    @DatabaseField
    private long serverWatermark;

    @DatabaseField
    private long clientWatermark;

    @DatabaseField
    private String lastResponse = "";

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        this.Id = id;
    }

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

    public long getServerWatermark() {
        return serverWatermark;
    }

    public void setServerWatermark(long serverWatermark) {
        this.serverWatermark = serverWatermark;
    }

    public long getClientWatermark() {
        return clientWatermark;
    }

    public void setClientWatermark(long clientWatermark) {
        this.clientWatermark = clientWatermark;
    }

    public String getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(String lastResponse) {
        this.lastResponse = lastResponse;
    }

}
