package com.michaelrmossman.docoutdoors.ui.favourites

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.utils.MapUtils.EMPTY_LAT_LNG
import com.michaelrmossman.docoutdoors.utils.MapUtils.getLatLngBounds

data class SingleMapState(
    val boundingBox: LatLngBounds       = getLatLngBounds(EMPTY_LAT_LNG),
    val campsiteKt : CampsiteKt?        = null,
    val hutKt      : HutKt?             = null,
    val trackCoords: List<List<LatLng>> = emptyList(),
    val trackKt    : TrackKt?           = null
)