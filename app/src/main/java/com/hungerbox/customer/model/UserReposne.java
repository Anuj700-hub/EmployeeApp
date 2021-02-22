package com.hungerbox.customer.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;

/**
 * Created by peeyush on 4/8/16.
 */
public class UserReposne {

    @SerializedName("data")
    public User user;
    @SerializedName("meta")
    public Meta meta;

    public static String api_key = "current_user";

    public static void getUserFromServer(Context context, final ResponseListener<UserReposne> responseListener, final ContextErrorListener errorListener) {
        String url = UrlConstant.USER_DETAIL;
        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<>(
                context,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        if (responseListener != null)
                            responseListener.response(responseObject);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorListener != null)
                            errorListener.handleError(errorCode, error, errorResponse);
                    }
                },
                UserReposne.class
        );
        userSimpleHttpAgent.get();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Meta getMeta() {
        if(meta == null)
            meta = new Meta();
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
