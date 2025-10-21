package com.michaelrmossman.docoutdoors.ui.maps

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ALL_PADDING_SMALL
import com.michaelrmossman.docoutdoors.utils.MapUtils.zoomAll
import kotlinx.coroutines.CoroutineScope

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun ZoomAllButton(
    boundingBox: LatLngBounds,
    cameraPositionState: CameraPositionState,
    coroutineScope: CoroutineScope,
    isEnabled: Boolean = true,
    padding: Int = MAP_ZOOM_ALL_PADDING_SMALL
) {
    IconButton(
        enabled = isEnabled,
        onClick = {
            zoomAll(
                boundingBox,
                coroutineScope,
                cameraPositionState,
                padding
            )
        }
    ) {
        Icon(
            Icons.Outlined.ZoomOutMap,
            contentDescription = stringResource(
                R.string.menu_zoom_all
            )
        )
    }
}