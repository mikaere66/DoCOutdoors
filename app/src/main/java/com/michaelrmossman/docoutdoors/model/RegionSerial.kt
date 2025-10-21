package com.michaelrmossman.docoutdoors.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RegionSerial(

    @SerialName(value = "id")
    val regionCode: String,

    @SerialName(value = "name")
    val regionName: String
)
