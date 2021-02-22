package com.hungerbox.customer.occupancy

import com.google.gson.annotations.SerializedName
import kotlin.collections.ArrayList

class OccupancyResponse {

    @SerializedName("data")
    var occupancyItems: ArrayList<OccupancyItem>? = null
        get(){
            return if(field == null){
                ArrayList()
            }
            else{
                field
            }
        }
}