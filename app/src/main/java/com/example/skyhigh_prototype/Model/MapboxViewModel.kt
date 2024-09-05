@file:Suppress("PackageName")

package com.example.skyhigh_prototype.Model

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
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

@Composable
fun Maps(mapboxViewModel: MapboxViewModel = viewModel()) {
    // Collect current location from ViewModel
    val currentLocation by mapboxViewModel.currentLocation.collectAsState()

    // Initialize map view state
    val mapViewState = rememberMapViewportState {
        CameraOptions.Builder()
            .zoom(2.0)
            .center(Point.fromLngLat(-98.5, 39.5))
            .build()
    }

    // MapboxMap composable
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewState
    ) {
        MapEffect(Unit) { mapView ->
            // Optionally remove or comment out debug options
            // mapView.debugOptions = setOf(
            //     MapViewDebugOptions.TILE_BORDERS,
            //     MapViewDebugOptions.PARSE_STATUS,
            //     MapViewDebugOptions.TIMESTAMPS,
            //     MapViewDebugOptions.COLLISION,
            //     MapViewDebugOptions.STENCIL_CLIP,
            //     MapViewDebugOptions.DEPTH_BUFFER,
            //     MapViewDebugOptions.MODEL_BOUNDS,
            //     MapViewDebugOptions.TERRAIN_WIREFRAME
            // )

            val mapboxMap = mapView.getMapboxMap()

            // Set default camera options
            mapboxMap.setCamera(CameraOptions.Builder()
                .zoom(2.0)
                .center(Point.fromLngLat(-98.5, 39.5))
                .build()
            )

            // Update camera if current location is available
            currentLocation?.let { location ->
                Log.d("Mapss", "Updating camera to location: ${location.latitude}, ${location.longitude}")
                mapboxMap.setCamera(CameraOptions.Builder()
                    .zoom(14.0)
                    .center(Point.fromLngLat(location.longitude, location.latitude))
                    .build()
                )
            }
        }
    }
}


