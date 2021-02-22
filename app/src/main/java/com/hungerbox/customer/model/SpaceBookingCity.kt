package com.hungerbox.customer.model

data class SpaceBookingCity (val id : Long, val name : String){
    var buildingList : MutableList<SpaceBookingBuilding> = ArrayList()
    fun addBuilding(building: Location){
        for(i in buildingList.indices){
            if (buildingList[i].id == building.addressId){
                buildingList[i].addFloor(building)
                return
            }
        }
        buildingList.add(extractBuildingAndFloor(building))

    }
    private fun extractBuildingAndFloor(location: Location) : SpaceBookingBuilding{
        val building = SpaceBookingBuilding(location.addressId, location.addressLine1)
        building.addFloor(location)
        return building
    }
    fun findLocationById(locationId : Long) : Pair<Int, Int>?{
        for(i in buildingList.indices){
            val floorPositionInBuilding = buildingList[i].findFloorById(locationId)
            if (floorPositionInBuilding>-1){
                return Pair(i,floorPositionInBuilding)
            }
        }
        return null
    }

    override fun toString(): String = name
}