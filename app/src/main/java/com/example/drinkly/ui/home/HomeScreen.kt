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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
) {
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val userLocation by homeViewModel.userLocation
    val hasLocationPermission by homeViewModel.hasLocationPermission
//    val venues by homeViewModel.venues
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        homeViewModel.updateLocationPermissionGranted(granted)
        if (granted) {
            homeViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            println("User denied location permission")
        }
    }

    // Inicijalno podešavanje dozvola i lokacije
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        homeViewModel.updateLocationPermissionGranted(granted)

        if (granted) {
            homeViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Učitaj venues odmah
//        homeViewModel.fetchVenues()
    }

    // Postavi kameru kada se učita korisnikova lokacija
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 14f)
            println("Kamera pozicionirana na: ${location.latitude}, ${location.longitude}")
        }
    }

    val mapStyleOptions = remember {
        MapStyleOptions(
            """
            [
              {
                "featureType": "poi",
                "elementType": "all",
                "stylers": [ { "visibility": "off" } ]
              }
            ]
            """.trimIndent()
        )
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
                isMyLocationEnabled = hasLocationPermission && userLocation != null,
                mapStyleOptions = mapStyleOptions
            )
        ) {
            // Marker za korisničku lokaciju
            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Your Location",
                    snippet = "You are here"
                )
            }

            // Markeri za venues
//            venues?.forEach { venue ->
//                venue.location?.let { gp ->
//                    // Proveri da li su koordinate validne
//                    if (gp.latitude != 0.0 && gp.longitude != 0.0) {
//                        val venuePosition = LatLng(gp.latitude, gp.longitude)
//                        Marker(
//                            state = MarkerState(position = venuePosition),
//                            title = venue.name ?: "Venue",
//                            snippet = venue.address ?: "No address",
//                            // Možete dodati custom ikonu ako je potrebno
//                            // icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
//                        )
//                        println("Added marker for: ${venue.name} at ${gp.latitude}, ${gp.longitude}")
//                    } else {
//                        println("Invalid coordinates for venue: ${venue.name} (${gp.latitude}, ${gp.longitude})")
//                    }
//                } ?: run {
//                    println("No location data for venue: ${venue.name}")
//                }
//            } ?: run {
//                println("No venues to display")
//            }

            // Hardcore marker za testiranje
            Marker(
                state = MarkerState(position = LatLng(43.3209, 21.8958)),
                title = "Test Venue",
                snippet = "This is a test marker"
            )
            Marker(
                state = MarkerState(position = LatLng(44.7890, 20.4500)),
                title = "Another Venue",
                snippet = "This is another test marker"
            )
        }
    }
}