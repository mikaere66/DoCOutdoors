package com.michaelrmossman.docoutdoors.ui.tracks

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.model.AlertExtra
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.ui.components.AlertBottomSheet
import com.michaelrmossman.docoutdoors.ui.components.SingleTopAppBar
import com.michaelrmossman.docoutdoors.ui.favourites.FavesViewModel

private var alert = AlertExtra()

@Composable
fun TrackSingleScreen(
    itemId: String,
    navigateToSingleMap: (String) -> Unit,
    navigateUp: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val viewModel: FavesViewModel = viewModel(factory = FavesViewModel.Factory)
    viewModel.resetDownloadState()

    val trackKt  = viewModel.getTrackKt(id = itemId).observeAsState()
    var showAlertById by rememberSaveable { mutableStateOf(String()) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val onToggleFave = { track: TrackKt ->
        viewModel.toggleFavourite(
            assetId = track.assetId,
            isFavourite = track.isFavourite,
            itemType = AssetType.Track
        )
    }
    val onDownloadClick = { itemId: String ->
        viewModel.downloadTrackExtras(itemId)
    }
    val onDismissRequest = {
        showAlertById = String()
        showBottomSheet = false
        alert = AlertExtra()
    }
    val onAlertsClick = { assetId: String ->
        showAlertById = assetId
    }

    LaunchedEffect(key1 = showAlertById) {
        if (showAlertById.isNotBlank()) {
            alert = viewModel.getAlertById(
                id = showAlertById,
                itemType = AssetType.Track
            )
            showBottomSheet = true
        }
    }

    if (showBottomSheet) {
        AlertBottomSheet(
            alert = alert,
            onDismissRequest = onDismissRequest
        )
    }

    Scaffold(
        topBar = {
            SingleTopAppBar(
                navigateUp = { navigateUp() },
                titleId = R.string.tracks_subtitle
            )
        }
    ) { contentPadding ->

        trackKt.value?.let { track ->
            TrackItemPage(
                downloadState = viewModel.downloadState,
                navigateToTracksMap = {
                    navigateToSingleMap(track.assetId)
                },
                onAlertsClick = onAlertsClick,
                onDownloadClick = onDownloadClick,
                onToggleFave = onToggleFave,
                paddingValues = paddingValues,
                track = track,
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        bottom = contentPadding.calculateBottomPadding(),
                        top = contentPadding.calculateTopPadding()
                    )
            )
        }
    }
}