package com.michaelrmossman.docoutdoors.ui

import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.FilterNone
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.VpnKeyOff
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.ui.theme.DoCOutdoorsTheme
import com.michaelrmossman.docoutdoors.utils.DEBUG_SHOW_ADDITIONAL_MESSAGES
import com.michaelrmossman.docoutdoors.utils.TextUtils.getStringFromArray
import com.michaelrmossman.docoutdoors.utils.fromHtml

@Composable
fun EmptyFaves(
    modifier: Modifier = Modifier
) {
    val color = colorResource(R.color.empty_list)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = Icons.Outlined.HeartBroken,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            colorFilter = ColorFilter.tint(
                color, blendMode = BlendMode.SrcIn
            )
        )
        Text(
            text = stringResource(R.string.empty_favourites),
            modifier = Modifier.padding(
                dimensionResource(R.dimen.padding_mega)
            )
        )
    }
}

@Composable
fun EmptyList(
    itemType: String,
    region: String,
    modifier: Modifier = Modifier
) {
    val color = colorResource(R.color.empty_list)
    val message = stringResource(
        R.string.empty_list,
        itemType,
        region
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = Icons.Outlined.FilterNone,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            colorFilter = ColorFilter.tint(
                color, blendMode = BlendMode.SrcIn
            )
        )
        Text(
            text = message.fromHtml(),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                dimensionResource(R.dimen.padding_mega)
            )
        )
    }
}

@Composable
fun EmptyScreen(
    itemType: FilterType,
    modifier: Modifier = Modifier,
    listFilterRegion: String? = null,
    listFilterDogs: Int? = null,
    listFilterBookable: Int? = null,
    retryAction: (() -> Unit)? = null,
    retryEnabled: Boolean = true
) {
    if (DEBUG_SHOW_ADDITIONAL_MESSAGES) {
        listFilterBookable?.let { filterBookable ->
            android.util.Log.d("HEY_BK", filterBookable.toString())
        }
        listFilterDogs?.let { filterDogs ->
            android.util.Log.d("HEY_DG", filterDogs.toString())
        }
    }

    val color = colorResource(R.color.empty_list)
    val sb = StringBuilder()

    /* Main msg body */
    if (
        listFilterRegion != null && listFilterRegion.isNotBlank()
    ) {
        sb.append(
            stringResource(
                R.string.empty_filter_by_region,
                itemType.name,
                listFilterRegion
            )
        )

    } else if (
        listFilterBookable != null
        ||
        listFilterDogs != null
    ) {
        if (
            (listFilterBookable != null && listFilterBookable > 0)
            ||
            (listFilterDogs != null && listFilterDogs > 0)
        ) {
            sb.append(
                stringResource(
                    R.string.empty_filter_by_bookable_and_dogs,
                    itemType.name
                )
            )
        }
    }

    sb.append(" ")

    /* Descriptors */
    if (
        (listFilterBookable != null && listFilterBookable > 0)
        &&
        (listFilterDogs != null && listFilterDogs > 0)
    ) {
        sb.append(" ")
        sb.append(
            pluralStringResource(
                id = R.plurals.empty_filter_with,
                count = listFilterBookable
                    .plus(listFilterDogs)
            )
        )

    } else if ( // Strange, but true

        (listFilterBookable != null && listFilterBookable > 0)
        ||
        (listFilterDogs != null && listFilterDogs > 0)
    ) {
        sb.append(
            stringResource(
                R.string.empty_filter_with
            )
        )
    }

    sb.append(" ")

    listFilterBookable?.let { filterBookable ->
        if (filterBookable > 0) {
            sb.append(
                stringResource(
                    R.string.empty_filter_bookable
                )
            )
        }
    }

    /* Could be simplified, but done this way for readability */
    if (
        (listFilterBookable != null && listFilterBookable > 0)
        &&
        (listFilterDogs != null && listFilterDogs > 0)
    ) sb.append(
        // HTML portion contains spaces before/after
        stringResource(R.string.common_filtered_and)
    )

    listFilterDogs?.let { filterDogs ->
        if (filterDogs > 0) {
            sb.append(getStringFromArray(
                R.array.filter_dogs_by,
                filterDogs
            ))
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(100.dp),
            imageVector = when (retryAction) {
                null -> Icons.Outlined.FilterAltOff
                else -> Icons.Outlined.CloudDownload
            },
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color, blendMode = BlendMode.SrcIn
            )
        )
        Text(
            text = sb.toString().fromHtml(),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                dimensionResource(R.dimen.padding_mega)
            )
        )
        retryAction?.let { retry ->
            Button(
                enabled = retryEnabled,
                onClick = retry
            ) {
                Text(
                    stringResource(
                        when (retryEnabled) {
                            true -> R.string.loading_try_dload
                            else -> R.string.loading_dload_clicked
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_connection_error_100),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.loading_failed),
            modifier = Modifier.padding(
                dimensionResource(R.dimen.padding_large)
            )
        )
        Button(onClick = retryAction) {
            Text(stringResource(R.string.loading_retry))
        }
    }
}

@Composable
fun ForbiddenScreen(
    modifier: Modifier = Modifier
) {
    val color = colorResource(R.color.empty_list)
    val message = stringResource(
        R.string.forbidden_api_key
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = Icons.Outlined.VpnKeyOff,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            colorFilter = ColorFilter.tint(
                color, blendMode = BlendMode.SrcIn
            )
        )
        Text(
            text = message.fromHtml(),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                dimensionResource(R.dimen.padding_mega)
            )
        )
    }
}

@Composable
fun LoadingScreen(
    @StringRes stringId: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "Infinite Transition"
    )
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        label = "Transition Angle",
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = integerResource(
                    id = R.integer.loading_anim_duration
                ),
                easing = LinearEasing
            )
        )
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            contentDescription = stringResource(R.string.loading),
            painter = painterResource(R.drawable.loading_image),
            modifier = Modifier
                .size(
                    dimensionResource(R.dimen.loading_anim_size)
                )
                .graphicsLayer {
                    rotationZ = angle
                }
        )
        Text(
            text = stringResource(stringId),
            modifier = Modifier.padding(
                dimensionResource(R.dimen.padding_large)
            )
        )
    }
}

//@Composable
//fun NotFoundScreen(
//    itemType: AssetType,
//    modifier: Modifier = Modifier
//) {
//    val color = colorResource(R.color.empty_list)
//    val message = stringResource(
//        R.string.found_not_message,
//        itemType.name
//    )

//    Column(
//        modifier = modifier,
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Image(
//            imageVector = Icons.Outlined.WarningAmber,
//            contentDescription = null,
//            modifier = Modifier.size(100.dp),
//            colorFilter = ColorFilter.tint(
//                color, blendMode = BlendMode.SrcIn
//            )
//        )
//        Text(
//            text = message.fromHtml(),
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(
//                dimensionResource(R.dimen.padding_mega)
//            )
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun EmptyFavesPreview() {
    DoCOutdoorsTheme {
        EmptyFaves()
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyListPreview() {
    DoCOutdoorsTheme {
        EmptyList(
            itemType = FilterType.Tracks.name,
            region = "Hawke's Bay"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyScreenPreview() {
    DoCOutdoorsTheme {
        EmptyScreen(
            itemType = FilterType.Campsites,
            listFilterRegion = "Canterbury",
            listFilterDogs = 2,
            listFilterBookable = 1,
            retryAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    DoCOutdoorsTheme {
        ErrorScreen({})
    }
}

@Preview(showBackground = true)
@Composable
fun ForbiddenScreenPreview() {
    DoCOutdoorsTheme {
        ForbiddenScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    DoCOutdoorsTheme {
        LoadingScreen(
            R.string.common_downloading_preview
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun NotFoundScreenPreview() {
//    DoCOutdoorsTheme {
//        NotFoundScreen(
//            AssetType.Campsite
//        )
//    }
//}