package com.michaelrmossman.docoutdoors.model

import com.google.gson.JsonArray
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TrackSerial(

    // Labels in same order as original JSON response
    val assetId: String,

    val name: String,

    @Contextual
    /* @SerialName(value = "region") does NOT work! */
    val region: JsonArray,

    val lat: Double?,

    val lon: Double?,

    /* Extended info (requires a
       separate d/load per item).
       Initialise all strings as
       empty Strings rather than
       null to avoid any NPEs */

    // (assetId already above)
    // (name already above)

    val introduction: String?,

    val introductionThumbnail: String?,

    val permittedActivities: List<String>?, // Array

    val distance: String?,

    val walkDuration: String?,

    val walkDurationCategory: List<String>?, // Array

    val walkTrackCategory: List<String>?, // Array

    val wheelchairsAndBuggies: String?,

    val mtbDuration: String?,

    val mtbDurationCategory: List<String>?, // Array

    val mtbTrackCategory: List<String>?, // Array

    val kayakingDuration: String?,

    val dogsAllowed: String?,

    val locationString: String?,

    val locationArray: List<String>?, // Array

    val staticLink: String?,

    // (region already above)
    // (lat already above)
    // (lon already above)

    /* SerialName doesn't work. A list of lines,
       with each list containing another list of
       two coordinates: latitude & longitude ...
       note: lat/lon may not be in that order */
    val line: List<List<List<Double>>> = emptyList()
)