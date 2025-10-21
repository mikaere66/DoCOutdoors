package com.michaelrmossman.docoutdoors.ui.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AppSettingsAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import com.michaelrmossman.docoutdoors.OutdoorsApplication.Companion.instance
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.ui.components.BackButton
import com.michaelrmossman.docoutdoors.ui.components.LargeDropdownMenu
import com.michaelrmossman.docoutdoors.ui.components.SingleActionMenu
import com.michaelrmossman.docoutdoors.ui.components.SingleChoiceDialog
import com.michaelrmossman.docoutdoors.ui.theme.DoCOutdoorsTheme
import com.michaelrmossman.docoutdoors.utils.DEBUG_SETTINGS_GENERATE_RANDOM
import com.michaelrmossman.docoutdoors.utils.TextUtils.fontDimensionResource
import com.michaelrmossman.docoutdoors.utils.TextUtils.getStringFromArray
import com.michaelrmossman.docoutdoors.utils.WORK_MANAGER_UNIQUE_NAME
import com.michaelrmossman.docoutdoors.utils.fromHtml
import com.michaelrmossman.docoutdoors.utils.setEdgeToEdgeConfig
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

class SettingsActivity: ComponentActivity() {

    @OptIn(
        ExperimentalMaterial3WindowSizeClassApi::class,
        ExperimentalMaterial3AdaptiveApi::class,
        ExperimentalMaterial3Api::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)

        setContent {

            DoCOutdoorsTheme {

                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.Factory
                )
                val viewState by viewModel.settingsUiState.collectAsState()

                val alertsAutoUpdate by
                    viewModel.alertsAutoUpdate.observeAsState()
                val alertsFilteredBy by
                    viewModel.alertsFilteredBy.observeAsState()
                val alertsSortByLatest by
                    viewModel.alertsSortByLatest.observeAsState()
                val alertsUpdAllowLTE by
                    viewModel.alertsUpdAllowLTE.observeAsState()
                val alertsUpdShowNotif by
                    viewModel.alertsUpdShowNotif.observeAsState()
                val alertsUpdWaitChrg by
                    viewModel.alertsUpdWaitChrg.observeAsState()
                val campsitesDownloadAll by
                    viewModel.campsitesDownloadAll.observeAsState()
                val campsitesFilteredBy by
                    viewModel.campsitesFilteredBy.observeAsState()
                val commonFilterBookable by
                    viewModel.commonFilterBookable.observeAsState()
                val commonFilterDogsBy by
                    viewModel.commonFilterDogsBy.observeAsState()
                val commonSatelliteView by
                    viewModel.commonSatelliteView.observeAsState()
                val commonShowLocation by
                    viewModel.commonShowLocation.observeAsState()
                val coroutineScope = rememberCoroutineScope()
                val hutsDownloadAll by
                    viewModel.hutsDownloadAll.observeAsState()
                val hutsFilteredBy by viewModel.hutsFilteredBy.observeAsState()
                var showResetDialog by remember { mutableStateOf(false) }
                var showUnsavedDialog by remember { mutableStateOf(false) }
                val subtitleFontSize =
                    fontDimensionResource(R.dimen.subtitle_font_size)
                val tracksDownloadAll by
                    viewModel.tracksDownloadAll.observeAsState()
                val tracksFilteredBy by
                    viewModel.tracksFilteredBy.observeAsState()
                val tracksZoomOnDload by
                    viewModel.tracksZoomOnDload.observeAsState()

                BackHandler(
                    enabled = viewState.settingsChanged != 0
                ) {
                    showUnsavedDialog = true
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                BackButton(navigateUp = { finish() })
                            },
                            title = {
                                Column {
                                    Text(stringResource(R.string.app_name))
                                    Text(
                                        stringResource(R.string.settings_title),
                                        fontSize = subtitleFontSize
                                    )
                                }
                            },
                            actions = {
                                SingleActionMenu(
                                    onSingleItemClick = {
                                        showResetDialog = true
                                    },
                                    itemStringId = R.string.settings_restore
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor =
                                    MaterialTheme.colorScheme.inversePrimary
                            )
                        )
                    }
                ) { contentPadding ->

                    SettingsLayout(
                        alertsAutoUpdate = alertsAutoUpdate,
                        alertsFilteredBy = alertsFilteredBy,
                        alertsSortByLatest = alertsSortByLatest,
                        alertsUpdAllowLTE = alertsUpdAllowLTE,
                        alertsUpdShowNotif = alertsUpdShowNotif,
                        alertsUpdWaitChrg = alertsUpdWaitChrg,
                        campsitesDownloadAll = campsitesDownloadAll,
                        campsitesFilteredBy = campsitesFilteredBy,
                        commonFilterBookable = commonFilterBookable,
                        commonFilterDogsBy = commonFilterDogsBy,
                        commonSatelliteView = commonSatelliteView,
                        commonShowLocation = commonShowLocation,
                        contentPadding = contentPadding,
                        hutsDownloadAll = hutsDownloadAll,
                        hutsFilteredBy = hutsFilteredBy,
                        modifier = Modifier.padding(
                            top = dimensionResource(R.dimen.padding_medium),
                            start = dimensionResource(R.dimen.padding_medium),
                            end = dimensionResource(R.dimen.padding_medium),
                        ),
                        saveAlertsAutoUpdAllPrefs =
                            viewModel::saveAlertsAutoUpdAllPrefs,
                        setAlertsAutoUpdPreference =
                            viewModel::setAlertsAutoUpdPreference,
                        setAlertsUpdAllowLTEPref =
                            viewModel::setAlertsUpdAllowLTEPref,
                        saveAlertsUpdShowNotifPref =
                            viewModel::saveAlertsUpdShowNotifPref,
                        setAlertsUpdWtChrgPreference =
                            viewModel::setAlertsUpdWtChrgPreference,
                        saveAlertFilterPreference =
                            viewModel::saveAlertFilterPreference,
                        saveAlertSortPreference =
                            viewModel::saveAlertSortPreference,
                        saveCampsiteDownloadAllPref =
                            viewModel::saveCampsiteDownloadAllPref,
                        saveCampsiteFilterPreference =
                            viewModel::saveCampsiteFilterPreference,
                        saveCommonFilterBookable =
                            viewModel::saveCommonFilterBookable,
                        saveCommonFilterDogsBy =
                            viewModel::saveCommonFilterDogsBy,
                        saveCommonSatelliteView =
                            viewModel::saveCommonSatelliteView,
                        saveCommonShowLocation =
                            viewModel::saveCommonShowLocation,
                        saveHutDownloadAllPref =
                            viewModel::saveHutDownloadAllPref,
                        saveTrackDownloadAllPref =
                            viewModel::saveTrackDownloadAllPref,
                        saveHutFilterPreference =
                            viewModel::saveHutFilterPreference,
                        saveTrackFilterPreference =
                            viewModel::saveTrackFilterPreference,
                        saveTrackZoomOnDlPreference =
                            viewModel::saveTrackZoomOnDlPreference,
                        tracksDownloadAll = tracksDownloadAll,
                        tracksFilteredBy = tracksFilteredBy,
                        tracksZoomOnDload = tracksZoomOnDload,
                        unsetAutoUpdateWorker =
                            viewModel::unsetAutoUpdateWorker,
                        viewState = viewState
                    )

                    if (showResetDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                // Dismiss the dialog when the user clicks outside the
                                // dialog or on the back button. If you want to disable
                                // that functionality, simply use an empty onCloseRequest
                                showResetDialog = false
                            },
                            title = {
                                Text(
                                text = stringResource(
                                    R.string.settings_restore
                                ).plus("?"))
                            },
                            text = {
                                Text(
                                    text = stringResource(
                                        R.string.settings_message
                                    ),
                                    textAlign = TextAlign.Justify
                                )
                            },
                            dismissButton = {
                                TextButton (
                                    onClick = { showResetDialog = false }
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.common_dialog_cancel
                                        )
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showResetDialog = false
                                        finish()
                                        coroutineScope.launch {
                                            viewModel.resetAllSettings()
                                        }
                                    }
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.common_dialog_confirm
                                        )
                                    )
                                }
                            }
                        )
                    }

                    if (showUnsavedDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                // Dismiss the dialog when the user clicks outside the
                                // dialog or on the back button. If you want to disable
                                // that functionality, simply use an empty onCloseRequest
                                showUnsavedDialog = false
                            },
                            title = {
                                Text(text = stringResource(
                                    R.string.settings_unsaved_title
                                ))
                            },
                            text = {
                                Text(
                                    text = stringResource(
                                        R.string.settings_unsaved_msg
                                    ),
                                    textAlign = TextAlign.Justify
                                )
                            },
                            dismissButton = { // Remain
                                TextButton (
                                    onClick = { showUnsavedDialog = false }
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.settings_unsaved_go_back
                                        )
                                    )
                                }
                            },
                            confirmButton = { // Discard
                                TextButton(
                                    onClick = {
                                        showUnsavedDialog = false
                                        viewModel.setUnsavedSettings(0)
                                        finish()
                                    }
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.settings_unsaved_discard
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsLayout(
    alertsAutoUpdate: Int?,
    alertsFilteredBy: Int?,
    alertsSortByLatest: Int?,
    alertsUpdAllowLTE: Int?,
    alertsUpdShowNotif: Int?,
    alertsUpdWaitChrg: Int?,
    campsitesDownloadAll: Int?,
    campsitesFilteredBy: Int?,
    commonFilterBookable: Int?,
    commonFilterDogsBy: Int?,
    commonSatelliteView: Int?,
    commonShowLocation: Int?,
    contentPadding: PaddingValues,
    hutsDownloadAll: Int?,
    hutsFilteredBy: Int?,
    modifier: Modifier = Modifier,
    saveAlertsAutoUpdAllPrefs: () -> Unit,
    setAlertsAutoUpdPreference: (Int) -> Unit,
    setAlertsUpdAllowLTEPref: (Int) -> Unit,
    saveAlertsUpdShowNotifPref: (Int) -> Unit,
    setAlertsUpdWtChrgPreference: (Int) -> Unit,
    saveAlertFilterPreference: (Int) -> Unit,
    saveAlertSortPreference: (Int) -> Unit,
    saveCampsiteDownloadAllPref: (Int, Int?) -> Unit,
    saveCampsiteFilterPreference: (Int) -> Unit,
    /* Bookable only applies to Campsite & Hut lists.
       Approx 193 of 327 campsites & 133 of 958 huts.
       59 percent & approx 14 percent respectively */
    saveCommonFilterBookable: (Int) -> Unit,
    /* Dog access only applies to Campsite and Track lists.
       Values are No Dogs|On Leash|With Permit|Dogs Allowed */
    saveCommonFilterDogsBy: (Int) -> Unit,
    saveCommonSatelliteView: (Int) -> Unit,
    saveCommonShowLocation: (Int) -> Unit,
    saveHutDownloadAllPref: (Int, Int?) -> Unit,
    saveHutFilterPreference: (Int) -> Unit,
    saveTrackDownloadAllPref: (Int, Int?) -> Unit,
    saveTrackFilterPreference: (Int) -> Unit,
    saveTrackZoomOnDlPreference: (Int) -> Unit,
    tracksDownloadAll: Int?,
    tracksFilteredBy: Int?,
    tracksZoomOnDload: Int?,
    unsetAutoUpdateWorker: (Int) -> Unit,
    viewState: SettingsUiState
) {
    val additionalPadding = dimensionResource(R.dimen.padding_small)
    val horizontalPadding = dimensionResource(R.dimen.padding_mini)
    val regionNames: MutableList<String> =
        viewState.regions.map { region -> region.regionName }.toMutableList()
    regionNames.add(0, stringResource(R.string.common_filter_by_none))
    val scrollState = rememberScrollState()
    val selectedPaddingHorizontal = dimensionResource(R.dimen.padding_mini)
    val selectedPaddingVertical = dimensionResource(R.dimen.padding_small)
    var showPermissionRequest by remember { mutableStateOf(false) }
    var showSystemSettings by remember { mutableStateOf(false) }
    val rowPaddingStart = dimensionResource(R.dimen.padding_mini)
    val rowPaddingTop = dimensionResource(R.dimen.padding_mini)

    Column(
        modifier = modifier
            .padding(
                bottom = contentPadding.calculateBottomPadding().plus(
                    additionalPadding
                ),
                end = contentPadding.calculateEndPadding(
                    LayoutDirection.Ltr
                ),
                start = contentPadding.calculateStartPadding(
                    LayoutDirection.Ltr
                ),
                top = contentPadding.calculateTopPadding()
            )
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_small)
        )
    ) {
        if (DEBUG_SETTINGS_GENERATE_RANDOM) {
            MessageItem(
                fontStyle = FontStyle.Italic,
                horizontalPadding = horizontalPadding,
                stringId = R.string.debug_random_msg
            )
        }

        alertsFilteredBy?.let { filteredBy ->

            alertsAutoUpdate?.let { autoUpdate ->

                val autoOptionStringId = when (viewState.settingsChanged) {
                    1    -> R.string.alerts_work_man_not_saved
                    else -> R.string.alerts_work_man_currently
                }
                var showAutoDialog by remember { mutableStateOf(false) }
                /* Note diff stringRes for AutoUpd (slightly shorter) */
                val showAutoText = stringResource(
                    autoOptionStringId,
                    getStringFromArray(
                        R.array.alerts_work_man_upd_interval,
                        when (filteredBy) {
                            0 -> 0
                            else -> autoUpdate
                        }
                    )
                )

                Column {
                    Row(
                        modifier = Modifier.padding(
                            start = rowPaddingStart
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1F),
                            text = stringResource(R.string.alerts_work_manager)
                        )
                        Button(
                            enabled = (filteredBy != 0),
                            onClick = { showAutoDialog = !showAutoDialog }
                        ) {
                            Text(stringResource(R.string.common_select_button))
                        }
                    }
                    Row(
                        modifier = Modifier.padding(
                            start = rowPaddingStart
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            color = when (viewState.settingsChanged) {
                                -2   -> Color.Red    // Unset
                                -1   -> Color.Green  // Saved
                                 0   -> MaterialTheme.colorScheme.onSurface
                                else -> Color.Yellow // Changed
                            },
                            modifier = Modifier.padding(
                                end = selectedPaddingHorizontal,
                                start = selectedPaddingHorizontal,
                                top = selectedPaddingVertical
                            ).weight(1F),
                            text = showAutoText
                        )
                        Button(
                            enabled = viewState.settingsChanged > 0,
                            onClick = { saveAlertsAutoUpdAllPrefs() }
                        ) {
                            Text(stringResource(R.string.alerts_work_man_save))
                        }
                    }

                    if (
                        (autoUpdate != 0 && filteredBy != 0)
                        ||
                        (filteredBy != 0 && viewState.settingsChanged > 0)
                    ) {

                        if (autoUpdate == 1) {
                            var workManStringId by remember { mutableIntStateOf(0) }

                            val workManager = WorkManager.getInstance(instance)
                            val workInfosFuture: ListenableFuture<List<WorkInfo>> =
                                workManager.getWorkInfosForUniqueWork(
                                    WORK_MANAGER_UNIQUE_NAME
                                )

                            try {
                                val workInfos: List<WorkInfo> = workInfosFuture.get()
                                workInfos.forEach { workInfo ->
                                    workManStringId = when (workInfo.state) {
                                        WorkInfo.State.ENQUEUED -> {
                                            R.string.alerts_work_man_enqu
                                        }
                                        WorkInfo.State.RUNNING -> {
                                            R.string.alerts_work_man_runs
                                        }
                                        WorkInfo.State.SUCCEEDED -> {
                                            R.string.alerts_work_man_succ
                                        }
                                        WorkInfo.State.FAILED -> {
                                            R.string.alerts_work_man_fail
                                        }
                                        WorkInfo.State.CANCELLED -> {
                                            R.string.alerts_work_man_canc
                                        }
                                        WorkInfo.State.BLOCKED -> {
                                            R.string.alerts_work_man_bloc
                                        }
                                    }
                                }

                            } catch (e: ExecutionException) {
                                e.printStackTrace()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }

                            if (workManStringId != 0) {
                                MessageItem(
                                    horizontalPadding = dimensionResource(
                                        R.dimen.padding_small
                                    ),
                                    verticalPadding = dimensionResource(
                                        R.dimen.padding_mini
                                    ),
                                    stringId = workManStringId
                                )
                            }
                        }

                        MessageItem(
                            fontWeight = FontWeight.Bold,
                            horizontalPadding = horizontalPadding,
                            verticalPadding = dimensionResource(
                                R.dimen.padding_medium
                            ),
                            stringId = R.string.alerts_work_man_options
                        )

                        alertsUpdAllowLTE?.let { allowLTE ->
                            SwitchItem(
                                onSwitch = setAlertsUpdAllowLTEPref,
                                rowPaddingStart = rowPaddingStart,
                                /* Note slightly larger padding,
                                   compared to other switches */
                                rowPaddingTop =  rowPaddingTop.plus(
                                    dimensionResource(R.dimen.padding_mini)
                                ),
                                stringId = R.string.alerts_work_man_lte,
                                switchedOn = allowLTE
                            )
                        }
                        alertsUpdShowNotif?.let { showNotif ->
                            SwitchItem(
                                onSwitch = saveAlertsUpdShowNotifPref,
                                rowPaddingStart = rowPaddingStart,
                                rowPaddingTop =  rowPaddingTop,
                                stringId = R.string.alerts_work_man_allow_notif,
                                switchedOn = showNotif
                            )

                            if (showNotif > 0) {
                                if (
                                    Build.VERSION.SDK_INT
                                    >=
                                    Build.VERSION_CODES.TIRAMISU
                                ) {
                                    SystemRequestNotificationPermission()
                                }
                            }
                        }

                        alertsUpdWaitChrg?.let { waitChrg ->
                            SwitchItem(
                                onSwitch = setAlertsUpdWtChrgPreference,
                                rowPaddingStart = rowPaddingStart,
                                rowPaddingTop = rowPaddingTop,
                                stringId = R.string.alerts_work_man_chg,
                                switchedOn = waitChrg
                            )
                        }
                    }

                    if (showAutoDialog) {
                        val stringArray = stringArrayResource(
                            R.array.alerts_work_man_upd_interval
                        )
                        SingleChoiceDialog(
                            defaultSelected = autoUpdate,
                            dialogTitle = stringResource(
                                R.string.alerts_work_man_title
                            ),
                            onDismissRequest = { showAutoDialog = false },
                            onSubmitButtonClick = { selected ->
                                showAutoDialog = false
                                when (selected) {
                                    0    -> unsetAutoUpdateWorker(-2)
                                    else -> setAlertsAutoUpdPreference(
                                        selected
                                    )
                                }
                            },
                            optionsList = stringArray,
                            optionalMessage = stringResource(
                                R.string.alerts_work_man_msg
                            ).fromHtml()
                        )
                    }
                }
            }

            FilterItem(
                filterByStringId = R.string.alerts_filter_by,
                filteredBy = filteredBy,
                regions = regionNames,
                saveFilterPreference = saveAlertFilterPreference,
                /* This is the only drop-down menu with
                   a modifier and showDivider param */
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small)
                ),
                showDivider = false
            )
        }

        alertsSortByLatest?.let { sortByLatest ->
            SwitchItem(
                onSwitch = saveAlertSortPreference,
                rowPaddingStart = rowPaddingStart,
                rowPaddingTop = rowPaddingTop,
                stringId = R.string.alerts_sort_by_latest,
                switchedOn = sortByLatest
            )
        }

        commonFilterBookable?.let { filterBookable ->
            DividerItem()

            SwitchItem(
                onSwitch = saveCommonFilterBookable,
                rowPaddingStart = rowPaddingStart,
                rowPaddingTop = rowPaddingTop,
                stringId = R.string.common_filter_bookable,
                switchedOn = filterBookable
            )
        }

        campsitesFilteredBy?.let { filteredBy ->
            FilterItem(
                filterByStringId = R.string.campsites_filter_by,
                filteredBy = filteredBy,
                regions = regionNames,
                saveFilterPreference = saveCampsiteFilterPreference
            )

            campsitesDownloadAll?.let { downloadAll ->
                SwitchItem(
                    onSwitch = { pref ->
                        saveCampsiteDownloadAllPref(pref, filteredBy)
                    },
                    rowPaddingStart = rowPaddingStart,
                    rowPaddingTop = rowPaddingTop,
                    stringId = R.string.campsites_dload_all,
                    switchedOn = when (filteredBy) {
                        0 -> 0
                        else -> downloadAll
                    },
                    isEnabled = filteredBy > 0
                )
            }
        }

        commonFilterDogsBy?.let { filteredBy ->
            var selectedDogOption by remember {
                mutableIntStateOf(filteredBy)
            }
            var showDogsDialog by remember { mutableStateOf(false) }
            val showDogsText = stringResource(
                R.string.common_select_current,
                getStringFromArray(
                    R.array.filter_dogs_by,
                    selectedDogOption
                )
            )

            DividerItem()

            Column {
                Row(
                    modifier = Modifier.padding(
                        start = rowPaddingStart
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1F),
                        text = stringResource(R.string.common_filter_dogs)
                    )
                    Button(onClick = { showDogsDialog = !showDogsDialog }) {
                        Text(stringResource(R.string.common_select_button))
                    }
                }
                Text(
                    modifier = Modifier.padding(
                        end = selectedPaddingHorizontal,
                        start = selectedPaddingHorizontal,
                        top = selectedPaddingVertical
                    ),
                    text = showDogsText
                )
            }

            if (showDogsDialog) {
                val stringArray = stringArrayResource(R.array.filter_dogs_by)
                SingleChoiceDialog(
                    defaultSelected = selectedDogOption,
                    dialogTitle = stringResource(
                        R.string.common_filter_dogs_title
                    ),
                    onDismissRequest = { showDogsDialog = false },
                    onSubmitButtonClick = { selected ->
                        showDogsDialog = false
                        selectedDogOption = selected
                        saveCommonFilterDogsBy(
                            selected
                        )
                    },
                    optionsList = stringArray,
                    optionalMessage =
                        stringResource(R.string.filter_dogs_msg).fromHtml()
                )
            }
        }

        hutsFilteredBy?.let { filteredBy ->
            FilterItem(
                filterByStringId = R.string.huts_filter_by,
                filteredBy = filteredBy,
                regions = regionNames,
                saveFilterPreference = saveHutFilterPreference
            )

            hutsDownloadAll?.let { downloadAll ->
                SwitchItem(
                    onSwitch = { pref ->
                        saveHutDownloadAllPref(pref, filteredBy)
                    },
                    rowPaddingStart = rowPaddingStart,
                    rowPaddingTop = rowPaddingTop,
                    stringId = R.string.huts_dload_all,
                    switchedOn = when (filteredBy) {
                        0 -> 0
                        else -> downloadAll
                    },
                    isEnabled = filteredBy > 0
                )
            }
        }

        tracksFilteredBy?.let { filteredBy ->
            FilterItem(
                filterByStringId = R.string.tracks_filter_by,
                filteredBy = filteredBy,
                regions = regionNames,
                saveFilterPreference = saveTrackFilterPreference
            )

            tracksDownloadAll?.let { downloadAll ->
                SwitchItem(
                    onSwitch = { pref ->
                        saveTrackDownloadAllPref(pref, filteredBy)
                    },
                    rowPaddingStart = rowPaddingStart,
                    rowPaddingTop = rowPaddingTop,
                    stringId = R.string.tracks_dload_all,
                    switchedOn = when (filteredBy) {
                        0 -> 0
                        else -> downloadAll
                    },
                    isEnabled = filteredBy > 0
                )
            }
        }

        tracksZoomOnDload?.let { zoomOnDload ->
            SwitchItem(
                onSwitch = saveTrackZoomOnDlPreference,
                rowPaddingStart = rowPaddingStart,
                rowPaddingTop = rowPaddingTop,
                stringId = R.string.settings_map_zoom,
                switchedOn = zoomOnDload
            )
        }

        commonShowLocation?.let { showLocation ->
            DividerItem()

            Text(
                text = stringResource(R.string.settings_map_header),
                fontWeight = FontWeight.Bold
            )

            SwitchItem(
                rowPaddingStart = rowPaddingStart,
                rowPaddingTop = rowPaddingTop,
                stringId = R.string.settings_map_location,
                switchedOn = showLocation,
                onSwitch = { show ->
                    saveCommonShowLocation(show)
                    if (show > 0) {
                        showPermissionRequest = true
                    }
                }
            )

            if (showLocation > 0) {
                DetermineLocationPermissionState(
                    { state ->
                        val color = when (state) {
                            1 -> Color.Green  // Granted
                            0 -> Color.Yellow // Neither
                            else -> Color.Red // Denied (-1)
                        }
                        val imageVector = when (state) {
                            1 -> Icons.Outlined.Check
                            0 -> Icons.Outlined.Info
                            else -> Icons.Outlined.AppSettingsAlt
                        }
                        val onIconClick: ((Boolean) -> Unit)? =
                            when (state) {
                                 0 -> { show: Boolean ->
                                    showPermissionRequest = show
                                 }
                                -1 -> { show: Boolean ->
                                    showSystemSettings = show
                                }
                                else -> null /* Do nothing if Granted */
                            }
                        val stringId = when (state) {
                            1 -> R.string.location_permission_granted
                            0 -> R.string.location_permission_unknown
                            else -> R.string.location_permission_denied
                        }

                        StatusItem(
                            color = color,
                            imageVector = imageVector,
                            onIconClick = onIconClick,
                            stringId = stringId
                        )
                    }
                )
            }

            if (showPermissionRequest) {
                showPermissionRequest = false
                SystemRequestLocationPermission()
            }

            if (showSystemSettings) {
                showSystemSettings = false
                SystemShowAppSettings()
            }
        }

        commonSatelliteView?.let { satellite ->
            SwitchItem(
                onSwitch = saveCommonSatelliteView,
                rowPaddingStart = rowPaddingStart,
                rowPaddingTop = rowPaddingTop,
                stringId = R.string.settings_map_type,
                switchedOn = satellite
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DetermineLocationPermissionState(
    onPermissionState: @Composable (Int) -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            /* Later versions of Android require BOTH fine AND coarse */
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    when {
        permissionState.allPermissionsGranted -> {
            onPermissionState(1) // Granted
        }
        permissionState.shouldShowRationale -> {
            onPermissionState(0) // Neither
        }
        !permissionState.allPermissionsGranted
        &&
        !permissionState.shouldShowRationale -> {
            onPermissionState(-1) // Denied
        }
    }
}

@Composable
fun DividerItem() {
    val dividerPadding = dimensionResource(R.dimen.padding_small)
    HorizontalDivider(
        modifier = Modifier.padding(
            vertical = dividerPadding
        )
    )
}

@Composable
fun FilterItem(
    @StringRes filterByStringId: Int,
    filteredBy: Int,
    regions: List<String>,
    saveFilterPreference: (Int) -> Unit,
    modifier: Modifier = Modifier,
    /* Each drop-down menu has a divider
       above it, EXCEPT the first one */
    showDivider: Boolean = true
) {
    val horizontalPadding = dimensionResource(R.dimen.padding_mini)
    var regionIndex by remember {
        mutableIntStateOf(filteredBy)
    }
    if (showDivider) { DividerItem() }
    Text(
        text = stringResource(filterByStringId),
        modifier = modifier.padding(start = horizontalPadding),
        fontWeight = FontWeight.Bold
    )
    LargeDropdownMenu(
        label = stringResource(R.string.label_filter_by),
        items = regions,
        selectedIndex = regionIndex,
        onItemSelected = { index, _ ->
            regionIndex = index
            saveFilterPreference(index)
        },
        modifier = Modifier.padding(start = horizontalPadding)
    )
}

@Composable
fun MessageItem(
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight = FontWeight.Normal,
    horizontalPadding: Dp,
    verticalPadding: Dp = 0.dp,
    @StringRes stringId: Int
) {
    Text(
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = horizontalPadding,
                start = horizontalPadding,
                top = verticalPadding
            ),
        text = stringResource(stringId)
    )
}

@Composable
fun StatusItem(
    color: Color,
    imageVector: ImageVector,
    onIconClick: ((Boolean) -> Unit)?,
    @StringRes stringId: Int,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
             onIconClick?.let { onClick ->
                onClick(true)
             }
        }) {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        }
        Text(
            color = color,
            modifier = modifier.weight(1F),
            text = stringResource(stringId)
        )
    }
}

@Composable
fun SwitchItem(
    onSwitch: (Int) -> Unit,
    rowPaddingStart: Dp,
    rowPaddingTop: Dp,
    @StringRes stringId: Int,
    switchedOn: Int,
    isEnabled: Boolean = true
) {
    var switchItemSwitchedOn by remember {
        /* Allow for random debug vals */
        mutableStateOf(switchedOn > 0)
    }
    Row(
        modifier = Modifier.padding(
            start = rowPaddingStart,
            top = rowPaddingTop
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1F),
            text = stringResource(stringId).fromHtml()
        )
        Switch(
            checked = switchItemSwitchedOn,
            enabled = isEnabled,
            onCheckedChange = { isChecked ->
                switchItemSwitchedOn = isChecked
                onSwitch(
                    when (isChecked) {
                        true -> 1
                        else -> 0
                    }
                )
            },
            thumbContent = when (switchItemSwitchedOn) {
                false -> null
                else -> {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(
                                SwitchDefaults.IconSize
                            )
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SystemRequestLocationPermission() {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SystemRequestNotificationPermission() {
    val permissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
}

@Composable
fun SystemShowAppSettings() {
    val context = LocalContext.current
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    ).apply {
        data = Uri.fromParts(
            "package", context.packageName,null
        )
    }
    context.startActivity(intent)
}