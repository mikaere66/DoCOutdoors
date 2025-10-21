package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Downloading
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.interfaces.DownloadState

@Composable
fun DownloadIcon(
    downloadState: DownloadState,
//    downloadText: String,
//    imageVector: ImageVector,
//    isDownloading: Boolean,
    itemId: String,
    itemType: FilterType,
    onDownloadClick: (String) -> Unit,
//    tint: Color,
    modifier: Modifier = Modifier,
//    isEnabled: Boolean = true
) {
    val downloadText = stringResource(when (downloadState) {
        is DownloadState.Done     -> R.string.dload_done_placeholder
        is DownloadState.Error    -> R.string.dload_retry
        is DownloadState.Loading  -> when (itemType) {
            FilterType.Alerts     -> R.string.dload_alert
            FilterType.Campsites  -> R.string.dload_campsite
            FilterType.Huts       -> R.string.dload_hut
            FilterType.Tracks     -> R.string.dload_track
        }
        is DownloadState.None     -> when (itemType) {
            FilterType.Alerts     -> R.string.alerts_dload_text
            FilterType.Campsites  -> R.string.campsites_dload_text
            FilterType.Huts       -> R.string.huts_dload_text
            FilterType.Tracks     -> R.string.tracks_dload_text
        }
        is DownloadState.NotFound -> when (itemType) {
            FilterType.Alerts     -> R.string.alerts_dload_not_found
            FilterType.Campsites  -> R.string.campsites_dload_not_found
            FilterType.Huts       -> R.string.huts_dload_not_found
            FilterType.Tracks     -> R.string.tracks_dload_not_found
        }
    })
    val imageVector = when (downloadState) {
        is DownloadState.Done     -> Icons.AutoMirrored.Outlined.ListAlt
        is DownloadState.Error    -> Icons.Outlined.FileDownloadOff
        is DownloadState.Loading  -> Icons.Outlined.Downloading
        is DownloadState.None     -> Icons.Outlined.Download
        is DownloadState.NotFound -> Icons.Outlined.FileDownloadOff
    }
    val infiniteTransition = rememberInfiniteTransition(
        label = "Infinite Transition"
    )
    val isDownloading = downloadState is DownloadState.Loading
    val isEnabled = (
        downloadState !is DownloadState.Loading
        && // Note not in both cases
        downloadState !is DownloadState.NotFound
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        label = "Transition Angle",
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = LinearEasing
            )
        )
    )
    val tint = when (downloadState) {
        /* When done, hide icon ASAP, while image loads */
        is DownloadState.Done -> Color.Transparent
        is DownloadState.Error -> Color.Yellow
        is DownloadState.NotFound -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = isEnabled,
            onClick = { onDownloadClick(itemId) }
        ) {
            Icon(
                contentDescription = stringResource(
                    R.string.common_details_download
                ),
                imageVector = imageVector,
                modifier = Modifier.graphicsLayer {
                    rotationZ = when (isDownloading) {
                        true -> rotation
                        else -> 0F
                    }
                },
                tint = tint
            )
        }
        Text(
            text = downloadText,
            modifier = modifier.weight(1F)
        )
    }
}