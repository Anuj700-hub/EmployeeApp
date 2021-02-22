package com.hungerbox.customer.mvvm.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonSerializer
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.mvvm.util.DesignPatternActions
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.VolleyRequestFactory
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants

class UpdateAccountViewModel : ViewModel() {

    var  userDetailsUpdate : MutableLiveData<DesignPatternActions> =  MutableLiveData()
    private val apiSignature : String by lazy { System.currentTimeMillis().toString() }
    var progressbarVisibility: MutableLiveData<Int> = MutableLiveData()

    init {
        progressbarVisibility.value = View.GONE
    }
    fun changeUserDetails(url : String, requestObject : Any, successMsg : String, action : Int){

        progressbarVisibility.value = View.VISIBLE
        val settingsHttpAgent = SimpleHttpAgent<Any>(MainApplication.appContext,
                url, { _ ->
            progressbarVisibility.value = View.GONE
            AppUtils.showToast(successMsg, true, 1)
            userDetailsUpdate.value = DesignPatternActions(null, action)
        }, { _, _, errorResponse ->
            progressbarVisibility.value = View.GONE
            if (errorResponse?.message != null) {
                AppUtils.showToast(errorResponse.message, true, 2)
            }
            userDetailsUpdate.value = DesignPatternActions(null, ApplicationConstants.Actions.ENABLED_BUTTON_UPDATE)
        }, Any::class.java)

        settingsHttpAgent.post(requestObject, HashMap<String, JsonSerializer<Any>>(), apiSignature)
    }


    override fun onCleared() {
        super.onCleared()
        VolleyRequestFactory.mRequestQueue.cancelAll(apiSignature)
    }
}