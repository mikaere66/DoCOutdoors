package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun AffectedItemsScreen(
    alert: Alert,
    alertIndex: Int,
    navigateToAffectedMap: (Int, Int) -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val headerText = stringResource(
        R.string.alerts_affected_header,
        alert.affectedAssets.size
    )
    val iconSize = dimensionResource(R.dimen.icon_size)
    val mappableAssets = getMappableAssets(alert.affectedAssets)
    val textHorizontalPadding = dimensionResource(R.dimen.padding_medium)
    val textVerticalPadding = dimensionResource(R.dimen.padding_small)
    val verticalSpacing = dimensionResource(R.dimen.vertical_spacing)

    Scaffold(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            .fillMaxSize()
            .statusBarsPadding()
    ) { contentPadding ->

        LazyColumn(
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_small),
                    vertical = dimensionResource(R.dimen.padding_medium)
                )
        ) {
            item(key = headerText) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        headerText,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(
                                horizontal = textHorizontalPadding,
                                vertical = textVerticalPadding
                            )
                            .weight(1F)
                    )
                    IconButton(
                        modifier = Modifier
                            .padding(
                                horizontal = textHorizontalPadding
                            )
                            .size(iconSize),
                        onClick = { onCloseClick() }
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
                    navigateToAffectedMap = navigateToAffectedMap
                )
            }
        }
    }
}