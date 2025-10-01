package com.example.drinkly

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.drinkly.navigation.AppNavigation
import com.example.drinkly.ui.theme.DrinklyTheme

class MainActivity : ComponentActivity() {
    private val locationViewModel by lazy {
        (application as DrinklyApplication).locationViewModel
    }

    /**
     * Launcher for Location Permission
     */
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationViewModel.start() // start tracking after permission granted
            } else {
                Log.e("MainActivity", "Location permission denied")
            }
        }

    /**
     * Launcher for Notification Permission
     */
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted")
                // You can dispatch a welcome/initial notification here if needed
            } else {
                Log.e("MainActivity", "Notification permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe location updates
        locationViewModel.location.observe(this) { loc ->
            Log.d("MainActivity", "Lat: ${loc.latitude}, Lon: ${loc.longitude}")
        }

        // Check and request BOTH permissions
        checkLocationPermission()
        checkNotificationPermission()

        enableEdgeToEdge()
        setContent {
            DrinklyTheme {
                AppNavigation()
            }
        }
    }

    /**
     * Start location updates based on activity lifecycle and permission status
     */
    override fun onStart() {
        super.onStart()
        if (hasLocationPermission()) {
            locationViewModel.start()
        }
    }

    /**
     * Stop location updates to save battery when activity is not visible
     */
    override fun onStop() {
        super.onStop()
        locationViewModel.stop()
    }

    /**
     * Check if location permission is granted
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check and request location permission
     */
    private fun checkLocationPermission() {
        if (!hasLocationPermission()) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     * ✨ Check and request notification permission for Android 13+ (API 33+)
     */
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission if not already granted
                requestNotificationPermissionLauncher.launch(permission)
            }
        }
    }
}