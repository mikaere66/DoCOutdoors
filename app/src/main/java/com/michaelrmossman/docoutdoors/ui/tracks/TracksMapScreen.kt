package com.michaelrmossman.docoutdoors.ui.tracks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.components.SingleActionMenu
import com.michaelrmossman.docoutdoors.ui.maps.CheckPermissions
import com.michaelrmossman.docoutdoors.ui.maps.ClusterItemMarker
import com.michaelrmossman.docoutdoors.ui.maps.ClusteringGroup
import com.michaelrmossman.docoutdoors.ui.maps.MapLoadProgress
import com.michaelrmossman.docoutdoors.ui.maps.MapsViewModel
import com.michaelrmossman.docoutdoors.ui.maps.OutdoorsMap
import com.michaelrmossman.docoutdoors.ui.maps.TrackClusterItem
import com.michaelrmossman.docoutdoors.ui.maps.TrackDloadProgress
import com.michaelrmossman.docoutdoors.ui.maps.TrackMarker
import com.michaelrmossman.docoutdoors.ui.maps.TrackPolyline
import com.michaelrmossman.docoutdoors.ui.maps.ZoomAllButton
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ALL_DELAY
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ALL_PADDING_MEDIUM
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ITEMS_ALL
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ITEM_SINGLE
import com.michaelrmossman.docoutdoors.utils.MapUtils.locationPermissionGranted
import com.michaelrmossman.docoutdoors.utils.MapUtils.zoomAll
import com.michaelrmossman.docoutdoors.utils.TextUtils.getMapsSubtitle
import kotlinx.coroutines.delay

/**
 * Shows a [GoogleMap] with collection of markers and/or polyline
 */
@Suppress("KotlinConstantConditions")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TracksMapScreen(
    itemIndex: Int,
    navigateUp: () -> Unit,
    onMarkerClick: (Marker) -> Boolean = { false }
) {
    val tracksViewModel: TracksViewModel = viewModel(
        factory = TracksViewModel.Factory
    )
    val viewState by tracksViewModel.tracksListState.collectAsState()
    val zoomOnDload by tracksViewModel.tracksZoomOnDload.observeAsState()
    /* A simple boolean just doesn't cut it for isDownloading. We want to
       hide the progress indicator, but not cancel the LaunchedEffect */
    var isDownloading by remember { mutableIntStateOf(0) }
    var isMapLoading by remember { mutableStateOf(true) }
    val listFilterRegion by tracksViewModel.tracksFilterByRegion.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = when (itemIndex) {
            Int.MAX_VALUE -> CameraPosition.fromLatLngZoom(
                viewState.boundingBox.center,
                MAP_ZOOM_ITEMS_ALL
            )
            else -> CameraPosition.fromLatLngZoom(
                LatLng(
                    viewState.tracksList[itemIndex].lat,
                    viewState.tracksList[itemIndex].lon
                ),
                MAP_ZOOM_ITEM_SINGLE
            )
        }
    }
    val mapsViewModel: MapsViewModel = viewModel(
        factory = MapsViewModel.Factory
    )
    var permissionGranted by remember {
        mutableStateOf(locationPermissionGranted())
    }
    val satelliteView by mapsViewModel.commonSatelliteView.observeAsState()
    val showLocation by mapsViewModel.commonShowLocation.observeAsState()

    when (itemIndex) {
        Int.MAX_VALUE -> tracksViewModel.resetBoundingBox()
        else -> when (viewState.tracksList[itemIndex].lineCount) {
            0 -> tracksViewModel.resetBoundingBox()
            else -> tracksViewModel.setCoordsByTrackId(
                id = viewState.tracksList[itemIndex].assetId,
                lineCount = viewState.tracksList[itemIndex].lineCount
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
                        boundingBox = viewState.boundingBox,
                        cameraPositionState = cameraPositionState,
                        coroutineScope = coroutineScope
                    )
                    /* Only show for single item map */
                    if (itemIndex != Int.MAX_VALUE) {
                        val track = viewState.tracksList[itemIndex]
                        SingleActionMenu(
                            onSingleItemClick = {
                                isDownloading = 1
                                tracksViewModel.downloadExtras(track.assetId)
                            },
                            isEnabled = track.lineCount == 0,
                            itemStringId = R.string.tracks_map_dl
                        )
                    }
                },
                navigateUp = { navigateUp() },
                titleId = R.string.tracks_map,
                subtitle = when (itemIndex) {
                    Int.MAX_VALUE -> getMapsSubtitle(
                        itemType = FilterType.Tracks,
                        region = listFilterRegion
                    )
                    else -> viewState.tracksList[itemIndex].name
                }
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
                when (itemIndex) {
                    Int.MAX_VALUE -> {
                        val trackClusterItems by remember(
                            viewState.tracksList
                        ) {
                            mutableStateOf(
                                viewState.tracksList.map { track ->
                                    TrackClusterItem(track = track)
                                }
                            )
                        }
                        ClusteringGroup(
                            items = trackClusterItems,
                            clusterItemContent = { trackItem ->
                                ClusterItemMarker(
                                    drawableId =
                                        R.drawable.baseline_hiking_black_24,
                                    status = String()
                                )
                            }
                        )
                    }
                    else -> {
                        val track = viewState.tracksList[itemIndex]
                        if (track.lineCount > 0) {
                            TrackPolyline(
                                coordsList = viewState.trackCoords
                            )

                            if (isDownloading != 0) {
                                isDownloading = -1
                                if (zoomOnDload == 1) {
                                    LaunchedEffect(key1 = Unit) {
                                        delay(MAP_ZOOM_ALL_DELAY)
                                        zoomAll(
                                            viewState.boundingBox,
                                            coroutineScope,
                                            cameraPositionState,
                                            /* With slightly larger padding than
                                               default, user gets satisfaction
                                               of seeing something happen when
                                               zoomAll on toolbar is clicked */
                                            MAP_ZOOM_ALL_PADDING_MEDIUM
                                        )
                                    }
                                }
                            }
                        }

                        TrackMarker(
                            track = track,
                            onMarkerClick = onMarkerClick
                        )
                    }
                }
            }

            if (isDownloading > 0) {
                TrackDloadProgress(color = Color.Red)
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