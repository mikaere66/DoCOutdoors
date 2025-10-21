package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString

@Composable
fun AffectedItemRow(
    contentDescription: String?,
    @DrawableRes drawableId: Int,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    onIconClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onIconClick?.let { onClick ->
                    onClick()
                }
            }
        ) {
            Icon(
                painterResource(id = drawableId),
                contentDescription = contentDescription
            )
        }
        Text(
            text = text,
            modifier = Modifier
                .clickable(
                    enabled = onIconClick != null
                ) {
                    onIconClick?.let { onClick ->
                        onClick()
                    }
                }
                .weight(1F)
        )
    }
}