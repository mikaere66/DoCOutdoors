package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.Alert
import com.michaelrmossman.docoutdoors.utils.fromHtml
import com.michaelrmossman.docoutdoors.utils.TextUtils.getBoldLabelWithText

@Composable
fun AlertDetails(
    alert: Alert,
    modifier: Modifier = Modifier
) {
    val undefinedText = stringResource(R.string.common_undefined)

    val descriptionText = stringResource(
        R.string.alert_details_description,
        alert.descriptionHtml
    )
    val descriptionHtml = descriptionText.fromHtml()
    val startDateText = getBoldLabelWithText(
        labelStringId = R.string.alert_details_start,
        plainText = alert.startDate
    )
    val endDateText = getBoldLabelWithText(
        labelStringId = R.string.alert_details_end,
        plainText = when (alert.endDate.isBlank()) {
            true -> undefinedText
            else -> alert.endDate
        }
    )

    Text(
        text = startDateText,
        modifier = modifier
    )
    Text(
        text = endDateText,
        modifier = modifier
    )
    Text(
        text = descriptionHtml,
        modifier = modifier
    )
}