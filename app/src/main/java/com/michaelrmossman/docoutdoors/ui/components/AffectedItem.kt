package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.AffectedExtraEntity
import com.michaelrmossman.docoutdoors.utils.fromHtml
import com.michaelrmossman.docoutdoors.utils.TextUtils.getBoldLabelWithText

@Composable
fun AffectedItem(
    affected: AffectedExtraEntity,
    modifier: Modifier = Modifier,
    titleText: String
) {
    val detailsText = stringResource(
        R.string.affected_extra_detail,
        affected.detail
    )
    val detailsHtml = detailsText.fromHtml()
    val displayDateText = getBoldLabelWithText(
        labelStringId = R.string.affected_extra_date,
        plainText = affected.displayDate
    )

    Text(
        text = titleText.fromHtml(),
        modifier = modifier
    )
    Text(
        text = displayDateText,
        modifier = modifier
    )
    Text(
        text = detailsHtml,
        modifier = modifier
    )
}