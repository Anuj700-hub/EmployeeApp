package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sandipanmitra on 1/11/18.
 */

public class ZetaRequestBase {
    @SerializedName("request")
    ZetaRequest zetaRequest;
    @SerializedName("certificate")
    ZetaCertificate zetaCertificate = new ZetaCertificate();

    public ZetaRequest getZetaRequest() {
        return zetaRequest;
    }

    public void setZetaRequest(ZetaRequest zetaRequest) {
        this.zetaRequest = zetaRequest;
    }

    public ZetaCertificate getZetaCertificate() {
        return zetaCertificate;
    }

    public void setZetaCertificate(ZetaCertificate zetaCertificate) {
        this.zetaCertificate = zetaCertificate;
    }
}
