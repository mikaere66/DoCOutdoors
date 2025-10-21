package com.michaelrmossman.docoutdoors.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Polyline
import com.michaelrmossman.docoutdoors.utils.MAP_POLYLINE_WIDTH

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TrackPolyline(
    coordsList: List<List<LatLng>>
) {
    coordsList.forEach { latLngList ->
        Polyline(
            points = latLngList,
            color = Color.Red,
            width = MAP_POLYLINE_WIDTH,
            clickable = true,
            onClick = { polyline ->
                println("Polyline clicked")
            }
        )
    }
}