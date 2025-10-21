package com.michaelrmossman.docoutdoors.model

import com.google.android.gms.maps.model.LatLng

data class Affected(

    val affectedId: String, // the actual/original affected

    val affectId: String, // the "affectedAsset" id

    val assetId: String, // the "asset affected" id

    val name: String, // the "asset affected" name

    val type: String, // CAMPSITE | HUNTING AREA | HUT | LODGE | MOUNTAIN BIKE TRACK | PLACE | WALK TRACK

    val docUrl: String,

    val latLng: LatLng,

    val affectedCount: Int // Added in Repo
)