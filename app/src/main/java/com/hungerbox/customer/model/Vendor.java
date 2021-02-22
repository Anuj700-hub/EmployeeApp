package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by peeyush on 23/6/16.
 */
@DatabaseTable(tableName = "vendor")
public class Vendor {

    public static final String VENDOR_NAME = "vendorName";
    public static final String VENDOR_DESC = "desc";
    public static final String ID = "id";
    public static final String CORONA_SAFE = "corona_safe";
    @SerializedName("vendorMenu")
    public ProductResponse menu;
    @SerializedName("vendormenu")
    public ProductResponse newMenu;

    @SerializedName("spaces")
    public ArrayList<Product> spaces;

    @DatabaseField
    @SerializedName("sort_order")
    public int sortOrder;

    @DatabaseField(id = true)
    @SerializedName("vendor_id")
    long id;

    @DatabaseField
    @SerializedName("name")
    String vendorName = "";

    @DatabaseField
    @SerializedName("description")
    String desc;

    @DatabaseField
    @SerializedName("delivery_expected_time")
    long avgOrderTime;

    @DatabaseField
    @SerializedName("is_buffet")
    int isBuffetAvailable;

    @DatabaseField
    @SerializedName("service_tax")
    double serviceTax;

    @SerializedName("vat")
    @DatabaseField
    double tax;

    @SerializedName("cgst")
    @DatabaseField
    double cgst = 0.0;

    @DatabaseField
    @SerializedName("sgst")
    double sgst = 0.0;

    @DatabaseField
    @SerializedName("delivery_charges")
    double deliveryCharge;

    @DatabaseField
    @SerializedName("container_charges")
    double conatainerCharge;

    @DatabaseField
    @SerializedName("type")
    String type;

    boolean isRestaurant;

    @DatabaseField
    @SerializedName("min_order_amount")
    int minimumOrderAmount = 0;

    @DatabaseField
    @SerializedName("active")
    int active = 0;

    @DatabaseField
    @SerializedName("logo")
    String logoImageSrc = "";

    @DatabaseField
    @SerializedName("image")
    String imageSrc = "";

    @DatabaseField
    @SerializedName("display_time")
    String displayTime;

    @DatabaseField
    @SerializedName("start_time")
    String startTime;

    @DatabaseField
    @SerializedName("end_time")
    String endTime;

    @DatabaseField
    @SerializedName("queued_order")
    int queuedOrders;

    @DatabaseField
    @SerializedName("ordering_enabled")
    boolean isPlaceOrderEnabled = true;

    @DatabaseField
    @SerializedName("phone_number")
    String vendorPhone = "";

    @DatabaseField
    @SerializedName("rating")
    float rating;

    @DatabaseField
    @SerializedName("customer_gst")
    double customerGst;

    @DatabaseField
    @SerializedName("desk_ordering_enabled")
    int deskOrderingEnabled = 0;

    @DatabaseField
    @SerializedName("badge_type")
    String badgeType="";

    @DatabaseField
    @SerializedName("banner_image")
    String bannerImage = "";
    @Nullable
    public String cart_error="";

    public boolean isCoronaSafe(){
        if (badgeType!=null && badgeType.equalsIgnoreCase(CORONA_SAFE)){
            return true;
        }
        return false;
    }

    public String getDeliveryType(int locationDelivery){

        if(locationDelivery == 1){
            return "Delivery";
        }else if(locationDelivery == 0){
            return "Pick up";
        }else{
            switch (deskOrderingEnabled) {
                case 0:
                    return "Pick up";
                case 1:
                    return "Delivery";
                case 2:
                    return "Pick up/Delivery";
                default:
                    return "";
            }
        }
    }

    public String getType() {
        return type;
    }

    public int getDeskOrderingEnabled() {
        return deskOrderingEnabled;
    }

    public void setDeskOrderingEnabled(int deskOrderingEnabled) {
        this.deskOrderingEnabled = deskOrderingEnabled;
    }

    public double getCustomerGst() {
        return customerGst;
    }

    public void setCustomerGst(double customerGst) {
        this.customerGst = customerGst;
    }

    public String getSdkType() {
        return sdkType;
    }

    public void setSdkType(String sdkType) {
        this.sdkType = sdkType;
    }

    public boolean isBigBasketVendor(){
        if(sdkType == null )
            return false;
        return sdkType.equalsIgnoreCase(ApplicationConstants.BIG_BASKET);
    }

    @DatabaseField
    @SerializedName("sdk_type")
    String sdkType;

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

    public Vendor setId(long id) {
        this.id = id;
        return this;
    }

    public String getVendorName() {
        return vendorName;
    }

    public Vendor setVendorName(String vendorName) {
        this.vendorName = vendorName;
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

    public Vendor setAvgOrderTime(long avgOrderTime) {
        this.avgOrderTime = avgOrderTime;
        return this;
    }

    public boolean isBuffetAvailable() {
        return (isBuffetAvailable == 1);
    }

    public Vendor setBuffetAvailable(int buffetAvailable) {
        isBuffetAvailable = buffetAvailable;
        return this;
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

    public Product getMenu() {
        if (menu != null && menu.products.size() > 0)
            return menu.products.get(0);
        else
            return null;
    }

    public void setMenu(ProductResponse menu) {
        this.menu = menu;
    }

    public List<Product> getProducts() {
        if (menu != null)
            return menu.getProducts();
        else if (newMenu != null)
            return newMenu.getProducts();
        else
            return new ArrayList<>();
    }

    public String getLogoImageSrc() {
        return logoImageSrc;
    }

    public void setLogoImageSrc(String logoImageSrc) {
        this.logoImageSrc = logoImageSrc;
    }

    @Override
    public Vendor clone() {
        Vendor vendor = new Vendor();
        vendor.id = id;
        vendor.vendorName = vendorName;vendor.desc = desc;
        vendor.avgOrderTime = avgOrderTime;
        vendor.isBuffetAvailable = isBuffetAvailable;
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
        vendor.rating = rating;
        vendor.customerGst = customerGst;
        vendor.deskOrderingEnabled = deskOrderingEnabled;
        vendor.badgeType = badgeType;
        vendor.bannerImage = bannerImage;
        return vendor;
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

    public boolean isVendingMachine(){
        if(type == null){
            return false;
        }
        return type.equalsIgnoreCase(ApplicationConstants.VENDING_MACHINE);
    }

    public boolean isSlotBookingVendor(){
        if(type == null){
            return false;
        }
        return type.equalsIgnoreCase(ApplicationConstants.SLOT_BOOKING);
    }

    public boolean isSpaceBookingVendor(){
        if(type == null)
            return false;
        return type.equalsIgnoreCase(ApplicationConstants.SPACE_BOOKING);
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

    public List<Product> getTo3Products() {
        List<Product> products = getProducts();
        if (products.size() > 3)
            return products.subList(0, 3);
        else
            return products;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }


    public String getBannerImage() {
        if(bannerImage == null)
            return "";
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }
}
