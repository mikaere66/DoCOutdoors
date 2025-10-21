package com.michaelrmossman.docoutdoors.ui.huts

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
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.ui.components.DownloadIcon
import com.michaelrmossman.docoutdoors.ui.components.FavouriteIcon
import com.michaelrmossman.docoutdoors.ui.components.ItemImage

@Composable
fun HutItemPage(
    downloadState: DownloadState,
    hut: HutKt,
    navigateToHutsMap: () -> Unit,
    onAlertsClick: (String) -> Unit,
    onDownloadClick: (String) -> Unit,
    onToggleFave: (HutKt) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val cardCornerShape = dimensionResource(R.dimen.card_corner_shape)
    val cardElevation = dimensionResource(R.dimen.card_elevation)
    val columnVerticalPadding = dimensionResource(R.dimen.padding_small)
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
                when (hut.introductionThumbnail.isNotBlank()) {
                    true -> ItemImage(
                        descrStringId = R.string.hut_details_image,
                        imageUrl = hut.introductionThumbnail
                    )
                    else -> DownloadIcon(
                        downloadState = downloadState,
                        itemId = hut.assetId,
                        itemType = FilterType.Huts,
                        onDownloadClick = onDownloadClick,
                        modifier = Modifier.padding(
                            horizontal = 0.dp
                        )
                    )
                }
            }

            FavouriteIcon(
                isFavourite = hut.isFavourite,
                onToggleFave = { onToggleFave(hut) }
            )

            HutListItem(
                hut = hut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textHorizontalPadding),
                onAlertsClick = onAlertsClick,
                showDownloadedIcon = false
            )

            HutDetails(
                hut = hut,
                navigateToHutsMap = navigateToHutsMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textHorizontalPadding)
            )
        }
    }
}