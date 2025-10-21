package com.michaelrmossman.docoutdoors.ui.alerts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.Alert
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.components.SearchBoxWithContent
import com.michaelrmossman.docoutdoors.ui.components.SearchButton

@Composable
fun AlertDetailsScreen(
    initialPage: Int,
    isSearchVisible: Boolean,
    navigateToAlertDetails: (Int) -> Unit,
    navigateToAffectedMap: (Int, Int) -> Unit,
    navigateUp: () -> Unit,
    onAffectedClick: (Alert, Int) -> Unit,
    onToggleSearch: () -> Unit,
    paddingValues: PaddingValues,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val viewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModel.Factory
    )
    val viewState by viewModel.alertsListState.collectAsState()

    val onDownloadClick = { itemId: String ->
        viewModel.downloadExtras(itemId)
    }

    Scaffold(
        topBar = {
            QuadrupleTopAppBar(
                actions = {
                    if (windowSize == WindowWidthSizeClass.Compact) {
                        SearchButton(
                            isSearchVisible = isSearchVisible,
                            onToggleSearch = onToggleSearch
                        )
                    }
                },
                navigateUp = { navigateUp() },
                titleId = when (windowSize) {
                    WindowWidthSizeClass.Compact -> {
                        R.string.alerts_subtitle
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

        val alerts = viewState.alertsList
        val alertsMap = viewModel.getAlertsHashMap()
        val onSearchItemClick: (String) -> Unit = { itemId ->
            val index = viewModel.getAlertIndex(itemId)
            navigateToAlertDetails(index)
            /* animateScrollToPage before hiding
               visibility creates cool effect */
            onToggleSearch()
        }
        val content: (@Composable (Alert) -> Unit) = { alert ->
            AlertItemPage(
                alert = alert,
                alertIndex = alerts.indexOf(alert),
                downloadState = viewModel.downloadState,
                onAffectedClick = onAffectedClick,
                onDownloadClick = onDownloadClick,
                navigateToAffectedMap = navigateToAffectedMap,
                // onToggleFave = onToggleFave,
                paddingValues = paddingValues,
                windowSize = windowSize,
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
                    pageCount = { alerts.size }
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
                    advancedSearch = true,
                    contentPadding = contentPadding,
                    enableFeatSearch = false,
                    hashMap = alertsMap,
                    isSearchVisible = isSearchVisible,
                    onAdvSearchNotAvailClick =
                        { /* Not used for Alerts */ },
                    onSearchByClick = viewModel::setAlertsHashMap,
                    onSearchItemClick = onSearchItemClick,
                    searchBy = viewState.searchBy,
                    content = {
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = 16.dp,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            content(alerts[page])
                        }
                    }
                )
            }
            else -> content(alerts[initialPage])
        }
    }
}