@file:Suppress("PackageName")

package com.example.skyhigh_prototype.Model

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.debugoptions.MapViewDebugOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

class MapboxViewModel {

    @Composable
    fun Maps() {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(2.0)
                    center(Point.fromLngLat(-98.0, 39.5))
                    pitch(0.0)
                    bearing(0.0)
                }
            },
        ) {
            MapEffect(Unit) { mapView ->
                // Use mapView to access the Mapbox Maps APIs not in the Compose extension.
                // Changes inside `MapEffect` may conflict with Compose states.
                // For example, to enable debug mode:
                mapView.debugOptions = setOf(
                    MapViewDebugOptions.TILE_BORDERS,
                    MapViewDebugOptions.PARSE_STATUS,
                    MapViewDebugOptions.TIMESTAMPS,
                    MapViewDebugOptions.COLLISION,
                    MapViewDebugOptions.STENCIL_CLIP,
                    MapViewDebugOptions.DEPTH_BUFFER,
                    MapViewDebugOptions.MODEL_BOUNDS,
                    MapViewDebugOptions.TERRAIN_WIREFRAME,
                )
            }

        }
    }
}