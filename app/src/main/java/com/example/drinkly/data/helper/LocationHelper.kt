package com.example.drinkly.data.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.drinkly.data.repository.AuthRepository
import com.example.drinkly.data.repository.UserRepository
import com.example.drinkly.data.repository.VenueRepository
import com.google.android.gms.location.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationHelper(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    var receiveNotifications: LiveData<Boolean> = MutableLiveData(false)

    /**
     * Start location updates with high accuracy and interval of 30 seconds.
     * Calls the provided callback with the new location.
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onLocationReceived: (Location) -> Unit) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 30000
        ).setMinUpdateIntervalMillis(15000).build()

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    onLocationReceived(location)
                    // Call the function to update location on the backend
                    updateUserLocationOnBackend(location)

                    if (receiveNotifications.value == true) {
                        // Notify about nearby users
                        notifyAboutNearbyUsers(context, location)

                        // Notify about nearby venues
                        notifyAboutNearbyVenues(context, location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            null
        )
    }

    /**
     * Stop location updates
     */
    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    /**
     * Enable or disable receiving notifications about nearby users and venues.
     */
    fun setReceiveNotifications(receive: Boolean) {
        (receiveNotifications as MutableLiveData).postValue(receive)
    }

    /**
     * Update the user's location on the backend.
     */
    private fun updateUserLocationOnBackend(location: Location) {
        // Use a coroutine to perform the network request off the main thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authRepository = AuthRepository()
                authRepository.updateUserLocation(
                    location = GeoPoint(location.latitude, location.longitude),
                    lastActiveAt = Timestamp.now()
                )

                println("Updating user location on backend: Lat: ${location.latitude}, Lon: ${location.longitude}")

            } catch (e: Exception) {
                println("Error updating user location on backend: ${e.message}")
            }
        }
    }

    /**
     * Check if there are users nearby and notify me.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notifyAboutNearbyUsers(context: Context, location: Location) {
        val userRepository = UserRepository()

        CoroutineScope(Dispatchers.IO).launch {
            val result = userRepository.fetchNearbyUsers(location, 50.0)
            result.onSuccess { users ->
                if (users.isNotEmpty()) {
                    val permission = Manifest.permission.POST_NOTIFICATIONS

                    println("Found ${users.size} users nearby.")
                    val notificationHelper = NotificationHelper(context)

                    for (user in users) {
                        val title = "User nearby"
                        val message = "${user.name} is nearby!"

                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val notificationId = user.uid.hashCode()

                            notificationHelper.showNotification(title, message, notificationId)
                        } else {
                            println("Notification permission not granted.")
                        }
                    }
                }
            }.onFailure { exception ->
                println("Error fetching nearby users: ${exception.message}")
            }
        }
    }

    /**
     * Check if there are venues nearby and notify me.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notifyAboutNearbyVenues(context: Context, location: Location) {
        val venueRepository = VenueRepository()

        CoroutineScope(Dispatchers.IO).launch {
            val result = venueRepository.fetchNearbyVenues(location, 5000)
            result.onSuccess { venues ->
                val nearbyVenues = venueRepository.filterVenuesByDistance(
                    venues,
                    location,
                    50
                )

                if (nearbyVenues.isNotEmpty()) {
                    val permission = Manifest.permission.POST_NOTIFICATIONS

                    println("Found ${nearbyVenues.size} venues nearby.")
                    val notificationHelper = NotificationHelper(context)

                    for (venue in nearbyVenues) {
                        val title = "Venue nearby"
                        val message = "${venue.name} is nearby!"

                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val notificationId = venue.id.hashCode()

                            notificationHelper.showNotification(title, message, notificationId)
                        } else {
                            println("Notification permission not granted.")
                        }
                    }
                }
            }.onFailure { exception ->
                println("Error fetching nearby venues: ${exception.message}")
            }
        }
    }
}