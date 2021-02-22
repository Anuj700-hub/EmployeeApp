package com.hungerbox.customer.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.util.AppUtils;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ranjeet on 17,December,2018
 */
public class BookMarkMenu extends RealmObject {

    @Ignore
    public static final String ID = "id";

    @PrimaryKey
    @SerializedName("menu_id")
    public long id;
    @SerializedName("name")
    public String name;

    @SerializedName("vendor_id")
    public long vendorId;
    public double price;
    @SerializedName("image")
    public String image;
    @SerializedName("description")
    public String desc;
    @SerializedName("category")
    public String category;
    @SerializedName("category_sort_order")
    public int categorySortOrder;
    @SerializedName("is_veg")
    public int isVeg;
    public int type;
    public int quantity;
    @SerializedName("active")
    public int active;
    @SerializedName("is_free")
    public int isFree;
    public int free;
    @SerializedName("available_free_qty")
    public int freeQuantity;
    @SerializedName("discounted_price")
    public double discountedPrice;
    public double containerPrice;
    @SerializedName("vendorMenuOptions")
    public MenuOptionResponse optionResponse;
    @SerializedName("container_charges")
    public double containerCharge;
    @SerializedName("sort_order")
    public int sortOrder;
    @SerializedName("recommended")
    public int recommeded;

    @SerializedName("recommendation_type")
    public String recommendationType;
    @SerializedName("recommendation_id")
    public long recommendationId;
    @SerializedName("recommendation_score")
    public double recommendationScore;
    public boolean expressCheckout = false;
    @SerializedName("nutrition")
    public Nutrition nutrition;
    @SerializedName("is_bookmarked")
    public boolean isBookmarked;
    @SerializedName("ordering_allowed")
    public boolean isOrderingAllowed;
    @SerializedName("calories")
    private double calories = 0;
    @SerializedName("max_qty")
    private int maxQty = 0;
    @SerializedName("tray_names")
    RealmList<String> trayNamesList = new RealmList<>();

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public long getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(long recommendationId) {
        this.recommendationId = recommendationId;
    }

    public double getRecommendationScore() {
        return recommendationScore;
    }

    public void setRecommendationScore(double recommendationScore) {
        this.recommendationScore = recommendationScore;
    }

    public int getCategorySortOrder() {
        return categorySortOrder;
    }

    public void setCategorySortOrder(int categorySortOrder) {
        this.categorySortOrder = categorySortOrder;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public MenuOptionResponse getOptionResponse() {
        if (optionResponse == null)
            return new MenuOptionResponse();
        return optionResponse;
    }

    public void setOptionResponse(MenuOptionResponse optionResponse) {
        this.optionResponse = optionResponse;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        if (name == null)
            return "";
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getFinalPrice() {
        if (isFree())
            return 0;
        else
            return getPrice();
    }

    public String getFinalPriceText(Context context) {
        if (isFree() && getDiscountedPrice() == 0) {
            if (AppUtils.getConfig(context).isHide_price())
                return "";
            else
                return AppUtils.getConfig(context).getCompany_paid_text();
        } else if (isFree() && getDiscountedPrice() > 0)
            return String.format("\u20B9 %.2f", getDiscountedPrice());
        else
            return String.format("\u20B9 %.2f", getPrice());
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        if (desc == null)
            return "";
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        if (category == null)
            return "";
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIsVeg() {
        return isVeg;
    }

    public void setIsVeg(int isVeg) {
        this.isVeg = isVeg;
    }

    public boolean isProductVeg() {
        return getIsVeg() == 1;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getContainerPrice() {
        return containerPrice;
    }

    public void setContainerPrice(double containerPrice) {
        this.containerPrice = containerPrice;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public boolean isActive() {
        return active == 1;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getFree() {
        return free;
    }

    public boolean isFree() {
        if (isFree == 1)
            return true;
        else
            return false;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }

    public void setFreeQuantity(int freeQuantity) {
        this.freeQuantity = freeQuantity;
    }


    public int getRecommeded() {
        return recommeded;
    }

    public void setRecommeded(int recommeded) {
        this.recommeded = recommeded;
    }

    public boolean isRecommended() {
        return recommeded > 0;
    }

    public Product copy() {
        Product product = new Product();
        product.id = id;
        product.name = name;
        product.vendorId = vendorId;
        product.active = active;
        product.desc = desc;
        product.categorySortOrder = categorySortOrder;
        product.type = type;
        product.price = price;
        product.category = category;
        product.quantity = quantity;
        product.image = image;
        product.isVeg = isVeg;
        product.freeQuantity = freeQuantity;
        product.free = free;
        product.isFree = isFree;
        product.discountedPrice = discountedPrice;
        product.containerCharge = containerCharge;
        product.recommeded = recommeded;
        product.recommendationId = recommendationId;
        product.recommendationScore = recommendationScore;
        product.recommendationType = recommendationType;
        product.expressCheckout = expressCheckout;
        product.vendorName = vendorName;
        product.isBookmarked = isBookmarked;
        product.isOrderingAllowed = isOrderingAllowed;
        product.calories = calories;
        product.maxQty = maxQty;


        if (getOptionResponse() != null) {
            MenuOptionResponse menuOptionResponse = new MenuOptionResponse();
            RealmList<SubProduct> cloneSubProducts = new RealmList<>();
            for (SubProduct subProduct : getOptionResponse().getSubProducts()) {
                cloneSubProducts.add(subProduct.clone());
            }
            product.setOptionResponse(menuOptionResponse);
            menuOptionResponse.setSubProducts(cloneSubProducts);
            product.setOptionResponse(menuOptionResponse);
        }

        //copy nutrition
        product.nutrition = new Nutrition();
        product.nutrition.setNutritionItems(new RealmList<NutritionItem>());
        for (NutritionItem nutritionItem : getNutrition().getNutritionItems()) {
            product.nutrition.nutritionItems.add(nutritionItem.clone());
        }
        return product;
    }

    public boolean containsSubProducts() {
        return getOptionResponse() == null
                || getOptionResponse().getSubProducts() == null
                || getOptionResponse().getSubProducts().size() == 0;
    }


    public boolean isConfigurable() {
        return getOptionResponse().getSubProducts().size() > 0;
    }

    public double getContainerCharge() {
        return containerCharge;
    }

    public void setContainerCharge(double containerCharge) {
        this.containerCharge = containerCharge;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Nutrition getNutrition() {
        if (nutrition == null) {
            return new Nutrition();
        }
        return nutrition;
    }

    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
    }

    public double getTotalCalories() {
        return getNutrition().getTotalCal();
    }


    public String vendorName;
    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String menuType;

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean isBookmarked){
        this.isBookmarked = isBookmarked;
    }

    public boolean isOrderingAllowed(){ return isOrderingAllowed;}

    public void setOrderingAllowed(boolean orderingAllowed) {
        this.isOrderingAllowed = orderingAllowed;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public int getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }

    public RealmList<String> getTrayNamesList() {
        return trayNamesList;
    }

    public void setTrayNamesList(RealmList<String> trayNamesList) {
        this.trayNamesList = trayNamesList;
    }
}
