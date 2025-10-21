package com.michaelrmossman.docoutdoors.ui

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.interfaces.CurrentScreen
import com.michaelrmossman.docoutdoors.interfaces.HomeScreenState
import com.michaelrmossman.docoutdoors.ui.alerts.AffectedItemsScreen
import com.michaelrmossman.docoutdoors.ui.alerts.AffectedMapScreen
import com.michaelrmossman.docoutdoors.ui.alerts.AlertDetailsScreen
import com.michaelrmossman.docoutdoors.ui.alerts.AlertsListScreen
import com.michaelrmossman.docoutdoors.ui.campsites.CampsiteDetailsScreen
import com.michaelrmossman.docoutdoors.ui.campsites.CampsiteSingleScreen
import com.michaelrmossman.docoutdoors.ui.campsites.CampsitesListScreen
import com.michaelrmossman.docoutdoors.ui.campsites.CampsitesMapScreen
import com.michaelrmossman.docoutdoors.ui.content.ContentPlaceholder
import com.michaelrmossman.docoutdoors.ui.favourites.FavesListScreen
import com.michaelrmossman.docoutdoors.ui.favourites.MultiMapScreen
import com.michaelrmossman.docoutdoors.ui.favourites.SingleMapScreen
import com.michaelrmossman.docoutdoors.ui.help.HelpScreen
import com.michaelrmossman.docoutdoors.ui.huts.HutDetailsScreen
import com.michaelrmossman.docoutdoors.ui.huts.HutSingleScreen
import com.michaelrmossman.docoutdoors.ui.huts.HutsListScreen
import com.michaelrmossman.docoutdoors.ui.huts.HutsMapScreen
import com.michaelrmossman.docoutdoors.ui.settings.SettingsActivity
import com.michaelrmossman.docoutdoors.ui.tracks.TrackDetailsScreen
import com.michaelrmossman.docoutdoors.ui.tracks.TrackSingleScreen
import com.michaelrmossman.docoutdoors.ui.tracks.TracksListScreen
import com.michaelrmossman.docoutdoors.ui.tracks.TracksMapScreen
import com.michaelrmossman.docoutdoors.utils.TextUtils.getListHeaderText

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun OutdoorsApp(
    windowSize: WindowSizeClass
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val additionalPadding = dimensionResource(R.dimen.padding_content_card)
    val context = LocalContext.current
    val directive = remember(adaptiveInfo) {
        calculatePaneScaffoldDirective(adaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val homeViewModel: HomeScreenViewModel = viewModel(
        factory = HomeScreenViewModel.Factory
    )
    var isDeetSearchVisible by remember { mutableStateOf(false) }
    var isListSearchVisible by remember { mutableStateOf(false) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        directive = directive
    )
    val navViewModel: NavigationViewModel = viewModel()
    val onToggleDeetSearch = { isDeetSearchVisible = !isDeetSearchVisible }
    val onToggleListSearch = { isListSearchVisible = !isListSearchVisible }
    val paddingStart = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            additionalPadding
        }
        else -> 0.dp
    }
    val paddingValues = PaddingValues(
        bottom = additionalPadding,
        end = additionalPadding,
        start = paddingStart,
        top = additionalPadding
    )

    val filterCampsitesBookable by
        homeViewModel.campsitesFilterBookable.observeAsState()
    val filterCampsitesDogAccess by
        homeViewModel.campsitesFilterDogAccess.observeAsState()
    val filterHutsBookable by
        homeViewModel.hutsFilterBookable.observeAsState()
    val filterTracksDogAccess by
        homeViewModel.tracksFilterDogAccess.observeAsState()

    val filterAlertsBy by homeViewModel.alertsFilterById.collectAsState()
    val filterCampsitesBy by homeViewModel.campsitesFilterById.collectAsState()
    val filterHutsBy by homeViewModel.hutsFilterById.collectAsState()
    val filterTracksBy by homeViewModel.tracksFilterById.collectAsState()

    /* Each of these four initialValue[s] in viewModel is -1 */
    val qtyAlertsDL by homeViewModel.alertCount.collectAsState()
    val qtyCampsitesDL by homeViewModel.campsiteCount.collectAsState()
    val qtyFaves by homeViewModel.faveCount.collectAsState()
    val qtyHutsDL by homeViewModel.hutCount.collectAsState()
    val qtyTracksDL by homeViewModel.trackCount.collectAsState()
    /* Remember that setHomeScreenState() is at End of File */

    val alertsFilterByRegion by
        homeViewModel.alertsFilterByRegion.observeAsState()
    val alertsQtyHeader = getListHeaderText(
        listFilterRegion = alertsFilterByRegion,
        listSize = qtyAlertsDL,
        pluralsIdFiltered = R.plurals.alerts_filter,
        stringIdUnfiltered = R.string.alerts_current_count,
        alertsFilterBy = filterAlertsBy
    )
    val campsitesFilterByRegion by
        homeViewModel.campsitesFilterByRegion.observeAsState()
    val campsitesQtyHeader = getListHeaderText(
        listFilterRegion = campsitesFilterByRegion,
        listSize = qtyCampsitesDL,
        pluralsIdFiltered = R.plurals.campsites_filter,
        stringIdUnfiltered = R.string.campsites_current_count,
        bookable = filterCampsitesBookable,
        dogAccess = filterCampsitesDogAccess
    )
    val hutsFilterByRegion by
        homeViewModel.hutsFilterByRegion.observeAsState()
    val hutsQtyHeader = getListHeaderText(
        listFilterRegion = hutsFilterByRegion,
        listSize = qtyHutsDL,
        pluralsIdFiltered = R.plurals.huts_filter,
        stringIdUnfiltered = R.string.huts_current_count,
        bookable = filterHutsBookable
    )
    val tracksFilterByRegion by
        homeViewModel.tracksFilterByRegion.observeAsState()
    val tracksQtyHeader = getListHeaderText(
        listFilterRegion = tracksFilterByRegion,
        listSize = qtyTracksDL,
        pluralsIdFiltered = R.plurals.tracks_filter,
        stringIdUnfiltered = R.string.tracks_current_count,
        dogAccess = filterTracksDogAccess
    )

    /* Navigation 3, where each entry on the back stack represents content ...
       This implementation uses "scene strategies" to support adaptive layouts */
    NavDisplay(
        backStack = navViewModel.backStack,
        onBack = {
            isDeetSearchVisible = false
            isListSearchVisible = false
            navViewModel.pop()
        },
        sceneStrategy = listDetailStrategy,
        entryProvider = entryProvider {
            entry<CurrentScreen.HomeScreen> {
                HomeStatusScreen(
                    favouriteCount = qtyFaves,
                    filterAlertsBy = filterAlertsBy,
                    filterCampsitesBookable = filterCampsitesBookable,
                    filterCampsitesBy = filterCampsitesBy.plus(
                        (filterCampsitesBookable ?: 0).plus(
                            filterCampsitesDogAccess ?: 0
                        )
                    ),
                    filterCampsitesDogAccess = filterCampsitesDogAccess,
                    filterHutsBookable = filterHutsBookable,
                    filterHutsBy = filterHutsBy.plus(
                        filterHutsBookable ?: 0
                    ),
                    filterTracksBy = filterTracksBy.plus(
                        filterTracksDogAccess ?: 0
                    ),
                    filterTracksDogAccess = filterTracksDogAccess,
                    headerQtyAlerts = alertsQtyHeader,
                    headerQtyCampsites = campsitesQtyHeader,
                    headerQtyHuts = hutsQtyHeader,
                    headerQtyTracks = tracksQtyHeader,
                    onAlertsClicked = {
                        navViewModel.put(CurrentScreen.AlertsList)
                    },
                    onCampsitesClicked = {
                        navViewModel.put(CurrentScreen.CampsitesList)
                    },
                    onFavesClicked = {
                        navViewModel.put(CurrentScreen.FavesScreen)
                    },
                    onHelpClicked = {
                        navViewModel.put(CurrentScreen.HelpScreen)
                    },
                    onHutsClicked = {
                        navViewModel.put(CurrentScreen.HutsList)
                    },
                    onSettingsClicked = {
                        context.startActivity(
                            Intent(context, SettingsActivity::class.java)
                        )
                    },
                    onTracksClicked = {
                        navViewModel.put(CurrentScreen.TracksList)
                    },
                    uiState = homeViewModel.uiState
                )
            }
            entry<CurrentScreen.AlertsList>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        ContentPlaceholder(
                            drawableId = R.drawable.detour_alert,
                            stringId =
                                R.string.details_placeholder_alert
                        )
                    }
                )
            ) {
                AlertsListScreen(
                    isSearchVisible = isListSearchVisible,
                    navigateToAlertDetails = { index ->
                        navViewModel.put(CurrentScreen.AlertDetails(index))
                    },
                    navigateToAffectedMap = { affectedIndex, alertIndex ->
                        navViewModel.put(
                            CurrentScreen.AffectedMap(
                                affectedIndex, alertIndex
                            )
                        )
                    },
                    navigateUp = { navViewModel.home() },
                    onToggleSearch = onToggleListSearch,
                    windowSize = windowSize.widthSizeClass
                )
            }
            entry<CurrentScreen.AlertDetails>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { currentScreen ->
                AlertDetailsScreen(
                    initialPage = currentScreen.index,
                    isSearchVisible = isDeetSearchVisible,
                    navigateToAlertDetails = { index ->
                        navViewModel.pop()
                        navViewModel.put(CurrentScreen.AlertDetails(index))
                    },
                    navigateToAffectedMap = { affectedIndex, alertIndex ->
                        navViewModel.put(
                            CurrentScreen.AffectedMap(
                                affectedIndex, alertIndex
                            )
                        )
                    },
                    navigateUp = {
                        isDeetSearchVisible = false
                        navViewModel.pop()
                    },
                    onAffectedClick = { alert, index ->
                        navViewModel.put(
                            CurrentScreen.AffectedList(alert, index)
                        )
                    },
                    onToggleSearch = onToggleDeetSearch,
                    paddingValues = paddingValues,
                    windowSize = windowSize.widthSizeClass
                )
            }
            entry<CurrentScreen.AffectedList>(
                metadata = ListDetailSceneStrategy.extraPane()
            ) { currentScreen ->
                AffectedItemsScreen(
                    alert = currentScreen.alert,
                    alertIndex = currentScreen.index,
                    navigateToAffectedMap = { affectedIndex, alertIndex ->
                        navViewModel.put(
                            CurrentScreen.AffectedMap(
                                affectedIndex, alertIndex
                            )
                        )
                    },
                    onCloseClick = { navViewModel.pop() }
                )
            }
            entry<CurrentScreen.AffectedMap> { currentScreen ->
                AffectedMapScreen(
                    affectedIndex = currentScreen.affectedIndex,
                    alertIndex = currentScreen.alertIndex,
                    navigateUp = { navViewModel.pop() }
                )
            }
            entry<CurrentScreen.CampsitesList>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        ContentPlaceholder(
                            drawableId =
                                R.drawable.routeburn_flats_campsite,
                            stringId =
                                R.string.details_placeholder_campsite
                        )
                    }
                )
            ) {
                CampsitesListScreen(
                    isSearchVisible = isListSearchVisible,
                    navigateToCampsiteDetails = { index ->
                        navViewModel.put(CurrentScreen.CampsiteDetails(index))
                    },
                    navigateToCampsitesMap = { index ->
                        navViewModel.put(CurrentScreen.CampsitesMap(index))
                    },
                    navigateUp = { navViewModel.home() },
                    onToggleSearch = onToggleListSearch,
                    windowSize = windowSize.widthSizeClass
                )
            }
            entry<CurrentScreen.CampsiteDetails>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { currentScreen ->
                CampsiteDetailsScreen(
                    initialPage = currentScreen.index,
                    isSearchVisible = isDeetSearchVisible,
                    navigateToCampsiteDetails = { index ->
                        navViewModel.pop()
                        navViewModel.put(CurrentScreen.CampsiteDetails(index))
                    },
                    navigateToCampsitesMap = { index ->
                        navViewModel.put(CurrentScreen.CampsitesMap(index))
                    },
                    navigateUp = {
                        isDeetSearchVisible = false
                        navViewModel.pop()
                    },
                    onToggleSearch = onToggleDeetSearch,
                    paddingValues = paddingValues,
                    windowSize = windowSize.widthSizeClass
                )
            }
            entry<CurrentScreen.CampsiteSingle> { currentScreen ->
                CampsiteSingleScreen(
                    itemId = currentScreen.itemId,
                    navigateToSingleMap = { assetId ->
                        navViewModel.put(
                            CurrentScreen.SingleMap(
                                assetId = assetId,
                                itemType = AssetType.Campsite
                            )
                        )
                    },
                    navigateUp = { navViewModel.pop() },
                    paddingValues = paddingValues
                )
            }
            entry<CurrentScreen.CampsitesMap> { currentScreen ->
                CampsitesMapScreen(
                    itemIndex = currentScreen.index,
                    navigateUp = { navViewModel.pop() }
                )
            }
            entry<CurrentScreen.FavesScreen> { currentScreen ->
                FavesListScreen(
                    navigateToCampsiteSingle = { itemId ->
                        navViewModel.put(CurrentScreen.CampsiteSingle(itemId))
                    },
                    navigateToHutSingle = { itemId ->
                        navViewModel.put(CurrentScreen.HutSingle(itemId))
                    },
                    navigateToMultiMap = {
                        navViewModel.put(CurrentScreen.MultiMap)
                    },
                    navigateToSingleMap = { assetId, itemType ->
                        navViewModel.put(
                            CurrentScreen.SingleMap(
                                assetId = assetId,
                                itemType = itemType
                            )
                        )
                    },
                    navigateToTrackSingle = { itemId ->
                        navViewModel.put(CurrentScreen.TrackSingle(itemId))
                    },
                    navigateUp = { navViewModel.pop() }
                )
            }
            entry<CurrentScreen.HelpScreen> { currentScreen ->
                HelpScreen(navigateUp = { navViewModel.pop() })
            }
            entry<CurrentScreen.HutsList>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        ContentPlaceholder(
                            drawableId = R.drawable.purity_hut,
                            stringId =
                                R.string.details_placeholder_hut
                        )
                    }
                )
            ) {
                HutsListScreen(
                    isSearchVisible = isListSearchVisible,
                    navigateToHutDetails = { index ->
                        navViewModel.put(CurrentScreen.HutDetails(index))
                    },
                    navigateToHutsMap = { index ->
                        navViewModel.put(CurrentScreen.HutsMap(index))
                    },
                    navigateUp = { navViewModel.home() },
                    onToggleSearch = onToggleListSearch,
                    windowSize = windowSize.widthSizeClass
                )
            }
            entry<CurrentScreen.HutDetails>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { currentScreen ->
                HutDetailsScreen(
                    initialPage = currentScreen.index,
                    isSearchVisible = isDeetSearchVisible,
                    navigateToHutDetails = { index ->
                        navViewModel.pop()
                        navViewModel.put(CurrentScreen.HutDetails(index))
                    },
                    navigateToHutsMap = { index ->
                        navViewModel.put(CurrentScreen.HutsMap(index))
                    },
                    navigateUp = {
                        isDeetSearchVisible = false
                        navViewModel.pop()
                    },
                    onToggleSearch = onToggleDeetSearch,
                    paddingValues = paddingValues,
                    windowSize = windowSize.widthSizeClass
                )
            }
            entry<CurrentScreen.HutSingle> { currentScreen ->
                HutSingleScreen(
                    itemId = currentScreen.itemId,
                    navigateToSingleMap = { assetId ->
                        navViewModel.put(
                            CurrentScreen.SingleMap(
                                assetId = assetId,
                                itemType = AssetType.Hut
                            )
                        )
                    },
                    navigateUp = { navViewModel.pop() },
                    paddingValues = paddingValues
                )
            }
            entry<CurrentScreen.HutsMap> { currentScreen ->
                HutsMapScreen(
                    itemIndex = currentScreen.index,
                    navigateUp = { navViewModel.pop() }
                )
            }
            entry<CurrentScreen.MultiMap> {
                MultiMapScreen(navigateUp = { navViewModel.pop() })
            }
            entry<CurrentScreen.SingleMap> { currentScreen ->
                SingleMapScreen(
                    assetId = currentScreen.assetId,
                    itemType = currentScreen.itemType,
                    navigateUp = { navViewModel.pop() }
                )
            }
            entry<CurrentScreen.TracksList>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        ContentPlaceholder(
                            drawableId = R.drawable.kepler_track,
                            stringId =
                                R.string.details_placeholder_track
                        )
                    }
                )
            ) {
                TracksListScreen(
                    isSearchVisible = isListSearchVisible,
                    navigateToTrackDetails = { index ->
                        navViewModel.put(CurrentScreen.TrackDetails(index))
                    },
                    navigateToTracksMap = { index ->
                        navViewModel.put(CurrentScreen.TracksMap(index))
                    },
                    navigateUp = { navViewModel.home() },
                    onToggleSearch = onToggleListSearch,
                    windowSize = windowSize.widthSizeClass
                )
            }
            entry<CurrentScreen.TrackDetails>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { currentScreen ->
                TrackDetailsScreen(
                    initialPage = currentScreen.index,
                    isSearchVisible = isDeetSearchVisible,
                    navigateToTrackDetails = { index ->
                        navViewModel.pop()
                        navViewModel.put(CurrentScreen.TrackDetails(index))
                    },
                    navigateToTracksMap = { index ->
                        navViewModel.put(CurrentScreen.TracksMap(index))
                    },
                    navigateUp = {
                        isDeetSearchVisible = false
                        navViewModel.pop()
                    },
                    onToggleSearch = onToggleDeetSearch,
                    paddingValues = paddingValues,
                    windowSize = windowSize.widthSizeClass
                )
            }
            entry<CurrentScreen.TrackSingle> { currentScreen ->
                TrackSingleScreen(
                    itemId = currentScreen.itemId,
                    navigateToSingleMap = { assetId ->
                        navViewModel.put(
                            CurrentScreen.SingleMap(
                                assetId = assetId,
                                itemType = AssetType.Track
                            )
                        )
                    },
                    navigateUp = { navViewModel.pop() },
                    paddingValues = paddingValues
                )
            }
            entry<CurrentScreen.TracksMap> { currentScreen ->
                TracksMapScreen(
                    itemIndex = currentScreen.index,
                    navigateUp = { navViewModel.pop() }
                )
            }
        }
    )

    if (
        qtyAlertsDL != -1
        &&
        qtyCampsitesDL != -1
        &&
        qtyHutsDL != -1
        &&
        qtyTracksDL != -1
    ) {
        homeViewModel.setHomeScreenState(HomeScreenState.Ready)
    }
}