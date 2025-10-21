package com.michaelrmossman.docoutdoors.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AffectedSerial(

    @Transient
    val affectedId: String = String(), // the alertId

    // Labels in same order as original JSON response
    val id: String?, // the unique "affectedAsset" id

    val assetId: String?, // the "asset affected" id(s)

    val name: String,     // the "asset affected" name

    val type: String, // CAMPSITE | HUNTING AREA | HUT | LODGE | MOUNTAIN BIKE TRACK | PLACE | WALK TRACK

    val docUrl: String,

    val lon: Double?,

    val lat: Double?
)