package com.hungerbox.customer.ksnetwork.ksnetworklib;

import android.content.Context;

import com.google.gson.Gson;
import com.hungerbox.customer.bluetooth.Model.ContactTracingData;
import com.hungerbox.customer.ksnetwork.KSContextErrorListener;
import com.hungerbox.customer.ksnetwork.KSErrorResponse;
import com.hungerbox.customer.ksnetwork.KSResponseListener;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.util.AppUtils;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class KSHttpAgent {

    public static <T> void callNetworkLib(KSApiService.apis apis, Object input, Object body,
                                          int type,
                                          final Context context,
                                          final KSResponseListener<T> responseListener,
                                          final KSContextErrorListener errorListener) {
        Call<T> call;
        if (type == ApiType.GET) {
            call = getCall(context, input, apis);
        } else {
            call = postCall(context, body, input, apis);

        }
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {

                if (response.isSuccessful()) {
                    responseListener.response(response.body());
                } else {

                    int code = response.code();
                    if (code == 401) {

                        if (context instanceof Context) {
                            AppUtils.doLogout(context);
                        }
                        try {
                            KSApiClient.resetApiClient();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } else {

                        Gson gson = new Gson();
                        try {
                            KSErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), KSErrorResponse.class);
                            errorListener.handleError(response.code(), errorResponse.getMessage(), errorResponse);

                        } catch (Exception e) {
                            e.printStackTrace();
                            errorListener.handleError(response.code(), "cant parse the error from server", null);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<T> call, Throwable e) {
                try {
                    if (e instanceof HttpException) {
                        ResponseBody responseBody = ((HttpException) e).response().errorBody();
                        int code = ((HttpException) e).code();
                        if (responseBody != null) {
                            if (code == 401) {

                                if (context instanceof Context) {
                                    AppUtils.doLogout(context);
                                }
                                try {
                                    KSApiClient.resetApiClient();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            } else {
                                Gson gson = new Gson();
                                try {
                                    KSErrorResponse errorResponse = gson.fromJson(responseBody.string(), KSErrorResponse.class);
                                    errorListener.handleError(code, errorResponse.getMessage(), errorResponse);

                                } catch (IOException ex) {
                                    errorListener.handleError(code, "cant parse the error from server", null);
                                }
                            }
                        } else {
                            errorListener.handleError(KSContextErrorListener.DONT_KNOW, "just out of sight", null);
                        }
                    } else if (e instanceof SocketTimeoutException) {
                        errorListener.handleError(KSContextErrorListener.TIMED_OUT, "We are facing some connectivity issues", null);
                    } else if (e instanceof IOException) {
                        errorListener.handleError(KSContextErrorListener.NO_NET_CONNECTION, "We are facing some connectivity issues", null);
                    } else {
                        errorListener.handleError(KSContextErrorListener.DONT_KNOW, "just out of sight", null);
                    }
                } catch (Exception e1) {
                    errorListener.handleError(KSContextErrorListener.DONT_KNOW, "just out of sight", null);
                    ;
                }
            }
        });

    }


    public static Call getCall(Context context, Object input, KSApiService.apis apis) {

        switch (apis) {

            case SERVERTIME:
                return KSApiClient.getApiInterface(context).getServerTime();
            case USER_SETTING:
                return KSApiClient.getApiInterface(context).getUserSetting();
            default:
                return KSApiClient.getApiInterface(context).getServerTime();
        }

    }


    public static Call postCall(Context context, Object body, Object input, KSApiService.apis apis) {
        switch (apis) {
            case LOGIN:
                return KSApiClient.getApiInterface(context).login((User) body);
            case SET_USER_SETTINGS:
                return KSApiClient.getApiInterface(context).setUserSetting((HashMap<String,Boolean>)body);
            case CONTACT_TRACING:
                return KSApiClient.getApiInterface(context).getContactTracing((ContactTracingData)body);
            case UPDATE_GENDER:
                return KSApiClient.getApiInterface(context).updateGenderOnServer((String)input,(HashMap<String, String>) body);
            default:
                return KSApiClient.getApiInterface(context).login((User) body);

        }
    }



    public interface ApiType {
        int POST = 1;
        int GET = 0;
    }
}
