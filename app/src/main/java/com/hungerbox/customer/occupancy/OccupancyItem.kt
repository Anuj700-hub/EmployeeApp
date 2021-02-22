package com.hungerbox.customer.occupancy

import com.google.gson.annotations.SerializedName
import com.hungerbox.customer.R

class OccupancyItem {
    @SerializedName("location_id")
    var locationId: Long = 0

    @SerializedName("name")
    var name: String = ""

    @SerializedName("type")
    var type: String = ""

    @SerializedName("subtype")
    var subType: String = ""

    @SerializedName("current_occupancy")
    var currentOccupancy: Int = 0

    @SerializedName("total_max_capacity")
    var totalMaxOccupancy: Int = 0

    @SerializedName("current_density")
    var currentDensity: String = ""

    public fun forMale(): Boolean{
        return subType == "male"
    }

    companion object{
        enum class CongestionLevel {
            LOW, MEDIUM, HIGH
        }
    }

    fun getCongestionLevel(): CongestionLevel{
        return when (currentDensity) {
            "low" -> {
                CongestionLevel.LOW
            }
            "medium" -> {
                CongestionLevel.MEDIUM
            }
            else -> {
                CongestionLevel.HIGH
            }
        }
    }


    fun getCongestionColor() : Int{
        return when (this.getCongestionLevel()) {
            CongestionLevel.LOW -> {
                R.color.green
            }
            CongestionLevel.MEDIUM -> {
                R.color.yellow
            }
            else -> {
                R.color.red
            }
        }
    }

    fun getCongestionString() : String{
        return when (this.getCongestionLevel()) {
            CongestionLevel.LOW -> {
                "Low"
            }
            CongestionLevel.MEDIUM -> {
                "Medium"
            }
            else -> {
                "High"
            }
        }
    }

    override fun toString(): String {
        if (name == null)
            name = ""
        return name
    }


}