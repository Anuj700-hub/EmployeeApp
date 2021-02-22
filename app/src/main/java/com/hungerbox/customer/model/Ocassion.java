package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.util.DateTimeUtil;

/**
 * Created by peeyush on 3/8/16.
 */
public class Ocassion implements Cloneable {


    @SerializedName("id")
    public long id;
    @SerializedName("occasion_id")
    public long occasionId;
    public String name;
    @SerializedName("start_time")
    public String startTime;
    @SerializedName("end_time")
    public String endTime;
    @SerializedName("pre_order")
    public boolean isPreOrder;
    @SerializedName("preorder_start_time")
    public String preorderStartTime;
    @SerializedName("preorder_end_time")
    public String preorderEndTime;
    @SerializedName("vendor")
    public VendorsResponse vendors;
    public boolean isCurrentOccasion = false;

    @SerializedName("current_timestamp")
    public long current_timestamp;

    @SerializedName("preorder_only")
    public int preOrderOnly = 0;

    @SerializedName("preorder_day")
    public String preOrderDay;

    public static enum PreOrderDayType{
        TODAY, TOMORROW, NEXT_WORKING_DAY_5, NEXT_WORKING_DAY_6
    }

    public PreOrderDayType getPreOrderDay() {
        if(preOrderDay == null){
            return PreOrderDayType.TODAY;
        }
        else {
            switch (preOrderDay) {
                case "tomorrow":
                    return PreOrderDayType.TOMORROW;
                case "next_working_day_5":
                    return PreOrderDayType.NEXT_WORKING_DAY_5;
                case "next_working_day_6":
                    return PreOrderDayType.NEXT_WORKING_DAY_6;
                default:
                    return PreOrderDayType.TODAY;
            }
        }
    }

    public boolean isPreOrderOnly() {
        return preOrderOnly == 1;
    }

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
        if (isCurrentOccasion)
            return name + " (Current)" + "\n" + formattedStartTime + " - " + formattedEndTime;
        else
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
        Ocassion ocassion = new Ocassion();
        ocassion.id = id;
        ocassion.occasionId = occasionId;
        ocassion.name = name;
        ocassion.endTime = endTime;
        ocassion.startTime = startTime;
        ocassion.startTime = startTime;
        ocassion.isPreOrder = isPreOrder;
        ocassion.preOrderOnly = preOrderOnly;
        ocassion.preOrderDay = preOrderDay;
        return ocassion;
    }
}
