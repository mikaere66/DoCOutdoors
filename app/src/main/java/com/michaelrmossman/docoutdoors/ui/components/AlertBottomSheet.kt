package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.AlertExtra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertBottomSheet(
    alert: AlertExtra,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val headerText = stringResource(
        R.string.alerts_affected_header,
        alert.affectedExtras.size
    )
    val iconLargePadding = dimensionResource(R.dimen.padding_great)
    val iconSize = dimensionResource(R.dimen.icon_size)
    val sheetState = rememberModalBottomSheetState()
    val textHorizontalPadding = dimensionResource(R.dimen.padding_medium)
    val rowVerticalPadding = dimensionResource(R.dimen.padding_small)
    val textVerticalPadding = dimensionResource(R.dimen.padding_mini)
    val verticalSpacing = dimensionResource(R.dimen.vertical_spacing)

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            item(key = headerText) {
                Row(
                    modifier = Modifier.padding(
                        vertical = rowVerticalPadding
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        headerText,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                horizontal = textHorizontalPadding
                            )
                            .weight(1F)
                    )
                    IconButton(
                        modifier = Modifier
                            .padding(horizontal = iconLargePadding)
                            .size(iconSize),
                        onClick = { onDismissRequest() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(
                                R.string.common_bottom_sheet_dismiss
                            )
                        )
                    }
                }
            }
            itemsIndexed(
                items = alert.affectedExtras,
                // key = { affected -> affected.id }
            ) { index, affected ->
                val dividerBottomPadding = dimensionResource(
                    R.dimen.padding_mini
                )
                val dividerHorizontalPadding = dimensionResource(
                    R.dimen.padding_medium
                )
                val dividerTopPadding = dimensionResource(
                    R.dimen.padding_small
                )
                val titleText = stringResource(
                    R.string.affected_extra_header,
                    index.plus(1),
                    alert.affectedExtras.size,
                    affected.heading
                )
                AffectedItem(
                    affected = affected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = textHorizontalPadding,
                            vertical = textVerticalPadding
                        ),
                    titleText = titleText
                )
                if (index < alert.affectedExtras.size.minus(1)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            bottom = dividerBottomPadding,
                            end = dividerHorizontalPadding,
                            start = dividerHorizontalPadding,
                            top = dividerTopPadding
                        )
                    )
                }
            }
        }
    }
}