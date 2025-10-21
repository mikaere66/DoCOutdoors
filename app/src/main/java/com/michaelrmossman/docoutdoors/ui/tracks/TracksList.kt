package com.michaelrmossman.docoutdoors.ui.tracks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.ui.components.BackToTop
import com.michaelrmossman.docoutdoors.ui.components.ListHeader
import kotlinx.coroutines.launch

@Composable
fun TracksList(
    contentPadding: PaddingValues,
    listHeader: String,
    navigateToTrackDetails: (Int) -> Unit,
    navigateToTracksMap: (Int) -> Unit,
    subHeaders: List<String>,
    tracks: List<TrackKt>,
    modifier: Modifier = Modifier
) {
    val columnVerticalPadding = dimensionResource(R.dimen.list_padding)
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 6
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
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
            item(key = listHeader) {
                ListHeader(
                    listHeader = listHeader,
                    subHeaders = subHeaders
                )
            }
            items(
                items = tracks,
                key = { track -> track.assetId }
            ) { track ->
                TrackItemCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            onClick = {
                                navigateToTrackDetails(
                                    tracks.indexOf(track)
                                )
                            },
                            onLongClick = {
                                navigateToTracksMap(
                                    tracks.indexOf(track)
                                )
                            }
                        ),
                    onAlertsClick = {
                        /* Repeat of onClick item */
                        navigateToTrackDetails(
                            tracks.indexOf(track)
                        )
                    },
                    track = track
                )
            }
        }

        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            visible = showButton
        ) {
            BackToTop(
                backToTop = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                },
                contentPadding = contentPadding
            )
        }
    }
}