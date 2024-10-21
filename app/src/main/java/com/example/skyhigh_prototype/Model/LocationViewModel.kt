package com.example.skyhigh_prototype.Model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

val currentLocations = com.example.skyhigh_prototype.Data.Location

class LocationViewModel : ViewModel() {
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    private val LOCATION_REQUEST_CODE = 100

    // Initialize the location client safely
    fun initLocationClient(context: Context) {
        if (!::fusedLocationClient.isInitialized) {
            Log.d("LocationViewModel", "Initializing FusedLocationProviderClient")
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        } else {
            Log.d("LocationViewModel", "FusedLocationProviderClient already initialized")
        }
    }

    // Request location permissions if not granted
    private fun requestLocationPermissions(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    // Check if location services are enabled
    private fun isLocationServicesEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsEnabled || isNetworkEnabled
    }

    fun requestLocation(context: Context) {
        // Ensure the fusedLocationClient is initialized
        initLocationClient(context)

        // Check for permissions, and request them if not granted
        requestLocationPermissions(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationViewModel", "Location permissions not granted")
            return
        }

        // Check if location services are enabled
        if (!isLocationServicesEnabled(context)) {
            Log.e("LocationViewModel", "Location services are disabled")
            return
        }

        // Create a location request
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        // Initialize location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    viewModelScope.launch {
                        _location.emit(location)
                    }
                    currentLocations.LATITUDE = location.latitude
                    currentLocations.LONGITUDE =  location.longitude
                    Log.d("LOCATION CURRENT USER: ", location.toString())
                }
            }
        }

        // Request location updates
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            context.mainLooper
        )
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates() // Ensure we stop updates when the ViewModel is cleared
    }
}

@Composable
fun LocationScreen(viewModel: LocationViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val location by viewModel.location.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initLocationClient(context) // Ensure init is called here
        viewModel.requestLocation(context) // Then request location
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLocationUpdates() // Stop location updates when composable is disposed
        }
    }

    // Use the location value in your UI
    location?.let { loc ->
        Text("Latitude: ${loc.latitude}, Longitude: ${loc.longitude}")
        currentLocations.LATITUDE = loc.latitude
        currentLocations.LONGITUDE = loc.longitude
    }
}
