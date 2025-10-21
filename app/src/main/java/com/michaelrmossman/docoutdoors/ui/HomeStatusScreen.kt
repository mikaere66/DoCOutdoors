package com.michaelrmossman.docoutdoors.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.interfaces.HomeScreenState
import com.michaelrmossman.docoutdoors.ui.components.ButtonWithIcons
import com.michaelrmossman.docoutdoors.ui.components.OutdoorsTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeStatusScreen(
    favouriteCount: Int?,
    filterAlertsBy: Int?,
    filterCampsitesBookable: Int?,
    filterCampsitesBy: Int?,
    filterCampsitesDogAccess: Int?,
    filterHutsBookable: Int?,
    filterHutsBy: Int?,
    filterTracksBy: Int?,
    filterTracksDogAccess: Int?,
    headerQtyAlerts: String,
    headerQtyCampsites: String,
    headerQtyHuts: String,
    headerQtyTracks: String,
    onAlertsClicked: () -> Unit,
    onCampsitesClicked: () -> Unit,
    onFavesClicked: () -> Unit,
    onHelpClicked: () -> Unit,
    onHutsClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onTracksClicked: () -> Unit,
    uiState: HomeScreenState,
    modifier: Modifier = Modifier
) {
    val lrgButtonWidth = dimensionResource(R.dimen.button_width_large)
    val smlButtonWidth = dimensionResource(R.dimen.button_width_small)
    val scrollState = rememberScrollState()
    val spacerSize = dimensionResource(R.dimen.padding_medium)

    Scaffold(
        topBar = { OutdoorsTopAppBar() }
    ) { contentPadding ->

        when (uiState) {
            is HomeScreenState.Loading -> LoadingScreen(
                stringId = R.string.home_screen_loading_msg,
                modifier = Modifier.fillMaxSize()
            )
            is HomeScreenState.Ready -> Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = headerQtyAlerts,
                    modifier = modifier
                )
                Button(
                    onClick = onAlertsClicked,
                    Modifier.widthIn(min = lrgButtonWidth)
                ) {
                    Text(stringResource(R.string.alerts_button))
                }

                Spacer(modifier = Modifier.size(spacerSize))
                Text(
                    text = headerQtyCampsites,
                    modifier = modifier
                )
                ButtonWithIcons(
                    buttonText = stringResource(R.string.campsites_button),
                    buttonWidth = lrgButtonWidth,
                    filterBookable = filterCampsitesBookable,
                    filterDogAccess = filterCampsitesDogAccess,
                    onButtonClicked = onCampsitesClicked
                )

                Spacer(modifier = Modifier.size(spacerSize))
                Text(
                    text = headerQtyHuts,
                    modifier = modifier
                )
                ButtonWithIcons(
                    buttonText = stringResource(R.string.huts_button),
                    buttonWidth = lrgButtonWidth,
                    filterBookable = filterHutsBookable,
                    onButtonClicked = onHutsClicked
                )

                Spacer(modifier = Modifier.size(spacerSize))
                Text(
                    text = headerQtyTracks,
                    modifier = modifier
                )
                ButtonWithIcons(
                    buttonText = stringResource(R.string.tracks_button),
                    buttonWidth = lrgButtonWidth,
                    filterDogAccess = filterTracksDogAccess,
                    onButtonClicked = onTracksClicked
                )

                Spacer(modifier = Modifier.size(spacerSize))
                Text(
                    text = stringResource(
                        R.string.faves_title,
                        favouriteCount ?: 0
                    ),
                    modifier = modifier
                )
                Button(
                    enabled = favouriteCount != 0,
                    onClick = onFavesClicked,
                    modifier = Modifier.widthIn(min = lrgButtonWidth)
                ) {
                    Text(stringResource(R.string.faves_button))
                }

                Spacer(modifier = Modifier.size(spacerSize))
                Text(
                    text = stringResource(R.string.help_title),
                    modifier = modifier
                )
                Row(
                    modifier = Modifier.widthIn(max = lrgButtonWidth),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onHelpClicked,
                        Modifier.widthIn(min = smlButtonWidth)
                    ) {
                        Text(stringResource(R.string.help_button))
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    Button(
                        onClick = onSettingsClicked,
                        Modifier.widthIn(min = smlButtonWidth)
                    ) {
                        Text(stringResource(R.string.settings_button))
                    }
                }

                val filteredById = (
                    filterAlertsBy != 0
                    ||
                    filterCampsitesBy != 0
                    ||
                    filterHutsBy != 0
                    ||
                    filterTracksBy != 0
                )
                val color by animateColorAsState(
                    when (filteredById) {
                        true -> MaterialTheme.colorScheme.onSurface
                        else -> Color.Transparent
                    },
                    label = "Change color if list(s) filtered"
                )
                Text(
                    color = color,
                    text = stringResource(R.string.home_screen_filtered_msg),
                    modifier = Modifier
                        .alpha(alpha = 0.6F)
                        .padding(
                            top = dimensionResource(R.dimen.padding_medium),
                            /* Bottom padding to mirror top
                               padding in OutdoorsTopAppBar */
                            bottom = dimensionResource(
                                R.dimen.image_padding_logo
                            )
                        )
                )
            }
        }
    }
}