package com.michaelrmossman.docoutdoors.interfaces

import com.michaelrmossman.docoutdoors.model.CampsiteSerial

data class CampsitesListResponse(
    val campsitesList  : List<CampsiteSerial> = emptyList(),
    val responseCode: Int = 0 /* raw HTTP status code */
)