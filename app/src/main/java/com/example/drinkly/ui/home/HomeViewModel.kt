package com.example.drinkly.ui.home

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.enum.MenuItemCategory
import com.example.drinkly.data.model.MenuItem
import com.example.drinkly.data.model.Venue
import com.example.drinkly.data.repository.MenuItemRepository
import com.example.drinkly.data.repository.VenueRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class HomeViewModel(
    private val venueRepository: VenueRepository = VenueRepository(),
    private  val menuItemRepository: MenuItemRepository = MenuItemRepository(),
) : ViewModel() {

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation

    private val _hasLocationPermission = mutableStateOf(false)
    val hasLocationPermission: State<Boolean> = _hasLocationPermission

    fun updateLocationPermissionGranted(granted: Boolean) {
        _hasLocationPermission.value = granted
    }

    fun getUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            _hasLocationPermission.value = true
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        _userLocation.value = LatLng(it.latitude, it.longitude)
                    }
                }.addOnFailureListener {
                    println("Failed to get last location: ${it.message}")
                }
            } catch (se: SecurityException) {
                println("SecurityException (location): ${se.message}")
            }
        } else {
            _hasLocationPermission.value = false
            println("Location permission not granted")
        }
    }

    private val _venues = mutableStateOf<List<Venue>?>(null)
    val venues: State<List<Venue>?> = _venues

    fun fetchVenues() {
        viewModelScope.launch {
            val result = venueRepository.searchVenues()
            result.onSuccess {
                _venues.value = it
                println("Loaded ${it.size} venues")
            }.onFailure {
                println("Venue load failed: ${it.message}")
            }
        }
    }

    /**
     * Ucitaj menu iteme za venueId
     */
    suspend fun getMenuItemsForVenue(venueId: String): List<MenuItem> {
        return try {
            val result = menuItemRepository.getMenuItemsForVenue(venueId)
            println("Loaded ${result.size} menu items for venue $venueId")
            result
        } catch (e: Exception) {
            println("Error loading menu items: ${e.message}")
            emptyList<MenuItem>()
        }
    }

    /**
     * Ucitaj menu iteme za venueId i kategoriju
     */
    suspend fun getMenuItemsForVenueByCategory(venueId: String, category: MenuItemCategory): List<MenuItem> {
        return try {
            val result = menuItemRepository.getMenuItemsForVenueByCategory(venueId, category)
            println("Loaded ${result.size} menu items for venue $venueId and category $category")
            result
        } catch (e: Exception) {
            println("Error loading menu items by category: ${e.message}")
            emptyList<MenuItem>()
        }
    }
}