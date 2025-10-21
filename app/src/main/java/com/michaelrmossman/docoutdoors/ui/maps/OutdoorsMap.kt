package com.michaelrmossman.docoutdoors.ui.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType

/**
 * Shows a [GoogleMap] with collection of markers and/or polyline
 */
@Composable
fun OutdoorsMap(
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
    showLocation: Int?,
    permissionGranted: Boolean,
    satelliteView: Int?,
    content: @Composable @GoogleMapComposable () -> Unit
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { onMapLoaded() },
        properties = MapProperties(
            isMyLocationEnabled = (
                showLocation == 1
                &&
                permissionGranted
            ),
            mapType = when (satelliteView == 1) {
                true -> MapType.SATELLITE
                else -> MapType.NORMAL
            }
        )
    ) {
        content()
    }
}