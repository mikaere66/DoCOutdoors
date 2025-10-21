package com.michaelrmossman.docoutdoors.model

import kotlinx.serialization.Serializable

/* Alert(s) for an individual asset */
@Serializable
data class TrackExtraSerial(

    /* Labels in same order as original JSON response.

     * This is the track version of AlertExtraSerial,
     * but with String as assetId, instead of an Int.
     * Also note empty string, instead of nullable */
    val assetId: String = String(),

    val name: String,

    val alerts: List<AffectedExtraSerial>
)