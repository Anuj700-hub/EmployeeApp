package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

public class ContestTasks {

    @SerializedName("completed")
    private int taskCompleted;

    @SerializedName("remaining")
    private int taskRemaining;

    @SerializedName("total")
    private int totalTask;

    public int getTaskCompleted() {
        return taskCompleted;
    }

    public void setTaskCompleted(int taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    public int getTaskRemaining() {
        return taskRemaining;
    }

    public void setTaskRemaining(int taskRemaining) {
        this.taskRemaining = taskRemaining;
    }

    public int getTotalTask() {
        return totalTask;
    }

    public void setTotalTask(int totalTask) {
        this.totalTask = totalTask;
    }
}
