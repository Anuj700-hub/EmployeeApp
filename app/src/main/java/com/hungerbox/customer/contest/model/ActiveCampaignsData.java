package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ActiveCampaignsData {

    @SerializedName("data")
    private ArrayList<ActiveCampaigns> activeCampaigns;

    public ArrayList<ActiveCampaigns> getActiveCampaigns() {
        return activeCampaigns;
    }

    public void setActiveCampaigns(ArrayList<ActiveCampaigns> activeCampaigns) {
        this.activeCampaigns = activeCampaigns;
    }
}
