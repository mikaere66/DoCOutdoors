package com.michaelrmossman.docoutdoors.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.GroupWork
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.SortFavesBy

@Composable
fun SortByActionMenu(
    isEnabled: Boolean,
    onSortByDateClick: () -> Unit,
    onSortByNameClick: () -> Unit,
    onSortByTypeClick: () -> Unit,
    sortedBy: SortFavesBy
) {
    var expanded by remember { mutableStateOf(false) }
    val imageVector = when (sortedBy) {
        SortFavesBy.Date -> Icons.Outlined.DateRange
        SortFavesBy.Name -> Icons.Outlined.SortByAlpha
        SortFavesBy.Type -> Icons.Outlined.GroupWork
    }

    IconButton(
        enabled = isEnabled,
        onClick = { expanded = true }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = stringResource(
                R.string.faves_sort_by_desc
            )
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(
                        R.string.faves_sort_by_date
                    )
                )
            },
            onClick = {
                expanded = false
                onSortByDateClick()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(
                        R.string.faves_sort_by_name
                    )
                )
            },
            onClick = {
                expanded = false
                onSortByNameClick()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(
                        R.string.faves_sort_by_type
                    )
                )
            },
            onClick = {
                expanded = false
                onSortByTypeClick()
            }
        )
    }
}