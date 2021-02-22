package com.hungerbox.customer.model

class SpaceBookingCityList {
    private var cityMutableMap : MutableMap<Long, SpaceBookingCity> = HashMap()
    private var cityList : List<SpaceBookingCity> = ArrayList()
    fun addCity(location: Location){
        if (cityMutableMap.containsKey(location.cityId)){
            cityMutableMap[location.cityId]?.addBuilding(location)
        } else{
            val city = SpaceBookingCity(location.cityId, location.cityName)
            city.addBuilding(location)
            cityMutableMap[location.cityId] = city
        }
    }
    fun getCityList() : List<SpaceBookingCity>{
        if (cityMutableMap.size != cityList.size){
            cityList = cityMutableMap.toList().map { it.second }
        }
        return cityList

    }
}