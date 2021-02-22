package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ContestResponse {

    @SerializedName("data")
    private ArrayList<ContestList> contestList;

    public ArrayList<ContestList> getContestList() {
        return contestList;
    }

    public void setContestList(ArrayList<ContestList> contestList) {
        this.contestList = contestList;
    }
}
