package com.michaelrmossman.docoutdoors.ui.favourites

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.model.Favourite
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.components.SingleActionMenu
import com.michaelrmossman.docoutdoors.ui.maps.CampsiteMarker
import com.michaelrmossman.docoutdoors.ui.maps.CheckPermissions
import com.michaelrmossman.docoutdoors.ui.maps.HutMarker
import com.michaelrmossman.docoutdoors.ui.maps.MapLoadProgress
import com.michaelrmossman.docoutdoors.ui.maps.MapsViewModel
import com.michaelrmossman.docoutdoors.ui.maps.OutdoorsMap
import com.michaelrmossman.docoutdoors.ui.maps.TrackDloadProgress
import com.michaelrmossman.docoutdoors.ui.maps.TrackMarker
import com.michaelrmossman.docoutdoors.ui.maps.TrackPolyline
import com.michaelrmossman.docoutdoors.ui.maps.ZoomAllButton
import com.michaelrmossman.docoutdoors.utils.MAP_MIDDLE_NZ_LAT
import com.michaelrmossman.docoutdoors.utils.MAP_MIDDLE_NZ_LON
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ALL_DELAY
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ALL_PADDING_MEDIUM
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ITEM_SINGLE
import com.michaelrmossman.docoutdoors.utils.MapUtils.locationPermissionGranted
import com.michaelrmossman.docoutdoors.utils.MapUtils.zoomAll
import kotlinx.coroutines.delay

/**
 * Shows a [GoogleMap] with single [Favourite] marker and/or polyline
 */
@Suppress("KotlinConstantConditions")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun SingleMapScreen(
    assetId: String,
    itemType: AssetType,
    navigateUp: () -> Unit,
    onMarkerClick: (Marker) -> Boolean = { false }
) {
    val viewModel: MapsViewModel = viewModel(
        factory = MapsViewModel.Factory
    )

    LaunchedEffect(key1 = Unit) {
        when (itemType) {
            AssetType.Campsite -> viewModel.getCampsiteById(assetId)
            AssetType.Hut      -> viewModel.getHutById(assetId)
            AssetType.Track    -> viewModel.getTrackById(assetId)
        }
    }

    val cameraPosition = LatLng(MAP_MIDDLE_NZ_LAT, MAP_MIDDLE_NZ_LON)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, 10F)
    }
    val coroutineScope = rememberCoroutineScope()
    var isCampsiteReady by remember { mutableStateOf(false) }
    /* A simple boolean just doesn't cut it for isDownloading. We want to
       hide the progress indicator, but not cancel the LaunchedEffect */
    var isDownloading by remember { mutableIntStateOf(0) }
    var isHutReady by remember { mutableStateOf(false) }
    var isMapLoading by remember { mutableStateOf(true) }
    var isTrackReady by remember { mutableStateOf(false) }
    var permissionGranted by remember {
        mutableStateOf(locationPermissionGranted())
    }
    val satelliteView by viewModel.commonSatelliteView.observeAsState()
    val showLocation by viewModel.commonShowLocation.observeAsState()
    val viewState by viewModel.singleMapState.collectAsState()
    viewState.trackKt?.let { track ->
        if (track.lineCount > 0) {
            viewModel.setCoordsByTrackId(
                id = track.assetId,
                lineCount = track.lineCount
            )
        }
    }
    val zoomOnDload by viewModel.tracksZoomOnDload.observeAsState()

    if (showLocation == 1) {
        CheckPermissions { result ->
            permissionGranted = result
        }
    }

    Scaffold(
        topBar = {
            QuadrupleTopAppBar(
                actions = {
                    /* Only show for single track */
                    viewState.trackKt?.let { track ->
                        ZoomAllButton(
                            boundingBox = viewState.boundingBox,
                            cameraPositionState = cameraPositionState,
                            coroutineScope = coroutineScope,
                            isEnabled = track.lineCount > 0
                        )
                        SingleActionMenu(
                            onSingleItemClick = {
                                isDownloading = 1
                                viewModel.downloadTrackExtras(
                                    track.assetId
                                )
                            },
                            isEnabled = track.lineCount == 0,
                            itemStringId = R.string.tracks_map_dl
                        )
                    }
                },
                navigateUp = { navigateUp() },
                titleId = R.string.faves_map,
                subtitle = when (itemType) {
                    AssetType.Campsite -> viewState.campsiteKt?.name
                    AssetType.Hut      -> viewState.hutKt?.name
                    AssetType.Track    -> viewState.trackKt?.name
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
                if (isCampsiteReady) {
                    viewState.campsiteKt?.let { campsite ->
                        CampsiteMarker(
                            campsite = campsite,
                            onMarkerClick = onMarkerClick
                        )
                    }
                }

                if (isHutReady) {
                    viewState.hutKt?.let { hut ->
                        HutMarker(
                            hut = hut,
                            onMarkerClick = onMarkerClick
                        )
                    }
                }

                if (isTrackReady) {
                    viewState.trackKt?.let { track ->
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

                LaunchedEffect(key1 = viewState) {
                    /* Only set camera position when first loading */
                    if (
                        !isCampsiteReady
                        &&
                        !isHutReady
                        &&
                        !isTrackReady
                    ) {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(
                                when(itemType) {
                                    AssetType.Campsite -> LatLng(
                                        viewState.campsiteKt?.lat
                                            ?: MAP_MIDDLE_NZ_LAT,
                                        viewState.campsiteKt?.lon
                                            ?: MAP_MIDDLE_NZ_LON
                                    )
                                    AssetType.Hut -> LatLng(
                                        viewState.hutKt?.lat
                                            ?: MAP_MIDDLE_NZ_LAT,
                                        viewState.hutKt?.lon
                                            ?: MAP_MIDDLE_NZ_LON
                                    )
                                    AssetType.Track -> LatLng(
                                        viewState.trackKt?.lat
                                            ?: MAP_MIDDLE_NZ_LAT,
                                        viewState.trackKt?.lon
                                            ?: MAP_MIDDLE_NZ_LON
                                    )
                                },
                                MAP_ZOOM_ITEM_SINGLE
                            )
                        )
                    }

                    viewState.campsiteKt?.let { campsite ->
                        isCampsiteReady = true
                    }
                    viewState.hutKt?.let { hut ->
                        isHutReady = true
                    }
                    viewState.trackKt?.let { track ->
                        isTrackReady = true
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