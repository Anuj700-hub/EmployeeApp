package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sandipanmitra on 1/17/17.
 */

public class FeedbackRatingReasonResponse {

    @SerializedName("data")
    HashMap<Integer, ArrayList<FeedbackReason>> feedbackRatingResponse;

    public HashMap<Integer, ArrayList<FeedbackReason>> getFeedbackRatingResponse() {
        return feedbackRatingResponse;
    }

    public void setFeedbackRatingResponse(HashMap<Integer, ArrayList<FeedbackReason>> feedbackRatingResponse) {
        this.feedbackRatingResponse = feedbackRatingResponse;
    }
}
