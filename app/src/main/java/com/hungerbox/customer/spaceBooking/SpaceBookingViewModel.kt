package com.hungerbox.customer.spaceBooking

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.model.*
import com.hungerbox.customer.spaceBooking.listeners.ResponseListener
import java.util.ArrayList

class SpaceBookingViewModel(private val repository: SpaceBookingRepository,private val defaultLocationId : Long, private val defaultCityId : Long) : ViewModel() {
    val companyResponse : LiveData<Resource<CompanyResponse>> get() = _companyResponse
    private var _companyResponse : MutableLiveData<Resource<CompanyResponse>> = MutableLiveData()

    val slotListResponse : LiveData<Resource<List<SpaceBookingDate>>> get() = _slotListResponse
    private var _slotListResponse : MutableLiveData<Resource<List<SpaceBookingDate>>> = MutableLiveData()

    val slotSpaceResponse : LiveData<Resource<List<Product>>> get() = _slotSpaceResponse
    private var _slotSpaceResponse : MutableLiveData<Resource<List<Product>>> = MutableLiveData()
    var spaceList : List<Product> = ArrayList()

    var dateListObject : SpaceBookingDateList? = null

    //private var _slotSpace : MutableLiveData<Resource<List<Space>>> = MutableLiveData()

    //spinner position livedatas
    var selectedCityPos : MutableLiveData<Int> = MutableLiveData()
    var selectedBuildingPos : MutableLiveData<Int> = MutableLiveData()
    var selectedFloorPos : MutableLiveData<Int> = MutableLiveData()
    var selectedSlotPos : MutableLiveData<Int> = MutableLiveData()
    var selectedNoOfPeoplePos : MutableLiveData<Int> = MutableLiveData()
    var setListenerLiveData : MutableLiveData<Boolean> = MutableLiveData()

    var selectedSpace : Product? = null
    var selectedVendor : Vendor? = null
    var selectedFloor : SpaceBookingFloor? = null
    var city:SpaceBookingCity?=null

    private var previousDatePosition = 0
    private var previousSpacePosition = 0
    var selectedBuilding : SpaceBookingBuilding? = null

    var cityListLiveData : MutableLiveData<List<SpaceBookingCity>> = MutableLiveData()
    var buildingListLiveData : MutableLiveData<List<SpaceBookingBuilding>> = MutableLiveData()
    var floorListLiveData : MutableLiveData<List<SpaceBookingFloor>> = MutableLiveData()
    var slotListLiveData : MutableLiveData<List<SpaceBookingSlot>> = MutableLiveData()
    var dateLiveData : MutableLiveData<SpaceBookingDate> = MutableLiveData()
    var showPopup : MutableLiveData<Pair<String, Boolean>> = MutableLiveData()
    var setEmptyTimeSlotSpinner : MutableLiveData<Boolean> = MutableLiveData()
    var isOccasionAvailable : MutableLiveData<Boolean> = MutableLiveData()
    var availableSpaceVisibility : MutableLiveData<Int> = MutableLiveData()
    var spaceBookingUIVisibility : MutableLiveData<Int> = MutableLiveData()

    //to execute one time
    private var isDefaultCityLoaded = false
    private var isDefaultLocationLoaded = false


    private var spinnerListenersActive = true
    init {
        if (defaultLocationId>0){
            spinnerListenersActive = false
        }
    }

    fun getListLocationWithAddress(type : String) {
        _companyResponse.postValue(Resource.Loading())
        repository.getListLocationWithAddress(type, object  : ResponseListener<CompanyResponse>{
            override fun onSuccess(response: CompanyResponse) {
                if (!response.companyResponse.locationResponse.locations.isNullOrEmpty()){
                    filterLocations(response.companyResponse.locationResponse.locations)
                    spaceBookingUIVisibility.postValue(View.VISIBLE)
                }
                else{
                    showPopup.postValue(Pair(first = "No locations available for [spacetype] booking.", second = true))
                    spaceBookingUIVisibility.postValue(View.GONE)
                }
                _companyResponse.postValue(Resource.Success(response))
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                _companyResponse.postValue(Resource.Error(message = errorMessage))
            }

        })

    }

    fun checkForOccasions(){
        if (repository.isOccasionAvailable()){
            isOccasionAvailable.postValue(true)
            spaceBookingUIVisibility.postValue(View.VISIBLE)
        }
        else {
            showPopup.postValue(Pair(first = "No occasions found.", second = true))
            isOccasionAvailable.postValue(false)
            spaceBookingUIVisibility.postValue(View.GONE)
        }
    }

    fun filterLocations(locationList : ArrayList<Location>){
        val spaceBookingCityList = SpaceBookingCityList()
        for (location in locationList){
            spaceBookingCityList.addCity(location)
        }
        cityListLiveData.value = spaceBookingCityList.getCityList()

        if (!isDefaultLocationLoaded && defaultLocationId>-1) {
            findLocationById(defaultLocationId)
        }
        else{
            selectDefaultCity()
        }

    }
    fun selectCity(position : Int){

        city = cityListLiveData.value?.get(position)
        buildingListLiveData.postValue(city?.buildingList)
//        if (defaultLocationId < 0) {
//            selectDefaultCity()
//        }
    }
    fun selectBuilding(position : Int){
        val building = buildingListLiveData.value?.get(position)
        selectedBuilding = building
        floorListLiveData.postValue(building?.floorList)
    }
    fun selectFloor(position: Int){
        selectedBuilding?.let {
            if (spinnerListenersActive)
            if (!it.floorList.isNullOrEmpty()){
                selectedFloor = it.floorList[position]
                getSlotSpaces(selectedFloor!!.id)
                getSlotList(selectedFloor!!.id)
            }
        }

    }
    fun selectDate(position: Int,setListener:Boolean){

        val list = (slotListResponse.value as Resource.Success<List<SpaceBookingDate>>).data
        list?.let {
            if (previousDatePosition < it.size) {
                it[previousDatePosition].isSelected = false
            }
            it[position].isSelected = true
            previousDatePosition = position

            //slotListLiveData.postValue(it[position].slots)
            dateLiveData.postValue(it[position])
            //_slotListResponse.postValue(Resource.Success(it))
        }


        setListenerLiveData.value = true
    }

    fun getSlotList(locationId : Long) {
        _slotListResponse.postValue(Resource.Loading())
        repository.getSlotList(locationId, object  : ResponseListener<SpaceBookingDateList>{
            override fun onSuccess(response: SpaceBookingDateList) {
                if(response.getDateList().isEmpty()){
                    showPopup.postValue(Pair(first = "No time slots are available for the selected location. Please select another location.", second = false))
                    setEmptyTimeSlotSpinner.postValue(true)
                    availableSpaceVisibility.postValue(View.GONE)
                }
                else{
                    setEmptyTimeSlotSpinner.postValue(false)
                    availableSpaceVisibility.postValue(View.VISIBLE)
                    setListenerLiveData.value = true

                    dateListObject = response
                    previousDatePosition = 0
                    _slotListResponse.value = (Resource.Success(response.getDateList()))

                   // selectDate(0,false)
                }

            }

            override fun onError(errorCode: Int, errorMessage: String) {
                _slotListResponse.postValue(Resource.Error(message = errorMessage))
            }

        })

    }

    fun getSlotSpaces(locationId : Long) {
        _slotListResponse.postValue(Resource.Loading())
        repository.getSlotSpaces(locationId, object  : ResponseListener<VendorResponse>{
            override fun onSuccess(response: VendorResponse) {

                if (response.vendor!=null && response.vendor.spaces!=null){
                    spaceList = response.vendor.spaces
                    //_slotSpaceResponse.postValue(Resource.Success(spaceList))

                    saveVendorToDb(response.vendor)

                } else{
                    _slotSpaceResponse.postValue(Resource.Error(message = "No space available"))
                }
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                _slotSpaceResponse.postValue(Resource.Error(message = errorMessage))
            }

        })

    }
    private fun filterSpaces(slotId : Long, date : String){
        val newList : MutableList<Product> = ArrayList()
        for (space in spaceList){
            space.isSelected = false
            selectedSpace = null
            if (space.slotId == slotId && space.date == date && space.maxQty>0){
                newList.add(space)
            }
        }
        previousSpacePosition = 0
        if(newList.size < 1){
            showPopup.postValue(Pair(first = "No spaces are available for the selected location. Please select another location or time.", second = false))
            availableSpaceVisibility.postValue(View.GONE)
        }
        else{
            availableSpaceVisibility.postValue(View.VISIBLE)
        }

        newList.sortWith(Comparator<Product> { o1, o2 ->
            when{
                o1!!.name>o2!!.name -> 1
                o1!!.name<o2!!.name -> -1
                else -> 0
            }
        })
        _slotSpaceResponse.postValue(Resource.Success(newList))

    }

    fun selectSlot(position: Int) {
        dateLiveData.value?.run {
            filterSpaces(slots[position].slotId, dateString)
        }
    }

    fun selectSpace(position : Int){
        if (slotSpaceResponse.value is Resource.Success) {
            val list = (slotSpaceResponse.value as Resource.Success<List<Product>>).data
            list?.let {
                if (previousSpacePosition < it.size) {
                    it[previousSpacePosition].isSelected = false
                }
                it[position].isSelected = true
                previousSpacePosition = position
                selectedSpace = it[position]

                _slotSpaceResponse.postValue(Resource.Success(it))
            }

        }

    }

    fun saveVendorToDb(vendor:Vendor){
        vendor.startTime = "00:00:00"
        vendor.endTime = "23:59:59"
        repository.addVendorToDb(vendor)
        selectedVendor = vendor
    }

    fun findLocationById(locationId : Long){
        cityListLiveData.value?.run {
            spinnerListenersActive = true
            for (i in indices){
               val locationPair =  this[i].findLocationById(locationId)
                if (locationPair!=null){
                    isDefaultLocationLoaded = true
                    selectedCityPos.postValue(i)
                    selectedBuildingPos.postValue(locationPair.first)
                    selectedFloorPos.postValue(locationPair.second)
                    break
                }
            }
            if(!isDefaultLocationLoaded){
                selectedCityPos.postValue(0)
                selectedBuildingPos.postValue(0)
                selectedFloorPos.postValue(0)
            }
            isDefaultLocationLoaded = true
        }

    }

//    fun findLocationById(locationId : Long) = viewModelScope.launch{
//        cityListLiveData.value?.run {
//            for (i in indices){
//                val locationPair =  this[i].findLocationById(locationId)
//                if (locationPair!=null){
//                    isDefaultLocationLoaded = true
//                    selectedCityPos.postValue(i)
//                    //delay(300)
//                    selectedBuildingPos.postValue(locationPair.first)
//                    //delay(300)
//                    selectedFloorPos.postValue(locationPair.second)
//                    break
//                }
//            }
//            isDefaultLocationLoaded = true
//        }
//
//    }
    private fun selectDefaultCity(){
        if (!isDefaultCityLoaded) {
            cityListLiveData.value?.run {
                for (i in indices) {
                    if (this[i].id == defaultCityId) {
                        isDefaultCityLoaded = true
                        selectedCityPos.postValue(i)
                        selectedBuildingPos.postValue(0)
                        selectedFloorPos.postValue(0)
                        break
                    }
                }
                if(!isDefaultCityLoaded){
                    isDefaultCityLoaded = true
                    selectedCityPos.postValue(0)
                    selectedBuildingPos.postValue(0)
                    selectedFloorPos.postValue(0)
                }
            }
        }

    }
}