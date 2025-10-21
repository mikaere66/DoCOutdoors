package com.michaelrmossman.docoutdoors.interfaces

import com.michaelrmossman.docoutdoors.model.AlertSerial

data class AlertsListResponse(
    val alertsList  : List<AlertSerial> = emptyList(),
    val responseCode: Int = 0 /* raw HTTP status code */
)