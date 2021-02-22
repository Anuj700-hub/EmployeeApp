package com.hungerbox.customer.spaceBooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SpaceBookingViewModelFactory(private val repository: SpaceBookingRepository, private val locationId : Long, private val cityId : Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpaceBookingViewModel::class.java)) {
            return SpaceBookingViewModel(repository, locationId, cityId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}