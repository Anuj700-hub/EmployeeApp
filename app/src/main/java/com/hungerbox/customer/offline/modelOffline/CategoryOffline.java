package com.hungerbox.customer.offline.modelOffline;

import com.google.gson.annotations.SerializedName;

public class CategoryOffline {

    public long id;
    public String name;
    @SerializedName("category_sort_order")
    public int categorySortOrder;

    public int getSortOrder() {
        return categorySortOrder;
    }

    public void setSortOrder(int categorySortOrder) {
        this.categorySortOrder = categorySortOrder;
    }

    public long getId() {
        return id;
    }

    public CategoryOffline setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CategoryOffline setName(String name) {
        this.name = name;
        return this;
    }

}
