package com.hungerbox.customer.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.NetworkHeaders;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.prelogin.activity.HBWelcomeActivity;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

import in.juspay.juspaysafe.JuspaySafeBrowser;
import io.realm.RealmObject;


/**
 * Created by peeyushpathak on 04/12/15.
 */
public class BaseHttpAgent<T> {
    public static final int POST = 1;
    public static final int PUT = 2;
    private static final String TAG = BaseHttpAgent.class.getSimpleName();
    private static String ORIGIN_HEADER = "x-origin";
    private static String VERSION_HEADER = "x-version";
    private static String ORIGIN_HEADER_ANDROID = "android_app";
    private static String ORIGIN_HEADER_CAFE = "cafeteria";
    private static String HEADER_OFFLINE = "x-api-key";
    private static String HEADER_OFFLINE_TOKEN = "iM89nKGnuH48Fem6ELTyI5KcsimcxDe8aKw0Y04B";
    private static String HB_DEVICE_ID = "hb-device-id";
    public static final String OPENURL_TOKEN = "$2b$10$LKXPsmPARhGsB6T6RlrUtutQZrHnA26SIUgPIAxLypSfIS5IftVPC";
    Context context;
    HashMap<String, String> headers;
    String url;
    ResponseListener<T> responseListener;
    ResponseListenerNew<T> responseListenerNew;
    ContextErrorListener contextErrorListener;
    Class<T> responseClass;
    JsonParserHook jsonParserHook;
    ErrorListener errorListener = new ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            AppUtils.HbLog("Volley", url);
            if (contextErrorListener != null) {
                String errorMessage = null;
                int code;
                String message = "";
                try {
                    if (error != null && error.networkResponse != null && error.networkResponse.data != null) {
                        code = error.networkResponse.statusCode;
                        if (code == 401) {
                            try{
                                JuspaySafeBrowser.performLogout(context);
                            }catch (Exception exp){
                                exp.printStackTrace();
                            }
                            if (context instanceof Activity) {
                                Intent intent = null;
                                SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
                                SharedPrefUtil.remove(ApplicationConstants.PREF_USER_ID);

                                try {
                                    if (!DbHandler.isStarted())
                                        DbHandler.start(context);
                                    DbHandler.getDbHandler(context).deleteDataTable();
                                }catch (Exception e ){
                                    e.printStackTrace();
                                }
                                Config config = AppUtils.getConfig(context);
                                if (config == null || config.getCompany_id() == -1){
                                    intent = new Intent(context, MainActivity.class);
                                }else {
                                    intent = new Intent(context, HBWelcomeActivity.class);
                                }

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                if (context instanceof Activity) {
                                    Activity activity = (Activity) context;
                                    activity.finish();
                                }
                            } else {
                                errorMessage = new String(error.networkResponse.data, "UTF-8");
                                contextErrorListener.handleError(code, message, null);
                            }
                        } else {
                            errorMessage = new String(error.networkResponse.data, "UTF-8");
                            AppUtils.HbLog("http_error", errorMessage);
                            Gson gson = new Gson();
                            try {
                                ErrorResponse errorResponse = gson.fromJson(errorMessage, ErrorResponse.class);
                                contextErrorListener.handleError(code, errorResponse.message, errorResponse);
                                if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"))
                                    AppUtils.showToast(errorResponse.message, true, 0);
                            } catch (Exception e) {
                                AppUtils.HbLog("peeyush_debug", "error in parsing the error");
                                contextErrorListener.handleError(code, "cant parse the error from server", null);
                                if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"))
                                    AppUtils.showToast("cant parse the error from server", true, 0);
                            }

                        }

                    } else if (error instanceof ParseError) {
                        contextErrorListener.handleError(ContextErrorListener.PARSE_ERROR, "wrong data response from server", null);
                    } else if (error instanceof NoConnectionError) {
                        contextErrorListener.handleError(ContextErrorListener.NO_NET_CONNECTION, "We are facing some connectivity issues", null);
                    } else if (error instanceof TimeoutError) {
                        contextErrorListener.handleError(ContextErrorListener.TIMED_OUT, "We are facing some connectivity issues", null);
                    } else {
                        contextErrorListener.handleError(ContextErrorListener.DONT_KNOW, "just out of sight", null);
                    }

                } catch (Exception e) {
                    contextErrorListener.handleError(ContextErrorListener.DONT_KNOW, "just out of sight", null);
                }
            }
        }
    };

    public BaseHttpAgent(Context activity, HashMap<String, String> headers, String url
            , ResponseListener<T> responseListener
            , ContextErrorListener errorListener, Class<T> responseClass) {
        this.context = activity;
        this.headers = headers;
        this.url = url;
        this.responseListener = responseListener;
        this.contextErrorListener = errorListener;
        this.responseClass = responseClass;
        this.errorListener.setUrl(url);
        headers.put("content-type", "application/json");
        headers.put(HEADER_OFFLINE,HEADER_OFFLINE_TOKEN);

        try {

            String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
            long userId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
            if (!authToken.isEmpty()) {
                headers.put("Authorization", "Bearer " + authToken);
                headers.put("user", String.valueOf(userId));
            }else{
                headers.put("Authorization",""+OPENURL_TOKEN);
            }
            headers.put("Content-type", "application/json");
            if (AppUtils.isCafeApp()) {
                headers.put(ORIGIN_HEADER, ORIGIN_HEADER_CAFE);
                if(SharedPrefUtil.getLong(ApplicationConstants.HB_DEVICE_ID,-1)!=-1) {
                    headers.put(HB_DEVICE_ID,SharedPrefUtil.getLong(ApplicationConstants.HB_DEVICE_ID,-1)+"");
                }
            }
            else
                headers.put(ORIGIN_HEADER, ORIGIN_HEADER_ANDROID);

            headers.put(VERSION_HEADER, AppUtils.getVersionName());
        } catch (Exception e) {
            AppUtils.HbLog(TAG, "caught exception in BaseHttpAgent : " + e);
        }
    }

    public BaseHttpAgent(Context activity, HashMap<String, String> headers, String url
            , ResponseListenerNew<T> responseListener
            , ContextErrorListener errorListener, Class<T> responseClass) {
        this.context = activity;
        this.headers = headers;
        this.url = url;
        this.responseListenerNew = responseListener;
        this.contextErrorListener = errorListener;
        this.responseClass = responseClass;
        this.errorListener.setUrl(url);
        headers.put("content-type", "application/json");
        headers.put(HEADER_OFFLINE,HEADER_OFFLINE_TOKEN);

        try {

            String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
            long userId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
            if (!authToken.isEmpty()) {
                headers.put("Authorization", "Bearer " + authToken);
                headers.put("user", String.valueOf(userId));
            }else{
                headers.put("Authorization",""+OPENURL_TOKEN);
            }
            headers.put("Content-type", "application/json");
            if (AppUtils.isCafeApp()) {
                headers.put(ORIGIN_HEADER, ORIGIN_HEADER_CAFE);
                if(SharedPrefUtil.getLong(ApplicationConstants.HB_DEVICE_ID,-1)!=-1) {
                    headers.put(HB_DEVICE_ID,SharedPrefUtil.getLong(ApplicationConstants.HB_DEVICE_ID,-1)+"");
                }
            }
            else
                headers.put(ORIGIN_HEADER, ORIGIN_HEADER_ANDROID);

            headers.put(VERSION_HEADER, AppUtils.getVersionName());
        } catch (Exception e) {
            AppUtils.HbLog(TAG, "caught exception in BaseHttpAgent : " + e);
        }
    }

    public void get() {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "get=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<T>(Request.Method.GET,
                url, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener,
                jsonParserHook
        );

        customBaseRequest.setTag(NetworkConstants.GET);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void getWithHeader() {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "get=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<T>(Request.Method.GET,
                url, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListenerNew != null){
                            try {
                                NetworkHeaders headers = (NetworkHeaders) ((JSONObject) response).get("headers");
                                responseListenerNew.response((T) ((JSONObject) response).get("data"),headers);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                },
                errorListener,
                jsonParserHook,
                true
        );

        customBaseRequest.setTag(NetworkConstants.GET);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void getWithHeader(String tag) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "get=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<T>(Request.Method.GET,
                url, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListenerNew != null){
                            try {
                                NetworkHeaders headers = (NetworkHeaders) ((JSONObject) response).get("headers");
                                responseListenerNew.response((T) ((JSONObject) response).get("data"),headers);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                },
                errorListener,
                jsonParserHook,
                true
        );

        customBaseRequest.setTag(tag);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void getWithoutHeader() {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "get=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<T>(Request.Method.GET,
                url,new HashMap<String, String>(), responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener,
                jsonParserHook
        );

        customBaseRequest.setTag(NetworkConstants.GET);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void getWithoutHeader(String tag) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "get=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<T>(Request.Method.GET,
                url,new HashMap<String, String>(), responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener,
                jsonParserHook
        );

        customBaseRequest.setTag(tag);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void get(String Tag) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "get=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<T>(Request.Method.GET,
                url, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener,
                jsonParserHook
        );

        customBaseRequest.setTag(Tag);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }


    public void post(Object requestObject, HashMap<String, JsonSerializer> jsonSerializerHashMap) {
        StringifyTask stringifyTask = new StringifyTask(POST);
        stringifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, requestObject, jsonSerializerHashMap);
    }

    public void post(Object requestObject, HashMap<String, JsonSerializer> jsonSerializerHashMap, String tag) {
        StringifyTask stringifyTask = new StringifyTask(POST, tag);
        stringifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, requestObject, jsonSerializerHashMap);
    }

    public void post(String requestData) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), requestData);

        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "post=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<>(Request.Method.POST,
                url, requestData, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener);

        customBaseRequest.setTag(NetworkConstants.POST);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void post(String requestData, String tag) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), requestData);

        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "post=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<>(Request.Method.POST,
                url, requestData, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener);

        customBaseRequest.setTag(tag);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void postWithHeader(String requestData) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), requestData);

        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "post=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<>(Request.Method.POST,
                url, requestData, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListenerNew != null) {
                            try {
                                NetworkHeaders headers = (NetworkHeaders) ((JSONObject) response).get("headers");
                                responseListenerNew.response((T) ((JSONObject) response).get("data"),headers);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                },
                errorListener,true);

        customBaseRequest.setTag(NetworkConstants.POST);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void postWithHeader(String requestData, String tag) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), requestData);

        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "post=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<>(Request.Method.POST,
                url, requestData, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListenerNew != null) {
                            try {
                                NetworkHeaders headers = (NetworkHeaders) ((JSONObject) response).get("headers");
                                responseListenerNew.response((T) ((JSONObject) response).get("data"),headers);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                },
                errorListener,true);

        customBaseRequest.setTag(tag);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void put(Object requestObject, HashMap<String, JsonSerializer> serializerHashMap) {
        StringifyTask stringifyTask = new StringifyTask(PUT);
        stringifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, requestObject, serializerHashMap);
    }

    public void put(String requestData) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "get=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<>(Request.Method.PUT,
                url, requestData, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener);

        customBaseRequest.setTag(NetworkConstants.PUT);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    public void put(String requestData, String tag) {
        AppUtils.HbLog(BaseHttpAgent.class.getSimpleName(), "headers=>+" + headers.toString() + "get=>" + url);
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<>(Request.Method.PUT,
                url, requestData, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener);

        customBaseRequest.setTag(tag);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }



    public void delete() {
        CustomBaseRequest<T> customBaseRequest = new CustomBaseRequest<>(Request.Method.DELETE,
                url, headers, responseClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        if (responseListener != null)
                            responseListener.response(response);
                    }
                },
                errorListener);
        VolleyRequestFactory.INSTANCE.getMRequestQueue().add(customBaseRequest);
    }

    class StringifyTask extends AsyncTask<Object, Integer, String> {

        int type;
        String tag = "";

        public StringifyTask(int type) {
            this.type = type;
        }

        public StringifyTask(int type, String tag) {
            this.type = type;
            this.tag = tag;
        }

        @Override
        protected String doInBackground(Object... params) {
            Object object = params[0];
            HashMap<String, JsonSerializer> jsonSerializerHashMap = (HashMap<String, JsonSerializer>) params[1];

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    });
            for (String key : jsonSerializerHashMap.keySet()) {
                try {
                    gsonBuilder.registerTypeAdapter(Class.forName(key), jsonSerializerHashMap.get(key));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Gson gson = gsonBuilder.create();
            return gson.toJson(object);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null)
                s = "";
            super.onPostExecute(s);
            AppUtils.HbLog("peeyush", s);
            switch (type) {
                case POST:
                    if(tag.trim().isEmpty())
                        post(s);
                    else
                        post(s, tag);
                    break;
                case PUT:
                    put(s);
                    break;
            }
        }
    }
}
