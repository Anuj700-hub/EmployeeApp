package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubProductOffline {
    public long id;
    public String name;
    public String description;
    //incase of M use it for Ordering and R type will be used for only review
    public String type;
    public double price;
    @SerializedName("vendorMenuOptionItems")
    OptionItemResponseOffline optionItemResponse;
    @SerializedName("min_select")
    int minimumSelection;
    @SerializedName("max_select")
    int maximumSelection;


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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OptionItemResponseOffline getOptionItemResponse() {
        return optionItemResponse;
    }

    public void setOptionItemResponse(OptionItemResponseOffline optionItemResponse) {
        this.optionItemResponse = optionItemResponse;
    }

    public ArrayList<OptionItemOffline> getMenuOptionsItems() {
        if (getOptionItemResponse() == null)
            return new ArrayList<>();
        return getOptionItemResponse().getMenuOptionsItems();
    }


    public int getMinimumSelection() {
        return minimumSelection;
    }

    public void setMinimumSelection(int minimumSelection) {
        this.minimumSelection = minimumSelection;
    }

    public int getMaximumSelection() {
        return maximumSelection;
    }

    public void setMaximumSelection(int maximumSelection) {
        this.maximumSelection = maximumSelection;
    }

    public SubProductOffline clone() {
        SubProductOffline subProduct = new SubProductOffline();
        subProduct.id = id;
        subProduct.name = name;
        subProduct.price = price;
        subProduct.minimumSelection = minimumSelection;
        subProduct.maximumSelection = maximumSelection;
        subProduct.optionItemResponse = new OptionItemResponseOffline();
        ArrayList<OptionItemOffline> optionItems = new ArrayList<>();
        subProduct.optionItemResponse.setMenuOptionsItems(optionItems);
        if (getOptionItemResponse() != null)
            for (OptionItemOffline optionItem : getOptionItemResponse().getMenuOptionsItems()) {
                OptionItemOffline cloned = null;
                cloned = optionItem.clone();
                optionItems.add(cloned);
            }
        return subProduct;
    }
}
