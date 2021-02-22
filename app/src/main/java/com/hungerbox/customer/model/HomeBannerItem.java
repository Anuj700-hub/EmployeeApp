package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sandipanmitra on 1/23/18.
 */

public class HomeBannerItem {

    @SerializedName("allowUser")
    public boolean allowUser;
    @SerializedName("error_text")
    public String errorText;
    @SerializedName("points")
    public UserPoints points;
    @SerializedName("matches")
    public ArrayList<Match> matches = null;
    @SerializedName("type")
    String type;
    @SerializedName("image")
    String image;
    @SerializedName("link")
    String link;
    @SerializedName("text")
    String text;
    @SerializedName("name")
    String name = "";
    @SerializedName("button_text")
    String buttonText = "";

    public int getIs_one_time() {
        return is_one_time;
    }

    public void setIs_one_time(int is_one_time) {
        this.is_one_time = is_one_time;
    }

    @SerializedName("is_one_time")
    int is_one_time = 0;

    public String getErrorText() {
        if (errorText.length() > 0)
            return errorText;
        else
            return "Verifying your eligibility. Please come back soon.";
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getButtonText() {
        if (buttonText == null)
            return "";
        else
            return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAllowUser() {
        return allowUser;
    }

    public void setAllowUser(boolean allowUser) {
        this.allowUser = allowUser;
    }

    public UserPoints getPoints() {
        return points;
    }

    public void setPoints(UserPoints points) {
        this.points = points;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<Match> matches) {
        this.matches = matches;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        if (text == null)
            return "";
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
