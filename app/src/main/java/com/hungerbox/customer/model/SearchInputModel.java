package com.hungerbox.customer.model;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class SearchInputModel {

    @SerializedName("location_id")
    @Expose
    private String locationId;
    @SerializedName("occasion_id")
    @Expose
    private String occasionId;
    @SerializedName("query_string")
    @Expose
    private String queryString;
    @SerializedName("withActiveVendorSchedules")
    @Expose
    private Boolean withActiveVendorSchedules ;
    @SerializedName("withActiveMenuSchedules")
    @Expose
    private Boolean withActiveMenuSchedules ;
    @SerializedName("filters")
    @Expose
    private SearchFilterModel filters;

    public SearchInputModel(String locationId, String occasionId, String queryString, Boolean withActiveVendorSchedules, Boolean withActiveMenuSchedules, SearchFilterModel filters) {
        this.locationId = locationId;
        this.occasionId = occasionId;
        this.queryString = queryString;
        this.withActiveVendorSchedules = withActiveVendorSchedules;
        this.withActiveMenuSchedules = withActiveMenuSchedules;
        this.filters = filters;
    }

    public SearchInputModel(String locationId, String occasionId, String queryString) {
        this.locationId = locationId;
        this.occasionId = occasionId;
        this.queryString = queryString;
        this.withActiveVendorSchedules = true;
        this.withActiveMenuSchedules = true;
        this.filters = new SearchFilterModel();
    }

    public SearchInputModel(long locationId, long occasionId, String queryString) {
        this.locationId = String.valueOf(locationId);
        this.occasionId = String.valueOf(occasionId);
        this.queryString = queryString;
        this.withActiveVendorSchedules = true;
        this.withActiveMenuSchedules = true;
        this.filters = new SearchFilterModel();
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = String.valueOf(locationId);
    }

    public String getOccasionId() {
        return occasionId;
    }

    public void setOccasionId(String occasionId) {
        this.occasionId = occasionId;
    }

    public void setOccasionId(long occasionId) {
        this.occasionId = String.valueOf(occasionId);
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Boolean getWithActiveVendorSchedules() {
        return withActiveVendorSchedules;
    }

    public void setWithActiveVendorSchedules(Boolean withActiveVendorSchedules) {
        this.withActiveVendorSchedules = withActiveVendorSchedules;
    }

    public Boolean getWithActiveMenuSchedules() {
        return withActiveMenuSchedules;
    }

    public void setWithActiveMenuSchedules(Boolean withActiveMenuSchedules) {
        this.withActiveMenuSchedules = withActiveMenuSchedules;
    }

    public SearchFilterModel getFilters() {
        return filters;
    }

    public void setFilters(SearchFilterModel filters) {
        this.filters = filters;
    }

}
