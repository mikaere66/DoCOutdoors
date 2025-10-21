package com.michaelrmossman.docoutdoors.ui.maps

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.michaelrmossman.docoutdoors.model.TrackKt

data class TrackClusterItem(
    val track: TrackKt
) : ClusterItem {
    override fun getPosition() = LatLng(track.lat,track.lon)
    override fun getTitle()    = track.name
    override fun getSnippet()  = track.regions
    override fun getZIndex()   = 0F
}