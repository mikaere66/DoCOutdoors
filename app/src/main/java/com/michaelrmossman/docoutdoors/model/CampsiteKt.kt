package com.michaelrmossman.docoutdoors.model

data class CampsiteKt(

    val assetId: String,
    val name: String,
    val status: String,
    val region: String?,
    val lat: Double,
    val lon: Double,
    val locationString: String,
    val introduction: String,
    val introductionThumbnail: String,
    val landscape: String, // Array
    val campsiteCategory: String,
    val access: String, // Array
    val facilities: String, // Array
    val activities: String, // Array
    val dogsAllowed: String, // Prepackaged
    val numberOfPoweredSites: Int?,
    val numberOfUnpoweredSites: Int?,
    val bookable: Boolean?, // Prepackaged
    val staticLink: String,
    val place: String,
    val regionCode: String,
    // Added directly within SQL query
    val affectedCount: Int,
    val isFavourite: Boolean
)