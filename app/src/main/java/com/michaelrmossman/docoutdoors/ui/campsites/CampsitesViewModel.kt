package com.michaelrmossman.docoutdoors.ui.campsites

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
import com.google.android.gms.maps.model.LatLng
import com.michaelrmossman.docoutdoors.OutdoorsApplication
import com.michaelrmossman.docoutdoors.data.AlertsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.CampsitesNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.CampsitesOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.FavouritesRepoBase
import com.michaelrmossman.docoutdoors.data.SettingsRepoBase
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.interfaces.CampsitesUiState
import com.michaelrmossman.docoutdoors.interfaces.DownloadState
import com.michaelrmossman.docoutdoors.model.AlertExtra
import com.michaelrmossman.docoutdoors.model.CampsiteSerial
import com.michaelrmossman.docoutdoors.utils.BATCH_DOWNLOAD_DELAY
import com.michaelrmossman.docoutdoors.utils.DEBUG_VIEW_MODELS_DOWNLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.MapUtils.getLatLngBounds
import com.michaelrmossman.docoutdoors.utils.MapUtils.isValidCoords
import com.michaelrmossman.docoutdoors.utils.PREF_CAMPSITES_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.TextUtils.getSearchResultWithExtras
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class CampsitesViewModel(
    private val alertsOfflineRepo: AlertsOfflineRepoBase,
    private val favesRepository: FavouritesRepoBase,
    private val networkRepository: CampsitesNetworkRepoBase,
    private val offlineRepository: CampsitesOfflineRepoBase,
    private val settingsRepository: SettingsRepoBase
) : ViewModel() {

    val commonFilterByBookable =
        offlineRepository.commonFilterByBookable.asLiveData()

    val commonFilterByDogAccess =
        offlineRepository.commonFilterByDogAccess.asLiveData()

    val campsitesAdvancedSearch: LiveData<Int> =
        offlineRepository.campsitesAdvancedSearch.asLiveData()

    val campsitesFilterByRegion: LiveData<String> =
        offlineRepository.campsitesFilterByRegion.asLiveData()

    /** The mutable State that stores the status of the most recent request */
    private val _campsitesListState by lazy { MutableStateFlow(CampsitesListState()) }
    val campsitesListState: StateFlow<CampsitesListState> = _campsitesListState

    var downloadState: DownloadState by mutableStateOf(DownloadState.None)
        private set

    // Get extras for all campsites [debug only]
    private suspend fun downloadAll(campsitesList: List<CampsiteSerial>) {
        try {
            campsitesList.forEachIndexed { index, campsite ->
                downloadExtras(
                    itemId = campsite.assetId.toString(),
                    standAlone = false
                )
                /* DoC requests limited to 100 per
                   second and/or 200 in a burst */
                delay(BATCH_DOWNLOAD_DELAY) // 100ms
                Log.d(TAG,"$index: ${ campsite.assetId }")
            }

        } catch (exception: IOException) {
            Log.d(TAG,exception.toString())
        }
    }

    // Get campsite extras individually
    fun downloadExtras(
        itemId: String, standAlone: Boolean = true /* Can be batch: see above */
    ) {
        if (standAlone) {
            downloadState = DownloadState.Loading
        }
        try {
            viewModelScope.launch {
                networkRepository.getCampsite(
                    id = itemId, callback = { response ->
                        when (response.responseCode) {
                            200 -> response.campsiteSerial?.let { campsiteSerial ->
                                downloadState = DownloadState.Done
                                viewModelScope.launch {
                                    if (
                                        updateCampsite(
                                            campsite = campsiteSerial
                                        ).await() > 0
                                    ) {
                                        if (standAlone) {
                                            updateCampsitesList(itemId)
                                        }
                                    }
                                }
                            }
                            else -> {
                                downloadState = when (response.responseCode) {
                                    404  -> DownloadState.NotFound
                                    else -> DownloadState.Error
                                }
                                updateCampsiteWithResponse(
                                    assetId = itemId,
                                    responseCode = response.responseCode
                                )
                            }
                        }
                    }
                )
            }

        } catch (exception: IOException) {
            downloadState = DownloadState.Error
            Log.d(TAG,exception.toString())
        }
    }

    suspend fun getAlertById(id: String): AlertExtra =
        alertsOfflineRepo.getAlertExtraByAssetId(
            assetId = id, itemType = AssetType.Campsite
        )

    /**
     * Populate list on init so we can display status immediately,
     * gathering ALL info only IF items are filtered by region
     */
    init {
        getAllCampsites(
            reset     = DEBUG_VIEW_MODELS_DOWNLOAD_ALL,
            allExtras = DEBUG_VIEW_MODELS_DOWNLOAD_ALL
        )
    }

    fun getCampsiteIndex(itemId: String): Int {
        val campsites = campsitesListState.value.campsitesList
        return campsites.indexOf(
            campsites.find { campsite ->
                campsite.assetId == itemId
            }
        )
    }

    fun setCampsitesHashMap(searchBy: SearchBy) {
        _campsitesListState.update { currentState ->
            currentState.copy(
                searchBy = searchBy
            )
        }
    }

    fun getCampsitesHashMap(): HashMap<String, String> {
        return campsitesListState.value.campsitesList.associateBy(
            keySelector    = { campsite -> campsite.assetId },
            valueTransform = {
                campsite -> when (campsitesListState.value.searchBy) {
                    SearchBy.Name -> campsite.name
                    SearchBy.Feat -> getSearchResultWithExtras(
                        campsite.name, campsite.facilities
                    )
                    SearchBy.Desc -> getSearchResultWithExtras(
                        campsite.name, campsite.introduction
                    )
                }
            }
        ) as HashMap<String, String>
    }

    /**
     * Gets campsites information from the Outdoors API Retrofit service
     */
    fun getAllCampsites(reset: Boolean, allExtras: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                _campsitesListState.update { currentState ->
                    currentState.copy(
                        campsiteState = CampsitesUiState.Downloading
                    )
                }

                val filterBy = settingsRepository.getSettingById(
                    id = PREF_CAMPSITES_FILTER_BY
                )
                val regionId = filterBy.first()
                try {
                    // Get assets from DoC API and add to DB
                    networkRepository.getAllCampsites(callback = { response ->
                        when (response.responseCode) {
                            200 -> upsertCampsites(
                                allExtras = allExtras,
                                regionId = regionId,
                                campsitesList = response.campsitesList
                            )
                            else -> {
                                _campsitesListState.update { currentState ->
                                    currentState.copy(
                                        campsitesList = emptyList(),
                                        campsiteState = when (
                                            response.responseCode
                                        ) {
                                            403  -> CampsitesUiState.Forbidden
                                            else -> CampsitesUiState.Error
                                        }
                                    )
                                }
                            }
                        }
                    })

                } catch (exception: IOException) {
                    _campsitesListState.update { currentState ->
                        currentState.copy(
                            campsiteState = CampsitesUiState.Error
                        )
                    }
                    Log.d(TAG,exception.toString())
                }

            } else {
                offlineRepository.getCampsiteNameCount().collect { count ->
                    if (count > 0) {

                        updateCampsitesUiState()

                    } else getAllCampsites(reset = true)
                }
            }
        }
    }

    fun getCampsiteAlerts(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                alertsOfflineRepo.deleteAllAlertExtras(
                    itemType = AssetType.Campsite
                )
            }
            try {
                // Get alerts from DoC API and add to DB
                networkRepository.getCampsiteAlerts { campsiteAlerts ->

                    if (campsiteAlerts.isNotEmpty()) {
                        viewModelScope.launch {

                            alertsOfflineRepo.insertCSOrHutAlertExtras(
                                alertsList = campsiteAlerts,
                                itemType = AssetType.Campsite
                            )
                        }

                    } else {
                        Log.d(TAG,"2.SocketTimeoutException|UnknownHost")
                    }
                }

            } catch (exception: IOException) {
                Log.d(TAG,exception.toString())
            }
        }
    }

    /* Update download state, to reset. Called
       from either ListScreen or DetailsScreen
       depending on WindowWidthSizeClass */
    fun resetDownloadState() {
        downloadState = DownloadState.None
    }

    /* isFavourite refers to PREVIOUS state */
    fun toggleFavourite(
        assetId: String,
        isFavourite: Boolean
    ) {
        viewModelScope.launch {
            val result = when (isFavourite) {
                true -> favesRepository.deleteFaveByIdAndType(
                    assetId = assetId,
                    itemType = AssetType.Campsite.name
                )
                else -> favesRepository.insertFave(
                    assetId = assetId,
                    itemType = AssetType.Campsite.name
                ).toInt()
            }
            if (result != 0) {
                updateCampsitesList(itemId = assetId)
            }
        }
    }

    private fun updateCampsite(campsite: CampsiteSerial): Deferred<Int> =
        viewModelScope.async {
            offlineRepository.updateCampsite(campsite = campsite)
        }

    private fun updateCampsiteWithResponse(
        assetId: String,
        responseCode: Int
    ) {
        viewModelScope.launch {
            offlineRepository.updateCampsiteWithResponse(
                assetId, responseCode
            )
        }
    }

    private suspend fun updateCampsitesList(itemId: String) {
        val updatedCampsite = offlineRepository.getCampsiteKt(
                id = itemId
            )
        _campsitesListState.update { currentState ->
            currentState.copy(campsitesList =
                currentState.campsitesList.map { campsiteKt ->
                    when (campsiteKt.assetId) {
                        itemId -> updatedCampsite.first()
                        else   -> campsiteKt
                    }
                }
            )
        }
    }

    private suspend fun updateCampsitesUiState() {
        offlineRepository.campsitesKtFlow.collect { campsites ->
            val latLngList = mutableListOf<LatLng>()
            campsites.forEach { campsite ->
                val isValidCoords = isValidCoords(
                    campsite.lat,campsite.lon
                )
                if (isValidCoords) {
                    latLngList.add(
                        LatLng(campsite.lat,campsite.lon)
                    )
                }
            }
            /* Possible empty list, thus empty LatLngBounds
               builder NPE, handled by getLatLngBounds() */
            val latLngBounds = getLatLngBounds(latLngList)
            _campsitesListState.update { currentState ->
                currentState.copy(
                    boundingBox = latLngBounds,
                    campsitesList = campsites,
                    campsiteState = CampsitesUiState.Success,
                    containsValidCoords = campsites.any { campsite ->
                        campsite.lat < 0.0 && campsite.lon > 0.0
                    }
                )
            }
        }
    }

    private fun upsertCampsites(
        allExtras: Boolean,
        regionId: Int,
        campsitesList: List<CampsiteSerial>
    ) {
        if (campsitesList.isNotEmpty()) {
            viewModelScope.launch {

                val result: List<Long> =
                    offlineRepository.upsertCampsites(
                        campsitesList
                    )
                if (result.isNotEmpty()) {

                    if (allExtras) { /* For debug only */
                        Log.d("HEY", campsitesList.size.toString())
                        downloadAll(campsitesList = campsitesList)
                    } else if (
                        campsitesAdvancedSearch.value != null
                        &&
                        campsitesAdvancedSearch.value != 0
                    ) {
                        downloadAll(
                           campsitesList.filter { campsite ->
                                campsite.region ==
                                    campsitesFilterByRegion.value
                           }
                        )
                    }

                    // Tidy up assets that may have been removed
                    val campsitesListIds = campsitesList.map { cs ->
                        cs.assetId.toString()
                    }
                    val campsiteIds = when (regionId) {
                        0 -> offlineRepository.getCampsiteIds()
                        else -> {
                            offlineRepository.getCampsiteIdsByRegionCode()
                        }
                    }
                    campsiteIds.forEach { id ->
                        if (
                            !campsitesListIds.contains(id)
                        ) {
                            offlineRepository.deleteCampsite(id)
                            Log.d(TAG,"$id deleted")
                        }
                    }

                    async { getCampsiteAlerts() }.await()

                    // Read back list of assets list from DB
                    updateCampsitesUiState()
                }
            }

        } else {
            Log.d(TAG,"1.SocketTimeoutException|UnknownHost")
            _campsitesListState.update { currentState ->
                currentState.copy(
                    campsiteState = CampsitesUiState.Error
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as OutdoorsApplication)
                val alertsOfflineRepo = application.container.alertsOfflineRepo
                val favouritesRepository = application.container.favesRepo
                val networkRepository = application.container.campsitesNetworkRepo
                val offlineRepository = application.container.campsitesOfflineRepo
                val settingsRepository = application.container.settingsRepo
                CampsitesViewModel(
                    alertsOfflineRepo, favouritesRepository, networkRepository,
                    offlineRepository, settingsRepository
                )
            }
        }
        const val TAG = "CampsitesViewModel"
    }
}