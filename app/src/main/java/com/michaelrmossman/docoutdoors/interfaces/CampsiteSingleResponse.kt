package com.michaelrmossman.docoutdoors.interfaces

import com.michaelrmossman.docoutdoors.model.CampsiteSerial

data class CampsiteSingleResponse(
    val campsiteSerial: CampsiteSerial? = null,
    val responseCode  : Int = 0
)