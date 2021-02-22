package com.hungerbox.customer.model

import com.google.gson.annotations.SerializedName

class OfflineInitiatePayment {

    @SerializedName("order_id")
    lateinit var orderID: String

    @SerializedName("redirect_url")
    lateinit var redirectURL: String

    @SerializedName("merchant_id")
    lateinit var merchantID: String
}