package com.hungerbox.customer.model;

import io.realm.RealmObject;

/**
 * Created by peeyush on 23/6/16.
 */
public class RealmString extends RealmObject {
    public String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
