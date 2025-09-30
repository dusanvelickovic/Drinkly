package com.example.drinkly.ui.home

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.drinkly.data.enum.MenuItemCategory
import com.example.drinkly.data.model.MenuItem
import com.example.drinkly.data.model.Venue
import com.example.drinkly.ui.components.VenueBottomSheet
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import androidx.core.graphics.scale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    navController: NavController,
) {
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current

    // Lokacija i dozvole iz ViewModel-a
    val userLocation by homeViewModel.userLocation
    val hasLocationPermission by homeViewModel.hasLocationPermission
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        homeViewModel.updateLocationPermissionGranted(granted)
        if (granted) {
            homeViewModel.getUserLocation(context, fusedLocationClient)
        } else {
            println("User denied location permission")
        }
    }

    // Venues iz ViewModel-a
    val venues by homeViewModel.venues

    // Inicijalno podešavanje dozvola i lokacije
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        homeViewModel.updateLocationPermissionGranted(granted)

        if (granted) {
            homeViewModel.getUserLocation(context, fusedLocationClient)
        } else {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        homeViewModel.fetchVenues()
    }

    // Postavi kameru kada se učita korisnikova lokacija
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
            println("Kamera pozicionirana na: ${location.latitude}, ${location.longitude}")
        }
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }

    // Map settings
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

    // States za bottom sheet
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val scope = rememberCoroutineScope()
    var selectedVenue by remember { mutableStateOf<Venue?>(null) }
    var menuItems by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoadingMenu by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Handle klik na marker
    val onMarkerClick: (Venue) -> Unit = { venue ->
        selectedVenue = venue
        isLoadingMenu = true

        scope.launch {
            try {
                // Učitaj menu items za selected venue
                menuItems = homeViewModel.getMenuItemsForVenue(venue.id)
                isLoadingMenu = false
                showBottomSheet = true
            } catch (e: Exception) {
                isLoadingMenu = false
                println("Greška pri učitavanju menu items: ${e.message}")
            }
        }
    }

    val onMenuItemCategoryClick: (MenuItemCategory) -> Unit = { category ->
        if (selectedVenue != null) {
            isLoadingMenu = true
            scope.launch {
                try {

                    // Ako je klik na All, učitaj sve iteme
                    if (category == MenuItemCategory.ALL) {
                        menuItems = homeViewModel.getMenuItemsForVenue(selectedVenue!!.id)
                    } else {
                        menuItems = homeViewModel.getMenuItemsForVenueByCategory(selectedVenue!!.id, category)
                    }

                    isLoadingMenu = false
                } catch (e: Exception) {
                    isLoadingMenu = false
                    println("Greška pri učitavanju menu items po kategoriji: ${e.message}")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map
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
                    mapStyleOptions = mapStyleOptions,
                    mapType = mapType
                )
            ) {
                val originalBitmap = BitmapFactory.decodeResource(context.resources, com.example.drinkly.R.drawable.glass_icon)
                val icon = originalBitmap?.let {
                    val scaledBitmap = it.scale(60, 100, false)
                    BitmapDescriptorFactory.fromBitmap(scaledBitmap)
                }

                // Markeri za venues
                venues?.forEach { venue ->
                    Marker(
                        state = MarkerState(position = LatLng(
                            venue.location.latitude,
                            venue.location.longitude
                        )),
                        title = venue.name,
                        snippet = venue.category,
                        onClick = { marker ->
                            onMarkerClick(venue)
                            true
                        },
                        icon = icon,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            MapTypeIconButtons(
                currentMapType = mapType,
                onMapTypeChange = { newType -> mapType = newType }
            )
        }

        // Modal Bottom Sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = bottomSheetState,
                dragHandle = {
                    // Custom drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(Color.Gray, androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                    )
                },
            ) {
                VenueBottomSheet(
                    venue = selectedVenue,
                    menuItems = menuItems,
                    isLoadingMenu = isLoadingMenu,
                    onMenuItemClick = { menuItem ->
                        // Handle menu item click
                        println("Clicked on menu item: ${menuItem.name}")
                    },
                    onCategoryChange = { category -> onMenuItemCategoryClick(category)},
                    onCloseBottomSheet = {
                        scope.launch {
                            bottomSheetState.hide()
                            showBottomSheet = false
                        }
                    },
                    onVenueClick = {
                        navController.navigate("venueScreen/${selectedVenue?.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun MapTypeIconButtons(
    currentMapType: MapType,
    onMapTypeChange: (MapType) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmallMapTypeButton(
            icon = Icons.Default.Map,
            isSelected = currentMapType == MapType.NORMAL,
            onClick = { onMapTypeChange(MapType.NORMAL) }
        )
        SmallMapTypeButton(
            icon = Icons.Default.Satellite,
            isSelected = currentMapType == MapType.SATELLITE,
            onClick = { onMapTypeChange(MapType.SATELLITE) }
        )
        SmallMapTypeButton(
            icon = Icons.Default.Layers,
            isSelected = currentMapType == MapType.HYBRID,
            onClick = { onMapTypeChange(MapType.HYBRID) }
        )
    }
}

@Composable
fun SmallMapTypeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = if (isSelected) Color(0xFFFF6B35) else Color.White,
        contentColor = if (isSelected) Color.White else Color.Black,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}