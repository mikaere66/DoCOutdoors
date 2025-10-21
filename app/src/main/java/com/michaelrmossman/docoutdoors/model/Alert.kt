package com.michaelrmossman.docoutdoors.model

import kotlinx.serialization.Serializable

@Serializable
data class Alert(

    // Labels in same order as original JSON response
    val id: String = String(),

    val summary: String = String(),

    val description: String = String(),

    val descriptionHtml: String = String(),

    val startDate: String = String(),

    val endDate: String = String(),

    val lastUpdated: String = String(),

    val regions: List<RegionSerial> = emptyList(),

    // Added for downloaded extras, on per Alert basis
    val affectedAssets: List<AffectedEntity> = emptyList(),

//    val isFavourite: Boolean = false, // Added: see repository

    val updateMillis: Long = 0L   // Added, for sort by latest
)