package com.hungerbox.customer.model


import com.google.gson.annotations.SerializedName

data class ListSpaceResponse(
    @SerializedName("data")
    val listData: ListSpacesResponseData? = null
)