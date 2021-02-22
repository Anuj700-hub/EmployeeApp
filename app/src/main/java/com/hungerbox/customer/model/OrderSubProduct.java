package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by peeyush on 28/6/16.
 */
public class OrderSubProduct implements Serializable {

    static final long serialVersionUID = 1L;

    public long id;
    @SerializedName("option_item")
    public long optionItem;
    public String name;
    public double price;
    public String image;
    @SerializedName("items")
    public ArrayList<OrderOptionItem> orderOptionItems;

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

    public ArrayList<OrderOptionItem> getOrderOptionItems() {
        if (orderOptionItems == null)
            orderOptionItems = new ArrayList<>();
        return orderOptionItems;
    }

    public void setOrderOptionItems(ArrayList<OrderOptionItem> orderOptionItems) {
        this.orderOptionItems = orderOptionItems;
    }

    public String getItemsSelectedText() {
        String selectedItems = "";
        for (OrderOptionItem orderOptionItem : getOrderOptionItems()) {
            if (selectedItems.length() > 1)
                selectedItems += ",";

            selectedItems += orderOptionItem.getName();
        }
        return selectedItems;
    }

    public void caluclatePrice() {
        double price = 0;
        for (OrderOptionItem orderOptionItem : getOrderOptionItems()) {
            price += orderOptionItem.getPrice();
        }
        this.setPrice(price);
    }

    public String getSelectedItemPriceText() {
        caluclatePrice();
        return String.format("\u20B9 %.1f", getPrice());
    }

    public void copy(SubProduct option) {
        id = option.getId();
        name = option.getName();
    }

    public void addOptionItem(OptionItem optionItem) {
        getOrderOptionItems().add(new OrderOptionItem().copy(optionItem));
    }

    public void removeOptionItem(OptionItem optionItem) {
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
