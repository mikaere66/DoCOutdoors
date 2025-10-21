package com.michaelrmossman.docoutdoors.ui.alerts

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.michaelrmossman.docoutdoors.OutdoorsApplication
import com.michaelrmossman.docoutdoors.data.AlertsNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.AlertsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.RegionsRepoBase
import com.michaelrmossman.docoutdoors.data.SettingsRepoBase
import com.michaelrmossman.docoutdoors.enums.ByRegionType
import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.interfaces.AlertsUiState
import com.michaelrmossman.docoutdoors.interfaces.DownloadState
import com.michaelrmossman.docoutdoors.model.AlertSerial
import com.michaelrmossman.docoutdoors.utils.BATCH_DOWNLOAD_DELAY
import com.michaelrmossman.docoutdoors.utils.DEBUG_VIEW_MODELS_DOWNLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.TextUtils.getSearchResultWithExtras
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class AlertsViewModel(
    private val networkRepository: AlertsNetworkRepoBase,
    private val offlineRepository: AlertsOfflineRepoBase,
    private val regionsRepository: RegionsRepoBase,
    private val settingsRepository: SettingsRepoBase
) : ViewModel() {

    val alertsFilterByRegion: LiveData<String> =
        offlineRepository.alertsFilterByRegion.asLiveData()

    val alertsListIncomplete: LiveData<Boolean> =
        offlineRepository.alertsListIncomplete.asLiveData()

    /** The mutable State that stores the status of the most recent request */
    private val _alertsListState by lazy { MutableStateFlow(AlertsListState()) }
    val alertsListState: StateFlow<AlertsListState> = _alertsListState

    var downloadState: DownloadState by mutableStateOf(DownloadState.None)
        private set

    // Get affected assets for all alerts [debug only]
    private suspend fun downloadAll(alertsList: List<AlertSerial>) {
        try {
            alertsList.forEach { alert ->
                downloadExtras(
                    itemId = alert.id,
                    standAlone = false
                )
                /* DoC requests limited to 100 per
                   second and/or 200 in a burst */
                delay(BATCH_DOWNLOAD_DELAY) // 100ms
            }

        } catch (exception: IOException) {
            println(exception)
        }
    }

    // Get affected assets individually
    fun downloadExtras(
        itemId: String, standAlone: Boolean = true /* Can be batch: see above */
    ) {
        if (standAlone) {
            downloadState = DownloadState.Loading
        }
        try {
            viewModelScope.launch {
                networkRepository.getAlert(id = itemId, callback = { response ->
                    when (response.responseCode) {
                        200 -> response.alertSerial?.let { alertSerial ->
                            if (standAlone) {
                                downloadState = DownloadState.Done
                            }
                            updateAlert(
                                alertSerial = alertSerial,
                                id = itemId,
                                responseCode = response.responseCode,
                                standAlone = standAlone
                            )
                        }
                        else -> {
                            downloadState = when (response.responseCode) {
                                404  -> DownloadState.NotFound
                                else -> DownloadState.Error
                            }
                            updateAlertWithResponse(
                                id = itemId,
                                responseCode = response.responseCode
                            )
                        }
                    }
                })
            }

        } catch (exception: IOException) {
            downloadState = DownloadState.Error
            println(exception)
        }
    }

    fun getAlertIndex(itemId: String): Int {
        val alerts = alertsListState.value.alertsList
        return alerts.indexOf(
            alerts.find { alert ->
                alert.id == itemId
            }
        )
    }

    fun getAlertsHashMap(): HashMap<String, String> {
        return alertsListState.value.alertsList.associateBy(
            keySelector    = { alert -> alert.id },
            valueTransform = {
                alert -> when (alertsListState.value.searchBy) {
                    SearchBy.Name -> alert.summary
                    /* Facilities does not apply to Alerts */
                    else -> getSearchResultWithExtras(
                        alert.summary, alert.description
                    )
                }
            }
        ) as HashMap<String, String>
    }

    /**
     * Call getAlerts() on init so we can display status immediately
     */
    init {
        getAllAlerts(
            reset       = DEBUG_VIEW_MODELS_DOWNLOAD_ALL,
            allAffected = DEBUG_VIEW_MODELS_DOWNLOAD_ALL
        )
    }

    /**
     * Gets alerts information from the Outdoors API Retrofit service
     */
    fun getAllAlerts(reset: Boolean, allAffected: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                async { offlineRepository.deleteAllAlerts() }.await()
            }

            offlineRepository.getAlertCountAll().collect { alertCount ->
                // If database alerts table is not empty, use them
                if (alertCount > 0) {
                    updateAlertsUiState()

                } else {
                    _alertsListState.update { currentState ->
                        currentState.copy(
                            alertState = AlertsUiState.Downloading
                        )
                    }
                    // Else, get alerts from DoC API and add to DB
                    try {
                        val filterBy = settingsRepository.getSettingById(
                            id = PREF_ALERTS_FILTER_BY
                        )
                        val regionId = filterBy.first()
                        networkRepository.getAllAlerts(id = when (regionId) {
                            0 -> null
                            else -> regionsRepository.getRegionCodeByActualId(
                                id = regionId,
                                // Refer note in RegionsRepository
                                byRegionType = ByRegionType.Alerts
                            )
                        },
                        callback = { response ->
                            when (response.responseCode) {
                                200 -> upsertAlerts(
                                    alertsList = response.alertsList,
                                    allAffected = allAffected,
                                    regionId = regionId,
                                    reset = reset
                                )
                                else -> {
                                    _alertsListState.update { currentState ->
                                        currentState.copy(
                                            alertState = when (
                                                response.responseCode
                                            ) {
                                                403  -> AlertsUiState.Forbidden
                                                else -> AlertsUiState.Error
                                            }
                                        )
                                    }
                                }
                            }
                        })

                    } catch (exception: IOException) {
                        _alertsListState.update { currentState ->
                            currentState.copy(
                                alertState = AlertsUiState.Error
                            )
                        }
                        println(exception)
                    }
                }
            }
        }
    }

    /* Update download state, to reset. Called
       from either ListScreen or DetailsScreen
       depending on WindowWidthSizeClass */
    fun resetDownloadState() {
        downloadState = DownloadState.None
    }

    fun setAlertsHashMap(searchBy: SearchBy) {
        _alertsListState.update { currentState ->
            currentState.copy(
                searchBy = searchBy
            )
        }
    }

    private fun updateAlert(
        alertSerial: AlertSerial,
        id: String,
        responseCode: Int,
        standAlone: Boolean
    ) {
        viewModelScope.launch {
            val originalAlert =
                offlineRepository.getAlertById(id = id)
            /* Don't update "updateMillis" value for
               new alert, as this causes the list to
               jump all over the place while viewing */
            if (offlineRepository.updateAlert(
                alert = alertSerial,
                responseCode = responseCode,
                updateMillis = originalAlert.updateMillis
            ) > 0) {
                if (standAlone) {
                    val updatedAlert = offlineRepository.getAlertById(
                        id = id
                    )
                    _alertsListState.update { currentState ->
                        currentState.copy(alertsList =
                                currentState.alertsList.map { alert ->
                                    when (alert.id) {
                                        id   -> updatedAlert
                                        else -> alert
                                    }
                                }
                        )
                    }
                }
            }
        }
    }

    private fun updateAlertWithResponse(
        id: String,
        responseCode: Int
    ) {
        viewModelScope.launch {
            offlineRepository.updateAlertWithResponse(
                id, responseCode
            )
        }
    }

    private suspend fun updateAlertsUiState() {
        offlineRepository.alertsFlow.collect { alerts ->
            _alertsListState.update { currentState ->
                currentState.copy(
                    alertsList = alerts,
                    alertState = AlertsUiState.Success
                )
            }
        }
    }

    private fun upsertAlerts(
        alertsList: List<AlertSerial>,
        allAffected: Boolean,
        regionId: Int,
        reset: Boolean
    ) {
        if (alertsList.isNotEmpty()) {

            viewModelScope.launch {
                /* Reset all alertsDload values to zero.
                   Refer to note in RegionsRepository */
                if (regionId == 0) {
                    regionsRepository.resetAlertsDload()
                }

                /* Also deletes records from Affected table */
                if (reset) {
                    async {
                        offlineRepository.deleteAllAlerts() // TODO
                    }.await()
                }

                val result: List<Long> =
                    offlineRepository.insertAlerts(
                        alertsList
                    )
                if (result.isNotEmpty()) {
                    if (allAffected) { /* For debug only */
                        Log.d("HEY", alertsList.size.toString())
                        downloadAll(alertsList = alertsList)
                    }

                    // Read back the alerts, with region(s)
                    updateAlertsUiState()
                }
            }

        } else {
            Log.d(TAG,"SocketTimeoutException|UnknownHost")
            _alertsListState.update { currentState ->
                currentState.copy(
                    alertState = AlertsUiState.Error
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as OutdoorsApplication)
                val networkRepository = application.container.alertsNetworkRepo
                val offlineRepository = application.container.alertsOfflineRepo
                val regionsRepository = application.container.regionsRepo
                val settingsRepository = application.container.settingsRepo
                AlertsViewModel(
                    networkRepository, offlineRepository,
                    regionsRepository, settingsRepository
                )
            }
        }
        const val TAG = "AlertsViewModel"
    }
}