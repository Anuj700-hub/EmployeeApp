package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/19/17.
 */

public class NoOrderFeedbackResponse {
    @SerializedName("order_doc")
    Order order;
    @SerializedName("review_doc")
    FeedbackResponse feedbackOptionResposnes;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public FeedbackResponse getFeedbackOptionResposnes() {
        return feedbackOptionResposnes;
    }

    public void setFeedbackOptionResposnes(FeedbackResponse feedbackOptionResposnes) {
        this.feedbackOptionResposnes = feedbackOptionResposnes;
    }
}
