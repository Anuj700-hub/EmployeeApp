package com.hungerbox.customer.model;

import androidx.annotation.DrawableRes;

import com.hungerbox.customer.R;

import java.io.Serializable;

/**
 * Created by peeyush on 22/6/16.
 */
public class NavigationItem{

    long id;
    String text = "";
    String image;
    int localImage;
    boolean isTinted = false;
    int tintColor;
    String key;
    String isNew = "";
    String icon = "";
    boolean isMore = false;

    public String getUrl() {
        return url;
    }

    public NavigationItem setUrl(String url) {
        this.url = url;
        return this;
    }

    String url = "";

    public long getId() {
        return id;
    }

    public NavigationItem setId(long id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public NavigationItem setText(String text) {
        this.text = text;
        return this;
    }

    public String getImage() {
        return image;
    }

    public NavigationItem setImage(String image) {
        this.image = image;
        return this;
    }

    public int getLocalImage() {
        return localImage;
    }

    public NavigationItem setLocalImage(int localImage) {
        this.localImage = localImage;
        return this;
    }

    public boolean isTinted() {
        return isTinted;
    }

    public NavigationItem setTinted(boolean tinted) {
        isTinted = tinted;
        return this;
    }

    public String getKey() {
        return key;
    }

    public NavigationItem setKey(String key) {
        this.key = key;
        return this;
    }

    public int getTintColor() {
        if (tintColor == 0)
            return R.color.colorPrimary;
        else
            return tintColor;
    }

    public NavigationItem setTintColor(int tintColor) {
        this.tintColor = tintColor;
        return this;
    }

    public String isNew() {
        return isNew;
    }

    public NavigationItem setNew(String aNew) {
        isNew = aNew;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public NavigationItem setIcon(String icon) {
        this.icon = icon;
        return this;
    }
}
