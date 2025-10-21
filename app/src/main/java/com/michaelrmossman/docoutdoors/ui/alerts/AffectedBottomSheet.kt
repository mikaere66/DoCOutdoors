package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.michaelrmossman.docoutdoors.model.Alert
import com.michaelrmossman.docoutdoors.utils.MapUtils.getMappableAssets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffectedBottomSheet(
    alert: Alert,
    alertIndex: Int,
    navigateToAffectedMap: (Int, Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val columnHorizontalPadding = dimensionResource(R.dimen.padding_small)
    val headerText = stringResource(
        R.string.alerts_affected_header,
        alert.affectedAssets.size
    )
    val iconLargePadding = dimensionResource(R.dimen.padding_great)
    val iconSize = dimensionResource(R.dimen.icon_size)
    val mappableAssets = getMappableAssets(alert.affectedAssets)
    val sheetState = rememberModalBottomSheetState()
    val textHorizontalPadding = dimensionResource(R.dimen.padding_medium)
    val rowVerticalPadding = dimensionResource(R.dimen.padding_small)
    val verticalSpacing = dimensionResource(R.dimen.vertical_spacing)

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            modifier = modifier
                .padding(
                    horizontal = columnHorizontalPadding
                )
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            item(key = headerText) {
                Row(
                    modifier = Modifier.padding(
                        start = textHorizontalPadding,
                        top = rowVerticalPadding
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
            items(
                items = alert.affectedAssets,
                key = { affected -> affected.affectId }
            ) { affected ->

                AffectedItemCard(
                    affected = affected,
                    affectedIndex = mappableAssets.indexOf(affected),
                    alertIndex = alertIndex,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = textHorizontalPadding),
                    navigateToAffectedMap = navigateToAffectedMap
                )
            }
        }
    }
}