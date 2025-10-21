package com.michaelrmossman.docoutdoors.ui.maps

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.michaelrmossman.docoutdoors.model.CampsiteKt

data class CampsiteClusterItem(
    val campsite: CampsiteKt,
    val region: String
) : ClusterItem {
    override fun getPosition() = LatLng(campsite.lat,campsite.lon)
    override fun getTitle()    = campsite.name
    override fun getSnippet()  = region
    override fun getZIndex()   = 0F
}