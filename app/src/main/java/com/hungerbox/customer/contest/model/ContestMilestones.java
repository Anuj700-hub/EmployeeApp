package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

public class ContestMilestones {

    @SerializedName("total")
    private int milestoneTotal;

    @SerializedName("completed")
    private int milestoneCompleted;

    public int getMilestoneTotal() {
        return milestoneTotal;
    }

    public void setMilestoneTotal(int milestoneTotal) {
        this.milestoneTotal = milestoneTotal;
    }

    public int getMilestoneCompleted() {
        return milestoneCompleted;
    }

    public void setMilestoneCompleted(int milestoneCompleted) {
        this.milestoneCompleted = milestoneCompleted;
    }

    public int getRemainingMilestones(){
        return milestoneTotal - milestoneCompleted;
    }
}