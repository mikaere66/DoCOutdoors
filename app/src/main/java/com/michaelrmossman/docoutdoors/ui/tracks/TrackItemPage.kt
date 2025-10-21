package com.michaelrmossman.docoutdoors.ui.tracks

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.interfaces.DownloadState
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.ui.components.DownloadIcon
import com.michaelrmossman.docoutdoors.ui.components.FavouriteIcon
import com.michaelrmossman.docoutdoors.ui.components.ItemImage

@Composable
fun TrackItemPage(
    downloadState: DownloadState,
    navigateToTracksMap: () -> Unit,
    onAlertsClick: (String) -> Unit,
    onDownloadClick: (String) -> Unit,
    onToggleFave: (TrackKt) -> Unit,
    paddingValues: PaddingValues,
    track: TrackKt,
    modifier: Modifier = Modifier
) {
    val cardCornerShape = dimensionResource(R.dimen.card_corner_shape)
    val cardElevation = dimensionResource(R.dimen.card_elevation)
    val columnVerticalPadding = dimensionResource(R.dimen.padding_small)
    val textHorizontalPadding = dimensionResource(R.dimen.padding_medium)
    val scrollState = rememberScrollState()

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
                when (track.introductionThumbnail.isNotBlank()) {
                    true -> ItemImage(
                        descrStringId = R.string.track_details_image,
                        imageUrl = track.introductionThumbnail
                    )
                    else -> DownloadIcon(
                        downloadState = downloadState,
                        itemId = track.assetId,
                        itemType = FilterType.Tracks,
                        onDownloadClick = onDownloadClick,
                        modifier = Modifier.padding(
                            horizontal = 0.dp
                        )
                    )
                }
            }

            FavouriteIcon(
                isFavourite = track.isFavourite,
                onToggleFave = { onToggleFave(track) }
            )

            TrackListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textHorizontalPadding),
                onAlertsClick = onAlertsClick,
                showDownloadedIcon = false,
                track = track
            )

            TrackDetails(
                track = track,
                navigateToTracksMap = navigateToTracksMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textHorizontalPadding)
            )
        }
    }
}