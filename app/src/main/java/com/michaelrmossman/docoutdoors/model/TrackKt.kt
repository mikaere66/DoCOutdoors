package com.michaelrmossman.docoutdoors.model

data class TrackKt(

    // Labels in same order as original JSON response
    val assetId: String,
    val name: String,
    val regions: String,
    val lat: Double,
    val lon: Double,
    val introduction: String = String(),
    val introductionThumbnail: String = String(),
    val permittedActivities: String = String(), // Array
    val distance: String = String(),
    val walkDuration: String = String(),
    val walkDurationCategory: String = String(), // Array
    val walkTrackCategory: String = String(), // Array
    val wheelchairsAndBuggies: String = String(),
    val mtbDuration: String = String(),
    val mtbDurationCategory: String = String(), // Array
    val mtbTrackCategory: String = String(), // Array
    val kayakingDuration: String = String(),
    val dogsAllowed: String = String(), // Prepackaged
    val locationString: String = String(),
    val locationArray: String = String(), // Array
    val staticLink: String = String(),
    val lineCount: Int = 0,
    val regionCodes: String, // Array
    // Added directly within SQL query
    val affectedCount: Int,
    val isFavourite: Boolean
)