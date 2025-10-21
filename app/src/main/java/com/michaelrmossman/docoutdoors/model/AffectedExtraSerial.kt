package com.michaelrmossman.docoutdoors.model

import kotlinx.serialization.Serializable

/* List item for Alert(s) per individual asset */
@Serializable
data class AffectedExtraSerial(

    /* Labels in same order as original JSON response ...
     * Used by both AlertExtraSerial and TrackExtraSerial */
    val displayDate: String,

    val heading: String,

    val detail: String
)