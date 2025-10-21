package com.michaelrmossman.docoutdoors.ui.huts

import com.google.android.gms.maps.model.LatLngBounds
import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.interfaces.HutsUiState
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.utils.MapUtils.EMPTY_LAT_LNG
import com.michaelrmossman.docoutdoors.utils.toLatLngBounds

data class HutsListState(
    val boundingBox: LatLngBounds    = listOf(EMPTY_LAT_LNG).toLatLngBounds(),
    val containsValidCoords: Boolean = false,
    val hutsList: List<HutKt>        = emptyList(),
    val hutState: HutsUiState        = HutsUiState.Loading,
    val searchBy: SearchBy           = SearchBy.Name
)