package com.hungerbox.customer.spaceBooking.listeners


interface ResponseListener<T> {
    fun onSuccess(response : T)
    fun onError(errorCode : Int , errorMessage : String)
}