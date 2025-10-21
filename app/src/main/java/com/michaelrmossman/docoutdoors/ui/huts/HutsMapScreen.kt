package com.michaelrmossman.docoutdoors.ui.huts

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
import com.michaelrmossman.docoutdoors.ui.maps.CheckPermissions
import com.michaelrmossman.docoutdoors.ui.maps.ClusterItemMarker
import com.michaelrmossman.docoutdoors.ui.maps.ClusteringGroup
import com.michaelrmossman.docoutdoors.ui.maps.HutClusterItem
import com.michaelrmossman.docoutdoors.ui.maps.HutMarker
import com.michaelrmossman.docoutdoors.ui.maps.MapLoadProgress
import com.michaelrmossman.docoutdoors.ui.maps.MapsViewModel
import com.michaelrmossman.docoutdoors.ui.maps.OutdoorsMap
import com.michaelrmossman.docoutdoors.ui.maps.ZoomAllButton
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ITEMS_ALL
import com.michaelrmossman.docoutdoors.utils.MAP_ZOOM_ITEM_SINGLE
import com.michaelrmossman.docoutdoors.utils.MapUtils.locationPermissionGranted
import com.michaelrmossman.docoutdoors.utils.TextUtils.getMapsSubtitle
import com.michaelrmossman.docoutdoors.utils.TextUtils.getSnippetForCampsiteOrHut

/**
 * Shows a [GoogleMap] with collection of markers
 */
@Suppress("KotlinConstantConditions")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun HutsMapScreen(
    itemIndex: Int,
    navigateUp: () -> Unit,
    onMarkerClick: (Marker) -> Boolean = { false }
) {
    val hutsViewModel: HutsViewModel = viewModel(
        factory = HutsViewModel.Factory
    )
    val viewState by hutsViewModel.hutsListState.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = when (itemIndex) {
            Int.MAX_VALUE -> CameraPosition.fromLatLngZoom(
                viewState.boundingBox.center,
                MAP_ZOOM_ITEMS_ALL
            )
            else -> CameraPosition.fromLatLngZoom(
                LatLng(
                    viewState.hutsList[itemIndex].lat,
                    viewState.hutsList[itemIndex].lon
                ),
                MAP_ZOOM_ITEM_SINGLE
            )
        }
    }
    val coroutineScope = rememberCoroutineScope()
    var isMapLoading by remember { mutableStateOf(true) }
    val listFilterRegion by hutsViewModel.hutsFilterByRegion.observeAsState()
    val mapsViewModel: MapsViewModel = viewModel(
        factory = MapsViewModel.Factory
    )
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
                titleId = R.string.huts_map,
                subtitle = when (itemIndex) {
                    Int.MAX_VALUE -> getMapsSubtitle(
                        itemType = FilterType.Huts,
                        region = listFilterRegion
                    )
                    else -> viewState.hutsList[itemIndex].name
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
                        val hutClusterItems by remember(
                            viewState.hutsList
                        ) {
                            mutableStateOf(
                                viewState.hutsList.map { hut ->
                                    HutClusterItem(
                                        hut = hut,
                                        region = getSnippetForCampsiteOrHut(
                                            region = hut.region,
                                            status = hut.status
                                        )
                                    )
                                }
                            )
                        }
                        ClusteringGroup(
                            items = hutClusterItems,
                            clusterItemContent = { hutItem ->
                                ClusterItemMarker(
                                    drawableId =
                                        R.drawable.outline_house_black_24,
                                    status = (
                                        hutItem as HutClusterItem
                                    ).hut.status
                                )
                            }
                        )
                    }
                    else -> {
                        val hut = viewState.hutsList[itemIndex]
                        HutMarker(
                            hut = hut,
                            onMarkerClick = onMarkerClick
                        )
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