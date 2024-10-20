package com.example.skyhigh_prototype.Model

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skyhigh_prototype.Intent.Hotspot
import com.example.skyhigh_prototype.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    birdViewModel: BirdViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current

    // Collect location and bird hotspots from ViewModels
    val userLocation by locationViewModel.location.collectAsState()
    val birdHotspots by birdViewModel.hotspot.collectAsState()
    var selectedRange by remember { mutableDoubleStateOf(10.0) } // Default range to 10 km
    var selectedHotspot by remember { mutableStateOf<Hotspot?>(null) }

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

    // Use a Box layout to overlay the map and the RangeSelector
    Box(modifier = Modifier.fillMaxSize()) {
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
                    snippet = "Birds seen here",
                    onClick = {
                        selectedHotspot = Hotspot(
                            locId = "id",
                            locName = "Name",
                            lat = hotspot.latitude(),
                            lng = hotspot.longitude()
                        ) // Set the selected hotspot
                        showDirectionsToHotspot(context, userLocation, LatLng(hotspot.latitude(), hotspot.longitude()))
                        true
                    }
                )
            }
        }
        // Range selector UI, overlaid on top of the map
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter) // Align the RangeSelector to the bottom center
                .padding(16.dp) // Add some padding
        ) {
            RangeSelector { range ->
                selectedRange = range
            }
        }
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
    var selectedRange by remember { mutableDoubleStateOf(10.0) } // Default range to 10 km

    Column {
        Text(text = "Select travel range: $selectedRange km")
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

// Function to show directions to the hotspot
fun showDirectionsToHotspot(context: Context, userLocation: Location?, hotspotLatLng: LatLng) {
    if (userLocation != null) {
        val gmmIntentUri = Uri.parse(
            "http://maps.google.com/maps?saddr=${userLocation.latitude},${userLocation.longitude}&daddr=${hotspotLatLng.latitude},${hotspotLatLng.longitude}"
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        context.startActivity(mapIntent)
    } else {
        Toast.makeText(context, "Current location not available", Toast.LENGTH_SHORT).show()
    }
}