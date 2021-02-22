package com.hungerbox.customer.prelogin.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.amazonaws.com.google.gson.reflect.TypeToken
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.gson.Gson
import com.hungerbox.customer.BuildConfig
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.model.*
import com.hungerbox.customer.model.db.DbHandler
import com.hungerbox.customer.network.*
import com.hungerbox.customer.network.VolleyRequestFactory.mRequestQueue
import com.hungerbox.customer.order.activity.GlobalActivity
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.EventUtil
import com.hungerbox.customer.util.SharedPrefUtil
import com.hungerbox.customer.util.view.ErrorPopFragment
import com.hungerbox.customer.util.view.ErrorPopFragment.Companion.newInstance
import io.realm.Realm
import io.realm.RealmConfiguration
import org.json.JSONObject
import java.util.*


open class ParentActivity() : MswipeActivity() {

    lateinit  var appUpdateManager : AppUpdateManager
    lateinit var context: Context
    lateinit var configUrl: String
    open var fromNotification = false
    lateinit var realm : Realm
    open var apiTag : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fromNotification = intent.getBooleanExtra(ApplicationConstants.FROM_NOTIFICATION, false)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        realm  = Realm.getInstance(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())

        restoreFromSavedInstance(savedInstanceState);
    }

    protected fun restoreFromSavedInstance(savedInstanceState: Bundle?){
        if (MainApplication.cart == null && savedInstanceState != null) {
            val gson = Gson()
            MainApplication.cart = gson.fromJson(savedInstanceState.getString("cart"), Cart::class.java)
            val type = object : TypeToken<HashMap<Long?, Ocassion?>?>() {}.type
            MainApplication.selectedOcassionMap = gson.fromJson(savedInstanceState.getString("selectedOcassionMap"), type)
            MainApplication.selectedOcassionId = savedInstanceState.getLong("selectedOcassionId")
        }
    }

    protected fun finalize() {
        AppUtils.HbLog("finalize", this.javaClass.simpleName);
    }

    override fun onResume() {
        super.onResume()

        var context = if (MainApplication.appContext != null)
            MainApplication.appContext
        else
            this

        if (!(AppUtils.getConfig(context) != null || AppUtils.getConfig(context).company_id == -1L)) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

    }


    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            if(!apiTag.isEmpty()){
                mRequestQueue.cancelAll(apiTag)
            }
        }
        catch (e: Exception){
            e.printStackTrace();
        }
    }

    override fun finish() {

        try {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (exp: Exception) {
            exp.printStackTrace()
        }

        try {
            if(fromNotification){
                val intent = Intent(this, GlobalActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        } catch (e: java.lang.Exception){
            e.printStackTrace()
        }
        super.finish()
    }

    fun initialiseWatermarkCalls(context: Context, configUrl: String){
        this.context = context
        this.configUrl = configUrl
        getAllWaterMark(configUrl)
    }

     private fun getAllWaterMark(configUrl: String) {

         // Start Watermark Counts
         // Start Watermark Counts
         SharedPrefUtil.putBoolean(ApplicationConstants.WATERMARK_FIREBASE_COUNT_SWITCH, true)
         SharedPrefUtil.putInt(ApplicationConstants.WATERMARK_ONLINE_CALLS, 0)
         SharedPrefUtil.putInt(ApplicationConstants.WATERMARK_OFFLINE_CALLS, 0)

        val url = UrlConstant.WATERMARK_ALL

        val reqPayload = HashMap<String?, Any?>()
        if (BuildConfig.APPLICATION_ID.contains("common")) {
            reqPayload["key"] = "unified"
        } else {
            reqPayload["key"] = BuildConfig.APPLICATION_ID
        }
        reqPayload["location_id"] = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0).toString()
        val reqJSONObject = JSONObject(reqPayload)


        val waterMarkSimpleHttpAgent : SimpleHttpAgent<Array<WaterMark>> = SimpleHttpAgent<Array<WaterMark>>(
                this,
                url,
                ResponseListenerNew<Array<WaterMark>?> { responseObject, networkHeaders ->
                    if (responseObject != null && responseObject.size > 0) {

                        val serverTime = networkHeaders?.current_timestamp
                        var timeDifference: Long = 0
                        try {
                            timeDifference = Calendar.getInstance().timeInMillis - (serverTime!! * 1000)
                        } catch (exp: java.lang.Exception) {
                            exp.printStackTrace()
                        }
                        if (serverTime == 0L) {
                            timeDifference = 0
                        }

                        SharedPrefUtil.putLong(ApplicationConstants.TIME_DIFFERENCE, timeDifference)
                        storeWaterMarkInDb(responseObject, configUrl)

                    } else {
                        callConfigFromServer(configUrl)
                    }
                },
                ContextErrorListener { errorCode, error, errorResponse -> callConfigFromServer(configUrl) },
                Array<WaterMark>::class.java
        )
        waterMarkSimpleHttpAgent.postWithHeader(reqJSONObject.toString(), apiTag)
    }

     private fun storeWaterMarkInDb(responseObject: Array<WaterMark>?, url: String) {
        if (!DbHandler.isStarted()) DbHandler.start(applicationContext)
        val dataClassArrayList = ArrayList<DataClass>()

        for (waterMark in responseObject!!) {

            val dataKeyClass = DataKeyClass()
            dataKeyClass.apiKey = waterMark!!.apiKey
            dataKeyClass.object1Id = waterMark!!.object1Id
            dataKeyClass.object2Id = waterMark!!.object2Id

            var dataClass = DbHandler.getDbHandler(this).getDataClass(dataKeyClass)
            if (dataClass == null) {
                dataClass = DataClass()
                dataClass.apiKey = dataKeyClass.apiKey
                dataClass.object1Id = dataKeyClass.object1Id
                dataClass.object2Id = dataKeyClass.object2Id
                dataClass.lastResponse = null
                dataClass.serverWatermark = waterMark.timeStamp
                dataClass.clientWatermark = 0
            } else {
                dataClass.serverWatermark = waterMark.timeStamp
            }
            dataClassArrayList.add(dataClass)
        }

        DbHandler.getDbHandler(this).createDataClass(dataClassArrayList)
        callConfigFromServer(url)
    }

     private fun callConfigFromServer(url: String) {
        val objectIds = HashMap<String, String>()
         if (BuildConfig.APPLICATION_ID.contains("common")) {
             objectIds[ApplicationConstants.OBJECT_ID_1] = SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, 0).toString() + ""
         } else {
             objectIds[ApplicationConstants.OBJECT_ID_1] = BuildConfig.APPLICATION_ID
         }

        objectIds[ApplicationConstants.OBJECT_ID_2] = "android"

         val dHExtras = DataHandlerExtras()
         dHExtras.tag = apiTag

        val configSimpleHttpAgent = DataHandler<Config>(
                this,
                url,
                ResponseListener<Config?> { responseObject ->
                    try {
                        if (responseObject != null && responseObject.company_id != -1L) {
                            EventUtil.logUser(context, responseObject.company_id.toString())
                        }
                    } catch (exp: java.lang.Exception) {
                        exp.printStackTrace()
                    }
                    if (responseObject == null) {
                        val orderErrorHandleDialog = newInstance(
                                "Some error occurred.", "RETRY", true, ApplicationConstants.GENERAL_ERROR,
                                object : ErrorPopFragment.OnFragmentInteractionListener {
                                    override fun onPositiveInteraction() {
                                        callConfigFromServer(url)
                                    }

                                    override fun onNegativeInteraction() {
                                        finish()
                                    }
                                })
                        val fragmentManager = supportFragmentManager
                        fragmentManager.beginTransaction()
                                .add(orderErrorHandleDialog, "fetch_config")
                                .commitAllowingStateLoss()
                    } else {
                        getCurrentUserDetailsFromServer();
                    }
                },
                ContextErrorListener { errorCode, error, errorResponse ->
                    var message = ""
                    message = if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                        "Please check your internet connection."
                    } else {
                        "Some error occured."
                    }
                    val orderErrorHandleDialog = newInstance(
                            message, "RETRY", true, ApplicationConstants.GENERAL_ERROR,
                            object : ErrorPopFragment.OnFragmentInteractionListener {
                                override fun onPositiveInteraction() {
                                    callConfigFromServer(url)
                                }

                                override fun onNegativeInteraction() {
                                    finish()
                                }
                            })
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                            .add(orderErrorHandleDialog, "fetch_config")
                            .commitAllowingStateLoss()
                },
                Config::class.java,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras
        )
    }

    open fun getCurrentUserDetailsFromServer() {

        val url = UrlConstant.CURRENT_USER_V3

        val objectIds = HashMap<String, String>()
        objectIds[ApplicationConstants.OBJECT_ID_1] = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0).toString() + ""
        objectIds[ApplicationConstants.OBJECT_ID_2] = ""

        val dHExtras = DataHandlerExtras()
        dHExtras.tag = apiTag

        val userSimpleHttpAgent = DataHandler<UserReposne>(
                this,
                url,
                ResponseListener<UserReposne?> {
                    getLocationDetailsFromServer()
                },
                ContextErrorListener { errorCode, error, errorResponse ->
                    val orderErrorHandleDialog = newInstance(
                            "Some error occurred.", "RETRY", true, ApplicationConstants.GENERAL_ERROR,
                            object : ErrorPopFragment.OnFragmentInteractionListener {
                                override fun onPositiveInteraction() {
                                    getCurrentUserDetailsFromServer()
                                }

                                override fun onNegativeInteraction() {
                                    finish()
                                }
                            })
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                            .add(orderErrorHandleDialog, "fetch_user")
                            .commitAllowingStateLoss()
                },
                UserReposne::class.java,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras
        )
    }

    private fun getLocationDetailsFromServer() {

        val url = UrlConstant.COMPANY_LOCATION_GET_V3 + SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, 0)

        val objectIds = HashMap<String, String>()
        objectIds[ApplicationConstants.OBJECT_ID_1] = SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, 0).toString() + ""
        objectIds[ApplicationConstants.OBJECT_ID_2] = ""

        val dHExtras = DataHandlerExtras()
        dHExtras.tag = apiTag

        val userSimpleHttpAgent = DataHandler<CompanyResponse>(
                this,
                url,
                ResponseListener<CompanyResponse?> { companyResponse ->

                    if (companyResponse != null) {

                        val defLoc: Long = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0)
                        for (location in companyResponse.companyResponse.locationResponse.locations) {
                            if (defLoc == location.id) {
                                SharedPrefUtil.putInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, location.deskOrderingEnabled)
                                SharedPrefUtil.putInt(ApplicationConstants.ENFORCED_CAPACITY, location.enforcedCapacity)
                                SharedPrefUtil.putStringArrayList(ApplicationConstants.OTHER_TYPE_LOCATION,location.otherLocationTypes);
                                SharedPrefUtil.putLong(ApplicationConstants.LOCATION_CITY_ID,location.cityId)
                                break
                            }
                        }
                    }

                    getOccassionDetailsFromServer()
                },
                ContextErrorListener { errorCode, error, errorResponse ->
                    val orderErrorHandleDialog = newInstance(
                            "Some error occurred.", "RETRY", true, ApplicationConstants.GENERAL_ERROR,
                            object : ErrorPopFragment.OnFragmentInteractionListener {
                                override fun onPositiveInteraction() {
                                    getLocationDetailsFromServer()
                                }

                                override fun onNegativeInteraction() {
                                    finish()
                                }
                            })
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                            .add(orderErrorHandleDialog, "fetch_location_details")
                            .commitAllowingStateLoss()
                },
                CompanyResponse::class.java,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras
        )
    }

    private fun getOccassionDetailsFromServer() {

        val locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0)

        val url = UrlConstant.OCCASION_LIST_V3 + "?location_id=" + locationId

        val objectIds = HashMap<String, String>()
        objectIds[ApplicationConstants.OBJECT_ID_1] = locationId.toString() + ""
        objectIds[ApplicationConstants.OBJECT_ID_2] = ""

        val dHExtras = DataHandlerExtras()
        dHExtras.tag = apiTag

        val ocassionReposneSimpleHttpAgent = DataHandler<OcassionReposne>(
                this,
                url,
                ResponseListener<OcassionReposne?> {
                    getVendorsDetailsFromServer()
                },
                ContextErrorListener { errorCode, error, errorResponse ->
                    val orderErrorHandleDialog = newInstance(
                            "Some error occurred.", "RETRY", true, ApplicationConstants.GENERAL_ERROR,
                            object : ErrorPopFragment.OnFragmentInteractionListener {
                                override fun onPositiveInteraction() {
                                    getOccassionDetailsFromServer()
                                }

                                override fun onNegativeInteraction() {
                                    finish()
                                }
                            })
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                            .add(orderErrorHandleDialog, "fetch_occasion_details")
                            .commitAllowingStateLoss()
                },
                OcassionReposne::class.java,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras
        )
    }

    private fun getVendorsDetailsFromServer() {

        val locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0)

        val url = UrlConstant.LIST_VENDORS_V3 + "?locationId=" + locationId

        val objectIds = HashMap<String, String>()
        objectIds[ApplicationConstants.OBJECT_ID_1] = locationId.toString() + ""
        objectIds[ApplicationConstants.OBJECT_ID_2] = ""

        val dHExtras = DataHandlerExtras()
        dHExtras.tag = apiTag

        val vendorReposneSimpleHttpAgent = DataHandler<AllVendorResponse>(
                this,
                url,
                ResponseListener<AllVendorResponse?> { responseObject ->

                    onAllApiCallSuccess(configUrl)
                },
                ContextErrorListener { errorCode, error, errorResponse ->
                    val orderErrorHandleDialog = newInstance(
                            "Some error occurred.", "RETRY", true, ApplicationConstants.GENERAL_ERROR,
                            object : ErrorPopFragment.OnFragmentInteractionListener {
                                override fun onPositiveInteraction() {
                                    getVendorsDetailsFromServer()
                                }

                                override fun onNegativeInteraction() {
                                    finish()
                                }
                            })
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                            .add(orderErrorHandleDialog, "fetch_vendor_details")
                            .commitAllowingStateLoss()
                },
                AllVendorResponse::class.java,
                objectIds,
                ApplicationConstants.CRUD.GET,
                dHExtras
        )
    }

    open fun onAllApiCallSuccess(configUrl: String){

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val mainApplication = application as MainApplication
        if (mainApplication.cart != null) {
            val gson = Gson()
            val cart = gson.toJson(mainApplication.cart)
            val selectedOcassionMap = gson.toJson(MainApplication.selectedOcassionMap)
            outState.putString("cart", cart)
            outState.putString("selectedOcassionMap", selectedOcassionMap)
            outState.putLong("selectedOcassionId", MainApplication.selectedOcassionId)
        }
    }
}