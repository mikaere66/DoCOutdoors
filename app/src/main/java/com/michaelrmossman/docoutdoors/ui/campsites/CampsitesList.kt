package com.michaelrmossman.docoutdoors.ui.campsites

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
import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.ui.components.BackToTop
import com.michaelrmossman.docoutdoors.ui.components.ListHeader
import kotlinx.coroutines.launch

@Composable
fun CampsitesList(
    campsites: List<CampsiteKt>,
    contentPadding: PaddingValues,
    listHeader: String,
    navigateToCampsiteDetails: (Int) -> Unit,
    navigateToCampsitesMap: (Int) -> Unit,
    subHeaders: List<String>,
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
                items = campsites,
                key = { campsite -> campsite.assetId }
            ) { campsite ->
                CampsiteItemCard(
                    campsite = campsite,
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            onClick = {
                                navigateToCampsiteDetails(
                                    campsites.indexOf(campsite)
                                )
                            },
                            onLongClick = {
                                navigateToCampsitesMap(
                                    campsites.indexOf(campsite)
                                )
                            }
                        ),
                    onAlertsClick = {
                        /* Repeat of onClick item */
                        navigateToCampsiteDetails(
                            campsites.indexOf(campsite)
                        )
                    }
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