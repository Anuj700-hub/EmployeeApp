package com.hungerbox.customer.model

data class SpaceBookingBuilding(val id : Long, val name : String) {
    var floorList : MutableList<SpaceBookingFloor> = ArrayList()
    fun addFloor(location: Location){
        val floor = SpaceBookingFloor(location.id, location.name)
        floorList.add(floor)
    }
    fun findFloorById(locationId : Long) : Int{
        for (i in floorList.indices){
           if (locationId == floorList[i].id) return i
        }
        return -1
    }
    override fun toString(): String = name
}