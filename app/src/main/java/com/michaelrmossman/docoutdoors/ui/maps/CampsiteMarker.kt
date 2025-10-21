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
import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.utils.BitmapParameters
import com.michaelrmossman.docoutdoors.utils.IconColor
import com.michaelrmossman.docoutdoors.utils.TextUtils.getSnippetForCampsiteOrHut
import com.michaelrmossman.docoutdoors.utils.markerColors
import com.michaelrmossman.docoutdoors.utils.vectorToBitmap

/**
 * Shows a single [CampsiteKt] marker
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun CampsiteMarker(
    campsite: CampsiteKt,
    onMarkerClick: (Marker) -> Boolean = { false }
) {
    val colors: IconColor = campsite.status.markerColors()
    val campsiteIcon = vectorToBitmap(
        LocalContext.current,
        BitmapParameters(
            id = R.drawable.icons_lib_campsite_black_24,
            iconColor = colors.iconColor.toArgb(),
            backgroundColor = colors.backgroundColor.toArgb()
        )
    )
    Marker(
        icon = campsiteIcon,
        state = rememberMarkerState(
            position = LatLng(campsite.lat,campsite.lon)
        ),
        title = campsite.name,
        snippet = getSnippetForCampsiteOrHut(
            region = campsite.region,
            status = campsite.status
        ),
        onClick = { marker ->
            onMarkerClick(marker)
            false
        }
    )
}