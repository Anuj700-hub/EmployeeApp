package com.hungerbox.customer.mvvm.listeners

import com.hungerbox.customer.bluetooth.Model.ContactTracingResponse

interface ContactTracingResponseListener {

    fun onSuccess(contactTracingResponse: ContactTracingResponse)
    fun onError()
}