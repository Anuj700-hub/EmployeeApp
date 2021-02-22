package com.hungerbox.customer.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 1/17/18.
 */

public class NavItemModel implements Serializable, Comparable<NavItemModel> {
    @SerializedName("key")
    String key;
    @SerializedName("label")
    String name;
    @SerializedName("sort_order")
    int sortOrder = 0;

    @SerializedName("is_new")
    String isNew = "";

    @SerializedName("icon")
    String icon = "";

    @SerializedName("is_more")
    Boolean isMore = false;

    public String isNew() {
        return isNew;
    }

    public void setNew(String aNew) {
        isNew = aNew;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsMore() {
        return isMore;
    }

    public void setMore(Boolean more) {
        isMore = more;
    }

    @SerializedName("url")
    String url;


    public NavItemModel() {
        this.key = "";
        this.name = "";
    }


    public NavItemModel(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        if (key == null)
            key = "";
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NavItemModel) {
            NavItemModel navItemModel = (NavItemModel) obj;
            return key.equals(navItemModel.getKey());
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull NavItemModel navItemModel) {
        return sortOrder - navItemModel.sortOrder;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
