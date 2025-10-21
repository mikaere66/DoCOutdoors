package com.michaelrmossman.docoutdoors.model

data class HutKt(

    val assetId: String,
    val name: String,
    val status: String,
    val region: String?,
    val lat: Double,
    val lon: Double,
    val locationString: String,
    val numberOfBunks: Int?,
    val facilities: String, // Array
    val hutCategory: String,
    val proximityToRoadEnd: String,
    val bookable: Boolean?, // Prepackaged
    val introduction: String,
    val introductionThumbnail: String,
    val staticLink: String,
    val place: String,
    val regionCode: String,
    // Added directly within SQL query
    val affectedCount: Int,
    val isFavourite: Boolean
)