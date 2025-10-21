package com.michaelrmossman.docoutdoors.model

import kotlinx.serialization.Serializable

@Serializable
data class HutSerial(

    // Labels in same order as original JSON response
    val assetId: Int,

    val name: String,

    val status: String?,

    val region: String?,

    val lat: Double?,

    val lon: Double?,

    /* Extended info (requires a
       separate d/load per item).
       Initialise all strings as
       empty Strings rather than
       null to avoid any NPEs */

    // (assetId already above)
    // (name already above)

    val locationString: String?,

    val numberOfBunks: Int? = null,

    val facilities: List<String>?, // Array

    val hutCategory: String?,

    val proximityToRoadEnd: String?,

    val bookable: Boolean?,

    val introduction: String?,

    val introductionThumbnail: String?,

    val staticLink: String?,

    // (region already above)

    val place: String?

    // (status already above)
    // (lon already above)
    // (lat already above)
)