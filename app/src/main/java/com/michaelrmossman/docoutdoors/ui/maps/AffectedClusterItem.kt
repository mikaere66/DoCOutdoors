package com.michaelrmossman.docoutdoors.ui.maps

import com.google.maps.android.clustering.ClusterItem
import com.michaelrmossman.docoutdoors.model.Affected

data class AffectedClusterItem(
    val affected: Affected
) : ClusterItem {
    override fun getPosition() = affected.latLng
    override fun getTitle()    = affected.name
    /* Remember that the snippet is also
     * used to determine asset's type */
    override fun getSnippet()  = affected.type
    override fun getZIndex()   = 0F
}