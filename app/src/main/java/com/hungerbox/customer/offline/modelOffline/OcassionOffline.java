package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.util.DateTimeUtil;

public class OcassionOffline implements Cloneable {


    @SerializedName("occasion_id")
    public long id;
    public String name;
    public String label;
    public int active;
    @SerializedName("display_start_time")
    public String displayStartTime;
    @SerializedName("start_time")
    public String startTime;
    @SerializedName("end_time")
    public String endTime;
    public String type;
    @SerializedName("pre_order")
    public boolean isPreOrder;
    @SerializedName("vendor")
    public VendorsResponseOffline vendors;
    public boolean isCurrentOccasion = false;

    @SerializedName("current_timestamp")
    public long current_timestamp;

    public String getName() {
        if (name == null)
            return "";
        else
            return name;
    }

    @Override
    public String toString() {

        String formattedStartTime = android.text.format.DateFormat.format("hh:mm a",
                DateTimeUtil.getTodaysCalenderFromString(startTime).getTime()).toString();
        String formattedEndTime = android.text.format.DateFormat.format("hh:mm a",
                DateTimeUtil.getTodaysCalenderFromString(endTime).getTime()).toString();
        return name + " \n" + formattedStartTime + " - " + formattedEndTime;
    }


    public boolean isPreorder() {
        return isPreOrder;
    }

    public long getTimeStamp() {
        return current_timestamp;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        OcassionOffline ocassion = new OcassionOffline();
        ocassion.id = id;
        ocassion.name = name;
        ocassion.endTime = endTime;
        ocassion.type = type;
        ocassion.startTime = startTime;
        ocassion.startTime = startTime;
        ocassion.displayStartTime = displayStartTime;
        ocassion.isPreOrder = isPreOrder;
        return ocassion;
    }
}
