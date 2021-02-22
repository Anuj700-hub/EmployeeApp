package com.hungerbox.customer.mvvm.listeners

import com.hungerbox.customer.model.UserSettings

interface UserSettingsListener {
    fun onSuccess(responseObject : UserSettings?)
    fun onError(error : String)
}