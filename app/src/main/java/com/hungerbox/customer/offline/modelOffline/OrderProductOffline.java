package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class OrderProductOffline implements Serializable {

    static final long serialVersionUID = 1L;

    @SerializedName("product_id")
    public long id;
    @SerializedName("order_item_id")
    public long orderItemId;
    @SerializedName("product_name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("price")
    public double price;
    @SerializedName("logo")
    public String image;
    @SerializedName("type")
    public int type;
    @SerializedName("qty")
    public int quantity;
    @SerializedName("is_veg")
    public boolean isVeg;
    @SerializedName("container_charge")
    public double conatinerCharge;
    public double tax;
    public double serviceTax;
    @SerializedName("is_free")
    public int isFree;
    //    @SerializedName("options")
//    public OrderMenuOptionReponse orderMenuOptionReponse;
    @SerializedName("options")
    public ArrayList<OrderSubProductOffline> subProducts;

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
    @SerializedName("discounted_price")
    public double discountedPrice;

    private transient ProductOffline product;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceString() {
        return String.format("\u20B9 %.2f", price);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    @Override
    public String toString() {
        return String.format("%dX %s: %.2f", getQuantity(), getName(), (getPrice() * getQuantity()));
    }

    public void addQuantity() {
        quantity += 1;
    }

    public long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }

//    public OrderMenuOptionReponse getOrderMenuOptionReponse() {
//        if(orderMenuOptionReponse==null)
//            orderMenuOptionReponse = new OrderMenuOptionReponse();
//        return orderMenuOptionReponse;
//    }
//
//    public void setOrderMenuOptionReponse(OrderMenuOptionReponse orderMenuOptionReponse) {
//        this.orderMenuOptionReponse = orderMenuOptionReponse;
//    }


    public void createDefaultFrom(ProductOffline product) {
        copy(product);

        for (SubProductOffline subProduct : product.getOptionResponse().getSubProducts()) {
            if (subProduct.getMinimumSelection() > 0) {
                OrderSubProductOffline orderSubProduct = new OrderSubProductOffline();
                orderSubProduct.copy(subProduct);
                for (int i = 0; i < subProduct.getMinimumSelection(); i++) {
                    if (subProduct.getMenuOptionsItems().size() > i) {
                        OrderOptionItemOffline orderOptionItem = new OrderOptionItemOffline();
                        orderOptionItem.copy(subProduct.getMenuOptionsItems().get(i));
                        orderSubProduct.getOrderOptionItems().add(orderOptionItem);
                    }
                }
                getSubProducts().add(orderSubProduct);
            }
        }
    }

    public ArrayList<OrderSubProductOffline> getSubProducts() {
        if (subProducts == null)
            subProducts = new ArrayList<>();
        return subProducts;
    }

    public void setSubProducts(ArrayList<OrderSubProductOffline> subProducts) {
        this.subProducts = subProducts;
    }

    public boolean isVeg() {
        return isVeg;
    }

    public void setVeg(boolean veg) {
        isVeg = veg;
    }

    public ProductOffline getProduct() {
        return product;
    }

    public void setProduct(ProductOffline product) {
        this.product = product;
    }

    public double getConatinerCharge() {
        return conatinerCharge;
    }

    public void setConatinerCharge(double conatinerCharge) {
        this.conatinerCharge = conatinerCharge;
    }

    public boolean isFree() {
        return isFree >= 1 ? true : false;
    }

    public boolean isPriceZero() {
        return isFree >= 1 && getDiscountedPrice() == 0;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = (isFree) ? 1 : 0;
    }

    public OrderProductOffline copy(ProductOffline product) {

        id = product.id;
        orderItemId = new Date().getTime();
        name = product.getName();
        description = product.getDesc();
        if (product.isFree() && product.getDiscountedPrice() >= 0)
            price = product.getDiscountedPrice();
        else
            price = product.getPrice();

        isVeg = (product.getIsVeg() == 1) ? true : false;
        quantity = 1;
        isFree = product.isFree() ? 1 : 0;
        conatinerCharge = product.getContainerCharge();
        recommeded = product.getRecommeded();
        recommendationId = product.getRecommendationId();
        recommendationScore = product.getRecommendationScore();
        recommendationType = product.getRecommendationType();
        expressCheckout = product.expressCheckout;
        discountedPrice = product.discountedPrice;
        return this;
    }


    public double getTotalPrice() {
        double total = price;
        if (!isFree()) {
            for (OrderSubProductOffline orderOption : getSubProducts()) {
                for (OrderOptionItemOffline orderOptionItem : orderOption.getOrderOptionItems()) {
                    total += orderOptionItem.getPrice();
                }
            }
        }
        return total * quantity;
    }

    public double getTotalServerPrice() {
        return price * quantity;
    }


    public String getTotalPriceString() {
        double totalPrice = getTotalPrice();
        return String.format("\u20B9 %.2f", totalPrice);
    }

    public String getMultiLineOrderOptionText() {
        StringBuffer orderOptionText = new StringBuffer();
        for (OrderSubProductOffline orderSubProduct : getSubProducts()) {
            if (orderSubProduct.getOrderOptionItems().size() > 0) {
                orderOptionText.append(orderSubProduct.getName() + " : ");
                for (int j = 0; j < orderSubProduct.getOrderOptionItems().size(); j++) {
                    orderOptionText.append(orderSubProduct.getOrderOptionItems().get(j).getName());
                    if (j < orderSubProduct.getOrderOptionItems().size() - 1) {
                        orderOptionText.append(',');
                    }
                }
                orderOptionText.append('\n');
            }
        }
        if (orderOptionText.length() > 2)
            orderOptionText.deleteCharAt(orderOptionText.length() - 1);

        return orderOptionText.toString();
    }

    public int getRecommeded() {
        return recommeded;
    }

    public void setRecommeded(int recommeded) {
        this.recommeded = recommeded;
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

    public boolean isExpressCheckout() {
        return expressCheckout;
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
        double totalCalories = 0.00;
        for (NutritionItemOffline nutritionItem : getNutrition().getNutritionItems()) {
            totalCalories += nutritionItem.getCalorie();
        }
        return totalCalories;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}