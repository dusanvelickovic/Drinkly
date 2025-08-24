package com.example.drinkly.ui.home

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import timber.log.Timber

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel()
) {
    // Initialize the camera position state, which controls the camera's position on the map
    val cameraPositionState = rememberCameraPositionState()

    // Obtain the current context
    val context = LocalContext.current

    // Trenutna lokacija korisnika
    val userLocation by homeViewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Launcher za zahtev za dozvolu
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Dozvola je odobrena, dohvati korisnikovu lokaciju i ažuriraj kameru
            homeViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            // Handle the case when permission is denied
            Timber.e("Location permission was denied by the user.")
        }
    }

    // Dozvola za lokaciju
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            // Proveri da li je dozvola za pristup lokaciji odobrena
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Dozvola je odobrena, dohvati korisnikovu lokaciju i ažuriraj kameru
                homeViewModel.fetchUserLocation(context, fusedLocationClient)
            }
            else -> {
                // Dozvola nije odobrena, pokreni zahtev za dozvolu
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = userLocation != null,
            ),
        ) {
            // If the user's location is available, place a marker on the map
            userLocation?.let {
//                Marker(
//                    state = MarkerState(position = it), // Place the marker at the user's location
//                    title = "Your Location", // Set the title for the marker
//                    snippet = "This is where you are currently located." // Set the snippet for the marker
//                )
                // Pozicioniraj kameru na korisnikovu lokaciju
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 16f)
            }
        }
    }
}