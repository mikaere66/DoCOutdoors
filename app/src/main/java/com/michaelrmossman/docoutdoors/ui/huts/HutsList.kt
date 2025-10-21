package com.michaelrmossman.docoutdoors.ui.huts

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
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.ui.components.BackToTop
import com.michaelrmossman.docoutdoors.ui.components.ListHeader
import kotlinx.coroutines.launch

@Composable
fun HutsList(
    contentPadding: PaddingValues,
    huts: List<HutKt>,
    listHeader: String,
    navigateToHutDetails: (Int) -> Unit,
    navigateToHutsMap: (Int) -> Unit,
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
                items = huts,
                key = { hut -> hut.assetId }
            ) { hut ->
                HutItemCard(
                    hut = hut,
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            onClick = {
                                navigateToHutDetails(
                                    huts.indexOf(hut)
                                )
                            },
                            onLongClick = {
                                navigateToHutsMap(
                                    huts.indexOf(hut)
                                )
                            }
                        ),
                    onAlertsClick = {
                        /* Repeat of onClick item */
                        navigateToHutDetails(
                            huts.indexOf(hut)
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