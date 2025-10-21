package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.michaelrmossman.docoutdoors.R

@Composable
fun ButtonWithIcons(
    buttonText: String,
    buttonWidth: Dp,
    modifier: Modifier = Modifier,
    filterBookable: Int? = null,
    filterDogAccess: Int? = null,
    onButtonClicked: () -> Unit
) {
    /* Icons are present regardless, so that their alignment is always
       the same. Only their visibility changes, using transparency */

    Button(
        onClick = onButtonClicked,
        modifier.widthIn(min = buttonWidth, max = buttonWidth)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val bookableIconTint = when (filterBookable) {
                null -> Color.Transparent
                0 -> Color.Transparent
                else -> MaterialTheme.colorScheme.onSecondary
            }
            Icon(
                Icons.Filled.Book,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        R.dimen.padding_mini
                    )
                ),
                contentDescription = stringResource(
                    R.string.common_filtered_bookable
                ),
                tint = bookableIconTint
            )

            Spacer(modifier = Modifier.weight(1F))
            Text(text = buttonText)
            Spacer(modifier = Modifier.weight(1F))

            val dogAccessIconTint = when (filterDogAccess) {
                null -> Color.Transparent
                0 -> Color.Transparent
                else -> MaterialTheme.colorScheme.onSecondary
            }
            Icon(
                Icons.Outlined.Pets,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        R.dimen.padding_mini
                    )
                ),
                contentDescription = stringResource(
                    R.string.common_filtered_dog_access
                ),
                tint = dogAccessIconTint
            )
        }
    }
}