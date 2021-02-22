package com.hungerbox.customer.model

import com.google.gson.annotations.SerializedName

data class SendOrderDetail (@SerializedName("order_id") val orderId : Long,@SerializedName("contact_number") val contactNumber: String)