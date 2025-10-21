package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R

@Composable
fun FavouriteIcon(
    isFavourite: Boolean,
    onToggleFave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faveIcon = when (isFavourite) {
        true -> R.drawable.baseline_favorite_24
        else -> R.drawable.outline_favorite_border_24
    }
    val textHorizontalPadding = 12.dp
    val toggleText = stringResource(
        when (isFavourite) {
            true -> R.string.common_fave_rem
            else -> R.string.common_fave_add
        }
    )

    IconButton(
        onClick = { onToggleFave() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = textHorizontalPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(faveIcon),
                contentDescription = null
            )
            Text(
                text = toggleText,
                modifier = modifier
                    .padding(horizontal = textHorizontalPadding)
                    .weight(1F)
            )
        }
    }
}