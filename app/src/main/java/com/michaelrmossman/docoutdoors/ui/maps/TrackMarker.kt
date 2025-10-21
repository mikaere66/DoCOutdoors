package com.michaelrmossman.docoutdoors.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.utils.BitmapParameters
import com.michaelrmossman.docoutdoors.utils.IconColor
import com.michaelrmossman.docoutdoors.utils.markerColors
import com.michaelrmossman.docoutdoors.utils.vectorToBitmap

/**
 * Shows a single [TrackKt] marker
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TrackMarker(
    track: TrackKt,
    onMarkerClick: (Marker) -> Boolean = { false }
) {
    val colors: IconColor = String().markerColors()
    val trackIcon = vectorToBitmap(
        LocalContext.current,
        BitmapParameters(
            id = R.drawable.baseline_hiking_black_24,
            iconColor = colors.iconColor.toArgb(),
            backgroundColor = colors.backgroundColor.toArgb()
        )
    )
    Marker(
        icon = trackIcon,
        state = rememberMarkerState(
            position = LatLng(track.lat,track.lon)
        ),
        title = track.name,
        snippet = track.regions,
        onClick = { marker ->
            onMarkerClick(marker)
            false
        }
    )
}