package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CrisisAlert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R

@Composable
fun ItemAlerts(
    itemId: String,
    onAlertsClick: (String) -> Unit,
    showClickableText: Boolean,
    modifier: Modifier = Modifier
) {
    val iconHorizontalPadding = dimensionResource(
        R.dimen.padding_mini
    )
    val textHorizontalPadding = dimensionResource(
        R.dimen.padding_small
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showClickableText) {
            Text(
                text = stringResource(R.string.affected_icon_text),
                modifier = modifier
                    .padding(
                        horizontal = textHorizontalPadding
                    )
                    .clickable { onAlertsClick(itemId) }
            )
        }
        IconButton(
            onClick = { onAlertsClick(itemId) },
//            modifier = modifier.padding(
//                horizontal = iconHorizontalPadding
//            )
        ) {
            Icon(
                imageVector = Icons.Outlined.CrisisAlert,
                contentDescription = stringResource(
                    R.string.affected_icon_descr
                )
            )
        }
    }
}