package com.michaelrmossman.docoutdoors.ui.tracks

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.ui.components.DataIcon
import com.michaelrmossman.docoutdoors.ui.components.ItemAlerts
import com.michaelrmossman.docoutdoors.ui.components.TypeIcon
import com.michaelrmossman.docoutdoors.utils.ITEM_SEPARATOR

@Composable
fun TrackListItem(
    onAlertsClick: (String) -> Unit,
    showDownloadedIcon: Boolean,
    track: TrackKt,
    modifier: Modifier = Modifier
) {
    val regionLabel = when (track.regions.isBlank()) {
        true -> stringResource(R.string.region_unknown)
        else -> pluralStringResource(
            R.plurals.common_regions,
            count = track.regions.split(ITEM_SEPARATOR).size
        )
    }
    val regionText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = when (showDownloadedIcon) {
                    true -> FontWeight.Normal
                    else -> FontWeight.Bold
                }
            )
        ) {
            append(regionLabel)
        }
        append (" ")
        append(track.regions)
    }
    val textHorizontalPadding = when (showDownloadedIcon) {
        true -> dimensionResource(R.dimen.padding_small)
        else -> 0.dp
    }

    Row(
        modifier = Modifier.padding(
            horizontal = dimensionResource(R.dimen.padding_medium)
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TypeIcon(
            drawableId = R.drawable.baseline_hiking_black_24
        )
        Text(
            text = track.name,
            style = when (showDownloadedIcon) {
                true -> MaterialTheme.typography.titleSmall
                else -> MaterialTheme.typography.titleMedium
            },
            fontWeight = FontWeight.Bold,
            modifier = modifier.weight(1F)
        )
        if (showDownloadedIcon) {
            DataIcon(
                isDownloaded = track.introduction.isNotBlank()
            )
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = regionText,
            modifier = modifier
                .weight(1F)
                .padding(horizontal = textHorizontalPadding)
        )
        if (track.affectedCount > 0) {
            ItemAlerts(
                itemId = track.assetId,
                onAlertsClick = onAlertsClick,
                showClickableText = !showDownloadedIcon
            )
        }
    }
}