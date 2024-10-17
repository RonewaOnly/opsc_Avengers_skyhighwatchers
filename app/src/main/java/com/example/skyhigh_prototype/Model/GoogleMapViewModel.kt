package com.example.skyhigh_prototype.Model

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skyhigh_prototype.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    birdViewModel: BirdViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current

    // Collect location and bird hotspots from ViewModels
    val userLocation by locationViewModel.location.collectAsState()
    val birdHotspots by birdViewModel.hotspot.collectAsState()
    var selectedRange by remember { mutableStateOf(10.0) } // Default range to 10 km

    // Request location permission
    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                locationViewModel.requestLocation(context) // Request location through the ViewModel
            }
        }
    )

    // Check for location permissions and request location updates if granted
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationViewModel.requestLocation(context) // Request location if permission is already granted
        }
    }

    // Handle map and markers
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = rememberCameraPositionState {
            position = userLocation?.let {
                CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 10f)
            } ?: CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 1f)
        }
    ) {
        userLocation?.let {
            Marker(
                state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                title = "Your Location"
            )

            // Fetch and display bird hotspots within the selected range
            birdViewModel.getHotspotByLocation(it.latitude, it.longitude, selectedRange, getString(context,R.string.ebird_api_key))
        }

        // Add markers for bird hotspots
        birdHotspots.forEach { hotspot ->
            Marker(
                state = MarkerState(position = LatLng(hotspot.latitude(), hotspot.longitude())),
                title = "Bird Hotspot",
                snippet = "Birds seen here"
            )
        }
    }

    // Range selector UI
    RangeSelector { range ->
        selectedRange = range
    }
}


// Function to get current location using FusedLocationProviderClient
@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onLocationReceived: (Location) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        location?.let { onLocationReceived(it) }
    }
}

@Composable
fun RangeSelector(onRangeSelected: (Double) -> Unit) {
    var selectedRange by remember { mutableStateOf(10.0) } // Default range to 10 km

    Column {
        Text(text = "Select travel range: ${selectedRange} km")
        Slider(
            value = selectedRange.toFloat(),
            onValueChange = { selectedRange = it.toDouble() },
            valueRange = 1f..50f,
            steps = 49,
            onValueChangeFinished = {
                onRangeSelected(selectedRange)
            }
        )
    }
}