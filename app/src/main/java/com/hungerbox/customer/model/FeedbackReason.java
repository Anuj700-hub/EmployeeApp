package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/17/17.
 */

public class FeedbackReason {

    @SerializedName("id")
    private long id;
    @SerializedName("question")
    private String reason;
    @SerializedName("type")
    private String type;

    private String answer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnswer() {
        if (answer == null)
            answer = "";
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
