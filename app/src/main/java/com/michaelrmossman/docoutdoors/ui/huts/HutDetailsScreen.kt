package com.michaelrmossman.docoutdoors.ui.huts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.AlertExtra
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.ui.components.AlertBottomSheet
import com.michaelrmossman.docoutdoors.ui.components.MapButton
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.components.SearchBoxWithContent
import com.michaelrmossman.docoutdoors.ui.components.SearchButton
import com.michaelrmossman.docoutdoors.utils.showAdvSearchNotAvailToast

private var alert = AlertExtra()

@Composable
fun HutDetailsScreen(
    initialPage: Int,
    isSearchVisible: Boolean,
    navigateToHutDetails: (Int) -> Unit,
    navigateToHutsMap: (Int) -> Unit,
    navigateUp: () -> Unit,
    onToggleSearch: () -> Unit,
    paddingValues: PaddingValues,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val viewModel: HutsViewModel = viewModel(
        factory = HutsViewModel.Factory
    )
    val viewState by viewModel.hutsListState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAlertById by rememberSaveable { mutableStateOf(String()) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val onToggleFave = { hut: HutKt ->
        viewModel.toggleFavourite(
            assetId = hut.assetId,
            isFavourite = hut.isFavourite
        )
    }
    val onDownloadClick = { itemId: String ->
        viewModel.downloadExtras(itemId)
    }
    val onDismissRequest = {
        showAlertById = String()
        showBottomSheet = false
        alert = AlertExtra()
    }
    val onAlertsClick = { assetId: String ->
        showAlertById = assetId
    }
    val hutsAdvancedSearch by
        viewModel.hutsAdvancedSearch.observeAsState()
    val context = LocalContext.current
    val onAdvSearchNotAvailClick = {
        context.showAdvSearchNotAvailToast()
    }
    val advancedSearch = hutsAdvancedSearch != 0

    LaunchedEffect(key1 = showAlertById) {
        if (showAlertById.isNotBlank()) {
            alert = viewModel.getAlertById(
                id = showAlertById
            )
            showBottomSheet = true
        }
    }

    if (showBottomSheet) {
        AlertBottomSheet(
            alert = alert,
            onDismissRequest = onDismissRequest
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            QuadrupleTopAppBar(
                actions = {
                    when (windowSize == WindowWidthSizeClass.Compact) {
                        true -> SearchButton(
                            isSearchVisible = isSearchVisible,
                            onToggleSearch = onToggleSearch
                        )
                        else -> MapButton(
                            isEnabled = viewState.containsValidCoords,
                            navigateToMap = navigateToHutsMap
                        )
                    }
                },
                navigateUp = { navigateUp() },
                titleId = when (windowSize) {
                    WindowWidthSizeClass.Compact -> {
                        R.string.huts_subtitle
                    }
                    /* Pass zero as titleId for larger screens,
                       to indicate NO navigation or title text */
                    else -> 0
                }
            )
        }
    ) { contentPadding ->

        val searchBoxPaddingBottom = contentPadding.calculateBottomPadding()
        val searchBoxPaddingTop = when (isSearchVisible) {
            true -> when (windowSize == WindowWidthSizeClass.Compact) {
                true -> 0.dp
                else -> contentPadding.calculateTopPadding()
            }
            else -> contentPadding.calculateTopPadding()
        }

        val huts = viewState.hutsList
        val hutsMap = viewModel.getHutsHashMap()
        val onSearchItemClick: (String) -> Unit = { itemId ->
            val index = viewModel.getHutIndex(itemId)
            navigateToHutDetails(index)
            /* animateScrollToPage before hiding
               visibility creates cool effect */
            onToggleSearch()
        }
        val content: (@Composable (HutKt) -> Unit) = { hut ->
            HutItemPage(
                downloadState = viewModel.downloadState,
                hut = hut,
                navigateToHutsMap = {
                    navigateToHutsMap(
                        huts.indexOf(hut)
                    )
                },
                onAlertsClick = onAlertsClick,
                onDownloadClick = onDownloadClick,
                onToggleFave = onToggleFave,
                paddingValues = paddingValues,
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        bottom = searchBoxPaddingBottom,
                        top = searchBoxPaddingTop
                    )
            )
        }
        when (windowSize == WindowWidthSizeClass.Compact) {
            true -> {
                var pageIndex by rememberSaveable {
                    mutableIntStateOf(initialPage)
                }
                val pagerState = rememberPagerState(
                    initialPage = pageIndex,
                    pageCount = { huts.size }
                )
                LaunchedEffect(pagerState) {
                    // Collect from the snapshotFlow reading the currentPage
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        /* Store current index as pageIndex, in case of
                           activity recreation, e.g. screen rotation */
                        pageIndex = page
                        viewModel.resetDownloadState()
                    }
                }
                SearchBoxWithContent(
                    advancedSearch = advancedSearch,
                    onAdvSearchNotAvailClick =
                        onAdvSearchNotAvailClick,
                    contentPadding = contentPadding,
                    enableFeatSearch = true,
                    hashMap = hutsMap,
                    isSearchVisible = isSearchVisible,
                    onSearchItemClick = onSearchItemClick,
                    onSearchByClick = viewModel::setHutsHashMap,
                    searchBy = viewState.searchBy,
                    content = {
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = 16.dp,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            content(huts[page])
                        }
                    }
                )
            }
            else -> content(huts[initialPage])
        }
    }
}