package com.michaelrmossman.docoutdoors.ui.campsites

import com.google.android.gms.maps.model.LatLngBounds
import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.interfaces.CampsitesUiState
import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.utils.MapUtils.EMPTY_LAT_LNG
import com.michaelrmossman.docoutdoors.utils.toLatLngBounds

data class CampsitesListState(
    val boundingBox  : LatLngBounds     = listOf(EMPTY_LAT_LNG).toLatLngBounds(),
    val campsitesList: List<CampsiteKt> = emptyList(),
    val campsiteState: CampsitesUiState = CampsitesUiState.Loading,
    val containsValidCoords: Boolean    = false,
    val searchBy           : SearchBy   = SearchBy.Name
)