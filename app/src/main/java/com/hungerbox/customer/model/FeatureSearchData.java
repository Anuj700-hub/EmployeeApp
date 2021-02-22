package com.hungerbox.customer.model;

        import java.util.List;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class FeatureSearchData {

    @SerializedName("vendor")
    @Expose
    private List<SearchVendorModel> vendor = null;
    @SerializedName("vendor_menu")
    @Expose
    private List<FeatureSearchVendorMenu> vendorMenu = null;

    public List<SearchVendorModel> getVendor() {
        return vendor;
    }

    public void setVendor(List<SearchVendorModel> vendor) {
        this.vendor = vendor;
    }

    public List<FeatureSearchVendorMenu> getVendorMenu() {
        return vendorMenu;
    }

    public void setVendorMenu(List<FeatureSearchVendorMenu> vendorMenu) {
        this.vendorMenu = vendorMenu;
    }

}
