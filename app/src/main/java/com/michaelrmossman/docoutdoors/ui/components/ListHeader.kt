package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.utils.fromHtml

@Composable
fun ListHeader(
    listHeader: String,
    subHeaders: List<String>,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.padding_medium)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = horizontalPadding
            )
    ) {
        Text(
            text = when (subHeaders.isEmpty()) {
                true -> listHeader
                else -> listHeader.plus(
                    stringResource(R.string.common_filtered_ellipsis)
                )
            }
        )

        if (subHeaders.isNotEmpty()) {
            val sb = StringBuilder()

            subHeaders.forEachIndexed { index, subHeader ->

                sb.append(subHeader)

                if (subHeaders.size > 1) {

                    if (index in 0..subHeaders.size.minus(2)) {
                        sb.append(
                            /* HTML portion contains
                               spaces before/after */
                            stringResource(
                                R.string.common_filtered_and
                            )
                        )
                    }
                }
            }
            val subText = stringResource(
                R.string.common_filtered_by,
                sb.toString()
            )
            Text(text = subText.fromHtml())
        }
    }
}