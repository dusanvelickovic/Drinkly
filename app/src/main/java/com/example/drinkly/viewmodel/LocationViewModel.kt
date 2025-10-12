package com.example.drinkly.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.*
import com.example.drinkly.data.helper.LocationHelper
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationHelper = LocationHelper(application)

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    val cameraPositionState: CameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(LatLng(44.7866, 20.4489), 12f) // Default to Belgrade
    )
    var isInitialPositionSet = false

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
