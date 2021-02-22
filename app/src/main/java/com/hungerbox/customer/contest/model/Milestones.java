package com.hungerbox.customer.contest.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Milestones {

    @SerializedName("data")
    @Expose
    private List<MilestoneData> data = null;

    public List<MilestoneData> getMilestoneData() {
        return data;
    }

    public void setMilestoneData(List<MilestoneData> data) {
        this.data = data;
    }

}
