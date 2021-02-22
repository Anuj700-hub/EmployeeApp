package com.hungerbox.customer.model;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class SearchVendor {

    @SerializedName("active")
    @Expose
    private Integer active;

    public SearchVendor() {
        active = 1;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

}
