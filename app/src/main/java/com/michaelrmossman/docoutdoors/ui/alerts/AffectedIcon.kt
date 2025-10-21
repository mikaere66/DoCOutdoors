package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@Composable
fun AffectedIcon(
    itemCount: Int,
    mapIconEnabled: Boolean,
    onMapIconClick: () -> Unit,
    onViewAffectedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textHorizontalPadding = dimensionResource(R.dimen.padding_medium)
    val viewText = stringResource(
        R.string.alerts_affected_button, itemCount
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = textHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onViewAffectedClick() },
            modifier = Modifier.weight(1F)
        ) {
            /* Group list icon and "affected assets"
               text together, with single onClick */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                    contentDescription = viewText
                )
                Text(
                    text = viewText,
                    modifier = Modifier
                        .weight(1F)
                        .padding(horizontal = textHorizontalPadding)
                )
            }
        }
        IconButton(
            enabled = mapIconEnabled,
            onClick = { onMapIconClick() }
        ) {
            Icon(
                Icons.Outlined.Map,
                contentDescription = stringResource(
                    R.string.menu_map_all
                )
            )
        }
    }
}