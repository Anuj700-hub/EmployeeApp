package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class VendorOfflineCopy {

    @SerializedName("sort_order")
    public int sortOrder;
    @SerializedName("vendor_id")
    long id;
    @SerializedName("name")
    String vendorName = "";
    @SerializedName("vendor_code")
    String vendorCode;
    @SerializedName("location_id ")
    long locationId;
    ArrayList<String> cuisines = new ArrayList<>();
    @SerializedName("description")
    String desc;
    @SerializedName("search_keyword")
    String cuisinesString = "";
    @SerializedName("delivery_expected_time")
    long avgOrderTime;
    @SerializedName("is_buffet")
    int isBuffetAvailable;
    double buffetPrice;
    boolean isAvailable;
    @SerializedName("service_tax")
    double serviceTax;
    @SerializedName("vat")
    double tax;
    @SerializedName("cgst")
    double cgst = 0.0;
    @SerializedName("sgst")
    double sgst = 0.0;
    @SerializedName("delivery_charges")
    double deliveryCharge;
    @SerializedName("container_charges")
    double conatainerCharge;
    @SerializedName("type")
    String type;
    boolean isRestaurant;
    @SerializedName("min_order_amount")
    int minimumOrderAmount = 0;
    @SerializedName("active")
    int active;
    @SerializedName("logo")
    String logoImageSrc = "";
    @SerializedName("image")
    String imageSrc = "";
    @SerializedName("display_time")
    String displayTime;
    @SerializedName("start_time")
    String startTime;
    @SerializedName("end_time")
    String endTime;
    @SerializedName("queued_order")
    int queuedOrders;
    @SerializedName("ordering_enabled")
    boolean isPlaceOrderEnabled = true;
    @SerializedName("phone_number")
    String vendorPhone = "";
    @SerializedName("take_away_available")
    int takeAwayAvailable = 0;
    @SerializedName("rating")
    float rating;

    public String getImageSrc() {
        if (imageSrc == null)
            return "";
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getVendorPhone() {
        if (vendorPhone == null)
            return "";
        else
            return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public boolean isPlaceOrderEnabled() {
        return isPlaceOrderEnabled;
    }

    public void setPlaceOrderEnabled(boolean placeOrderEnabled) {
        isPlaceOrderEnabled = placeOrderEnabled;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public long getId() {
        return id;
    }

    public VendorOfflineCopy setId(long id) {
        this.id = id;
        return this;
    }

    public String getVendorName() {
        return vendorName;
    }

    public VendorOfflineCopy setVendorName(String vendorName) {
        vendorName = vendorName;
        return this;
    }

    public ArrayList<String> getCuisines() {
        return cuisines;
    }

    public VendorOfflineCopy setCuisines(ArrayList<String> cuisines) {
        this.cuisines = cuisines;
        return this;
    }

    public String getAvgOrderTimeText() {
        return String.format("%d mins", getAvgOrderTime());
    }

    public long getAvgOrderTime() {
        if (avgOrderTime > 0)
            return avgOrderTime;
        else {
            int time = (int) (Math.random() * 20);
            time += 10;
            return time;
        }
    }

    public VendorOfflineCopy setAvgOrderTime(long avgOrderTime) {
        this.avgOrderTime = avgOrderTime;
        return this;
    }

    public boolean isBuffetAvailable() {
        return (isBuffetAvailable == 1);
    }

    public VendorOfflineCopy setBuffetAvailable(int buffetAvailable) {
        isBuffetAvailable = buffetAvailable;
        return this;
    }

    public double getBuffetPrice() {
        return buffetPrice;
    }

    public VendorOfflineCopy setBuffetPrice(double buffetPrice) {
        this.buffetPrice = buffetPrice;
        return this;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public VendorOfflineCopy setAvailable(boolean available) {
        isAvailable = available;
        return this;
    }

    public String getCuisinesString() {
        return cuisinesString;
    }

    public void setCuisinesString(String cuisinesString) {
        this.cuisinesString = cuisinesString;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getCgst() {
        return cgst;
    }

    public void setCgst(double cgst) {
        this.cgst = cgst;
    }

    public double getSgst() {
        return sgst;
    }

    public void setSgst(double sgst) {
        this.sgst = sgst;
    }

    public double getConatainerCharge() {
        return conatainerCharge;
    }

    public void setConatainerCharge(double conatainerCharge) {
        this.conatainerCharge = conatainerCharge;
    }

    public String getDesc() {
        if (desc == null)
            return "";
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getLogoImageSrc() {
        return logoImageSrc;
    }

    public void setLogoImageSrc(String logoImageSrc) {
        this.logoImageSrc = logoImageSrc;
    }

    @Override
    public VendorOffline clone() {
        VendorOffline vendor = new VendorOffline();
        vendor.id = id;
        vendor.vendorName = vendorName;
        vendor.vendorCode = vendorCode;
        vendor.desc = desc;
        vendor.cuisinesString = cuisinesString;
        vendor.avgOrderTime = avgOrderTime;
        vendor.isBuffetAvailable = isBuffetAvailable;
        vendor.buffetPrice = buffetPrice;
        vendor.isAvailable = isAvailable;
        vendor.tax = tax;
        vendor.cgst = cgst;
        vendor.sgst = sgst;
        vendor.serviceTax = serviceTax;
        vendor.deliveryCharge = deliveryCharge;
        vendor.conatainerCharge = conatainerCharge;
        vendor.type = type;
        vendor.isRestaurant = isRestaurant;
        vendor.minimumOrderAmount = minimumOrderAmount;
        vendor.active = active;
        vendor.logoImageSrc = logoImageSrc;
        vendor.imageSrc = imageSrc;
        vendor.displayTime = displayTime;
        vendor.startTime = startTime;
        vendor.endTime = endTime;
        vendor.queuedOrders = queuedOrders;
        vendor.sortOrder = sortOrder;
        vendor.isPlaceOrderEnabled = isPlaceOrderEnabled;
        vendor.vendorPhone = vendorPhone;
        vendor.takeAwayAvailable = takeAwayAvailable;
        vendor.rating = rating;

        return vendor;
    }


    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public int getIsBuffetAvailable() {
        return isBuffetAvailable;
    }

    public void setIsBuffetAvailable(int isBuffetAvailable) {
        this.isBuffetAvailable = isBuffetAvailable;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public boolean isRestaurant() {
        if (type == null)
            return false;
        return type.equalsIgnoreCase(ApplicationConstants.VENDOR_TYPE_RESTAURANT);
    }

    public void setRestaurant(boolean restaurant) {
        isRestaurant = restaurant;
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(int minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public double getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(double serviceTax) {
        this.serviceTax = serviceTax;
    }

    public boolean isVendorTakingOrder() {
        if (getDisplayTime() == null || getDisplayTime().isEmpty() || getEndTime() == null || getEndTime().isEmpty()) {
            return true;
        }
        long displayTime = DateTimeUtil.getTodaysTimeFromString(getDisplayTime());
        long endTime = DateTimeUtil.getTodaysTimeFromString(getEndTime());
        long currentTime = DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis();
        return currentTime > displayTime && currentTime < endTime;
    }

    public String getMinimumOrderAmountStr() {
        return String.format("₹%d min. order", getMinimumOrderAmount());
    }

    public int getQueuedOrders() {
        return queuedOrders;
    }

    public void setQueuedOrders(int queuedOrders) {
        this.queuedOrders = queuedOrders;
    }


    public boolean isTakeAwayAvailable() {
        return takeAwayAvailable > 0;
    }


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getRatingText() {
        if (rating == 0)
            return "...";
        return String.format("%.1f", rating);
    }

}