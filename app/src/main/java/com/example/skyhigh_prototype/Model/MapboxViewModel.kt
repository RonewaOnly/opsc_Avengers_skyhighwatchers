@file:Suppress("PackageName")

package com.example.skyhigh_prototype.Model

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
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
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions
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

@SuppressLint("IncorrectNumberOfArgumentsInExpression")
@Composable
fun Maps(mapboxViewModel: MapboxViewModel = viewModel()) {
    // Collect current location from ViewModel
    val currentLocation by mapboxViewModel.currentLocation.collectAsState()

    // Initialize map view state
    val mapViewState = rememberMapViewportState {
        CameraOptions.Builder()
            .zoom(2.0)
            .center(Point.fromLngLat(-currentLocations.LONGITUDE, currentLocations.LATITUDE))
            .build()
    }
    val mapViewportState = rememberMapViewportState()
    // Access resources from LocalContext
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        style = { MapboxStandardStyle() }

    ) {
        // Create and remember the icon image to be used for the point annotation
        // The icon image will be added to map automatically when associated with a PointAnnotation.
        val marker = rememberIconImage(key = R.drawable.red_marker, painter = painterResource(R.drawable.red_marker))
        // Insert a PointAnnotation composable function with the geographic coordinate to the content of MapboxMap composable function.
        PointAnnotation(point = Point.fromLngLat(18.06, 59.31)) {
            // specify the marker image
            iconImage = marker
        }
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
//                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
                pulsingEnabled = true
                locationPuck = LocationPuck2D(
                    topImage = ImageHolder.from(R.drawable.custom_user_icon), // ImageHolder also accepts Bitmap
                    bearingImage = ImageHolder.from(R.drawable.custom_user_puck_icon),
                    shadowImage = ImageHolder.from(R.drawable.custom_user_arrow),
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
            // Use density in the viewport state transition
//            mapViewportState.transitionToFollowPuckState(
//                followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
//                    .bearing(FollowPuckViewportStateBearing.Constant(0.0))
//                    .padding(EdgeInsets(200.0 * density, 0.0, 0.0, 0.0)) // Correctly use density
//                    .build(),
//            ) { success ->
//                // The transition has been completed
//            }
        }




//    // MapboxMap composable
//    MapboxMap(
//        modifier = Modifier.fillMaxSize(),
//        mapViewportState = mapViewState
//    ) {
//        MapEffect(Unit) { mapView ->
//            // Optionally remove or comment out debug options
//            // mapView.debugOptions = setOf(
//            //     MapViewDebugOptions.TILE_BORDERS,
//            //     MapViewDebugOptions.PARSE_STATUS,
//            //     MapViewDebugOptions.TIMESTAMPS,
//            //     MapViewDebugOptions.COLLISION,
//            //     MapViewDebugOptions.STENCIL_CLIP,
//            //     MapViewDebugOptions.DEPTH_BUFFER,
//            //     MapViewDebugOptions.MODEL_BOUNDS,
//            //     MapViewDebugOptions.TERRAIN_WIREFRAME
//            // )
//
//            val mapboxMap = mapView.getMapboxMap()
//
//            // Set default camera options
//            mapboxMap.setCamera(CameraOptions.Builder()
//                .zoom(2.0)
//                .center(Point.fromLngLat(-98.5, 39.5))
//                .build()
//            )
//
//            // Update camera if current location is available
//            currentLocation?.let { location ->
//                Log.d("Maps", "Updating camera to location: ${location.latitude}, ${location.longitude}")
//                mapboxMap.setCamera(CameraOptions.Builder()
//                    .zoom(14.0)
//                    .center(Point.fromLngLat(location.longitude, location.latitude))
//                    .build()
//                )
//            }
       }
}



