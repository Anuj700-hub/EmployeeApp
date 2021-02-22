package com.hungerbox.customer.ksnetwork.ksnetworklib;

import android.content.Context;
import android.util.Log;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KSApiClient {


    private static Retrofit retrofit;
    private static KSApiService apiInterface;
    private static OkHttpClient okHttpClient;
    private static String ORIGIN_HEADER = "x-origin";
    private static String ORIGIN_HEADER_ANDROID = "android_app";
    private static String ORIGIN_HEADER_CAFE = "cafeteria";
    private static String VERSION_HEADER = "x-version";
    private static String HB_DEVICE_ID = "hb-device-id";
    public static final String OPENURL_TOKEN = "$2b$10$LKXPsmPARhGsB6T6RlrUtutQZrHnA26SIUgPIAxLypSfIS5IftVPC";
    private static String HEADER_OFFLINE = "x-api-key";
    private static String HEADER_OFFLINE_TOKEN = "iM89nKGnuH48Fem6ELTyI5KcsimcxDe8aKw0Y04B";
    private static int REQUEST_TIMEOUT = 60;

    public static Retrofit getClient(Context context) {


        if (okHttpClient == null) {
            initOkHttpClient(context);
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(UrlConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }


        return retrofit;

    }

    private static void initOkHttpClient(Context context) {


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false);
        ;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (!BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            builder.addInterceptor(interceptor);
        }


        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                long userId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID);
                String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN);

                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder();
                requestBuilder.addHeader("content-type", "application/json");
                if (!authToken.isEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer " + authToken);
                    requestBuilder.addHeader("userId", String.valueOf(userId));
                }
                else{
                    requestBuilder.addHeader("Authorization",""+OPENURL_TOKEN);
                }
                requestBuilder.addHeader(VERSION_HEADER, BuildConfig.VERSION_NAME);
                if (AppUtils.isCafeApp()) {
                    requestBuilder.addHeader(ORIGIN_HEADER, ORIGIN_HEADER_CAFE);
                    if(SharedPrefUtil.getLong(ApplicationConstants.HB_DEVICE_ID,-1)!=-1) {
                        requestBuilder.addHeader(HB_DEVICE_ID,SharedPrefUtil.getLong(ApplicationConstants.HB_DEVICE_ID,-1)+"");
                    }
                }
                else
                    requestBuilder.addHeader(ORIGIN_HEADER, ORIGIN_HEADER_ANDROID);


                Request original = requestBuilder.build();

                if (!BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
                    Log.d("ranjeet", "headers=>+" + original.headers());
                }
                return chain.proceed(original);

            }
        });
        okHttpClient = builder.build();


    }

    public static void resetApiClient() {
        retrofit = null;
        okHttpClient = null;
    }

    public static KSApiService getApiInterface(Context context) {

        if (apiInterface == null) {
            apiInterface = KSApiClient.getClient(context).create(KSApiService.class);
        }
        return apiInterface;
    }

    public static void clear(){
        if(okHttpClient!=null) {
            okHttpClient.dispatcher().cancelAll();
        }
    }
}
