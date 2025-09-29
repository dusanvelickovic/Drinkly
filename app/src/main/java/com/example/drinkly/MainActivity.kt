package com.example.drinkly

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.drinkly.navigation.AppNavigation
import com.example.drinkly.ui.theme.DrinklyTheme
import com.example.drinkly.viewmodel.LocationViewModel

class MainActivity : ComponentActivity() {
    private val locationViewModel: LocationViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationViewModel.start()
            } else {
                Log.e("MainActivity", "Location permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe location updates
        locationViewModel.location.observe(this) { loc ->
            Log.d("MainActivity", "Lat: ${loc.latitude}, Lon: ${loc.longitude}")
        }

        checkLocationPermission()

        enableEdgeToEdge()
        setContent {
            DrinklyTheme {
                AppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (hasLocationPermission()) {
            locationViewModel.start()
        }
    }

    override fun onStop() {
        super.onStop()
        locationViewModel.stop()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocationPermission() {
        if (!hasLocationPermission()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}