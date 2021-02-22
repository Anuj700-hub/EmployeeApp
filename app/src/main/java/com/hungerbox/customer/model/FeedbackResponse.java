package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 1/19/17.
 */

public class FeedbackResponse {
    @SerializedName("rating")
    int rating;
    @SerializedName("order_id")
    long orderId;
    @SerializedName("type")
    String type;
    @SerializedName("reference")
    String reference;
    @SerializedName("review_options")
    ArrayList<FeedbackOptionResposne> feedbackOptionResposnes;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public ArrayList<FeedbackOptionResposne> getFeedbackOptionResposnes() {
        return feedbackOptionResposnes;
    }

    public void setFeedbackOptionResposnes(ArrayList<FeedbackOptionResposne> feedbackOptionResposnes) {
        this.feedbackOptionResposnes = feedbackOptionResposnes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
