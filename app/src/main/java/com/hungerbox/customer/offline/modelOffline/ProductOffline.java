package com.hungerbox.customer.offline.modelOffline;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.util.AppUtils;

import java.util.ArrayList;

public class ProductOffline {
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
    public MenuOptionResponseOffline optionResponse;
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
    public NutritionOffline nutrition;
    public String vendorName;
    @SerializedName("is_bookmarked")
    public boolean isBookmarked;
    @SerializedName("ordering_allowed")
    public boolean isOrderingAllowed = true;
    public boolean isTrendingItem = false;

    public boolean isDescShowing = false;


    public boolean isDescShowing() {
        return isDescShowing;
    }

    public void setDescShowing(boolean descShowing) {
        isDescShowing = descShowing;
    }

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

    public MenuOptionResponseOffline getOptionResponse() {
        if (optionResponse == null)
            return new MenuOptionResponseOffline();
        return optionResponse;
    }

    public void setOptionResponse(MenuOptionResponseOffline optionResponse) {
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

    public ProductOffline clone() {
        ProductOffline product = new ProductOffline();
        product.id = id;
        product.name = name;
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
        product.isBookmarked = isBookmarked;
        product.isOrderingAllowed = isOrderingAllowed;
        product.isTrendingItem = isTrendingItem;


        if (getOptionResponse() != null) {
            MenuOptionResponseOffline menuOptionResponse = new MenuOptionResponseOffline();
            ArrayList<SubProductOffline> cloneSubProducts = new ArrayList<>();
            for (SubProductOffline subProduct : getOptionResponse().getSubProducts()) {
                cloneSubProducts.add(subProduct.clone());
            }
            product.setOptionResponse(menuOptionResponse);
            menuOptionResponse.setSubProducts(cloneSubProducts);
            product.setOptionResponse(menuOptionResponse);
        }

        //copy nutrition
        product.nutrition = new NutritionOffline();
        product.nutrition.setNutritionItems(new ArrayList<NutritionItemOffline>());
        for (NutritionItemOffline nutritionItem : getNutrition().getNutritionItems()) {
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

    public NutritionOffline getNutrition() {
        if (nutrition == null) {
            return new NutritionOffline();
        }
        return nutrition;
    }

    public void setNutrition(NutritionOffline nutrition) {
        this.nutrition = nutrition;
    }

    public double getTotalCalories() {
        return getNutrition().getTotalCal();
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
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

    public boolean isTrendingItem() {
        return isTrendingItem;
    }

    public void setTrendingItem(boolean trendingItem) {
        this.isTrendingItem = trendingItem;
    }
}
