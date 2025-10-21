package com.michaelrmossman.docoutdoors.ui.huts

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.model.HutEntity
import com.michaelrmossman.docoutdoors.ui.components.LocationWithMapIcon
import com.michaelrmossman.docoutdoors.ui.theme.DoCOutdoorsTheme
import com.michaelrmossman.docoutdoors.utils.MapUtils.isValidCoords
import com.michaelrmossman.docoutdoors.utils.TextUtils.getBoldLabelWithText
import com.michaelrmossman.docoutdoors.utils.fromHtml
import com.michaelrmossman.docoutdoors.utils.toHtml

@Composable
fun HutDetails(
    hut: HutKt,
    navigateToHutsMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val undefinedText = stringResource(R.string.common_undefined)

    val bookableText = getBoldLabelWithText(
        R.string.common_details_bookable,
        when (val bookable = hut.bookable) {
            null -> undefinedText
            else -> bookable.toString()
        }
    )
    val bunksText = getBoldLabelWithText(
        R.string.hut_details_num_bunks,
        when (val bunks = hut.numberOfBunks) {
            null -> undefinedText
            else -> bunks.toString()
        }
    )
    val linkText = stringResource(
        R.string.hut_details_doc_link,
        hut.staticLink.toHtml()
    )
    val linkHtml = linkText.fromHtml()
    val proximity = hut.proximityToRoadEnd
    val proximityText = getBoldLabelWithText(
        R.string.hut_details_proximity,
        when (proximity.isBlank()) {
            true -> undefinedText
            else -> proximity
        }
    )

    Text(
        text = getBoldLabelWithText(
            R.string.common_details_place,
            hut.place
        ),
        modifier = modifier
    )
    LocationWithMapIcon(
        mapIconEnabled = isValidCoords(
            hut.lat,hut.lon
        ),
        locationText = getBoldLabelWithText(
            R.string.common_details_locale,
            hut.locationString
        ),
        onMapIconClick = navigateToHutsMap,
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.common_details_intro,
            hut.introduction
        ),
        modifier = modifier
    )

    Text(
        text = bunksText,
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.common_details_facilities,
            hut.facilities
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.common_details_category,
            hut.hutCategory
        ),
        modifier = modifier
    )
    Text(
        text = proximityText,
        modifier = modifier
    )
    Text(
        text = bookableText,
        modifier = modifier
    )
    Text(
        text = linkHtml,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HutDetailsPreview() {
    DoCOutdoorsTheme {
        Column {
            HutDetails(
                HutEntity.empty().toHutKt(
                    affectedCount = 0,
                    isFavourite = false
                ),
                navigateToHutsMap = {}
            )
        }
    }
}