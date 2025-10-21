package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VerticalAlignTop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@Composable
fun BackToTop(
    backToTop: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val additionalPadding = dimensionResource(R.dimen.padding_medium)
    val horizontalPadding = dimensionResource(R.dimen.padding_large)

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = modifier
                .align(Alignment.BottomEnd)
                .padding(
                    horizontal = horizontalPadding,
                    vertical = contentPadding.calculateBottomPadding().plus(
                        additionalPadding
                    )
                ),
            onClick = backToTop
        ) {
            Icon(
                imageVector = Icons.Outlined.VerticalAlignTop,
                contentDescription = stringResource(R.string.back_to_top)
            )
        }
    }
}