package com.hungerbox.customer.model;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 1/17/17.
 */

public class FeedbackRatingReason implements Comparable<FeedbackRatingReason> {

    private int rating;
    private ArrayList<FeedbackReason> feedbackRatingReasons;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public ArrayList<FeedbackReason> getFeedbackRatingReasons() {
        if (feedbackRatingReasons == null)
            feedbackRatingReasons = new ArrayList<>();
        return feedbackRatingReasons;
    }

    public void setFeedbackRatingReasons(ArrayList<FeedbackReason> feedbackRatingReasons) {
        this.feedbackRatingReasons = feedbackRatingReasons;
    }

    @Override
    public int compareTo(FeedbackRatingReason another) {
        return rating - another.rating;
    }
}
