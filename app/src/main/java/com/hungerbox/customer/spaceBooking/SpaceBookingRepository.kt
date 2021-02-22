package com.hungerbox.customer.spaceBooking

import com.hungerbox.customer.model.*
import com.hungerbox.customer.spaceBooking.listeners.ResponseListener

interface SpaceBookingRepository {
    fun getListLocationWithAddress(type : String, responseListener: ResponseListener<CompanyResponse>)
    fun getSlotList(locationId : Long, responseListener: ResponseListener<SpaceBookingDateList>)
    fun getSlotSpaces(locationId : Long, responseListener: ResponseListener<VendorResponse>)
    fun addVendorToDb(vendor:Vendor)
    fun isOccasionAvailable():Boolean
}