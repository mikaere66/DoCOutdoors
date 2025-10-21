package com.michaelrmossman.docoutdoors.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun TypeIcon(
    @DrawableRes drawableId: Int,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = drawableId),
        contentDescription = null,
        modifier = modifier
    )
}