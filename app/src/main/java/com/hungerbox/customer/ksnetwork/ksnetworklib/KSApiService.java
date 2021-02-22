package com.hungerbox.customer.ksnetwork.ksnetworklib;


import com.hungerbox.customer.bluetooth.Model.ContactTracingData;
import com.hungerbox.customer.bluetooth.Model.ContactTracingResponse;
import com.hungerbox.customer.ksnetwork.KSUrlConstant;
import com.hungerbox.customer.model.ServerTime;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserSettingsResponse;
import com.hungerbox.customer.mvvm.listeners.UpdateUserInfoResponseListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;


public interface KSApiService {

    enum apis {
        LOGIN, SERVERTIME,USER_SETTING,SET_USER_SETTINGS,CONTACT_TRACING,UPDATE_GENDER
    }


    @POST(KSUrlConstant.LOGIN_URL)
    Call<User> login(@Body User user);

    @GET(KSUrlConstant.SERVER_TIME)
    Call<ServerTime> getServerTime();

    @GET(KSUrlConstant.USER_SETTINGS)
    Call<UserSettingsResponse> getUserSetting();

    @POST(KSUrlConstant.SET_USER_SETTINGS)
    Call<Void> setUserSetting(@Body HashMap<String,Boolean> setting);

    @POST(KSUrlConstant.CONTACT_TRACING)
    Call<ContactTracingResponse> getContactTracing(@Body ContactTracingData contactTracingData);

    @PUT
    Call<Object> updateGenderOnServer(@Url String url, @Body HashMap<String, String> body);
}
