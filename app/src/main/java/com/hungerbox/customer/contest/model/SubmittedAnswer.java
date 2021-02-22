package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubmittedAnswer {

    @SerializedName("option_id")
    @Expose
    private Integer optionId;
    @SerializedName("option_name")
    @Expose
    private String optionName;

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

}