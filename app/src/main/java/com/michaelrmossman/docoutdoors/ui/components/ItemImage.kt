package com.michaelrmossman.docoutdoors.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.michaelrmossman.docoutdoors.R

@Suppress("KotlinConstantConditions") /* isDownloading */
@Composable
fun ItemImage(
    @StringRes descrStringId: Int,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    val roundedCornerShape = dimensionResource(R.dimen.card_corner_shape)
    val imageMinHeight = dimensionResource(R.dimen.image_size_item)
    val imagePaddingHorizontal = dimensionResource(R.dimen.padding_small)
    val imagePaddingVertical = dimensionResource(R.dimen.padding_mini)
    var isDownloading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    when (
        isError
        &&
        !isDownloading
    ) {
        false -> AsyncImage(
            model = ImageRequest.Builder(
                context = LocalContext.current
            )
            .data(imageUrl)
            .crossfade(true)
            .listener(
                onError = { _, _ ->
                    isDownloading = false
                    isError = true
                },
                onSuccess = { _, _ ->
                    isDownloading = false
                    isError = false
                }
            )
            .build(),
            contentDescription = stringResource(
                descrStringId
            ),
            modifier = modifier
                .clip(
                    RoundedCornerShape(size = roundedCornerShape)
                )
                .fillMaxWidth()
                .heightIn(min = imageMinHeight)
                .padding(
                    horizontal = imagePaddingHorizontal,
                    vertical = imagePaddingVertical
                )
        )
        else -> RetryImageIcon(
            isDownloading = isDownloading,
            onDownloadClick = {
                isDownloading = true
            }
        )
    }
}