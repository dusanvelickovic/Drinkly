package com.example.drinkly.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.*

import com.example.drinkly.data.helper.LocationHelper

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationHelper = LocationHelper(application)

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    val receiveNotifications: LiveData<Boolean> = liveData {
        emitSource(locationHelper.receiveNotifications)
    }

    /**
     * Start location updates with high accuracy.
     */
    fun start() {
        locationHelper.startLocationUpdates { loc ->
            _location.postValue(loc)
        }
    }

    /**
     * Stop location updates to save battery.
     */
    fun stop() {
        locationHelper.stopLocationUpdates()
    }

    /**
     * Enable or disable receiving notifications about nearby users and venues.
     */
    fun setReceiveNotifications(receive: Boolean) {
        locationHelper.setReceiveNotifications(receive)
    }
}