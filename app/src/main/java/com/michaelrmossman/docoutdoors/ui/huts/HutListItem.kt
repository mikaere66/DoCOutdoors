package com.michaelrmossman.docoutdoors.ui.huts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.ui.components.DataIcon
import com.michaelrmossman.docoutdoors.ui.components.ItemAlerts
import com.michaelrmossman.docoutdoors.ui.components.TypeIcon
import com.michaelrmossman.docoutdoors.utils.TextUtils.getBoldLabelWithText

@Composable
fun HutListItem(
    hut: HutKt,
    onAlertsClick: (String) -> Unit,
    showDownloadedIcon: Boolean,
    modifier: Modifier = Modifier
) {
    val statusText = getBoldLabelWithText(
        labelStringId = R.string.common_status,
        plainText = hut.status,
        boldLabel = !showDownloadedIcon
    )
    val regionText = getBoldLabelWithText(
        labelStringId = R.string.common_region_1,
        plainText = when (hut.region.isNullOrBlank()) {
            true -> stringResource(R.string.region_unknown)
            else -> hut.region
        },
        boldLabel = !showDownloadedIcon
    )

    Row(
        modifier = Modifier.padding(
            horizontal = dimensionResource(R.dimen.padding_medium)
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TypeIcon(
            drawableId = R.drawable.outline_house_black_24
        )
        Text(
            text = hut.name,
            style = when (showDownloadedIcon) {
                true -> MaterialTheme.typography.titleSmall
                else -> MaterialTheme.typography.titleMedium
            },
            fontWeight = FontWeight.Bold,
            modifier = modifier.weight(1F)
        )
        if (showDownloadedIcon) {
            DataIcon(
                isDownloaded = hut.introduction.isNotBlank()
            )
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = statusText,
            modifier = modifier.weight(1F)
        )
        if (hut.affectedCount > 0) {
            ItemAlerts(
                itemId = hut.assetId,
                onAlertsClick = onAlertsClick,
                showClickableText = !showDownloadedIcon
            )
        }
    }
    Text(
        text = regionText,
        modifier = modifier
    )
}