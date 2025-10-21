package com.michaelrmossman.docoutdoors.ui.maps

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.michaelrmossman.docoutdoors.model.HutKt

data class HutClusterItem(
    val hut: HutKt,
    val region: String
) : ClusterItem {
    override fun getPosition() = LatLng(hut.lat,hut.lon)
    override fun getTitle()    = hut.name
    override fun getSnippet()  = region
    override fun getZIndex()   = 0F
}