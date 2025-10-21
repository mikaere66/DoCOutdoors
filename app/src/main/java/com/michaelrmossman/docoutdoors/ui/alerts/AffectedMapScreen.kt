package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.maps.AffectedClusterItem
import com.michaelrmossman.docoutdoors.ui.maps.CheckPermissions
import com.michaelrmossman.docoutdoors.ui.maps.ClusterItemMarker
import com.michaelrmossman.docoutdoors.ui.maps.ClusteringGroup
import com.michaelrmossman.docoutdoors.ui.maps.MapLoadProgress
import com.michaelrmossman.docoutdoors.ui.maps.MapsViewModel
import com.michaelrmossman.docoutdoors.ui.maps.OutdoorsMap
import com.michaelrmossman.docoutdoors.ui.maps.ZoomAllButton
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ITEMS_ALL
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ITEM_SINGLE
import com.michaelrmossman.docoutdoors.utils.MapUtils.EMPTY_LAT_LNG
import com.michaelrmossman.docoutdoors.utils.MapUtils.getAffectedDrawableId
import com.michaelrmossman.docoutdoors.utils.MapUtils.isValidLatLng
import com.michaelrmossman.docoutdoors.utils.MapUtils.locationPermissionGranted
import com.michaelrmossman.docoutdoors.utils.toLatLngBounds

/**
 * Shows a [GoogleMap] with collection of markers
 */
@Suppress("KotlinConstantConditions")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun AffectedMapScreen(
    affectedIndex: Int,
    alertIndex: Int,
    navigateUp: () -> Unit,
    // onMarkerClick: (Marker) -> Boolean = { false }
) {
    val alertsViewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModel.Factory
    )
    val viewState by alertsViewModel.alertsListState.collectAsState()

    val alert = viewState.alertsList[alertIndex]
    val affectedAssets = alert.affectedAssets.map { affected ->
        affected.toAffected(
            affectedCount = alert.affectedAssets.size
        )
    }
    val coroutineScope = rememberCoroutineScope()
    var isMapLoading by remember { mutableStateOf(true) }
    val mapsViewModel: MapsViewModel = viewModel(
        factory = MapsViewModel.Factory
    )
    var permissionGranted by remember {
        mutableStateOf(locationPermissionGranted())
    }
    val satelliteView by mapsViewModel.commonSatelliteView.observeAsState()
    val showLocation by mapsViewModel.commonShowLocation.observeAsState()
    val mappableAssets = affectedAssets.filter { affected ->
        isValidLatLng(affected.latLng)
    }
    val latLngList = mappableAssets.map { affected ->
        affected.latLng
    }
    val boundingBox = when (latLngList.isNotEmpty()) {
        true -> latLngList.toLatLngBounds()
        else -> {
            /* Just for safety. However, shouldn't be able to
               make it this far without valid coordinates */
            listOf(EMPTY_LAT_LNG).toLatLngBounds()
        }
    }
    val cameraPositionState = rememberCameraPositionState {
        position = when (affectedIndex) {
            Int.MAX_VALUE -> CameraPosition.fromLatLngZoom(
                boundingBox.center, MAP_ZOOM_ITEMS_ALL
            )
            else -> CameraPosition.fromLatLngZoom(
                mappableAssets[affectedIndex].latLng,
                MAP_ZOOM_ITEM_SINGLE
            )
        }
    }

    if (showLocation == 1) {
        CheckPermissions { result ->
            permissionGranted = result
        }
    }

    Scaffold(
        topBar = {
            QuadrupleTopAppBar(
                actions = {
                    ZoomAllButton(
                        boundingBox = boundingBox,
                        cameraPositionState = cameraPositionState,
                        coroutineScope = coroutineScope
                    )
                },
                navigateUp = { navigateUp() },
                titleId = R.string.affected_map,
                /* Assets affected (showing 4 of 5) */
                subtitle = stringResource(
                    R.string.affected_subtitle,
                    mappableAssets.size,
                    affectedAssets.size
                )
            )
        }
    ) { contentPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            OutdoorsMap(
                cameraPositionState = cameraPositionState,
                onMapLoaded = { isMapLoading = false },
                showLocation = showLocation,
                permissionGranted = permissionGranted,
                satelliteView = satelliteView
            ) {
                val affectedClusterItems by remember(
                    viewState.alertsList
                ) {
                    mutableStateOf(
                        mappableAssets.map { affected ->
                            AffectedClusterItem(affected = affected)
                        }
                    )
                }
                ClusteringGroup(
                    items = affectedClusterItems,
                    clusterItemContent = { affectedItem ->
                        ClusterItemMarker(
                            drawableId = getAffectedDrawableId(
                                itemType = (
                                    affectedItem as AffectedClusterItem
                                ).snippet
                            ),
                            status = String()
                        )
                    }
                )
                if (affectedIndex != Int.MAX_VALUE) {
                    /* Add an invisible marker and show its InfoWindow when map
                       called from AffectedBottomSheet | AffectedItemsScreen */
                    val affected = mappableAssets[affectedIndex]
                    val markerState = rememberMarkerState(
                        position = affected.latLng
                    )
                    Marker(
                        alpha = 0.0F,
                        anchor = Offset(0.5F, 0.75F),
                        state = markerState,
                        title = affected.name,
                        snippet = affected.type,
                        zIndex = -1.0F
                    )
                    markerState.showInfoWindow()
                }
            }

            if (isMapLoading) {
                MapLoadProgress(
                    isVisible = isMapLoading,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}