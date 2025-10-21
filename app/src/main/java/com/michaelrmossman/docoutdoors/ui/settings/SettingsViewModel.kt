package com.michaelrmossman.docoutdoors.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.michaelrmossman.docoutdoors.OutdoorsApplication
import com.michaelrmossman.docoutdoors.data.AlertsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.CampsitesNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.CampsitesOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.HutsNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.HutsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.MapsRepoBase
import com.michaelrmossman.docoutdoors.data.SettingsRepoBase
import com.michaelrmossman.docoutdoors.data.TracksNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.TracksOfflineRepoBase
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.model.RegionEntity
import com.michaelrmossman.docoutdoors.utils.BATCH_DOWNLOAD_DELAY
import com.michaelrmossman.docoutdoors.utils.ON_SETTINGS_SAVED_DELAY
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_BY_LATEST
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_AUTOMATIC
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_LTE_OKAY
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_SHOW_NOTIF
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_WAIT_CHRG
import com.michaelrmossman.docoutdoors.utils.PREF_CAMPSITES_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_CAMPSITES_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_BOOKABLE
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_DOGS_BY
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_SATELLITE_VIEW
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_SHOW_LOCATION
import com.michaelrmossman.docoutdoors.utils.PREF_HUTS_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_HUTS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_ZOOM_ON_DLOAD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val alertsAutoUpd: Int   = 0,
    val alertsUpdOkLTE: Int  = 0,
    val alertsUpdWtChrg: Int = 0,
    val regions: List<RegionEntity> = emptyList(),
    val settingsChanged: Int = 0
)

class SettingsViewModel(
    private val alertsRepository    : AlertsOfflineRepoBase,
    private val applicationScope    : CoroutineScope,
    private val campsitesNetworkRepo: CampsitesNetworkRepoBase,
    private val campsitesOfflineRepo: CampsitesOfflineRepoBase,
    private val hutsNetworkRepo     : HutsNetworkRepoBase,
    private val hutsOfflineRepo     : HutsOfflineRepoBase,
    private val mapsRepository      : MapsRepoBase,
    private val settingsRepository  : SettingsRepoBase,
    private val tracksNetworkRepo   : TracksNetworkRepoBase,
    private val tracksOfflineRepo   : TracksOfflineRepoBase
) : ViewModel() {

    /* Live Data */

    val alertsAutoUpdate: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_ALERTS_UPD_AUTOMATIC
    ).asLiveData()

    val alertsFilteredBy: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_ALERTS_FILTER_BY
    ).asLiveData()

    val alertsSortByLatest: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_ALERTS_BY_LATEST
    ).asLiveData()

    val alertsUpdAllowLTE: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_ALERTS_UPD_LTE_OKAY
    ).asLiveData()

    val alertsUpdShowNotif: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_ALERTS_UPD_SHOW_NOTIF
    ).asLiveData()

    val alertsUpdWaitChrg: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_ALERTS_UPD_WAIT_CHRG
    ).asLiveData()

    val campsitesDownloadAll: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_CAMPSITES_DLOAD_ALL
    ).asLiveData()

    val campsitesFilteredBy: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_CAMPSITES_FILTER_BY
    ).asLiveData()

    val commonFilterBookable: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_COMMON_FILTER_BOOKABLE
    ).asLiveData()

    val commonFilterDogsBy: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_COMMON_FILTER_DOGS_BY
    ).asLiveData()

    val commonShowLocation: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_COMMON_SHOW_LOCATION
    ).asLiveData()

    val commonSatelliteView: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_COMMON_SATELLITE_VIEW
    ).asLiveData()

    val hutsDownloadAll: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_HUTS_DLOAD_ALL
    ).asLiveData()

    val hutsFilteredBy: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_HUTS_FILTER_BY
    ).asLiveData()

    val tracksDownloadAll: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_TRACKS_DLOAD_ALL
    ).asLiveData()

    val tracksFilteredBy: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_TRACKS_FILTER_BY
    ).asLiveData()

    val tracksZoomOnDload: LiveData<Int> = settingsRepository.getSettingById(
        id = PREF_TRACKS_ZOOM_ON_DLOAD
    ).asLiveData()

    /* SettingsUiState */

    private val _settingsUiState by lazy { MutableStateFlow(SettingsUiState()) }
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState
    init {
        viewModelScope.launch {
            _settingsUiState.update { currentState ->
                currentState.copy(
                    alertsAutoUpd = settingsRepository.getSettingById(
                        id = PREF_ALERTS_UPD_AUTOMATIC
                    ).first(),
                    alertsUpdOkLTE = settingsRepository.getSettingById(
                        id = PREF_ALERTS_UPD_LTE_OKAY
                    ).first(),
                    alertsUpdWtChrg = settingsRepository.getSettingById(
                        id = PREF_ALERTS_UPD_WAIT_CHRG
                    ).first(),
                    regions = settingsRepository.getRegionsList()
                )
            }
        }
    }

    /* Download functions, so if user enables downloadExtras
       AFTER regional items have ALREADY been downloaded ...
       note: each of these 3 uses applicationScope, to allow
       the download to complete, even after leaving settings */
    private fun downloadCampsiteExtrasIfReqd(filterBy: Int) {
        applicationScope.launch {
            val campsiteIds =
                campsitesOfflineRepo.getCampsiteIdsNotDownloaded(
                    filterBy = filterBy
                )
            // android.util.Log.d("HEY",campsiteIds.size.toString())
            if (campsiteIds.isNotEmpty()) {
                campsiteIds.forEach { itemId ->
                    /* DoC requests limited to 100 per second and/or 200
                       in a burst. It may seem weird having this at the
                       START of the loop, but that way it's within scope */
                    delay(BATCH_DOWNLOAD_DELAY) // 100ms
                    campsitesNetworkRepo.getCampsite(
                        id = itemId, callback = { response ->
                            when (response.responseCode) {
                                200 -> response.campsiteSerial?.let { campsite ->
                                    applicationScope.launch {
                                        campsitesOfflineRepo.updateCampsite(
                                            campsite = campsite
                                        )
                                    }
                                }
                                else -> {
                                    applicationScope.launch {
                                        updateAssetWithResponse(
                                            assetId = itemId,
                                            itemType = AssetType.Campsite,
                                            responseCode = response.responseCode
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun downloadHutExtrasIfReqd(filterBy: Int) {
        applicationScope.launch {
            val hutIds =
                hutsOfflineRepo.getHutIdsNotDownloaded(
                    filterBy = filterBy
                )
            // android.util.Log.d("HEY",hutIds.size.toString())
            if (hutIds.isNotEmpty()) {
                hutIds.forEach { itemId ->
                    /* Refer note in downloadCampsiteExtrasIfReqd() */
                    delay(BATCH_DOWNLOAD_DELAY) // 100ms
                    hutsNetworkRepo.getHut(
                        id = itemId, callback = { response ->
                            when (response.responseCode) {
                                200 -> response.hutSerial?.let { hut ->
                                    applicationScope.launch {
                                        hutsOfflineRepo.updateHut(
                                            hut = hut
                                        )
                                    }
                                }
                                else -> {
                                    applicationScope.launch {
                                        updateAssetWithResponse(
                                            assetId = itemId,
                                            itemType = AssetType.Hut,
                                            responseCode = response.responseCode
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun downloadTrackExtrasIfReqd(filterBy: Int) {
        applicationScope.launch {
            val trackIds =
                tracksOfflineRepo.getTrackIdsNotDownloaded(
                    filterBy = filterBy
                )
            // android.util.Log.d("HEY",trackIds.size.toString())
            if (trackIds.isNotEmpty()) {
                trackIds.forEach { itemId ->
                    /* Refer note in downloadCampsiteExtrasIfReqd() */
                    delay(BATCH_DOWNLOAD_DELAY) // 100ms
                    tracksNetworkRepo.getTrack(
                        id = itemId, callback = { response ->
                            when (response.responseCode) {
                                200 -> response.trackSerial?.let { track ->
                                    applicationScope.launch {
                                        tracksOfflineRepo.updateTrack(
                                            track = track
                                        )
                                    }
                                }
                                else -> {
                                    applicationScope.launch {
                                        updateAssetWithResponse(
                                            assetId = itemId,
                                            itemType = AssetType.Track,
                                            responseCode = response.responseCode
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    /* For resetAllSettings(), we also need to call MOST of the
       set*Functions in the relevant repo, but at the same time,
       skip saveSetting, as that's performed by THIS function */
    suspend fun resetAllSettings() {
        settingsRepository.getSettingsAll().forEach { setting ->
            when (setting.settingId) {
                PREF_ALERTS_BY_LATEST -> {
                    saveAlertSortPreference(
                        sortByLatest = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_ALERTS_FILTER_BY -> {
                    saveAlertFilterPreference(
                        filterByRegion = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_ALERTS_UPD_AUTOMATIC -> {
                    /* Note that this is the ONLY setting
                       that defaults to OFF when reset */
                    saveAlertsAutoUpdAllPrefs(
                        fromSettingsActivity = false
                    )
                }
                PREF_CAMPSITES_DLOAD_ALL -> {
                    saveCampsiteDownloadAllPref(
                        downloadAll = setting.setting,
                        filterByRegion = null,
                        fromSettingsActivity = false
                    )
                }
                PREF_CAMPSITES_FILTER_BY -> {
                    saveCampsiteFilterPreference(
                        filterByRegion = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_COMMON_FILTER_BOOKABLE -> {
                    saveCommonFilterBookable(
                        filterBookable = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_COMMON_FILTER_DOGS_BY -> {
                    saveCommonFilterDogsBy(
                        filterDogsBy = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_COMMON_SATELLITE_VIEW -> {
                    saveCommonSatelliteView(
                        satelliteView = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_COMMON_SHOW_LOCATION -> {
                    saveCommonShowLocation(
                        showLocation = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_HUTS_DLOAD_ALL -> {
                    saveHutDownloadAllPref(
                        downloadAll = setting.setting,
                        filterByRegion = null,
                        fromSettingsActivity = false
                    )
                }
                PREF_HUTS_FILTER_BY -> {
                    saveHutFilterPreference(
                        filterByRegion = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_TRACKS_DLOAD_ALL -> {
                    saveTrackDownloadAllPref(
                        downloadAll = setting.setting,
                        filterByRegion = null,
                        fromSettingsActivity = false
                    )
                }
                PREF_TRACKS_FILTER_BY -> {
                    saveTrackFilterPreference(
                        filterByRegion = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                PREF_TRACKS_ZOOM_ON_DLOAD -> {
                    saveTrackZoomOnDlPreference(
                        zoomOnDload = setting.setting,
                        fromSettingsActivity = false
                    )
                }
                else -> return@forEach
            }
        }
        settingsRepository.resetAllSettings()
    }

    private suspend fun resetUnsavedSettings(newState: Int) {
        /* On successfully saving the various Auto Update settings,
           change "settingsChanged" state to -1 or -2, depending on
           where this function is called from. If just saving, text
           for "Current Setting" momentarily turns green, but if
           called from unsetAutoUpdateWorker(), text turns red */
        setUnsavedSettings(newState)
        if (newState != 0) {
            delay(ON_SETTINGS_SAVED_DELAY) // 2_500ms
            setUnsavedSettings(0)
        }
   }

    fun saveAlertFilterPreference(
        filterByRegion: Int, fromSettingsActivity: Boolean = true
    ) {
        alertsRepository.setAlertsRegionCode(
            region = filterByRegion
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_ALERTS_FILTER_BY,
                    setting = filterByRegion
                )
            }
            /* Turn off auto-update, if alerts
               NO LONGER filtered by region */
            if (
                filterByRegion == 0
                &&
                settingsUiState.value.alertsAutoUpd != 0
            ) {
                unsetAutoUpdateWorker(newState = -2)
            }
        }
    }

    fun saveAlertSortPreference(
        sortByLatest: Int, fromSettingsActivity: Boolean = true
    ) {
        alertsRepository.setAlertsByLatest(
            byLatest = sortByLatest
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_ALERTS_BY_LATEST,
                    setting = sortByLatest
                )
            }
        }
    }

    fun saveAlertsAutoUpdAllPrefs(
        fromSettingsActivity: Boolean = true
    ) {
        if (fromSettingsActivity) {
            viewModelScope.launch {
                val previousSetting = alertsAutoUpdate.value
                if (
                    saveAlertsAutoUpdPreference().await() > 0
                    &&
                    saveAlertsUpdAllowLTEPref().await() > 0
                    &&
                    saveAlertsUpdWtChrgPref().await() > 0
                ) {
                    when (settingsUiState.value.alertsAutoUpd) {
                        0 -> unsetAutoUpdateWorker(newState = -2)
                        else -> {
                            setAutoUpdateWorker(
                                previousSetting = previousSetting
                            )
                        }
                    }
                    resetUnsavedSettings(newState = -1)
                }
            }

        } else unsetAutoUpdateWorker(newState = 0)
    }

    fun saveAlertsAutoUpdPreference(): Deferred<Int> = viewModelScope.async {
        settingsRepository.saveSetting(
            settingId = PREF_ALERTS_UPD_AUTOMATIC,
            setting = settingsUiState.value.alertsAutoUpd
        )
    }

    fun saveAlertsUpdAllowLTEPref(): Deferred<Int> = viewModelScope.async {
        settingsRepository.saveSetting(
            settingId = PREF_ALERTS_UPD_LTE_OKAY,
            setting = settingsUiState.value.alertsUpdOkLTE
        )
    }

    fun saveAlertsUpdShowNotifPref(showNotif: Int) {
        viewModelScope.launch {
            settingsRepository.saveSetting(
                settingId = PREF_ALERTS_UPD_SHOW_NOTIF,
                setting = showNotif
            )
        }
    }

    fun saveAlertsUpdWtChrgPref(): Deferred<Int> = viewModelScope.async {
        settingsRepository.saveSetting(
            settingId = PREF_ALERTS_UPD_WAIT_CHRG,
            setting = settingsUiState.value.alertsUpdWtChrg
        )
    }

    fun saveCampsiteDownloadAllPref(
        downloadAll: Int,
        filterByRegion: Int? = null,
        fromSettingsActivity: Boolean = true
    ) {
        campsitesOfflineRepo.setCampsitesAdvancedSearch(
            advSearch = downloadAll
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_CAMPSITES_DLOAD_ALL,
                    setting = downloadAll
                )
            }
            if (downloadAll > 0) {
                filterByRegion?.let { filterBy ->
                    downloadCampsiteExtrasIfReqd(filterBy)
                }
            }
        }
    }

    fun saveCampsiteFilterPreference(
        filterByRegion: Int, fromSettingsActivity: Boolean = true
    ) {
        campsitesOfflineRepo.setCampsitesRegionCode(
            region = filterByRegion
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_CAMPSITES_FILTER_BY,
                    setting = filterByRegion
                )
                if (filterByRegion == 0) {
                    saveCampsiteDownloadAllPref(downloadAll = 0)
                }
            }
        }
    }

    fun saveCommonFilterBookable(
        filterBookable: Int, fromSettingsActivity: Boolean = true
    ) {
        campsitesOfflineRepo.setCommonFilterBookable(
            filterBookable = filterBookable
        )
        hutsOfflineRepo.setCommonFilterBookable(
            filterBookable = filterBookable
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_COMMON_FILTER_BOOKABLE,
                    setting = filterBookable
                )
            }
        }
    }

    fun saveCommonFilterDogsBy(
        filterDogsBy: Int, fromSettingsActivity: Boolean = true
    ) {
        campsitesOfflineRepo.setCommonFilterDogsBy(
            filterDogsBy = filterDogsBy
        )
        tracksOfflineRepo.setCommonFilterDogsBy(
            filterDogsBy = filterDogsBy
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_COMMON_FILTER_DOGS_BY,
                    setting = filterDogsBy
                )
            }
        }
    }

    fun saveCommonSatelliteView(
        satelliteView: Int, fromSettingsActivity: Boolean = true
    ) {
        mapsRepository.setCommonSatelliteView(satelliteView)
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_COMMON_SATELLITE_VIEW,
                    setting = satelliteView
                )
            }
        }
    }

    fun saveCommonShowLocation(
        showLocation: Int, fromSettingsActivity: Boolean = true
    ) {
        mapsRepository.setCommonShowLocation(showLocation)
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_COMMON_SHOW_LOCATION,
                    setting = showLocation
                )
            }
        }
    }

    fun saveHutDownloadAllPref(
        downloadAll: Int,
        filterByRegion: Int? = null,
        fromSettingsActivity: Boolean = true
    ) {
        hutsOfflineRepo.setHutsAdvancedSearch(
            advSearch = downloadAll
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_HUTS_DLOAD_ALL,
                    setting = downloadAll
                )
            }
            if (downloadAll > 0) {
                filterByRegion?.let { filterBy ->
                    downloadHutExtrasIfReqd(filterBy)
                }
            }
        }
    }

    fun saveHutFilterPreference(
        filterByRegion: Int, fromSettingsActivity: Boolean = true
    ) {
        hutsOfflineRepo.setHutsRegionCode(
            region = filterByRegion
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_HUTS_FILTER_BY,
                    setting = filterByRegion
                )
                if (filterByRegion == 0) {
                    saveHutDownloadAllPref(downloadAll = 0)
                }
            }
        }
    }

    fun saveTrackDownloadAllPref(
        downloadAll: Int,
        filterByRegion: Int? = null,
        fromSettingsActivity: Boolean = true
    ) {
        tracksOfflineRepo.setTracksAdvancedSearch(
            advSearch = downloadAll
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_TRACKS_DLOAD_ALL,
                    setting = downloadAll
                )
            }
            if (downloadAll > 0) {
                filterByRegion?.let { filterBy ->
                    downloadTrackExtrasIfReqd(filterBy)
                }
            }
        }
    }

    fun saveTrackFilterPreference(
        filterByRegion: Int, fromSettingsActivity: Boolean = true
    ) {
        viewModelScope.launch {
            tracksOfflineRepo.setTracksRegionCode(
                region = filterByRegion
            )
        }
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_TRACKS_FILTER_BY,
                    setting = filterByRegion
                )
                if (filterByRegion == 0) {
                    saveTrackDownloadAllPref(downloadAll = 0)
                }
            }
        }
    }

    fun saveTrackZoomOnDlPreference(
        zoomOnDload: Int, fromSettingsActivity: Boolean = true
    ) {
        tracksOfflineRepo.setTracksZoomOnDload(
            zoomOnDload = zoomOnDload
        )
        if (fromSettingsActivity) {
            viewModelScope.launch {
                settingsRepository.saveSetting(
                    settingId = PREF_TRACKS_ZOOM_ON_DLOAD,
                    setting = zoomOnDload
                )
            }
        }
    }

    fun setAlertsAutoUpdPreference(alertsAutoUpd: Int) {
        _settingsUiState.update { currentState ->
            currentState.copy(
                alertsAutoUpd = alertsAutoUpd,
                settingsChanged = 1
            )
        }
    }

    fun setAlertsUpdAllowLTEPref(alertsUpdOkLTE: Int) {
        _settingsUiState.update { currentState ->
            currentState.copy(
                alertsUpdOkLTE = alertsUpdOkLTE,
                settingsChanged = 1
            )
        }
    }

    fun setAlertsUpdWtChrgPreference(alertsUpdWtChrg: Int) {
        _settingsUiState.update { currentState ->
            currentState.copy(
                alertsUpdWtChrg = alertsUpdWtChrg,
                settingsChanged = 1
            )
        }
    }

    fun setAutoUpdateWorker(
        /* If work was ALREADY set, replace it with updated
           params. Otherwise, just start a new work request */
        previousSetting: Int? = 1
    ) {
        alertsAutoUpdate.value?.let          { alertsAutoUpd ->
            alertsUpdAllowLTE.value?.let     { alertsUpdOkLTE ->
                alertsUpdWaitChrg.value?.let { alertsUpdWtChrg ->
                    settingsRepository.setAutoUpdateWorker(
                        interval = alertsAutoUpd,
                        network  = alertsUpdOkLTE,
                        update   = previousSetting == 1,
                        waitChrg = alertsUpdWtChrg
                    )
                }
            }
        }
    }

    fun setUnsavedSettings(changed: Int) {
        _settingsUiState.update { currentState ->
            currentState.copy(
                settingsChanged = changed
            )
        }
    }

    private suspend fun updateAssetWithResponse(
        assetId: String,
        itemType: AssetType,
        responseCode: Int
    ) {
        when (itemType) {
            AssetType.Campsite -> {
                campsitesOfflineRepo.updateCampsiteWithResponse(
                    assetId, responseCode
                )
            }
            AssetType.Hut -> {
                hutsOfflineRepo.updateHutWithResponse(
                    assetId, responseCode
                )
            }
            AssetType.Track -> {
                tracksOfflineRepo.updateTrackWithResponse(
                    assetId, responseCode
                )
            }
        }
    }

    fun unsetAutoUpdateWorker(newState: Int) {
        viewModelScope.launch {
            if (settingsRepository.saveSetting(
                settingId = PREF_ALERTS_UPD_AUTOMATIC,
                setting = 0
            ) > 0) {
                settingsRepository.unsetAutoUpdateWorker()
                resetUnsavedSettings(newState = newState)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application          = (this[APPLICATION_KEY] as OutdoorsApplication)
                val alertsRepository     = application.container.alertsOfflineRepo
                val applicationScope     = application.applicationScope
                val campsitesNetworkRepo = application.container.campsitesNetworkRepo
                val campsitesOfflineRepo = application.container.campsitesOfflineRepo
                val hutsNetworkRepo      = application.container.hutsNetworkRepo
                val hutsOfflineRepo      = application.container.hutsOfflineRepo
                val mapsRepository       = application.container.mapsRepo
                val settingsRepository   = application.container.settingsRepo
                val tracksNetworkRepo    = application.container.tracksNetworkRepo
                val tracksOfflineRepo    = application.container.tracksOfflineRepo
                SettingsViewModel(
                    alertsRepository     = alertsRepository,
                    applicationScope     = applicationScope,
                    campsitesNetworkRepo = campsitesNetworkRepo,
                    campsitesOfflineRepo = campsitesOfflineRepo,
                    hutsNetworkRepo      = hutsNetworkRepo,
                    hutsOfflineRepo      = hutsOfflineRepo,
                    mapsRepository       = mapsRepository,
                    settingsRepository   = settingsRepository,
                    tracksNetworkRepo    = tracksNetworkRepo,
                    tracksOfflineRepo    = tracksOfflineRepo
                )
            }
        }
    }
}