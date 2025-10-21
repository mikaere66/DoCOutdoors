package com.michaelrmossman.docoutdoors.model

import com.google.gson.JsonArray
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AlertSerial(

    // Labels in same order as original JSON response
    val id: String,

    val summary: String,

    val description: String,

    val descriptionHtml: String,

    val startDate: String,

    val endDate: String,

    val lastUpdated: String,

    @Contextual
    val regions: JsonArray,

    val affectedAssets: List<AffectedSerial>
)