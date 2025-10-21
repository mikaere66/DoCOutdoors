package com.michaelrmossman.docoutdoors.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/* Named as such 'coz it's used for single fave Campsite | Hut | or Track */
fun SingleTopAppBar(
    navigateUp: () -> Unit,
    @StringRes titleId: Int,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    R.string.app_title,
                    stringResource(titleId)
                ),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            BackButton(navigateUp = navigateUp)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer,
            titleContentColor =
                MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}