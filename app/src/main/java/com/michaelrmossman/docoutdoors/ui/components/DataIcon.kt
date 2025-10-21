package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.outlined.PictureInPicture
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@Composable
fun DataIcon(
    isDownloaded: Boolean,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.padding_mini)

    Icon(
        imageVector = when (isDownloaded) {
            true -> Icons.Filled.PictureInPicture
            else -> Icons.Outlined.PictureInPicture
        },
//        modifier = modifier.padding(horizontal = horizontalPadding),
        contentDescription = stringResource(R.string.common_downloaded_desc)
    )
}