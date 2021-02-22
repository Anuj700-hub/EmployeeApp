package com.hungerbox.customer.network;

import android.content.Context;

import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.util.AppUtils;

import java.util.HashMap;

/**
 * Created by peeyushpathak on 05/12/15.
 */
public class SimpleHttpAgent<T> extends BaseHttpAgent<T> {


    public SimpleHttpAgent(Context context, String url, ResponseListener<T> responseListener
            , Class<T> responseClass) {
        this(context, new HashMap<String, String>(), url, responseListener
                , new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        AppUtils.HbLog("SIMPLE_ERROR", errorCode + " : " + error);
                    }
                }
                , responseClass);
    }

    public SimpleHttpAgent(Context activity, String url, ResponseListener<T> responseListener, ContextErrorListener errorListener, Class<T> responseClass) {
        super(activity, new HashMap<String, String>(), url, responseListener, errorListener, responseClass);
    }


    public SimpleHttpAgent(Context activity, String url, ResponseListenerNew<T> responseListener, ContextErrorListener errorListener, Class<T> responseClass) {
        super(activity, new HashMap<String, String>(), url, responseListener, errorListener, responseClass);
    }

    public SimpleHttpAgent(Context activity, HashMap<String, String> headers, String url, ResponseListener<T> responseListener, ContextErrorListener errorListener, Class<T> responseClass) {
        super(activity, headers, url, responseListener, errorListener, responseClass);
    }
}
