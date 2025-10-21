package com.michaelrmossman.docoutdoors.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.utils.TextUtils.fontDimensionResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/* Named as such because it's used by Alerts | Campsites | Huts | and Tracks */
fun QuadrupleTopAppBar(
    actions: @Composable RowScope.() -> Unit,
    navigateUp: () -> Unit,
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    val subtitleFontSize = fontDimensionResource(R.dimen.subtitle_font_size)

    TopAppBar(
        title = {
            Column {
                /* Title not used if TopAppBar is @ centre of large screen */
                if (titleId != 0) {
                    Text(
                        text = stringResource(
                            R.string.app_title,
                            stringResource(titleId)
                        ),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                subtitle?.let { sub ->
                    Text(
                        fontSize = subtitleFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = sub
                    )
                }
            }
        },
        navigationIcon = {
            if (titleId != 0) {
                BackButton(navigateUp = navigateUp)
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer,
            titleContentColor =
                MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}