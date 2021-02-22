package com.hungerbox.customer.contest.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CampaignResponse {

    @SerializedName("data")
    @Expose
    private List<CampaignData> data = null;

    public List<CampaignData> getCampaignData() {
        return data;
    }

    public void setCampaignData(List<CampaignData> data) {
        this.data = data;
    }

}
