package com.hungerbox.customer.model;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class SearchVendorMenu {

    @SerializedName("active")
    @Expose
    protected Integer active;

    public SearchVendorMenu() {
        active = 1;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

}
