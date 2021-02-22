package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

public class ProjectCodeSetting {
    @SerializedName("project_code")
    private String projectCode;

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
}
