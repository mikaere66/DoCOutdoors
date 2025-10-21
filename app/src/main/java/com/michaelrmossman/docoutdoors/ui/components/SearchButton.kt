package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@Composable
fun SearchButton(
    isSearchVisible: Boolean,
    onToggleSearch: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    IconButton(
        modifier = modifier,
        onClick = onToggleSearch,
        enabled = isEnabled
    ) {
        Icon(
            imageVector = when (isSearchVisible) {
                true -> Icons.Outlined.SearchOff
                else -> Icons.Outlined.Search
            },
            contentDescription = stringResource(
                when (isSearchVisible) {
                    true -> R.string.menu_search_on
                    else -> R.string.menu_search_off
                }
            )
        )
    }
}