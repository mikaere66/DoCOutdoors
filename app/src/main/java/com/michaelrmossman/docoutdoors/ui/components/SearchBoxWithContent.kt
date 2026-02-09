package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.utils.TextUtils.getHighlightedSearchResult
import com.michaelrmossman.docoutdoors.utils.replaceMacrons
import java.util.Locale

@Composable
fun SearchBoxWithContent(
    advancedSearch: Boolean,
    content: @Composable () -> Unit,
    contentPadding: PaddingValues,
    enableFeatSearch: Boolean,
    hashMap: HashMap<String, String>,
    isSearchVisible: Boolean,
    onAdvSearchNotAvailClick: () -> Unit,
    onSearchByClick: (SearchBy) -> Unit,
    onSearchItemClick: (String) -> Unit,
    searchBy: SearchBy,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = isSearchVisible,
            enter = slideInVertically(
                // Enters by sliding down from an offset above the
                // screen. Lambda takes full height of the content
                initialOffsetY = { fullHeight -> 0.minus(fullHeight) }
            )
        ) {
            SearchBox(
                advancedSearch = advancedSearch,
                contentPadding = contentPadding,
                hashMap = hashMap,
                enableFeatSearch = enableFeatSearch,
                onAdvSearchNotAvailClick =
                    onAdvSearchNotAvailClick,
                onSearchByClick = onSearchByClick,
                onSearchItemClick = { itemId ->
                    onSearchItemClick(itemId)
                },
                searchBy = searchBy
            )
        }

        content()
    }
}

@Composable
fun SearchBox(
    advancedSearch: Boolean,
    contentPadding: PaddingValues,
    enableFeatSearch: Boolean,
    hashMap: HashMap<String, String>,
    onAdvSearchNotAvailClick: () -> Unit,
    onSearchByClick: (SearchBy) -> Unit,
    onSearchItemClick: (String) -> Unit,
    searchBy: SearchBy,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = dimensionResource(R.dimen.padding_medium)
    val iconSmallPadding = dimensionResource(R.dimen.padding_small)
    val iconSize = dimensionResource(R.dimen.icon_size)
    val sortedMap = hashMap.entries
        /* Sort alphabetically, allowing for special characters */
        .sortedBy  { entry -> entry.value.replaceMacrons() }
        .associate { entry -> entry.key to entry.value }
     /* Just "name" values, or "summary" in the case of Alerts" */
    val sortedValues = sortedMap.values.toList()
    val verticalPadding = dimensionResource(R.dimen.padding_small)
    val verticalSpacing = dimensionResource(R.dimen.vertical_spacing)

    OutlinedCard(
        modifier = modifier
            .padding(
                end = horizontalPadding,
                start = horizontalPadding,
                top = contentPadding.calculateTopPadding().plus(
                    verticalPadding
                )
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors()
    ) {
        var searchList  by remember { mutableStateOf(emptyList<String>()) }
        var searchQuery by remember { mutableStateOf(String()) }
        OutlinedTextField(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(),
            leadingIcon = {
                SearchActionMenu(
                    advancedSearch = advancedSearch,
                    enableFeatSearch = enableFeatSearch,
                    onAdvSearchNotAvailClick =
                            onAdvSearchNotAvailClick,
                    onSearchByClick = onSearchByClick
                )
            },
            trailingIcon = {
                IconButton(
                    modifier = Modifier
                        .padding(horizontal = iconSmallPadding)
                        .size(iconSize),
                    onClick = {
                        searchQuery = String()
                        searchList = emptyList()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Backspace,
                        contentDescription = stringResource(
                            R.string.common_search_clear
                        )
                    )
                }
            },
            maxLines = 1,
            singleLine = true,
            value = searchQuery,
            onValueChange = { value ->
                searchQuery = value
                searchList = when (searchQuery.isBlank()) {
                    true -> emptyList()
                    else -> sortedValues.filter { item ->
                        item.lowercase(Locale.getDefault()).contains(
                            searchQuery.lowercase(Locale.getDefault())
                        )
                    }
                }
            },
            placeholder = {
                val stringId = when (searchBy) {
                    SearchBy.Name -> R.string.common_search_by_name
                    SearchBy.Feat -> R.string.common_search_by_feat
                    SearchBy.Desc -> R.string.common_search_by_desc
                }
                Text(text = stringResource(stringId))
            }
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_small)
                )
        ) {
            itemsIndexed(
                items = searchList,
            ) { index, searchResult ->
                Text(
                    text = when (searchQuery.length) {
                        1 -> buildAnnotatedString {
                            append(searchResult)
                        }
                        else -> getHighlightedSearchResult(
                            searchQuery = searchQuery,
                            searchResult = searchResult
                        )
                    },
                    Modifier
                        .padding(
                            vertical = 10.dp,
                            horizontal = 15.dp
                        )
                        .fillMaxWidth()
                        .clickable {
                            searchQuery = String()
                            searchList = emptyList()
                            sortedMap.entries.find { entry ->
                                entry.value == searchResult
                            }?.key?.let { key ->
                                onSearchItemClick(key)
                            }
                        }
                )
                if (index < searchList.size.minus(1)) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    )

                } else {
                    Spacer(
                        modifier = Modifier.statusBarsPadding()
                    )
                }
            }
        }
    }
}