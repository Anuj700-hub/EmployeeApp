package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/2/18.
 */

public class BannerItem {
//    "reference_type": "company_banners",
//            "reference_id": 2,
//            "active": 1,
//            "text": null,
//            "redirect_link": null,
//            "from_date": null,
//            "to_date": null,
//            "logoImageSrc": "http://content.hungerbox.com/resize_images/55/original_event4.jpg (8 kB)

    @SerializedName("reference_type")
    String referenceType;
    @SerializedName("reference_id")
    String referenceId;
    @SerializedName("active")
    int active;
    @SerializedName("redirect_link")
    String redirectLink;
    @SerializedName("image")
    String image;
    @SerializedName("text")
    String text;


    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
