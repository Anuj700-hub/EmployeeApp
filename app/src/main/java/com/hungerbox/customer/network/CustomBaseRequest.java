package com.hungerbox.customer.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.hungerbox.customer.model.NetworkHeaders;
import com.hungerbox.customer.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peeyushpathak on 04/12/15.
 */
public class CustomBaseRequest<T> extends JsonRequest<T> {

    HashMap<String, String> headers;
    Class<T> responseClass;
    Response.Listener<T> listener;
    JsonParserHook<T> jsonParserHook;
    boolean getWithHeader = false;

    public CustomBaseRequest(int method, String url, String requestBody
            , HashMap<String, String> headers
            , Class<T> responseClass
            , Response.Listener<T> listener
            , Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        this.headers = headers;
        this.responseClass = responseClass;
        this.listener = listener;
    }

    public CustomBaseRequest(int method, String url, String requestBody
            , HashMap<String, String> headers
            , Class<T> responseClass
            , Response.Listener<T> listener
            , Response.ErrorListener errorListener,boolean getWithHeader) {
        super(method, url, requestBody, listener, errorListener);
        this.headers = headers;
        this.responseClass = responseClass;
        this.listener = listener;
        this.getWithHeader = getWithHeader;
    }


    public CustomBaseRequest(int method, String url, String requestBody
            , HashMap<String, String> headers
            , Class<T> responseClass
            , Response.Listener<T> listener
            , Response.ErrorListener errorListener
            , JsonParserHook<T> jsonParserHook) {
        super(method, url, requestBody, listener, errorListener);
        this.headers = headers;
        this.responseClass = responseClass;
        this.listener = listener;
        this.jsonParserHook = jsonParserHook;
    }


    public CustomBaseRequest(int method, String url, HashMap<String, String> headers
            , Class<T> responseClass
            , Response.Listener<T> listener
            , Response.ErrorListener errorListener) {
        this(method, url, "", headers, responseClass, listener, errorListener);
    }

    public CustomBaseRequest(int method, String url, HashMap<String, String> headers
            , Class<T> responseClass
            , Response.Listener<T> listener
            , Response.ErrorListener errorListener, JsonParserHook<T> jsonParserHook) {
        this(method, url, "", headers, responseClass, listener, errorListener);
        this.jsonParserHook = jsonParserHook;
    }

    public CustomBaseRequest(int method, String url, HashMap<String, String> headers
            , Class<T> responseClass
            , Response.Listener<T> listener
            , Response.ErrorListener errorListener, JsonParserHook<T> jsonParserHook,boolean getWithHeader) {
        this(method, url, "", headers, responseClass, listener, errorListener,getWithHeader);
        this.jsonParserHook = jsonParserHook;
        this.getWithHeader = getWithHeader;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return headers;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonStr;
            if (response.data == null)
                return Response.error(new ParseError());
            else {
                if(getWithHeader) {

                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    AppUtils.HbLog("ranjeet", jsonString);

                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("data", parseNormal(jsonString));
                    jsonResponse.put("headers", getParseHeader(new JSONObject(response.headers)));

                    return (Response<T>) Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));

                }else{

                    jsonStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    AppUtils.HbLog("ranjeet", jsonStr);
                    if (jsonParserHook != null) {
                        return Response.success(jsonParserHook.overRideJsonParsing(jsonStr), HttpHeaderParser.parseCacheHeaders(response));
                    } else {
                        return Response.success(parseNormal(jsonStr)
                                , HttpHeaderParser.parseCacheHeaders(response));
                    }

                }
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }


    public T parseNormal(String data) {
        return new Gson().fromJson(data, responseClass);
    }




    public NetworkHeaders getParseHeader(JSONObject data){

        return new Gson().fromJson(data.toString(),NetworkHeaders.class);
    }


    @Override
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        return super.setRetryPolicy(new DefaultRetryPolicy((2000 * 30), 0, 1f));
    }

    @Override
    protected void deliverResponse(T response) {

        listener.onResponse(response);
    }
}
