package com.hungerbox.customer.network;

import android.content.Context;


import com.google.gson.Gson;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.AllVendorResponse;
import com.hungerbox.customer.model.CompanyResponse;
import com.hungerbox.customer.model.DataClass;
import com.hungerbox.customer.model.DataHandlerExtras;
import com.hungerbox.customer.model.DataKeyClass;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.HomeBannerItemResponse;
import com.hungerbox.customer.model.NetworkHeaders;
import com.hungerbox.customer.model.OcassionReposne;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;


import java.util.HashMap;

public class DataHandler<T> {

    public DataHandler(final Context context, String url, final ResponseListener<T> responseListener,
                       final ContextErrorListener errorListener, final Class<T> responseClass,
                       HashMap<String, String> objectIds, ApplicationConstants.CRUD type, DataHandlerExtras extras) {

        boolean makeServerCall = true;
        final DataKeyClass dataKeyClass = getDataKeyClass(responseClass, objectIds);



        if (dataKeyClass != null && AppUtils.isFlavorAllowed()) {

            if (!DbHandler.isStarted())
                DbHandler.start(context);

            DataClass dataClass = DbHandler.getDbHandler(context).getDataClass(dataKeyClass);
            if (dataClass != null) {
                if (dataClass.getClientWatermark() >= dataClass.getServerWatermark()) {
                    if (dataClass.getLastResponse() != null) {

                        makeServerCall = false;
                        AppUtils.HbLog("watermark-local", dataClass.getLastResponse());
                        AppUtils.HbLog("watermark-local",url);
                        AppUtils.HbLog("server , client",dataClass.getServerWatermark()+" , " +dataClass.getClientWatermark());

                        if(SharedPrefUtil.getBoolean(ApplicationConstants.WATERMARK_FIREBASE_COUNT_SWITCH, true)){

                            SharedPrefUtil.putInt(ApplicationConstants.WATERMARK_OFFLINE_CALLS, SharedPrefUtil.getInt(ApplicationConstants.WATERMARK_OFFLINE_CALLS, 1) + 1);

                        }


                        responseListener.response(new Gson().fromJson(dataClass.getLastResponse(), responseClass));
                    }
                }
            }
        }


        if (makeServerCall) {
            AppUtils.HbLog("watermark-online", url);
            if(SharedPrefUtil.getBoolean(ApplicationConstants.WATERMARK_FIREBASE_COUNT_SWITCH, true)){

                SharedPrefUtil.putInt(ApplicationConstants.WATERMARK_ONLINE_CALLS, SharedPrefUtil.getInt(ApplicationConstants.WATERMARK_ONLINE_CALLS, 1) + 1);

            }
            SimpleHttpAgent<T> simpleHttpAgent = new SimpleHttpAgent<T>(
                    context,
                    url,
                    new ResponseListenerNew<T>() {
                        @Override
                        public void response(T responseObject, NetworkHeaders headers) {
                            if (responseClass != null && dataKeyClass != null) {
                                if(AppUtils.isFlavorAllowed()) {
                                    updateDataClass( responseObject, context, dataKeyClass, headers);
                                }
                            }

                            responseListener.response(responseObject);
                        }
                    },
                    new ContextErrorListener() {
                        @Override
                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                            errorListener.handleError(errorCode, error, errorResponse);
                        }
                    }, responseClass
            );

            switch (type) {
                case GET:
                    if(!extras.getTag().isEmpty())
                        simpleHttpAgent.getWithHeader(extras.getTag());
                    else
                        simpleHttpAgent.getWithHeader();
                    break;
            }
        }

    }

    private void updateDataClass(T responseObject, Context context, DataKeyClass dataKeyClass,NetworkHeaders headers) {

        if (!DbHandler.isStarted())
            DbHandler.start(context);


        DataClass dataClass = DbHandler.getDbHandler(context).getDataClass(dataKeyClass);
        Gson gson = new Gson();
        if (dataClass == null) {
            dataClass = new DataClass();
            dataClass.setApiKey(dataKeyClass.getApiKey());
            dataClass.setObject1Id(dataKeyClass.getObject1Id());
            dataClass.setObject2Id(dataKeyClass.getObject2Id());
            dataClass.setLastResponse(gson.toJson(responseObject));
            dataClass.setServerWatermark(0);
            dataClass.setClientWatermark(headers.getLastUpdateAt());
        } else {
            dataClass.setLastResponse(gson.toJson(responseObject));
            dataClass.setClientWatermark(headers.getLastUpdateAt());

            AppUtils.HbLog("server , client",dataClass.getServerWatermark()+" , " +dataClass.getClientWatermark());

        }
        DbHandler.getDbHandler(context).createDataClass(dataClass);

    }

    public static DataKeyClass getDataKeyClass(Class responseClass, HashMap<String, String> objectIds) {

        if(objectIds==null)
            return null;

        DataKeyClass dataKeyClass = new DataKeyClass();
        if (responseClass.isInstance(new OcassionReposne())) {
            dataKeyClass.setApiKey(OcassionReposne.api_key);
        } else if (responseClass.isInstance(new UserReposne())) {
            dataKeyClass.setApiKey(UserReposne.api_key);
        }else if(responseClass.isInstance(new HomeBannerItemResponse())) {
            dataKeyClass.setApiKey(HomeBannerItemResponse.api_key);
        }else if(responseClass.isInstance(new Config())){
            dataKeyClass.setApiKey(Config.api_key);
        }else if(responseClass.isInstance(new CompanyResponse())){
            dataKeyClass.setApiKey(CompanyResponse.api_key);
        }else if(responseClass.isInstance(new AllVendorResponse())){
            dataKeyClass.setApiKey(AllVendorResponse.api_key);
        }else {
            dataKeyClass = null;
        }


        if (dataKeyClass != null) {
            if (objectIds.containsKey(ApplicationConstants.OBJECT_ID_1)) {
                dataKeyClass.setObject1Id(objectIds.get(ApplicationConstants.OBJECT_ID_1));
            }

            if (objectIds.containsKey(ApplicationConstants.OBJECT_ID_2)) {
                dataKeyClass.setObject2Id(objectIds.get(ApplicationConstants.OBJECT_ID_2));
            }

        }

        return dataKeyClass;

    }

    public static void resetDataClass(Context context, final Class responseClass, HashMap<String, String> objectIds){

        DataKeyClass dataKeyClass = getDataKeyClass(responseClass, objectIds);
        try{
            if(dataKeyClass != null)
                DbHandler.getDbHandler(context).deleteDataClass(dataKeyClass);
        }
        catch (Exception e){
            AppUtils.HbLog("DataHandler", "Error in deleting Data key Class" + e.getMessage());
        }
    }


}
