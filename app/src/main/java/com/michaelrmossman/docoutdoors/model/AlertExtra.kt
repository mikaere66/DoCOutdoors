package com.michaelrmossman.docoutdoors.model

data class AlertExtra(

    // Labels in same order as original JSON response
    val id: Int = 0,

    val assetId: String = String(),

    val name: String = String(),

    val itemCount: Int = 0,

    val itemType: String = String(),

    val affectedExtras: List<AffectedExtraEntity> = emptyList()
)