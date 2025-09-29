package com.example.drinkly.data.helper

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*

class LocationHelper(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onLocationReceived: (Location) -> Unit) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 30000
        ).setMinUpdateIntervalMillis(5000).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    onLocationReceived(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            null
        )
    }

    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }
}