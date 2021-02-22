package com.hungerbox.customer.contest.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("data")
    @Expose
    private List<TaskData> taskData = null;

    public List<TaskData> getTaskData() {
        return taskData;
    }

    public void setTaskData(List<TaskData> taskData) {
        this.taskData = taskData;
    }

}