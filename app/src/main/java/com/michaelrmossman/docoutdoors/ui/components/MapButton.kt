package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@Composable
fun MapButton(
    isEnabled: Boolean,
    navigateToMap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        enabled = isEnabled,
        onClick = { navigateToMap(Int.MAX_VALUE) }, // All items
        modifier = modifier
    ) {
        Icon(
            Icons.Outlined.Map,
            contentDescription = stringResource(
                R.string.menu_map_all
            )
        )
    }
}