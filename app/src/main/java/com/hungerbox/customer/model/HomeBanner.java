package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/24/18.
 */

public class HomeBanner {

    @SerializedName("offer")
    HomeBannerItemResponse homeBannerItemResponse;

    public HomeBannerItemResponse getHomeBannerItemResponse() {
        if (homeBannerItemResponse == null)
            homeBannerItemResponse = new HomeBannerItemResponse();
        return homeBannerItemResponse;
    }

    public void setHomeBannerItemResponse(HomeBannerItemResponse homeBannerItemResponse) {
        this.homeBannerItemResponse = homeBannerItemResponse;
    }
}
