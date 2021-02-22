package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderSubProductOffline implements Serializable {

    static final long serialVersionUID = 1L;

    public long id;
    @SerializedName("option_item")
    public long optionItem;
    public String name;
    public double price;
    public String image;
    @SerializedName("items")
    public ArrayList<OrderOptionItemOffline> orderOptionItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOptionItem() {
        return optionItem;
    }

    public void setOptionItem(long optionItem) {
        this.optionItem = optionItem;
    }

    public String getName() {
        return name;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<OrderOptionItemOffline> getOrderOptionItems() {
        if (orderOptionItems == null)
            orderOptionItems = new ArrayList<>();
        return orderOptionItems;
    }

    public void setOrderOptionItems(ArrayList<OrderOptionItemOffline> orderOptionItems) {
        this.orderOptionItems = orderOptionItems;
    }

    public String getItemsSelectedText() {
        String selectedItems = "";
        for (OrderOptionItemOffline orderOptionItem : getOrderOptionItems()) {
            if (selectedItems.length() > 1)
                selectedItems += ",";

            selectedItems += orderOptionItem.getName();
        }
        return selectedItems;
    }

    public void caluclatePrice() {
        double price = 0;
        for (OrderOptionItemOffline orderOptionItem : getOrderOptionItems()) {
            price += orderOptionItem.getPrice();
        }
        this.setPrice(price);
    }

    public String getSelectedItemPriceText() {
        caluclatePrice();
        return String.format("\u20B9 %.1f", getPrice());
    }

    public void copy(SubProductOffline option) {
        id = option.getId();
        name = option.getName();
    }

    public void addOptionItem(OptionItemOffline optionItem) {
        getOrderOptionItems().add(new OrderOptionItemOffline().copy(optionItem));
    }

    public void removeOptionItem(OptionItemOffline optionItem) {
        int indexToBeRemoved = 0;
        for (int i = 0; i < getOrderOptionItems().size(); i++) {
            if (getOrderOptionItems().get(i).getId() == optionItem.getId()) {
                indexToBeRemoved = i;
                break;
            }
        }
        getOrderOptionItems().remove(indexToBeRemoved);
    }
}
