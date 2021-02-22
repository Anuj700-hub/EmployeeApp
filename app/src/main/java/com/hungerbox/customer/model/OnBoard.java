package com.hungerbox.customer.model;

/**
 * Created by peeyush on 7/9/16.
 */
public class OnBoard {

    String title;
    int textId;
    int imageId;

    public String getText() {
        return text;
    }

    public OnBoard setText(String text) {
        this.text = text;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public OnBoard setHeader(String header) {
        this.header = header;
        return this;
    }

    String text;
    String header;
    int iconId;
    boolean isContinue;
    int headerBodyId;
    String companyLogoUrl="";

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public OnBoard setCompanyLogoUrl(String logoUrl) {
        this.companyLogoUrl = logoUrl;
        return this;
    }

    public boolean isContinue() {
        return isContinue;
    }

    public OnBoard setContinue(boolean aContinue) {
        isContinue = aContinue;
        return this;
    }

    public int getIconId() {
        return iconId;
    }

    public OnBoard setIconId(int iconId) {
        this.iconId = iconId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public OnBoard setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getTextId() {
        return textId;
    }

    public OnBoard setTextId(int textId) {
        this.textId = textId;
        return this;
    }

    public int getImageId() {
        return imageId;
    }

    public OnBoard setImageId(int imageId) {
        this.imageId = imageId;
        return this;
    }

    public int getHeaderBodyId(){return headerBodyId;}

    public OnBoard setHeaderBody(int headerBodyId){
        this.headerBodyId = headerBodyId;
        return this;
    }
}
