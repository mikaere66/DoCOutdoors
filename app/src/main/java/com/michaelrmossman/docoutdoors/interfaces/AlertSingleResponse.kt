package com.michaelrmossman.docoutdoors.interfaces

import com.michaelrmossman.docoutdoors.model.AlertSerial

data class AlertSingleResponse(
    val alertSerial : AlertSerial? = null,
    val responseCode: Int = 0
)