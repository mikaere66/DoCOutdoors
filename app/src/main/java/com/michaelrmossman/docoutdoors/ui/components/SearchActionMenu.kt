package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
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
import com.michaelrmossman.docoutdoors.enums.SearchBy

@Composable
fun SearchActionMenu(
    advancedSearch: Boolean,
    enableFeatSearch: Boolean,
    onAdvSearchNotAvailClick: () -> Unit,
    onSearchByClick: (SearchBy) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            when (advancedSearch) {
                true -> expanded = true
                else -> onAdvSearchNotAvailClick()
            }
        }
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.ManageSearch,
            contentDescription = stringResource(
                R.string.common_search_desc
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
                        R.string.common_search_by_name
                    )
                )
            },
            onClick = {
                expanded = false
                onSearchByClick(SearchBy.Name)
            }
        )
        /* Feat actually refers to Facilities
           Only applies to Campsites and Huts */
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(
                        R.string.common_search_by_feat
                    )
                )
            },
            onClick = {
                expanded = false
                onSearchByClick(SearchBy.Feat)
            },
            enabled = enableFeatSearch
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(
                        R.string.common_search_by_desc
                    )
                )
            },
            onClick = {
                expanded = false
                onSearchByClick(SearchBy.Desc)
            }
        )
    }
}