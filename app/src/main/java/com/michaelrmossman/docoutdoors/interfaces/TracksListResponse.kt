package com.michaelrmossman.docoutdoors.interfaces

import com.michaelrmossman.docoutdoors.model.TrackSerial

data class TracksListResponse(
    val responseCode: Int = 0, /* raw HTTP status code */
    val tracksList  : List<TrackSerial> = emptyList()
)