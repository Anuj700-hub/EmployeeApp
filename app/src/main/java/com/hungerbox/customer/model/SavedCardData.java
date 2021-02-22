package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manas on 27/4/18.
 */

public class SavedCardData {

    @SerializedName("name_on_card")
    String nameOnCard = "";
    @SerializedName("card_token")
    String token = "";
    @SerializedName("bin_id")
    String binId = "";

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBinId() {
        return binId;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }
}
