package com.michaelrmossman.docoutdoors.ui.tracks

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.interfaces.TracksUiState
import com.michaelrmossman.docoutdoors.ui.EmptyList
import com.michaelrmossman.docoutdoors.ui.EmptyScreen
import com.michaelrmossman.docoutdoors.ui.ForbiddenScreen
import com.michaelrmossman.docoutdoors.ui.ErrorScreen
import com.michaelrmossman.docoutdoors.ui.LoadingScreen
import com.michaelrmossman.docoutdoors.ui.components.DoubleActionMenu
import com.michaelrmossman.docoutdoors.ui.components.MapButton
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.components.SearchBoxWithContent
import com.michaelrmossman.docoutdoors.ui.components.SearchButton
import com.michaelrmossman.docoutdoors.ui.components.SnackbarMessage
import com.michaelrmossman.docoutdoors.utils.TextUtils.getListHeaderText
import com.michaelrmossman.docoutdoors.utils.TextUtils.getListSubHeaders
import com.michaelrmossman.docoutdoors.utils.showAdvSearchNotAvailToast

@Composable
fun TracksListScreen(
    isSearchVisible: Boolean,
    navigateToTrackDetails: (Int) -> Unit,
    navigateToTracksMap: (Int) -> Unit,
    navigateUp: () -> Unit,
    onToggleSearch: () -> Unit,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val viewModel: TracksViewModel = viewModel(
        factory = TracksViewModel.Factory
    )
    val viewState by viewModel.tracksListState.collectAsState()
    val tracksAdvancedSearch by
        viewModel.tracksAdvancedSearch.observeAsState()

    val advancedSearch = (
        tracksAdvancedSearch != null
        &&
        tracksAdvancedSearch != 0
    )
    val context = LocalContext.current
    val listFilterRegion by
        viewModel.tracksFilterByRegion.observeAsState()
    val listFilterDogs by
        viewModel.commonFilterByDogAccess.observeAsState()
    val onAdvSearchNotAvailClick = {
        context.showAdvSearchNotAvailToast()
    }
    var retryEnabled by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val subHeaders = getListSubHeaders(
        listFilterDogs = listFilterDogs
    )
    val tracksListIncomplete by
        viewModel.tracksListIncomplete.observeAsState()

    if (
        listFilterRegion?.isBlank() == true
        &&
        tracksListIncomplete != null
        &&
        tracksListIncomplete == true
    ) {
        SnackbarMessage(
            onRefreshClick = { viewModel.getAllTracks(true) },
            snackbarHostState = snackbarHostState
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            QuadrupleTopAppBar(
                actions = {
                    SearchButton(
                        isEnabled =
                            viewState.trackState is TracksUiState.Success,
                        isSearchVisible = isSearchVisible,
                        onToggleSearch = onToggleSearch
                    )
                    if (windowSize == WindowWidthSizeClass.Compact) {
                        MapButton(
                            isEnabled = (
                                viewState.containsValidCoords
                                &&
                                viewState.trackState is TracksUiState.Success
                            ),
                            navigateToMap = navigateToTracksMap
                        )
                    }
                    DoubleActionMenu(
                        isEnabled = (
                            viewState.trackState is TracksUiState.Success
                        ),
                        onRefreshAllClick  = {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            viewModel.getAllTracks(true)
                        },
                        onRefreshAlertsClick = {
                            viewModel.getTrackAlerts(true)
                        }
                    )
                },
                navigateUp = { navigateUp() },
                titleId = R.string.app_title_sub,
                subtitle = stringResource(R.string.tracks_header)
            )
        }
    ) { contentPadding ->

        val searchBoxPaddingBottom = contentPadding.calculateBottomPadding()
        val searchBoxPaddingTop = when (isSearchVisible) {
            true -> 0.dp
            else -> contentPadding.calculateTopPadding()
        }

        when (viewState.trackState) {
            is TracksUiState.Downloading -> LoadingScreen(
                stringId = R.string.dload_tracks,
                modifier = modifier.fillMaxSize()
            )
            is TracksUiState.Empty -> EmptyList(
                itemType = FilterType.Tracks.name.lowercase(),
                region = listFilterRegion ?: String(),
                modifier = modifier.fillMaxSize()
            )
            is TracksUiState.Error -> ErrorScreen(
                retryAction = { viewModel.getAllTracks(true) },
                modifier = modifier.fillMaxSize()
            )
            is TracksUiState.Forbidden -> ForbiddenScreen(
                modifier = modifier.fillMaxSize()
            )
            is TracksUiState.Loading -> LoadingScreen(
                stringId = R.string.loading_tracks,
                modifier = modifier.fillMaxSize()
            )
            is TracksUiState.Success -> {
                when (viewState.tracksList.isEmpty()) {
                    true -> EmptyScreen(
                        itemType = FilterType.Tracks,
                        listFilterDogs = listFilterDogs,
                        listFilterRegion = listFilterRegion,
                        modifier = modifier.fillMaxSize(),
                        retryAction = when (
                            listFilterRegion?.isBlank() == true
                        ) {
                            true -> null
                            else -> {{ /* Double brackets intentional */
                                retryEnabled = false
                                /* Although the reset param will try and
                                   delete non-existent assets, there are
                                   OTHER functions controlled by this */
                                viewModel.getAllTracks(reset = true)
                            }}
                        },
                        retryEnabled = retryEnabled
                    )
                    else -> {
                        val content: (@Composable () -> Unit) = {
                            val listHeader = getListHeaderText(
                                listFilterRegion = listFilterRegion,
                                listSize = viewState.tracksList.size,
                                pluralsIdFiltered = R.plurals.tracks_filter,
                                stringIdUnfiltered = R.string.tracks_current_count
                            )
                            TracksList(
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
                                navigateToTrackDetails = { index ->
                                    if (
                                        windowSize
                                        !=
                                        WindowWidthSizeClass.Compact
                                    ) {
                                        viewModel.resetDownloadState()
                                    }
                                    navigateToTrackDetails(index)
                                },
                                navigateToTracksMap = navigateToTracksMap,
                                subHeaders = subHeaders,
                                tracks = viewState.tracksList
                            )
                        }
                        val tracksMap = viewModel.getTracksHashMap()
                        val onSearchItemClick: (String) -> Unit = { itemId ->
                            /* For list screen, hide searchBox asap */
                            onToggleSearch()
                            val index = viewModel.getTrackIndex(itemId)
                            navigateToTrackDetails(index)
                        }
                        SearchBoxWithContent(
                            advancedSearch = advancedSearch,
                            onAdvSearchNotAvailClick =
                                onAdvSearchNotAvailClick,
                            contentPadding = contentPadding,
                            enableFeatSearch = false,
                            hashMap = tracksMap,
                            isSearchVisible = isSearchVisible,
                            onSearchByClick = viewModel::setTracksHashMap,
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