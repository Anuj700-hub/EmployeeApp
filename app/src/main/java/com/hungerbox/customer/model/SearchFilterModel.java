package com.hungerbox.customer.model;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class SearchFilterModel {

    @SerializedName("vendor")
    @Expose
    private SearchVendor vendor;
    @SerializedName("vendor_menu")
    @Expose
    private SearchVendorMenu vendorMenu;

    public SearchFilterModel(SearchVendor vendor, SearchVendorMenu vendorMenu) {
        this.vendor = vendor;
        this.vendorMenu = vendorMenu;
    }

    public SearchFilterModel() {
        this.vendor = new SearchVendor();
        this.vendorMenu = new SearchVendorMenu();
    }

    public SearchVendor getVendor() {
        return vendor;
    }

    public void setVendor(SearchVendor vendor) {
        this.vendor = vendor;
    }

    public SearchVendorMenu getVendorMenu() {
        return vendorMenu;
    }

    public void setVendorMenu(SearchVendorMenu vendorMenu) {
        this.vendorMenu = vendorMenu;
    }

}
