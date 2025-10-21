package com.michaelrmossman.docoutdoors.interfaces

import com.michaelrmossman.docoutdoors.model.HutSerial

data class HutsListResponse(
    val hutsList    : List<HutSerial> = emptyList(),
    val responseCode: Int = 0 /* raw HTTP status code */
)