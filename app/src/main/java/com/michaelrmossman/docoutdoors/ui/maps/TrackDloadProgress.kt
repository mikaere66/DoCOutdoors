package com.michaelrmossman.docoutdoors.ui.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.michaelrmossman.docoutdoors.R

@Composable
fun TrackDloadProgress(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                dimensionResource(
                    R.dimen.padding_circular_progress
                )
            ),
        contentAlignment = Alignment.TopEnd
    ) {
        CircularProgressIndicator(
            color = color,
            modifier = modifier
        )
    }
}