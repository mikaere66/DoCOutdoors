package com.michaelrmossman.docoutdoors.model

import kotlinx.serialization.Serializable

@Serializable
data class CampsiteSerial(

    // Labels in same order as original JSON response
    val assetId: Int,

    val name: String,

    val status: String,

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

    val introduction: String?,

    val introductionThumbnail: String?,

    val landscape: List<String>?, // Array

    val campsiteCategory: String?,

    val access: List<String>?, // Array

    val facilities: List<String>?, // Array

    val activities: List<String>?, // Array

    val dogsAllowed: String?,

    val numberOfPoweredSites: Int? = null,

    val numberOfUnpoweredSites: Int? = null,

    val bookable: Boolean? = null,

    val staticLink: String?,

    // (region already above)

    val place: String?

    // (status already above)
    // (lat already above)
    // (lon already above)
)