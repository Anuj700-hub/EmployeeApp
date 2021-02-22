package com.hungerbox.customer.mvvm.repository

import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.bluetooth.Model.ContactTracingData
import com.hungerbox.customer.bluetooth.Model.ContactTracingResponse
import com.hungerbox.customer.bluetooth.Model.UserViolation
import com.hungerbox.customer.bluetooth.ViolationLogManager
import com.hungerbox.customer.ksnetwork.KSErrorResponse
import com.hungerbox.customer.ksnetwork.ksnetworklib.KSApiService
import com.hungerbox.customer.ksnetwork.ksnetworklib.KSHttpAgent
import com.hungerbox.customer.model.*
import com.hungerbox.customer.mvvm.listeners.*
import com.hungerbox.customer.mvvm.util.MyAccountUtil
import com.hungerbox.customer.network.*
import com.hungerbox.customer.util.ApplicationConstants
import org.json.JSONObject
import org.json.JSONStringer
import java.util.*
import kotlin.collections.HashMap

class MyAccountRepositoryImpl(private val myAccountUtil: MyAccountUtil) : MyAccountRepository {

    override fun updateUserSetting(key:String, isChecked:Boolean, userSettingListener: UserSettingsListener, apiSignature: String) {
        val jObject: JSONStringer = JSONStringer()
                .`object`()
                .key(key).value(isChecked)
                .endObject()
        val inputJson = JSONObject(jObject.toString())

        val settingsHttpAgent = SimpleHttpAgent<UserSettings>(MainApplication.appContext,
                UrlConstant.SET_USER_SETTINGS, { responseObject ->

            userSettingListener.onSuccess(responseObject)
        }, { _, error, _ ->
            userSettingListener.onError(error)
        }, UserSettings::class.java)
        settingsHttpAgent.post(inputJson.toString(), apiSignature)
    }

    override fun fetchUserSetting(userSettingResponseListener: UserSettingResponseListener, apiSignature: String) {
        val settingsHttpAgent = SimpleHttpAgent<UserSettingsResponse>(MainApplication.appContext,
                UrlConstant.USER_SETTINGS, { responseObject ->
            userSettingResponseListener.onSuccess(responseObject)
        }, { _, _, _ ->
            userSettingResponseListener.onError()
        }, UserSettingsResponse::class.java)
        settingsHttpAgent[apiSignature]
    }

    override fun fetchUserDetail(userId: String, userDetailResponseListener: UserDetailResponseListener, apiSignature: String) {
        val url = UrlConstant.CURRENT_USER_V3
        val objectIds = HashMap<String, String>()
        objectIds[ApplicationConstants.OBJECT_ID_1] = userId
        objectIds[ApplicationConstants.OBJECT_ID_2] = ""
        val dHExtras = DataHandlerExtras()
        dHExtras.tag = apiSignature

        DataHandler<UserReposne>(
                MainApplication.appContext,
                url,
                {
                    userDetailResponseListener.onSuccess(it)
                },
                { _, _, _ ->
                    userDetailResponseListener.onError()
                },
                UserReposne::class.java,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras
        )
    }

    override fun sendContactTracingData(userViolations: List<UserViolation>, contactTracingResponseListener: ContactTracingResponseListener, apiSignature: String) {

        val contactTracingHttpAgent = SimpleHttpAgent<ContactTracingResponse>(MainApplication.appContext, UrlConstant.CONTACT_TRACING, { responseObject ->

            contactTracingResponseListener.onSuccess(responseObject)

        }, { _, _, _ ->

            contactTracingResponseListener.onError()

        }, ContactTracingResponse::class.java)

        val data = ContactTracingData()
        data.contacts = ArrayList(userViolations)

        contactTracingHttpAgent.post(data, HashMap(), apiSignature)
    }

    override fun getViolationsFromDb(): List<UserViolation> {
        return ViolationLogManager.getViolationForXDays(MainApplication.appContext, myAccountUtil.getContactTracingMaxDays())
    }

    override fun deleteAllViolationsFromDb() {
        ViolationLogManager.deleteAllViolations(MainApplication.appContext)
    }

    override fun updateGenderOnServer(gender: String, updateUserInfoResponseListener: UpdateUserInfoResponseListener, apiSignature: String) {
        val updateGenderHttpAgent = SimpleHttpAgent<Object>(MainApplication.appContext, UrlConstant.UPDATE_USER_INFO + "/" + myAccountUtil.getUserId(), { responseObject ->

            updateUserInfoResponseListener.onSuccess(responseObject)

        }, { _, _, _ ->

            updateUserInfoResponseListener.onError()

        }, Object::class.java)

        updateGenderHttpAgent.put("{\"gender\":\"$gender\"}", apiSignature)
    }

    override fun getGendersList() : ArrayList<String>{
        return ArrayList(listOf("Select", "Male", "Female", "Non-Binary"))
    }

    //Retrofit calls

    override fun updateUserSetting(key: String, isChecked: Boolean, userSettingListener: UserSettingsListener) {

        val body = HashMap<String,Boolean>()
        body.put(key,isChecked)

        KSHttpAgent.callNetworkLib(KSApiService.apis.SET_USER_SETTINGS,null,body, KSHttpAgent.ApiType.POST,MainApplication.appContext,
                { responseObject: UserSettings? ->
                    userSettingListener.onSuccess(responseObject)

                },
                { errorCode: Int, error: String, errorResponse: KSErrorResponse? ->
                    userSettingListener.onError(error)
                })
    }

    override fun fetchUserSetting(userSettingResponseListener: UserSettingResponseListener) {

        KSHttpAgent.callNetworkLib(KSApiService.apis.USER_SETTING, null, null, KSHttpAgent.ApiType.GET, MainApplication.appContext,
                { responseObject: UserSettingsResponse ->
                    userSettingResponseListener.onSuccess(responseObject)

                },
                { errorCode: Int, error: String?, errorResponse: KSErrorResponse? ->
                    userSettingResponseListener.onError()
                })
    }

    override fun sendContactTracingData(userViolations: List<UserViolation>, contactTracingResponseListener: ContactTracingResponseListener) {
        val data = ContactTracingData()
        data.contacts = ArrayList(userViolations)

        KSHttpAgent.callNetworkLib(KSApiService.apis.CONTACT_TRACING,null,data,KSHttpAgent.ApiType.POST,MainApplication.appContext,{
            responseObject : ContactTracingResponse ->
            contactTracingResponseListener.onSuccess(responseObject)},
            {
                errorCode: Int, error: String?, errorResponse: KSErrorResponse? ->
                contactTracingResponseListener.onError()
            }
        )
    }

    override fun updateGenderOnServer(gender: String, updateUserInfoResponseListener: UpdateUserInfoResponseListener) {
        val url = UrlConstant.UPDATE_USER_INFO + "/" + myAccountUtil.getUserId()
        val body = HashMap<String,String>()
        body.put("gender",gender);

        // should use PUT
        KSHttpAgent.callNetworkLib(KSApiService.apis.UPDATE_GENDER,url,body,KSHttpAgent.ApiType.POST,MainApplication.appContext,{
            responseObject:Object ->
            updateUserInfoResponseListener.onSuccess(responseObject)
        },{
            _,_,_->
            updateUserInfoResponseListener.onError()
        })
    }

}