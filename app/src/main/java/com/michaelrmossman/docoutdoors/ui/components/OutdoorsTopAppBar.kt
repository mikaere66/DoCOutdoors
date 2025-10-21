package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutdoorsTopAppBar(
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.padding_small)
    val topPadding = dimensionResource(R.dimen.image_padding_logo)

    CenterAlignedTopAppBar(
        modifier = modifier.padding(
            /* Top padding to mirror bottom
               padding in HomeStatusScreen */
            top = topPadding
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_size_logo))
                        .padding(horizontal = horizontalPadding),
                    painter = painterResource(R.drawable.osm_nz_200),
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
    )
}