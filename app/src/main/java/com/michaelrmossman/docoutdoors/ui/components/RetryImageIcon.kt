package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@Composable
fun RetryImageIcon(
    isDownloading: Boolean,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val downloadText = stringResource(
        R.string.image_retry
    )
    val infiniteTransition = rememberInfiniteTransition(
        label = "Infinite Transition"
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = !isDownloading,
            onClick = onDownloadClick
        ) {
            Icon(
                contentDescription = null,
                imageVector = Icons.Outlined.Download,
                modifier = Modifier.graphicsLayer {
                    rotationZ = when (isDownloading) {
                        true -> rotation
                        else -> 0F
                    }
                },
                tint = Color.Red
            )
        }
        Text(
            text = downloadText,
            modifier = modifier.weight(1F)
        )
    }
}