package com.michaelrmossman.docoutdoors.ui.maps

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.utils.IconColor
import com.michaelrmossman.docoutdoors.utils.markerColors

@Composable
fun ClusterItemMarker(
    @DrawableRes drawableId: Int,
    status: String
) {
    val colors: IconColor = status.markerColors()

    Icon(
        painterResource(id = drawableId),
        tint = colors.iconColor,
        contentDescription = null,
        modifier = Modifier
            .size(32.dp)
            .padding(1.dp)
            .drawBehind {
                drawCircle(
                    color = colors.backgroundColor,
                    style = Fill
                )
                drawCircle(
                    color = colors.borderColor,
                    style = Stroke(width = 3F)
                )
            }
            .padding(4.dp)
    )
}