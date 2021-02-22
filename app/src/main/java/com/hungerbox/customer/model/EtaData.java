package com.hungerbox.customer.model;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class EtaData {

    @SerializedName("eta")
    @Expose
    private long eta;

    public long getEta() {
        return eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }

}
