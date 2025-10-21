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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.model.Favourite
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.maps.CampsiteMarker
import com.michaelrmossman.docoutdoors.ui.maps.CheckPermissions
import com.michaelrmossman.docoutdoors.ui.maps.HutMarker
import com.michaelrmossman.docoutdoors.ui.maps.MapLoadProgress
import com.michaelrmossman.docoutdoors.ui.maps.MapsViewModel
import com.michaelrmossman.docoutdoors.ui.maps.OutdoorsMap
import com.michaelrmossman.docoutdoors.ui.maps.TrackMarker
import com.michaelrmossman.docoutdoors.ui.maps.ZoomAllButton
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ITEMS_ALL
import com.michaelrmossman.docoutdoors.utils.MapUtils.locationPermissionGranted

/**
 * Shows a [GoogleMap] with all [Favourite] markers
 */
@Suppress("KotlinConstantConditions")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MultiMapScreen(
    navigateUp: () -> Unit,
    onMarkerClick: (Marker) -> Boolean = { false }
) {
    val favesViewModel: FavesViewModel = viewModel(factory = FavesViewModel.Factory)
    val favourites by favesViewModel.favourites.observeAsState(initial = emptyList())
    val mapsViewModel: MapsViewModel = viewModel(
        factory = MapsViewModel.Factory
    )
    val viewState by mapsViewModel.singleMapState.collectAsState()
    mapsViewModel.setFavesBoundingBox(favourites)

    val cameraPositionState = rememberCameraPositionState()
    LaunchedEffect(key1 = Unit) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            viewState.boundingBox.center, MAP_ZOOM_ITEMS_ALL
        )
    }

    val coroutineScope = rememberCoroutineScope()
    var isMapLoading by remember { mutableStateOf(true) }
    var permissionGranted by remember {
        mutableStateOf(locationPermissionGranted())
    }
    val satelliteView by mapsViewModel.commonSatelliteView.observeAsState()
    val showLocation by mapsViewModel.commonShowLocation.observeAsState()

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
                },
                navigateUp = { navigateUp() },
                titleId = R.string.faves_map,
                subtitle = stringResource(R.string.faves_map_subtitle)
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
                favourites.forEach { fave ->
                    when (fave.itemType) {
                        AssetType.Campsite -> {
                            fave.campsiteKt?.let { campsite ->
                                CampsiteMarker(
                                    campsite = campsite,
                                    onMarkerClick = onMarkerClick
                                )
                            }
                        }
                        AssetType.Hut -> {
                            fave.hutKt?.let { hut ->
                                HutMarker(
                                    hut = hut,
                                    onMarkerClick = onMarkerClick
                                )
                            }
                        }
                        AssetType.Track -> {
                            fave.trackKt?.let { track ->
                                TrackMarker(
                                    track = track,
                                    onMarkerClick = onMarkerClick
                                )
                            }
                        }
                    }
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