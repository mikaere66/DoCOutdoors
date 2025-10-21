package com.michaelrmossman.docoutdoors.ui.huts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.interfaces.HutsUiState
import com.michaelrmossman.docoutdoors.ui.EmptyScreen
import com.michaelrmossman.docoutdoors.ui.ErrorScreen
import com.michaelrmossman.docoutdoors.ui.ForbiddenScreen
import com.michaelrmossman.docoutdoors.ui.LoadingScreen
import com.michaelrmossman.docoutdoors.ui.components.DoubleActionMenu
import com.michaelrmossman.docoutdoors.ui.components.MapButton
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.components.SearchBoxWithContent
import com.michaelrmossman.docoutdoors.ui.components.SearchButton
import com.michaelrmossman.docoutdoors.utils.TextUtils.getListHeaderText
import com.michaelrmossman.docoutdoors.utils.TextUtils.getListSubHeaders
import com.michaelrmossman.docoutdoors.utils.showAdvSearchNotAvailToast

@Composable
fun HutsListScreen(
    isSearchVisible: Boolean,
    navigateToHutDetails: (Int) -> Unit,
    navigateToHutsMap: (Int) -> Unit,
    navigateUp: () -> Unit,
    onToggleSearch: () -> Unit,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val viewModel: HutsViewModel = viewModel(
        factory = HutsViewModel.Factory
    )
    val viewState by viewModel.hutsListState.collectAsState()
    val hutsAdvancedSearch by
        viewModel.hutsAdvancedSearch.observeAsState()

    val advancedSearch = hutsAdvancedSearch != 0
    val context = LocalContext.current
    val listFilterRegion by
        viewModel.hutsFilterByRegion.observeAsState()
    val listFilterBookable by
        viewModel.commonFilterByBookable.observeAsState()
    val onAdvSearchNotAvailClick = {
        context.showAdvSearchNotAvailToast()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val subHeaders = getListSubHeaders(
        listFilterBookable = listFilterBookable
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            QuadrupleTopAppBar(
                actions = {
                    SearchButton(
                        isEnabled = viewState.hutState is HutsUiState.Success,
                        isSearchVisible = isSearchVisible,
                        onToggleSearch = onToggleSearch
                    )
                    if (windowSize == WindowWidthSizeClass.Compact) {
                        MapButton(
                            isEnabled = (
                                viewState.containsValidCoords
                                &&
                                viewState.hutState is HutsUiState.Success
                            ),
                            navigateToMap = navigateToHutsMap
                        )
                    }
                    DoubleActionMenu(
                        isEnabled = (
                            viewState.hutState is HutsUiState.Success
                        ),
                        onRefreshAllClick  = {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            viewModel.getAllHuts(true)
                        },
                        onRefreshAlertsClick = {
                            viewModel.getHutAlerts(true)
                        }
                    )
                },
                navigateUp = { navigateUp() },
                titleId = R.string.app_title_sub,
                subtitle = stringResource(R.string.huts_header)
            )
        }
    ) { contentPadding ->

        val searchBoxPaddingBottom = contentPadding.calculateBottomPadding()
        val searchBoxPaddingTop = when (isSearchVisible) {
            true -> 0.dp
            else -> contentPadding.calculateTopPadding()
        }

        when (viewState.hutState) {
            is HutsUiState.Downloading -> LoadingScreen(
                stringId = R.string.dload_huts,
                modifier = modifier.fillMaxSize()
            )
            is HutsUiState.Error -> ErrorScreen(
                retryAction = { viewModel.getAllHuts(true) },
                modifier = modifier.fillMaxSize()
            )
            is HutsUiState.Forbidden -> ForbiddenScreen(
                modifier = modifier.fillMaxSize()
            )
            is HutsUiState.Loading -> LoadingScreen(
                stringId = R.string.loading_huts,
                modifier = modifier.fillMaxSize()
            )
            is HutsUiState.Success -> {
                when (viewState.hutsList.isEmpty()) {
                    true -> EmptyScreen(
                        itemType = FilterType.Huts,
                        listFilterBookable = listFilterBookable,
                        listFilterRegion = listFilterRegion,
                        modifier = modifier.fillMaxSize()
                    )
                    else -> {
                        val content: (@Composable () -> Unit) = {
                            val listHeader = getListHeaderText(
                                listFilterRegion = listFilterRegion,
                                listSize = viewState.hutsList.size,
                                pluralsIdFiltered = R.plurals.huts_filter,
                                stringIdUnfiltered = R.string.huts_current_count
                            )
                            HutsList(
                                contentPadding = contentPadding,
                                huts = viewState.hutsList,
                                listHeader = listHeader,
                                modifier = modifier
                                    .padding(
                                        bottom = searchBoxPaddingBottom,
                                        end = dimensionResource(
                                            R.dimen.padding_medium
                                        ),
                                        start = dimensionResource(
                                            R.dimen.padding_medium
                                        ),
                                        top = searchBoxPaddingTop
                                    ),
                                navigateToHutDetails = { index ->
                                    if (
                                        windowSize
                                        !=
                                        WindowWidthSizeClass.Compact
                                    ) {
                                        viewModel.resetDownloadState()
                                    }
                                    navigateToHutDetails(index)
                                },
                                navigateToHutsMap = navigateToHutsMap,
                                subHeaders = subHeaders
                            )
                        }
                        val hutsMap = viewModel.getHutsHashMap()
                        val onSearchItemClick: (String) -> Unit = { itemId ->
                            /* For list screen, hide searchBox asap */
                            onToggleSearch()
                            val index = viewModel.getHutIndex(itemId)
                            navigateToHutDetails(index)
                        }
                        SearchBoxWithContent(
                            advancedSearch = advancedSearch,
                            onAdvSearchNotAvailClick =
                                onAdvSearchNotAvailClick,
                            contentPadding = contentPadding,
                            enableFeatSearch = true,
                            hashMap = hutsMap,
                            isSearchVisible = isSearchVisible,
                            onSearchByClick = viewModel::setHutsHashMap,
                            onSearchItemClick = onSearchItemClick,
                            searchBy = viewState.searchBy,
                            content = { content() }
                        )
                    }
                }
            }
        }
    }
}