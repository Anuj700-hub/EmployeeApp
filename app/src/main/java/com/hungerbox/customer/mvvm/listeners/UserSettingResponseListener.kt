package com.hungerbox.customer.mvvm.listeners

import com.hungerbox.customer.model.UserSettingsResponse

interface UserSettingResponseListener {
    fun onSuccess(responseObject : UserSettingsResponse?)
    fun onError()
}