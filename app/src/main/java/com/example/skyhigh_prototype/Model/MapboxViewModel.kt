@file:Suppress("PackageName")

package com.example.skyhigh_prototype.Model

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
import android.content.Intent
import android.net.Uri
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skyhigh_prototype.R
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapboxViewModel : ViewModel() {
    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted.asStateFlow()

    private val locationService: LocationService = LocationServiceFactory.getOrCreate()
    private var locationProvider: DeviceLocationProvider? = null
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation




    private val _route = MutableStateFlow<List<Point>?>(null)
    val route = _route.asStateFlow()

    fun onPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            _isPermissionGranted.value = granted
            if (granted) {
                setupLocationProvider()
            }
        }
    }

    fun setupLocationProvider() {
        val request = LocationProviderRequest.Builder()
            .interval(IntervalSettings.Builder().interval(1000L).minimumInterval(1000L).maximumInterval(5000L).build())
            .displacement(0F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build()

        val result = locationService.getDeviceLocationProvider(request)
        if (result.isValue) {
            locationProvider = result.value!!
            startLocationUpdates()
        } else {
            Log.e("MapboxViewModel", "Failed to get device location provider")
        }
    }

    private fun startLocationUpdates() {
        locationProvider?.addLocationObserver(locationObserver)
    }

    private val locationObserver = object : LocationObserver {
        override fun onLocationUpdateReceived(locations: MutableList<Location>) {
            _currentLocation.value = locations.lastOrNull()
        }
    }

    fun stopLocationUpdates() {
        locationProvider?.removeLocationObserver(locationObserver)
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}

@SuppressLint("IncorrectNumberOfArgumentsInExpression")
@Composable
fun Maps(
    mapboxViewModel: MapboxViewModel = viewModel(),
    birdView: BirdViewModel = viewModel()
) {
    val hotspot by birdView.hotspot.collectAsState()
    val isLoading by birdView.isLoading.collectAsState()
    val context = LocalContext.current
    var selectedHotspot by remember { mutableStateOf<Point?>(null) }
    var showDetails by remember { mutableStateOf(false) }

    // Trigger fetching of hotspot details
    LaunchedEffect(Unit) {
        mapboxViewModel.currentLocation.collect { location ->
            location?.let {
                birdView.getHotspotByLocation(-it.latitude, it.longitude, getString(context, R.string.ebird_api_key))
            }
        }
    }

    // Show a loading indicator if data is being fetched
    if (isLoading) {
        CircularProgressIndicator()
    }

    // Collect current location from ViewModel
    val currentLocation by mapboxViewModel.currentLocation.collectAsState()

    // Initialize map view state
    val mapViewState = rememberMapViewportState {
        currentLocation?.let {
            CameraOptions.Builder()
                .zoom(14.0)
                .center(Point.fromLngLat(it.longitude, it.latitude))
                .build()
        } ?: CameraOptions.Builder()
            .zoom(2.0)
            .center(Point.fromLngLat(-98.5, 39.5))//Default location
            .build()
    }

    val mapViewportState = rememberMapViewportState()

    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewState,
        style = { MapboxStandardStyle() }
    ) {
        // Create and remember the icon image to be used for the point annotation
        val marker = rememberIconImage(key = R.drawable.red_marker, painter = painterResource(R.drawable.red_marker))

        hotspot.forEach { point ->
            PointAnnotation(
                point = point,
                onClick = {
                    selectedHotspot = point
                    showDetails = true
                    true  // Return true to indicate that the click was handled
                }
            ){
                iconImage = marker
            }
        }
       PointAnnotation(
            point = Point.fromLngLat(-98.5, 39.5),
            onClick = {
                selectedHotspot = Point.fromLngLat(-98.5, 39.5)
               showDetails = true
                true  // Return true to indicate that the click was handled
            }
        ){
            iconImage = marker
        }
        // Display hotspot details in a dialog if one is selected
        if (showDetails && selectedHotspot != null) {
            HotspotDetailsDialog(
                hotspot = selectedHotspot!!,
                currentLocations,
                onDismiss = { showDetails = false }
            )
        }

        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
                pulsingEnabled = true
                locationPuck = LocationPuck2D(
                    topImage = ImageHolder.from(R.drawable.custom_user_puck_icon),
                    scaleExpression = interpolate {
                        linear()
                        zoom()
                        stop {
                            literal(0.0)
                            literal(0.6)
                        }
                        stop {
                            literal(20.0)
                            literal(1.0)
                        }
                    }.toJson()
                )
            }
        }
    }
}

// Dialog to show hotspot details and navigate
@Composable
fun HotspotDetailsDialog(hotspot: Point, currentLocation: com.example.skyhigh_prototype.Data.Location, onDismiss: () -> Unit) {
    // Create a Google Maps directions URL
    val uri = "https://www.google.com/maps/dir/?api=1&origin=${currentLocation.LATITUDE},${currentLocation.LONGITUDE}&destination=${hotspot.latitude()},${hotspot.longitude()}"

    val context = LocalContext.current
    val intent = remember {
        Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hotspot Details") },
        text = { Text("Details for hotspot at ${hotspot.latitude()}, ${hotspot.longitude()}") },
        confirmButton = {
            Button(onClick = {
                context.startActivity(intent)  // Launch Google Maps
            }) {
                Text("View Route")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

//Will testing something new


