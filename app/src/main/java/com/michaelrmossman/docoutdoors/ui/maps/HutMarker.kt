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
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.utils.BitmapParameters
import com.michaelrmossman.docoutdoors.utils.IconColor
import com.michaelrmossman.docoutdoors.utils.TextUtils.getSnippetForCampsiteOrHut
import com.michaelrmossman.docoutdoors.utils.markerColors
import com.michaelrmossman.docoutdoors.utils.vectorToBitmap

/**
 * Shows a single [HutKt] marker
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun HutMarker(
    hut: HutKt,
    onMarkerClick: (Marker) -> Boolean = { false }
) {
    val colors: IconColor = hut.status.markerColors()
    val hutIcon = vectorToBitmap(
        LocalContext.current,
        BitmapParameters(
            id = R.drawable.outline_house_black_24,
            iconColor = colors.iconColor.toArgb(),
            backgroundColor = colors.backgroundColor.toArgb()
        )
    )
    Marker(
        icon = hutIcon,
        state = rememberMarkerState(
            position = LatLng(hut.lat,hut.lon)
        ),
        title = hut.name,
        snippet = getSnippetForCampsiteOrHut(
            region = hut.region,
            status = hut.status
        ),
        onClick = { marker ->
            onMarkerClick(marker)
            false
        }
    )
}