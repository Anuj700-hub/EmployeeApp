package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

/**
 * Created by peeyush on 24/6/16.
 */
public class Category extends RealmObject {

    public long id;
    @PrimaryKey
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

    public Category setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    public RealmList<Product> getProducts(Realm realm, long vendorId) {
        RealmResults<Product> productList = realm.where(Product.class).equalTo("category", name)
                .equalTo("vendorId", vendorId)
                .findAll().sort("sortOrder", Sort.DESCENDING);
        RealmList<Product> productRealmList = new RealmList<>();
        productRealmList.addAll(productList);
        return productRealmList;
    }


}
