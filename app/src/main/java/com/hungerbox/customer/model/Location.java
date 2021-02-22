package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by peeyush on 20/8/16.
 */
@DatabaseTable(tableName = "c_location")
public class Location implements Serializable {
    static final long serialVersionUID = 1L;

    @DatabaseField(id = true)
    public long id;
    @DatabaseField
    public String name;
    @DatabaseField
    public long companyId;
    @DatabaseField
    public String label;
    @SerializedName("type")
    @DatabaseField
    private String type;
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
    @SerializedName("desk_ordering_enabled")
    int deskOrderingEnabled = 0;
    @SerializedName("enforce_capacity")
    private int enforcedCapacity = 0;
    @SerializedName("address_id")
    @DatabaseField
    private long addressId=0;
    @SerializedName("address_line_1")
    private String addressLine1;
    @DatabaseField
    @SerializedName("other_location_types")
    private String otherLocationTypes;

    public String getType() {
        return type == null? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
        if (o instanceof Location) {
            Location otherLocation = (Location) o;
            return otherLocation.id == id;
        } else
            return false;
    }

    public int getEnforcedCapacity() {
        return enforcedCapacity;
    }

    public void setEnforcedCapacity(int enforcedCapacity) {
        this.enforcedCapacity = enforcedCapacity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name == null? "" : name;
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

    public int getDeskOrderingEnabled() {
        return deskOrderingEnabled;
    }

    public void setDeskOrderingEnabled(int deskOrderingEnabled) {
        this.deskOrderingEnabled = deskOrderingEnabled;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public ArrayList<String> getOtherLocationTypes() {
        try {
            if (otherLocationTypes == null)
                return new ArrayList<>();
            String[] types = otherLocationTypes.split(",");
            return new ArrayList<>(Arrays.asList(types));
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public void setOtherLocationTypes(String otherLocationTypes) {
        this.otherLocationTypes = otherLocationTypes;
    }
}
