package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PastCampaignsData {

    @SerializedName("data")
    private ArrayList<PastCampaigns> pastCampaigns;

    public ArrayList<PastCampaigns> getPastCampaigns() {
        return pastCampaigns;
    }

    public void setPastCampaigns(ArrayList<PastCampaigns> pastCampaigns) {
        this.pastCampaigns = pastCampaigns;
    }
}
