package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by peeyush on 28/6/16.
 */
public class OrderProduct implements Serializable {

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
    @SerializedName("status")
    private String status;

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        if(status == null){
            return "";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double tax;
    public double serviceTax;
    @SerializedName("is_free")
    public int isFree;
    //    @SerializedName("options")
//    public OrderMenuOptionReponse orderMenuOptionReponse;
    @SerializedName("options")
    public ArrayList<OrderSubProduct> subProducts;

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
    @SerializedName("discounted_price")
    public double discountedPrice;
    @SerializedName("batch_id")
    private long batchId;
    @SerializedName("slot_id")
    private long slotId;
    @SerializedName("slot_end_time")
    private String slotEndTime;
    @SerializedName("slot_start_time")
    private String slotStartTime;
    @SerializedName("date")
    private String date;
    private Product product;


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


    public ArrayList<OrderSubProduct> getSubProducts() {
        if (subProducts == null)
            subProducts = new ArrayList<>();
        return subProducts;
    }

    public void setSubProducts(ArrayList<OrderSubProduct> subProducts) {
        this.subProducts = subProducts;
    }

    public boolean isVeg() {
        return isVeg;
    }

    public void setVeg(boolean veg) {
        isVeg = veg;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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

    public boolean isPriceZero(){
        return isFree >=1 && getDiscountedPrice()==0;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = (isFree) ? 1 : 0;
    }

    public OrderProduct copy(Product product) {

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
        batchId = product.getBatchId();
        slotId = product.getSlotId();
        slotStartTime = product.getSlotStartTime();
        slotEndTime = product.getSlotEndTime();
        date = product.getDate();
        return this;
    }

    public void createDefaultFrom(Product product) {
        copy(product);

        for (SubProduct subProduct : product.getOptionResponse().getSubProducts()) {
            if (subProduct.getMinimumSelection() > 0) {
                OrderSubProduct orderSubProduct = new OrderSubProduct();
                orderSubProduct.copy(subProduct);
                for (int i = 0; i < subProduct.getMinimumSelection(); i++) {
                    if (subProduct.getMenuOptionsItems().size() > i) {
                        OrderOptionItem orderOptionItem = new OrderOptionItem();
                        orderOptionItem.copy(subProduct.getMenuOptionsItems().get(i));
                        orderSubProduct.getOrderOptionItems().add(orderOptionItem);
                    }
                }
                getSubProducts().add(orderSubProduct);
            }
        }
    }

    public double getTotalPrice() {
        double total = price;
        if (!isFree()) {
            for (OrderSubProduct orderOption : getSubProducts()) {
                for (OrderOptionItem orderOptionItem : orderOption.getOrderOptionItems()) {
                    total += orderOptionItem.getPrice();
                }
            }
        }
        return total * quantity;
    }

    public double getTotalServerPrice(){
        return price*quantity;
    }


    public String getTotalPriceString() {
        double totalPrice = getTotalPrice();
        return String.format("\u20B9 %.2f", totalPrice);
    }

    public String getMultiLineOrderOptionText() {
        StringBuffer orderOptionText = new StringBuffer();
        for (OrderSubProduct orderSubProduct : getSubProducts()) {
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
        double totalCalories = 0.00;
        for (NutritionItem nutritionItem : getNutrition().getNutritionItems()) {
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

    public void setSlotId(long slotId) {
        this.slotId = slotId;
    }

    public long getSlotId() {
        return slotId;
    }

    public String getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(String slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public String getSlotEndTime() {
        return slotEndTime;
    }

    public void setSlotEndTime(String slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
