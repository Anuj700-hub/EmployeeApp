package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by peeyush on 24/6/16.
 */
public class SubProduct extends RealmObject {

    @PrimaryKey
    public long id;
    public String name;
    public String description;
    //incase of M use it for Ordering and R type will be used for only review
    public String type;
    public double price;
    @SerializedName("vendorMenuOptionItems")
    OptionItemResponse optionItemResponse;
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

    public OptionItemResponse getOptionItemResponse() {
        return optionItemResponse;
    }

    public void setOptionItemResponse(OptionItemResponse optionItemResponse) {
        this.optionItemResponse = optionItemResponse;
    }

    public RealmList<OptionItem> getMenuOptionsItems() {
        if (getOptionItemResponse() == null)
            return new RealmList<>();
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

    public SubProduct clone() {
        SubProduct subProduct = new SubProduct();
        subProduct.id = id;
        subProduct.name = name;
        subProduct.price = price;
        subProduct.minimumSelection = minimumSelection;
        subProduct.maximumSelection = maximumSelection;
        subProduct.optionItemResponse = new OptionItemResponse();
        RealmList<OptionItem> optionItems = new RealmList<>();
        subProduct.optionItemResponse.setMenuOptionsItems(optionItems);
        if (getOptionItemResponse() != null)
            for (OptionItem optionItem : getOptionItemResponse().getMenuOptionsItems()) {
                OptionItem cloned = null;
                cloned = optionItem.clone();
                optionItems.add(cloned);
            }
        return subProduct;
    }
}
