package com.michaelrmossman.docoutdoors.ui.campsites

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.michaelrmossman.docoutdoors.model.CampsiteEntity
import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.ui.components.LocationWithMapIcon
import com.michaelrmossman.docoutdoors.ui.theme.DoCOutdoorsTheme
import com.michaelrmossman.docoutdoors.utils.MapUtils.isValidCoords
import com.michaelrmossman.docoutdoors.utils.TextUtils.getBoldLabelWithText
import com.michaelrmossman.docoutdoors.utils.fromHtml
import com.michaelrmossman.docoutdoors.utils.toHtml

@Composable
fun CampsiteDetails(
    campsite: CampsiteKt,
    navigateToCampsitesMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val undefinedText = stringResource(R.string.common_undefined)

    val bookableText = getBoldLabelWithText(
        R.string.common_details_bookable,
        when (val bookable = campsite.bookable) {
            null -> undefinedText
            else -> bookable.toString()
        }
    )
    val dogsText = stringResource(
        R.string.common_details_dog_access,
        campsite.dogsAllowed
    )
    val dogsHtml = dogsText.fromHtml()
    val linkText = stringResource(
        R.string.campsite_details_doc_link,
        campsite.staticLink.toHtml()
    )
    val linkHtml = linkText.fromHtml()
    val poweredText = getBoldLabelWithText(
        R.string.campsite_details_powered_sites,
        when (val powered = campsite.numberOfPoweredSites) {
            null -> undefinedText
            else -> powered.toString()
        }
    )
    val unpoweredText = getBoldLabelWithText(
        R.string.campsite_details_unpowered_sites,
        when (val unpowered = campsite.numberOfUnpoweredSites) {
            null -> undefinedText
            else -> unpowered.toString()
        }
    )

    Text(
        text = getBoldLabelWithText(
            R.string.common_details_place,
            campsite.place
        ),
        modifier = modifier
    )
    LocationWithMapIcon(
        mapIconEnabled = isValidCoords(
            campsite.lat,campsite.lon
        ),
        locationText = getBoldLabelWithText(
            R.string.common_details_locale,
            campsite.locationString
        ),
        onMapIconClick = navigateToCampsitesMap,
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.common_details_intro,
            campsite.introduction
        ),
        modifier = modifier
    )

    Text(
        text = getBoldLabelWithText(
            R.string.campsite_details_landscape,
            campsite.landscape
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.common_details_category,
            campsite.campsiteCategory
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.campsite_details_access,
            campsite.access
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.common_details_facilities,
            campsite.facilities
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.campsite_details_activities,
            campsite.activities
        ),
        modifier = modifier
    )
    Text(
        text = dogsHtml,
        modifier = modifier
    )
    Text(
        text = poweredText,
        modifier = modifier
    )
    Text(
        text = unpoweredText,
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
fun CampsiteDetailsPreview() {
    DoCOutdoorsTheme {
        Column {
            CampsiteDetails(
                CampsiteEntity.empty().toCampsiteKt(
                    affectedCount = 0,
                    isFavourite = false
                ),
                navigateToCampsitesMap = {}
            )
        }
    }
}