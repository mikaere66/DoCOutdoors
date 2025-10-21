package com.michaelrmossman.docoutdoors.ui.tracks

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.model.TrackEntity
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.ui.components.LocationWithMapIcon
import com.michaelrmossman.docoutdoors.ui.theme.DoCOutdoorsTheme
import com.michaelrmossman.docoutdoors.utils.MapUtils.isValidCoords
import com.michaelrmossman.docoutdoors.utils.TextUtils.getBoldLabelWithText
import com.michaelrmossman.docoutdoors.utils.fromHtml
import com.michaelrmossman.docoutdoors.utils.toHtml

@Composable
fun TrackDetails(
    track: TrackKt,
    navigateToTracksMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    /* Although json does not have HTML content
       for tracks, this may change. Plus, done
       this way for consistency with campsites */
    val dogsText = stringResource(
        R.string.common_details_dog_access,
        track.dogsAllowed
    )
    val dogsHtml = dogsText.fromHtml()

    val linkText = stringResource(
        R.string.track_details_doc_link,
        track.staticLink.toHtml()
    )
    val linkHtml = linkText.fromHtml()

    LocationWithMapIcon(
        mapIconEnabled = isValidCoords(
            track.lat,track.lon
        ),
        locationText = getBoldLabelWithText(
            R.string.common_details_locale,
            track.locationString
        ),
        onMapIconClick = navigateToTracksMap,
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.common_details_intro,
            track.introduction
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_activities,
            track.permittedActivities
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_distance,
            track.distance
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_duration_time,
            track.walkDuration
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_duration_cat,
            track.walkDurationCategory
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_track_cat,
            track.walkTrackCategory
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_wheeled,
            track.wheelchairsAndBuggies
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_mtb_duration_time,
            track.mtbDuration
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_mtb_duration_cat,
            track.mtbDurationCategory
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_mtb_track_cat,
            track.mtbTrackCategory
        ),
        modifier = modifier
    )
    Text(
        text = getBoldLabelWithText(
            R.string.track_details_duration_kayak,
            track.kayakingDuration
        ),
        modifier = modifier
    )
    Text(
        text = dogsHtml,
        modifier = modifier
    )
    Text(
        text = linkHtml,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun TrackDetailsPreview() {
    DoCOutdoorsTheme {
        Column {
            TrackDetails(
                track = TrackEntity.empty().toTrackKt(
                    affectedCount = 0,
                    isFavourite = false
                ),
                navigateToTracksMap = {}
            )
        }
    }
}