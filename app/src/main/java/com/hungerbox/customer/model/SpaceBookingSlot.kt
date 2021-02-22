package com.hungerbox.customer.model

data class SpaceBookingSlot (val slotId : Long, val slotText : String) {
    override fun toString(): String  = slotText
}