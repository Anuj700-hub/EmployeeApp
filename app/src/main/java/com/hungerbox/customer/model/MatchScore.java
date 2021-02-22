package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manas on 5/4/18.
 */

public class MatchScore {

    @SerializedName("match")
    public Match match;

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
