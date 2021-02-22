package com.hungerbox.customer.model

import com.google.gson.annotations.SerializedName

class LinkedLocationsResponse {

    @SerializedName("data")
    var linkedLocationsList: ArrayList<Location>? = null
        get() {
            if(field == null)
                return ArrayList()
            return field
        }
}