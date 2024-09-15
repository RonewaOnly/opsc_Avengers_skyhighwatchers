package com.example.skyhigh_prototype.Model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
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


class LocationViewModel : ViewModel() {
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun initLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun requestLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission not granted
            return
        }

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    viewModelScope.launch {
                        _location.emit(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
    }
}

@Composable
fun LocationScreen(viewModel: LocationViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val location by viewModel.location.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initLocationClient(context)
        viewModel.requestLocation(context)
    }

    // Use the location value in your UI
    location?.let { loc ->
        Text("Latitude: ${loc.latitude}, Longitude: ${loc.longitude}")
    }
}