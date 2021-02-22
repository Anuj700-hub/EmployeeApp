package com.hungerbox.customer.mvvm.listeners

interface UpdateUserInfoResponseListener {
    fun onSuccess(updateUserInfoResponse: Object)
    fun onError()
}