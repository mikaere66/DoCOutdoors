package com.michaelrmossman.docoutdoors.ui.huts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.HutKt

@Composable
fun HutItemCard(
    hut: HutKt,
    onAlertsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val columnVerticalPadding = 8.dp
    val textHorizontalPadding = 8.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = modifier.padding(vertical = columnVerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            )
        ) {
            HutListItem(
                hut = hut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = textHorizontalPadding),
                onAlertsClick = onAlertsClick,
                showDownloadedIcon = true
            )
        }
    }
}