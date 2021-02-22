package com.hungerbox.customer.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.model.SubProduct;

import java.lang.reflect.Type;

/**
 * Created by peeyush on 30/6/16.
 */
public class SubProductSerializer implements JsonSerializer<SubProduct> {
    @Override
    public JsonElement serialize(SubProduct src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("name", src.getName());
        return jsonObject;
    }
}
