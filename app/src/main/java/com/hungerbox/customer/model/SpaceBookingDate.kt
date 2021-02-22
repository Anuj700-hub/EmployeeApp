package com.hungerbox.customer.model

import kotlin.collections.ArrayList

data class SpaceBookingDate (val dateInMillis : Long, val dateString : String, val day : String, val formattedDateString : String) : Comparable<SpaceBookingDate> {
    val slots : MutableList<SpaceBookingSlot> = ArrayList()
    var isSelected = false

    override fun compareTo(other: SpaceBookingDate): Int {
        return if (this.dateInMillis > other.dateInMillis) 1 else if (this.dateInMillis < other.dateInMillis) -1 else 0
    }

    override fun toString() = dateString

}