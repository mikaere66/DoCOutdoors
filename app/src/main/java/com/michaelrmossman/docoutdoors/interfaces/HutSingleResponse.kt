package com.michaelrmossman.docoutdoors.interfaces

import com.michaelrmossman.docoutdoors.model.HutSerial

data class HutSingleResponse(
    val hutSerial   : HutSerial? = null,
    val responseCode: Int = 0
)