package com.hungerbox.customer.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.model.User;

import java.lang.reflect.Type;

/**
 * Created by peeyush on 21/6/16.
 */
public class UserSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("username", src.getUserName());
        jsonObject.addProperty("password", src.getPassword());
        jsonObject.addProperty("grant_type", src.getGrantType());
        jsonObject.addProperty("client_id", src.getClientId());
        jsonObject.addProperty("client_secret", src.getClientSecret());

        return jsonObject;
    }
}
