package com.michaelrmossman.docoutdoors.model

import com.michaelrmossman.docoutdoors.enums.AssetType

data class Favourite(

    val id: Int,

    val added: Long, // String,

    val assetId: String,

    val itemType: AssetType,

    val name: String = String(),

    val campsiteKt: CampsiteKt? = null,

    val hutKt: HutKt? = null,

    val trackKt: TrackKt? = null
)