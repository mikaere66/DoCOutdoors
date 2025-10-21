package com.michaelrmossman.docoutdoors.ui.campsites

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
import com.michaelrmossman.docoutdoors.interfaces.CampsitesUiState
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
fun CampsitesListScreen(
    isSearchVisible: Boolean,
    navigateToCampsiteDetails: (Int) -> Unit,
    navigateToCampsitesMap: (Int) -> Unit,
    navigateUp: () -> Unit,
    onToggleSearch: () -> Unit,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val viewModel: CampsitesViewModel = viewModel(
        factory = CampsitesViewModel.Factory
    )
    val viewState by viewModel.campsitesListState.collectAsState()
    val campsitesAdvancedSearch by
        viewModel.campsitesAdvancedSearch.observeAsState()

    val advancedSearch = campsitesAdvancedSearch != 0
    val context = LocalContext.current
    val listFilterRegion by viewModel.campsitesFilterByRegion.observeAsState()
    val listFilterDogs by viewModel.commonFilterByDogAccess.observeAsState()
    val listFilterBookable by viewModel.commonFilterByBookable.observeAsState()
    val onAdvSearchNotAvailClick = {
        context.showAdvSearchNotAvailToast()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    /* Must come before actual content ... note: listHeader and
       subHeaders done differently, compared to other lists */
    val subHeaders = getListSubHeaders(
        listFilterBookable = listFilterBookable,
        listFilterDogs = listFilterDogs
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            QuadrupleTopAppBar(
                actions = {
                    SearchButton(
                        isEnabled =
                            viewState.campsiteState is CampsitesUiState.Success,
                        isSearchVisible = isSearchVisible,
                        onToggleSearch = onToggleSearch
                    )
                    if (windowSize == WindowWidthSizeClass.Compact) {
                        MapButton(
                            isEnabled = (
                                viewState.containsValidCoords
                                &&
                                viewState.campsiteState is CampsitesUiState.Success
                            ),
                            navigateToMap = navigateToCampsitesMap
                        )
                    }
                    DoubleActionMenu(
                        isEnabled = (
                            viewState.campsiteState is CampsitesUiState.Success
                        ),
                        onRefreshAllClick  = {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            viewModel.getAllCampsites(true)
                        },
                        onRefreshAlertsClick = {
                            viewModel.getCampsiteAlerts(true)
                        }
                    )
                },
                navigateUp = { navigateUp() },
                titleId = R.string.app_title_sub,
                subtitle = stringResource(R.string.campsites_header)
            )
        }
    ) { contentPadding ->

        val searchBoxPaddingBottom = contentPadding.calculateBottomPadding()
        val searchBoxPaddingTop = when (isSearchVisible) {
            true -> 0.dp
            else -> contentPadding.calculateTopPadding()
        }

        when (viewState.campsiteState) {
            is CampsitesUiState.Downloading -> LoadingScreen(
                stringId = R.string.dload_campsites,
                modifier = modifier.fillMaxSize()
            )
            is CampsitesUiState.Error -> ErrorScreen(
                retryAction = { viewModel.getAllCampsites(true) },
                modifier = modifier.fillMaxSize()
            )
            is CampsitesUiState.Forbidden -> ForbiddenScreen(
                modifier = modifier.fillMaxSize()
            )
            is CampsitesUiState.Loading -> LoadingScreen(
                stringId = R.string.loading_campsites,
                modifier = modifier.fillMaxSize()
            )
            is CampsitesUiState.Success -> {
                when (viewState.campsitesList.isEmpty()) {
                    true -> EmptyScreen(
                        itemType = FilterType.Campsites,
                        listFilterBookable = listFilterBookable,
                        listFilterDogs = listFilterDogs,
                        listFilterRegion = listFilterRegion,
                        modifier = modifier.fillMaxSize()
                    )
                    else -> {
                        val campsitesMap = viewModel.getCampsitesHashMap()
                        val content: (@Composable () -> Unit) = {
                            val listHeader = getListHeaderText(
                                listFilterRegion = listFilterRegion,
                                listSize = viewState.campsitesList.size,
                                pluralsIdFiltered = R.plurals.campsites_filter,
                                stringIdUnfiltered = R.string.campsites_current_count
                            )
                            CampsitesList(
                                campsites = viewState.campsitesList,
                                contentPadding = contentPadding,
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
                                navigateToCampsiteDetails = { index ->
                                    if (
                                        windowSize
                                        !=
                                        WindowWidthSizeClass.Compact
                                    ) {
                                        viewModel.resetDownloadState()
                                    }
                                    navigateToCampsiteDetails(index)
                                },
                                navigateToCampsitesMap = navigateToCampsitesMap,
                                subHeaders = subHeaders
                            )
                        }
                        val onSearchItemClick: (String) -> Unit = { itemId ->
                            /* For list screen, hide searchBox asap */
                            onToggleSearch()
                            val index = viewModel.getCampsiteIndex(itemId)
                            navigateToCampsiteDetails(index)
                        }
                        SearchBoxWithContent(
                            advancedSearch = advancedSearch,
                            onAdvSearchNotAvailClick =
                                onAdvSearchNotAvailClick,
                            contentPadding = contentPadding,
                            enableFeatSearch = true,
                            hashMap = campsitesMap,
                            isSearchVisible = isSearchVisible,
                            onSearchByClick = viewModel::setCampsitesHashMap,
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