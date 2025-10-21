package com.michaelrmossman.docoutdoors.interfaces

import com.michaelrmossman.docoutdoors.model.TrackSerial

data class TrackSingleResponse(
    val responseCode: Int = 0,
    val trackSerial : TrackSerial? = null
)