package com.hungerbox.customer.contest.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TasksSummaryCount {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("completed")
    @Expose
    private Integer completed;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

}
