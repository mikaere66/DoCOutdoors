package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.interfaces.DownloadState
import com.michaelrmossman.docoutdoors.model.Alert
import com.michaelrmossman.docoutdoors.ui.components.DownloadIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertItemPage(
    alert: Alert,
    alertIndex: Int,
    downloadState: DownloadState,
    onAffectedClick: (Alert, Int) -> Unit,
    onDownloadClick: (String) -> Unit,
    navigateToAffectedMap: (Int, Int) -> Unit,
    // onToggleFave: (Alert) -> Unit,
    paddingValues: PaddingValues,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val cardCornerShape = dimensionResource(R.dimen.card_corner_shape)
    val cardElevation = dimensionResource(R.dimen.card_elevation)
    val columnVerticalPadding = dimensionResource(R.dimen.padding_small)
    val downloadReqd = rememberSaveable { mutableStateOf(false) }
    val showBottomSheet = rememberSaveable { mutableStateOf(false) }
    val onDismissRequest = { showBottomSheet.value = false }
    val onViewAffectedClick = {
        when (windowSize == WindowWidthSizeClass.Compact) {
            true -> showBottomSheet.value = true
            else -> onAffectedClick(alert, alertIndex)
        }
    }
    val scrollState = rememberScrollState()
    val textHorizontalPadding = dimensionResource(R.dimen.padding_medium)

    Card(
        modifier = modifier.padding(paddingValues),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        shape = RoundedCornerShape(size = cardCornerShape)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = columnVerticalPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            )
        ) {
            Box(
                modifier = Modifier.heightIn(
                    min = dimensionResource(R.dimen.min_download_height)
                )
            ) {
                when (alert.affectedAssets.isNotEmpty()) {
                    true -> {
                        /* Immediately show the bottomSheet
                           on download of affected items */
                        if (downloadReqd.value) {
                            downloadReqd.value = false
                            when (windowSize == WindowWidthSizeClass.Compact) {
                                true -> showBottomSheet.value = true
                                else -> onAffectedClick(alert, alertIndex)
                            }
                        }
                        AffectedIcon(
                            itemCount = alert.affectedAssets.size,
                            mapIconEnabled = alert.affectedAssets.any { affected ->
                                affected.lat < 0.0 && affected.lon > 0.0
                            },
                            onMapIconClick = {
                                navigateToAffectedMap(
                                    Int.MAX_VALUE, /* all affected */
                                    alertIndex
                                )
                            },
                            onViewAffectedClick = onViewAffectedClick
                        )
                    }
                    else -> DownloadIcon(
                        downloadState = downloadState,
                        itemId = alert.id,
                        itemType = FilterType.Alerts,
                        onDownloadClick = onDownloadClick.also {
                            /* See note above re affected */
                            downloadReqd.value = true
                        },
                        modifier = Modifier.padding(
                            horizontal = 0.dp
                        )
                    )
                }
            }

            /* Not yet implemented
            FavouriteIcon(
                isFavourite = alert.isFavourite,
                onToggleFave = { onToggleFave(alert) }
            )
            */

            AlertListItem(
                alert = alert,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textHorizontalPadding),
                showDownloadedIcon = false
            )

            AlertDetails(
                alert = alert,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textHorizontalPadding)
            )
        }
    }

    if (showBottomSheet.value) {
        AffectedBottomSheet(
            alert = alert,
            alertIndex = alertIndex,
            navigateToAffectedMap = navigateToAffectedMap,
            onDismissRequest = onDismissRequest
        )
    }
}