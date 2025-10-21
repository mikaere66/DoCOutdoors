package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.michaelrmossman.docoutdoors.R

@Composable
fun LocationWithMapIcon(
    mapIconEnabled: Boolean,
    locationText: AnnotatedString,
    onMapIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconHorizontalPadding = dimensionResource(
        R.dimen.padding_none
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = locationText,
            modifier = modifier.weight(1F)
        )
        IconButton(
            enabled = mapIconEnabled,
            onClick = { onMapIconClick() },
            modifier = Modifier.padding(
                horizontal = iconHorizontalPadding
            )
        ) {
            Icon(
                Icons.Outlined.Place,
                contentDescription = stringResource(
                    R.string.menu_map_one
                )
            )
        }
    }
}