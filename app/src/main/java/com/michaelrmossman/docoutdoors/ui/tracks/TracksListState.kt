package com.michaelrmossman.docoutdoors.ui.tracks

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.interfaces.TracksUiState
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.utils.MapUtils.EMPTY_LAT_LNG
import com.michaelrmossman.docoutdoors.utils.toLatLngBounds

data class TracksListState(
    val boundingBox: LatLngBounds       = listOf(EMPTY_LAT_LNG).toLatLngBounds(),
    val containsValidCoords: Boolean    = false,
    val searchBy   : SearchBy           = SearchBy.Name,
    val trackCoords: List<List<LatLng>> = emptyList(),
    // val tracksListIncomplete: Boolean   = false,
    val tracksList : List<TrackKt>      = emptyList(),
    val trackState : TracksUiState      = TracksUiState.Loading
)