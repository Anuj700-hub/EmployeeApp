package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peeyush on 8/9/16.
 */
public class UserFeedback {
    @SerializedName("order_id")
    long orderId;
    @SerializedName("rating")
    int rating;
    @SerializedName("comment")
    String comment;
    @SerializedName("reference")
    String reference = "order";


    public String getReference() {
        return reference;
    }

    public UserFeedback setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public long getOrderId() {
        return orderId;
    }

    public UserFeedback setOrderId(long orderId) {
        this.orderId = orderId;
        return this;
    }

    public int getRating() {
        return rating;
    }

    public UserFeedback setRating(int rating) {
        this.rating = rating;
        return this;
    }


    public String getComment() {
        return comment;
    }

    public UserFeedback setComment(String comment) {
        this.comment = comment;
        return this;
    }
}
