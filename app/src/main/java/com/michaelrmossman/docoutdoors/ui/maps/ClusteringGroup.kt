package com.michaelrmossman.docoutdoors.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.UiComposable
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun ClusteringGroup(
    items: Collection<ClusterItem>,
    clusterItemContent: @[UiComposable Composable] (ClusterItem) -> Unit,
    onClusterClick: (Cluster<out ClusterItem>) -> Boolean = { false },
    onClusterItemClick: (ClusterItem) -> Boolean = { false }
) {
    Clustering(
        items = items,
        onClusterClick = { cluster ->
            onClusterClick(cluster)
        },
        onClusterItemClick = { clusterItem ->
            onClusterItemClick(clusterItem)
            false
        },
        onClusterItemInfoWindowClick = {},
        clusterContent = null,
        clusterItemContent = { clusterItems ->
            clusterItemContent(clusterItems)
        }
    )
}