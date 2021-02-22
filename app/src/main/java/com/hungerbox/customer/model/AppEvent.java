package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;


/**
 * Created by sandipanmitra on 3/7/17.
 */
@DatabaseTable(tableName = "app_event")
public class AppEvent {

    @DatabaseField
    public transient String deviceId;
    @DatabaseField
    @SerializedName("vendor_id")
    public long vendorId;
    @DatabaseField
    @SerializedName("location_id")
    public long locationId;
    @DatabaseField
    @SerializedName("event_name")
    public String eventName;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    @SerializedName("data")
    public VendorEventRegistrable vendorEventRegistrable;
    @DatabaseField(id = true)
    long id = Calendar.getInstance().getTimeInMillis();

    public String getDeviceId() {
        return deviceId;
    }

    public AppEvent setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public long getVendorId() {
        return vendorId;
    }

    public AppEvent setVendorId(long vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    public long getLocationId() {
        return locationId;
    }

    public AppEvent setLocationId(long locationId) {
        this.locationId = locationId;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public AppEvent setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public VendorEventRegistrable getVendorEventRegistrable() {
        return vendorEventRegistrable;
    }

    public AppEvent setVendorEventRegistrable(VendorEventRegistrable vendorEventRegistrable) {
        this.vendorEventRegistrable = vendorEventRegistrable;
        return this;
    }
}
