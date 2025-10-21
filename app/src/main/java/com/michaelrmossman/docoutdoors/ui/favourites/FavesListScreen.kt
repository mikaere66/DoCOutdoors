package com.michaelrmossman.docoutdoors.ui.favourites

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.SortFavesBy
import com.michaelrmossman.docoutdoors.model.Favourite
import com.michaelrmossman.docoutdoors.ui.EmptyFaves
import com.michaelrmossman.docoutdoors.ui.campsites.CampsiteItemCard
import com.michaelrmossman.docoutdoors.ui.components.BackButton
import com.michaelrmossman.docoutdoors.ui.components.MapButton
import com.michaelrmossman.docoutdoors.ui.components.SingleActionMenu
import com.michaelrmossman.docoutdoors.ui.components.SortByActionMenu
import com.michaelrmossman.docoutdoors.ui.huts.HutItemCard
import com.michaelrmossman.docoutdoors.ui.tracks.TrackItemCard
import com.michaelrmossman.docoutdoors.utils.TextUtils.fontDimensionResource
import com.michaelrmossman.docoutdoors.utils.fromHtml

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavesListScreen(
    navigateToCampsiteSingle: (String) -> Unit,
    navigateToHutSingle     : (String) -> Unit,
    navigateToMultiMap      : (Int) -> Unit,
    navigateToSingleMap     : (String, AssetType) -> Unit,
    navigateToTrackSingle   : (String) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: FavesViewModel = viewModel(factory = FavesViewModel.Factory)
    val favesSortedBy = viewModel.favesSortedBy.observeAsState(initial = 0)
    val favourites by viewModel.favourites.observeAsState(initial = emptyList())
    var showRemoveAllDialog by remember { mutableStateOf(false) }
    val subtitleFontSize = fontDimensionResource(R.dimen.subtitle_font_size)

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(navigateUp = navigateUp) },
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            fontSize = subtitleFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = stringResource(
                                R.string.faves_title,
                                favourites.size
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor =
                        MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    MapButton(
                        isEnabled = favourites.any { fave ->
                            (fave.campsiteKt?.lat != 0.0
                            &&
                            fave.campsiteKt?.lon != 0.0)
                            ||
                            (fave.hutKt?.lat != 0.0
                            &&
                            fave.hutKt?.lon != 0.0)
                            ||
                            (fave.trackKt?.lat != 0.0
                            &&
                            fave.trackKt?.lon != 0.0)
                        },
                        navigateToMap = navigateToMultiMap
                    )
                    SortByActionMenu(
                        isEnabled = favourites.size > 1,
                        onSortByDateClick = { viewModel.setFavesSortedBy(
                            SortFavesBy.Date
                        )},
                        onSortByNameClick = { viewModel.setFavesSortedBy(
                            SortFavesBy.Name
                        )},
                        onSortByTypeClick = { viewModel.setFavesSortedBy(
                            SortFavesBy.Type
                        )},
                        sortedBy = SortFavesBy.entries[favesSortedBy.value]
                    )
                    SingleActionMenu(
                        onSingleItemClick = { showRemoveAllDialog = true },
                        isEnabled = favourites.isNotEmpty(),
                        itemStringId = R.string.menu_faves_delete_all
                    )
                },
                modifier = modifier
            )
        }
    ) { contentPadding ->

        when (favourites.isNotEmpty()) {
            true -> FavesList(
                contentPadding = contentPadding,
                favourites = favourites,
                modifier = modifier.padding(
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium)
                ),
                navigateToCampsiteSingle = navigateToCampsiteSingle,
                navigateToHutSingle      = navigateToHutSingle,
                navigateToSingleMap      = navigateToSingleMap,
                navigateToTrackSingle    = navigateToTrackSingle
            )
            else -> EmptyFaves(modifier = Modifier.fillMaxSize())
        }

        if (showRemoveAllDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the
                    // dialog or on the back button. If you want to disable
                    // that functionality, simply use an empty onCloseRequest
                    showRemoveAllDialog = false
                },
                title = {
                    Text(
                    text = stringResource(
                        R.string.menu_faves_delete_all
                    ).plus("?"))
                },
                text = {
                    Text(
                        text = stringResource(
                            R.string.faves_message
                        ).fromHtml(), // Note HTML
                        textAlign = TextAlign.Justify
                    )
                },
                dismissButton = {
                    TextButton (
                        onClick = { showRemoveAllDialog = false }
                    ) {
                        Text(
                            text = stringResource(
                                R.string.common_dialog_cancel
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showRemoveAllDialog = false
                            /* Quit on remove all faves */
                            navigateUp()
                            viewModel.deleteAllFavourites()
                        }
                    ) {
                        Text(
                            text = stringResource(
                                R.string.common_dialog_confirm
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun FavesList(
    contentPadding: PaddingValues,
    favourites: List<Favourite>,
    navigateToCampsiteSingle: (String) -> Unit,
    navigateToHutSingle     : (String) -> Unit,
    navigateToSingleMap     : (String, AssetType) -> Unit,
    navigateToTrackSingle   : (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val columnVerticalPadding = dimensionResource(R.dimen.list_padding)
    val listState = rememberLazyListState()

    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.list_spacing)
        ),
        modifier = modifier
            .fillMaxSize()
            .padding(
                vertical = columnVerticalPadding
            ),
        state = listState
    ) {
        items(
            items = favourites,
            key = { fave -> fave.id }
        ) { fave ->
            when (fave.itemType) {
                AssetType.Campsite -> fave.campsiteKt?.let { campsite ->
                    CampsiteItemCard(
                        campsite = campsite,
                        modifier = Modifier
                            .fillMaxSize()
                            .combinedClickable(
                                onClick = {
                                    navigateToCampsiteSingle(
                                        campsite.assetId
                                    )
                                },
                                onLongClick = {
                                    navigateToSingleMap(
                                        campsite.assetId,
                                        AssetType.Campsite
                                    )
                                }
                            ),
                        onAlertsClick = {
                            /* Repeat of onClick item */
                            navigateToCampsiteSingle(
                                campsite.assetId
                            )
                        }
                    )
                }
                AssetType.Hut -> fave.hutKt?.let { hut ->
                    HutItemCard(
                        hut = hut,
                        modifier = Modifier
                            .fillMaxSize()
                            .combinedClickable(
                                onClick = {
                                    navigateToHutSingle(
                                        hut.assetId
                                    )
                                },
                                onLongClick = {
                                    navigateToSingleMap(
                                        hut.assetId,
                                        AssetType.Hut
                                    )
                                }
                            ),
                        onAlertsClick = {
                            /* Repeat of onClick item */
                            navigateToHutSingle(
                                hut.assetId
                            )
                        }
                    )
                }
                AssetType.Track -> fave.trackKt?.let { track ->
                    TrackItemCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .combinedClickable(
                                onClick = {
                                    navigateToTrackSingle(
                                        track.assetId
                                    )
                                },
                                onLongClick = {
                                    navigateToSingleMap(
                                        track.assetId,
                                        AssetType.Track
                                    )
                                }
                            ),
                        onAlertsClick = {
                            /* Repeat of onClick item */
                            navigateToTrackSingle(
                                track.assetId
                            )
                        },
                        track = track
                    )
                }
            }
        }
    }
}