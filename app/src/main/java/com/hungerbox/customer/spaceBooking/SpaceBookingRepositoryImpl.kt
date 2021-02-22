package com.hungerbox.customer.spaceBooking

import android.content.Context
import android.view.View
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.model.*
import com.hungerbox.customer.model.db.DbHandler
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.spaceBooking.listeners.ResponseListener
import com.hungerbox.customer.util.DateTimeUtil
import java.text.SimpleDateFormat
import java.util.*

class SpaceBookingRepositoryImpl(private val context: Context) : SpaceBookingRepository{
    override fun getListLocationWithAddress(type: String, responseListener: ResponseListener<CompanyResponse>) {
        val url = UrlConstant.LIST_LOCATION_WITH_ADDRESS + "?type=$type"
        val companyResponse = SimpleHttpAgent<CompanyResponse>(context, url,
                { responseObject ->
                    responseListener.onSuccess(responseObject)
                },
                { errorCode, error, _ ->
                    responseListener.onError(errorCode,error)
                },
                CompanyResponse::class.java
        )
        companyResponse.get()
    }

    override fun getSlotList(locationId: Long, responseListener: ResponseListener<SpaceBookingDateList>) {
        val url = UrlConstant.LIST_LOCATION_SLOT + "?location_id=$locationId"  //replace with selected locationId
        val slotListResponse = SimpleHttpAgent<SlotListResponse>(context, url,
                { responseObject ->
                    responseListener.onSuccess(filterSlots(responseObject.slotLists))
                },
                { errorCode, error, _ ->
                    responseListener.onError(errorCode,error)

                }, SlotListResponse::class.java)
        slotListResponse.get()
    }

    override fun getSlotSpaces(locationId: Long, responseListener: ResponseListener<VendorResponse>) {
        val url = UrlConstant.LIST_SPACES + "?location_id=$locationId"+"&occasion_id=${MainApplication.selectedOcassionId}"
        val spaceListResponse = SimpleHttpAgent<VendorResponse>(context, url,
                { responseObject ->
                    responseListener.onSuccess(responseObject)
                    responseObject.vendor?.let {
                        it.spaces
                    }
                },
                { errorCode, error, _ ->
                    responseListener.onError(errorCode,error)

                }, VendorResponse::class.java)
        spaceListResponse.get()
    }

    private fun filterSlots(slotLists: List<SlotList>) : SpaceBookingDateList{
        val spaceBookingDateList  =  SpaceBookingDateList()
        for (slot in slotLists){
            val dates = getDateInMillis(slot.date, "yyyy-MM-dd")
            val startTime = DateTimeUtil.getDateStringFromDateString(slot.slot_start_time, "hh:mm:ss", "hh:mm a")
            val endTime = DateTimeUtil.getDateStringFromDateString(slot.slot_end_time, "hh:mm:ss", "hh:mm a")
            val spaceSlot = SpaceBookingSlot(slot.slot_id, "$startTime-$endTime")
            //val spaceDate = SpaceBookingDate(dates.first,slot.date.slice(slot.date.length-2 until slot.date.length), dates.second)
            val spaceDate = SpaceBookingDate(dates.first,slot.date, dates.second, dates.third)
            spaceBookingDateList.addDate(spaceDate,spaceSlot)
        }
        return spaceBookingDateList
    }

    private fun getDateInMillis(dateString: String, format: String): Triple<Long, String, String> {
        try {
            val sdf = SimpleDateFormat(format)
            val date = sdf.parse(dateString)
            val time = date.time
            val daySdf = SimpleDateFormat("EE")
            val day = daySdf.format(date)
            val formattedDateString = if (Date().date == date.date && Date().month == date.month && Date().year == date.year) { "Today" } else {dateString.slice(dateString.length-2 until dateString.length)}
            return Triple(time, day, formattedDateString)

        } catch (exception: Exception) {
            return Triple(-1, "", "")
        }
    }

    override fun addVendorToDb(vendor:Vendor){
        DbHandler.getDbHandler(MainApplication.appContext).createVendor(vendor)
    }

    override fun isOccasionAvailable() : Boolean = MainApplication.selectedOcassionId > 0


}