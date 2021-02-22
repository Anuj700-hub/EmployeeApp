package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 10/13/17.
 */

public class AppReview {
    @SerializedName("review")
    int review;
    @SerializedName("rating")
    double rating;
    @SerializedName("comment")
    String text;

    public boolean isReviewGiven() {
        return review == 1;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
