package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class LocationOffline implements Serializable {
    static final long serialVersionUID = 1L;

    @DatabaseField(id = true)
    public long id;
    @DatabaseField
    public String name;
    @DatabaseField
    public long companyId;
    @DatabaseField
    public String label;
    @DatabaseField
    public int active;
    @DatabaseField
    public double capacity;
    @SerializedName("city_name")
    @DatabaseField
    String cityName;
    @SerializedName("city_id")
    @DatabaseField
    long cityId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        if (name == null)
            name = "";
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LocationOffline) {
            LocationOffline otherLocation = (LocationOffline) o;
            return otherLocation.id == id;
        } else
            return false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

}
