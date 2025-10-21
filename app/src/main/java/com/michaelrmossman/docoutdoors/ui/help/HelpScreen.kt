package com.michaelrmossman.docoutdoors.ui.help

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.ui.components.BackButton
import com.michaelrmossman.docoutdoors.utils.TextUtils.fontDimensionResource
import com.michaelrmossman.docoutdoors.utils.fromHtml

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawableIds = listOf(
        R.drawable.outline_info_black_24,
        R.drawable.outline_list_alt_black_24,
        R.drawable.outline_download_black_24,
        R.drawable.outline_map_black_24,
        R.drawable.outline_settings_black_24,
        R.drawable.outline_filter_alt_black_24,
        R.drawable.outline_manage_search_black_24,
        R.drawable.outline_zoom_in_map_black_24,
        R.drawable.outline_my_location_black_24
    )
    val iconPaddingBottom = dimensionResource(R.dimen.padding_medium)
    val iconPaddingTop = dimensionResource(R.dimen.padding_micro)
    val rowPadding = dimensionResource(R.dimen.padding_small)
    val scrollState = rememberScrollState()
    val stringIds = stringArrayResource(R.array.help_sections_string_ids)
    val subtitleFontSize = fontDimensionResource(R.dimen.subtitle_font_size)
    val textPadding = dimensionResource(R.dimen.padding_small)

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(navigateUp = navigateUp) },
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            fontSize = subtitleFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = stringResource(R.string.help_subtitle)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor =
                        MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = modifier
            )
        }
    ) { contentPadding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(scrollState)
        ) {
            if (drawableIds.size == stringIds.size) {

                drawableIds.forEachIndexed { index, drawableId ->

                    HtmlParagraph(
                        drawableId = drawableId,
                        string = stringIds[index],
                        iconModifier = Modifier.padding(
                            bottom = iconPaddingBottom,
                            top = iconPaddingTop
                        ),
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(rowPadding),
                        textModifier = Modifier
                            .padding(horizontal = textPadding)
                            .weight(1F)
                    )
                }
            }
        }
    }
}

@Composable
fun HtmlParagraph(
    @DrawableRes drawableId: Int,
    string: String,
    modifier: Modifier,
    iconModifier: Modifier,
    textModifier: Modifier
) {
    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            Icon(
                contentDescription = null,
                modifier = iconModifier,
                painter = painterResource(
                    id = drawableId
                )
            )
        }
        Text(
            modifier = textModifier,
            text = string.fromHtml(),
            textAlign = TextAlign.Justify
        )
    }
}