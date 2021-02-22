package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/11/18.
 */

public class ZetaCertHeaders {
    @SerializedName("signatoryJID")
    String signatoryJID = "zetamerchant.services.olympus";
    @SerializedName("signature")
    String signature = "AAGbwDAwRQIhAIWO7TQkJvE8nkXnh6zYWyoEwLv+vNnTDK5UPh5Pvj4gAiATRuncM5gkFX/65z21GNLSea38nBbhyBE4FR3zanmITw==";

    public String getSignatoryJID() {
        return signatoryJID;
    }

    public void setSignatoryJID(String signatoryJID) {
        this.signatoryJID = signatoryJID;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
