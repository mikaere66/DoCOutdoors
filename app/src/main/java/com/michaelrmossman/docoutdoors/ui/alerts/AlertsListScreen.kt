package com.michaelrmossman.docoutdoors.ui.alerts

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.interfaces.AlertsUiState
import com.michaelrmossman.docoutdoors.ui.EmptyList
import com.michaelrmossman.docoutdoors.ui.EmptyScreen
import com.michaelrmossman.docoutdoors.ui.ErrorScreen
import com.michaelrmossman.docoutdoors.ui.ForbiddenScreen
import com.michaelrmossman.docoutdoors.ui.LoadingScreen
import com.michaelrmossman.docoutdoors.ui.components.QuadrupleTopAppBar
import com.michaelrmossman.docoutdoors.ui.components.SearchBoxWithContent
import com.michaelrmossman.docoutdoors.ui.components.SearchButton
import com.michaelrmossman.docoutdoors.ui.components.SingleActionMenu
import com.michaelrmossman.docoutdoors.ui.components.SnackbarMessage
import com.michaelrmossman.docoutdoors.utils.TextUtils.getListHeaderText

@Composable
fun AlertsListScreen(
    isSearchVisible: Boolean,
    navigateToAlertDetails: (Int) -> Unit,
    navigateToAffectedMap: (Int, Int) -> Unit,
    navigateUp: () -> Unit,
    onToggleSearch: () -> Unit,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val viewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModel.Factory
    )
    val viewState by viewModel.alertsListState.collectAsState()

    val alertsListIncomplete by viewModel.alertsListIncomplete.observeAsState()
    val listFilterRegion by viewModel.alertsFilterByRegion.observeAsState()
    var retryEnabled by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (
        listFilterRegion?.isBlank() == true
        &&
        alertsListIncomplete != null
        &&
        alertsListIncomplete == true
    ) {
        SnackbarMessage(
            onRefreshClick = { viewModel.getAllAlerts(true) },
            snackbarHostState = snackbarHostState
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            QuadrupleTopAppBar(
                actions = {
                    SearchButton(
                        isEnabled = viewState.alertsList.isNotEmpty(),
                        isSearchVisible = isSearchVisible,
                        onToggleSearch = onToggleSearch
                    )
                    /* "View all on Map" and "Refresh just
                       Alerts" are not used on Alerts list */
                    SingleActionMenu(
                        isEnabled = (
                            viewState.alertState is AlertsUiState.Success
                        ),
                        itemStringId = R.string.menu_refresh_all,
                        onSingleItemClick = {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            viewModel.getAllAlerts(true)
                        }
                    )
                },
                navigateUp = { navigateUp() },
                titleId = R.string.app_title_sub,
                subtitle = stringResource(R.string.alerts_header)
            )
        }
    ) { contentPadding ->

        val searchBoxPaddingBottom = contentPadding.calculateBottomPadding()
        val searchBoxPaddingTop = when (isSearchVisible) {
            true -> 0.dp
            else -> contentPadding.calculateTopPadding()
        }

        when (viewState.alertState) {
            is AlertsUiState.Downloading -> LoadingScreen(
                stringId = R.string.dload_alerts,
                modifier = modifier.fillMaxSize()
            )
            is AlertsUiState.Empty -> EmptyList(
                itemType = FilterType.Alerts.name.lowercase(),
                region = listFilterRegion ?: String(),
                modifier = modifier.fillMaxSize()
            )
            is AlertsUiState.Error -> ErrorScreen(
                retryAction = { viewModel.getAllAlerts(true) },
                modifier = modifier.fillMaxSize()
            )
            is AlertsUiState.Forbidden -> ForbiddenScreen(
                modifier = modifier.fillMaxSize()
            )
            is AlertsUiState.Loading -> LoadingScreen(
                stringId = R.string.loading_alerts,
                modifier = modifier.fillMaxSize()
            )
            is AlertsUiState.Success -> {
                when (viewState.alertsList.isEmpty()) {
                    true -> EmptyScreen(
                        itemType = FilterType.Alerts,
                        modifier = modifier.fillMaxSize(),
                        retryAction = when (
                            listFilterRegion?.isBlank() == true
                        ) {
                            true -> null
                            else -> {{ /* Double brackets intentional */
                                retryEnabled = false
                                /* Although the reset param will try and
                                   delete non-existent alerts, there are
                                   OTHER functions controlled by this */
                                viewModel.getAllAlerts(reset = true)
                            }}
                        },
                        retryEnabled = retryEnabled
                    )
                    else -> {
                        val alertsMap = viewModel.getAlertsHashMap()
                        val content: (@Composable () -> Unit) = {
                            val listHeader = getListHeaderText(
                                listFilterRegion = listFilterRegion,
                                listSize = viewState.alertsList.size,
                                pluralsIdFiltered = R.plurals.alerts_filter,
                                stringIdUnfiltered = R.string.alerts_current_count
                            )
                            AlertsList(
                                alerts = viewState.alertsList,
                                contentPadding = contentPadding,
                                listHeader = listHeader,
                                modifier = modifier
                                    .padding(
                                        bottom = searchBoxPaddingBottom,
                                        end = dimensionResource(R.dimen.padding_medium),
                                        start = dimensionResource(R.dimen.padding_medium),
                                        top = searchBoxPaddingTop
                                    ),
                                navigateToAlertDetails = { index ->
                                    if (windowSize != WindowWidthSizeClass.Compact) {
                                        viewModel.resetDownloadState()
                                    }
                                    navigateToAlertDetails(index)
                                },
                                navigateToAffectedMap = navigateToAffectedMap
                            )
                        }
                        val onSearchItemClick: (String) -> Unit = { itemId ->
                            /* For list screen, hide searchBox asap */
                            onToggleSearch()
                            val index = viewModel.getAlertIndex(itemId)
                            navigateToAlertDetails(index)
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
                            content = { content() }
                        )
                    }
                }
            }
        }
    }
}