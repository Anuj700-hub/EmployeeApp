package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubmitResponseData {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("contest_question_id")
    @Expose
    private String contestQuestionId;
    @SerializedName("contest_question_option_id")
    @Expose
    private String contestQuestionOptionId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContestQuestionId() {
        return contestQuestionId;
    }

    public void setContestQuestionId(String contestQuestionId) {
        this.contestQuestionId = contestQuestionId;
    }

    public String getContestQuestionOptionId() {
        return contestQuestionOptionId;
    }

    public void setContestQuestionOptionId(String contestQuestionOptionId) {
        this.contestQuestionOptionId = contestQuestionOptionId;
    }

}
