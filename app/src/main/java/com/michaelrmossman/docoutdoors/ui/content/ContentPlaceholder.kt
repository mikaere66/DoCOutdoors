package com.michaelrmossman.docoutdoors.ui.content

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ContentPlaceholder(
    @DrawableRes drawableId: Int,
    @StringRes stringId: Int,
    modifier: Modifier = Modifier
) {
    val additionalPadding = dimensionResource(R.dimen.padding_content_card)
    val cardCornerShape = dimensionResource(R.dimen.card_corner_shape)
    val cardElevation = dimensionResource(R.dimen.card_elevation)

    Scaffold(
        topBar = {
            QuadrupleTopAppBar(
                actions = {},
                navigateUp = {},
                titleId = 0
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) { contentPadding ->

        Card(
            modifier = modifier.padding(
                top = contentPadding.calculateTopPadding().plus(additionalPadding),
                end = contentPadding.calculateEndPadding(
                    LayoutDirection.Ltr
                ).plus(additionalPadding),
                bottom = contentPadding.calculateBottomPadding().plus(additionalPadding)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
            shape = RoundedCornerShape(size = cardCornerShape)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(drawableId),
                    modifier = Modifier.width(256.dp),
                    contentDescription = stringResource(
                        R.string.background_description
                    )
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    fontWeight = FontWeight.Bold,
                    text = stringResource(
                        R.string.details_placeholder,
                        stringResource(stringId)
                    )
                )
            }
        }
    }
}