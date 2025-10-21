package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.Alert
import com.michaelrmossman.docoutdoors.ui.components.DataIcon
import com.michaelrmossman.docoutdoors.ui.components.TypeIcon
import com.michaelrmossman.docoutdoors.utils.TextUtils.getBoldLabelWithText
import com.michaelrmossman.docoutdoors.utils.getRegionText

@Composable
fun AlertListItem(
    alert: Alert,
    showDownloadedIcon: Boolean,
    modifier: Modifier = Modifier
) {
    val regionText = alert.getRegionText(showAll = showDownloadedIcon)

    Row(
        modifier = Modifier.padding(
            horizontal = dimensionResource(R.dimen.padding_small)
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TypeIcon(
            drawableId = R.drawable.baseline_crisis_alert_black_24,
            modifier = Modifier.padding(start = 4.dp)
        )
        Text(
            text = alert.summary,
            style = when (showDownloadedIcon) {
                true -> MaterialTheme.typography.titleSmall
                else -> MaterialTheme.typography.titleMedium
            },
            fontWeight = FontWeight.Bold,
            modifier = modifier.weight(1F)
        )
        if (showDownloadedIcon) {
            DataIcon(
                isDownloaded = alert.affectedAssets.isNotEmpty()
            )
        }
    }
    Text(
        text = getBoldLabelWithText(
            labelStringId = R.string.last_updated_text,
            plainText = alert.lastUpdated,
            boldLabel = !showDownloadedIcon
        ),
        modifier = modifier
    )
    Text(
        text = regionText,
        modifier = modifier
    )
}