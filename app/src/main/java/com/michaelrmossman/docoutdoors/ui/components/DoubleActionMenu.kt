package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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

@Composable
fun DoubleActionMenu(
    onRefreshAlertsClick: () -> Unit,
    onRefreshAllClick   : () -> Unit,
    /* isEnabled affects BOTH menu items simultaneously */
    isEnabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            Icons.Filled.MoreVert,
            contentDescription = stringResource(
                R.string.menu_toggle_desc
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
                        R.string.menu_refresh_all
                    )
                )
            },
            onClick = {
                expanded = false
                onRefreshAllClick()
            },
            enabled = isEnabled
        )
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(
                        R.string.menu_refresh_alerts
                    )
                )
            },
            onClick = {
                expanded = false
                onRefreshAlertsClick()
            },
            enabled = isEnabled
        )
    }
}