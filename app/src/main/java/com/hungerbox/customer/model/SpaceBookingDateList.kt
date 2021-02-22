package com.hungerbox.customer.model

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SpaceBookingDateList {
    private var dateMutableMap : MutableMap<Long, SpaceBookingDate> = HashMap()
    private var dateList : MutableList<SpaceBookingDate> = ArrayList()
    var dateCalendarList : MutableList<Calendar> = ArrayList()
//    fun addDate(slot : SlotList, slotText : String, date :  Long,dateString :  String, day : String ){
//        if (dateMutableMap.containsKey(slot.slot_id)){
//            addSlot(slot.slot_id, slotText)
//        } else{
//            val date = SpaceBookingDate(date , dateString, day)
//            val spaceSlot = SpaceBookingSlot(slot.slot_id, slotText)
//            date.slots.add(spaceSlot)
//            dateMutableMap.put(slot.slot_id, date)
//
//        }
//    }
    fun addDate(date : SpaceBookingDate, slot : SpaceBookingSlot ){
        if (dateMutableMap.containsKey(date.dateInMillis)){
            dateMutableMap[date.dateInMillis]?.slots?.add(slot)
        } else{
            date.slots.add(slot)
            dateMutableMap[date.dateInMillis] = date

        }
    }
    fun getDateList() : List<SpaceBookingDate>{
        if (dateMutableMap.size != dateList.size){
            //dateList = dateMutableMap.toList().map { it.second }.toMutableList()
            for (key in dateMutableMap.keys){
                val date = dateMutableMap[key]!!
                dateList.add(date)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date.dateInMillis
                dateCalendarList.add(calendar)
            }
            dateList.sort()
        }
        return dateList

    }
    fun getCalendarArray() : Array<Calendar>{
        return dateCalendarList.toTypedArray()
    }
}