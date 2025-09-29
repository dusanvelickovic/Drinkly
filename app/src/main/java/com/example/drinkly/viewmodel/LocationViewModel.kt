package com.example.drinkly.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.*

import com.example.drinkly.data.helper.LocationHelper

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationHelper = LocationHelper(application)

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    fun start() {
        locationHelper.startLocationUpdates { loc ->
            _location.postValue(loc)
        }
    }

    fun stop() {
        locationHelper.stopLocationUpdates()
    }
}