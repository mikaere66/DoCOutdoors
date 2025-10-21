package com.michaelrmossman.docoutdoors.model

import kotlinx.serialization.Serializable

/* Alert(s) for an individual asset */
@Serializable
data class AlertExtraSerial(

    /* Labels in same order as original JSON response. Just
     * for Campsites and Huts (with Int as assetId) ... see
     * also TrackExtraSerial which has String as assetId */
    val assetId: Int?,

    val name: String,

    val alerts: List<AffectedExtraSerial>
)