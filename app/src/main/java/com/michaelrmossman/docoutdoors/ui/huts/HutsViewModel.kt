package com.michaelrmossman.docoutdoors.ui.huts

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
import com.michaelrmossman.docoutdoors.data.FavouritesRepoBase
import com.michaelrmossman.docoutdoors.data.HutsNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.HutsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.SettingsRepoBase
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.interfaces.DownloadState
import com.michaelrmossman.docoutdoors.interfaces.HutsUiState
import com.michaelrmossman.docoutdoors.model.AlertExtra
import com.michaelrmossman.docoutdoors.model.HutSerial
import com.michaelrmossman.docoutdoors.utils.BATCH_DOWNLOAD_DELAY
import com.michaelrmossman.docoutdoors.utils.DEBUG_VIEW_MODELS_DOWNLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.MapUtils.getLatLngBounds
import com.michaelrmossman.docoutdoors.utils.MapUtils.isValidCoords
import com.michaelrmossman.docoutdoors.utils.PREF_HUTS_FILTER_BY
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

class HutsViewModel(
    private val alertsOfflineRepo: AlertsOfflineRepoBase,
    private val favesRepository: FavouritesRepoBase,
    private val networkRepository: HutsNetworkRepoBase,
    private val offlineRepository: HutsOfflineRepoBase,
    private val settingsRepository: SettingsRepoBase
) : ViewModel() {

    val commonFilterByBookable =
        offlineRepository.commonFilterByBookable.asLiveData()

    var downloadState: DownloadState by mutableStateOf(DownloadState.None)
        private set

    val hutsAdvancedSearch: LiveData<Int> =
        offlineRepository.hutsAdvancedSearch.asLiveData()

    val hutsFilterByRegion: LiveData<String> =
        offlineRepository.hutsFilterByRegion.asLiveData()

    /** The mutable State that stores the status of the most recent request */
    private val _hutsListState by lazy { MutableStateFlow(HutsListState()) }
    val hutsListState: StateFlow<HutsListState> = _hutsListState

    // Get extras for all huts [debug only]
    private suspend fun downloadAll(hutsList: List<HutSerial>) {
        try {
            hutsList.forEachIndexed { index, hut ->
                downloadExtras(
                    itemId = hut.assetId.toString(),
                    standAlone = false
                )
                /* DoC requests limited to 100 per
                   second and/or 200 in a burst */
                delay(BATCH_DOWNLOAD_DELAY) // 100ms
                Log.d(TAG,"$index: ${ hut.assetId }")
            }

        } catch (exception: IOException) {
            Log.d(TAG,exception.toString())
        }
    }

    // Get hut extras individually
    fun downloadExtras(
        itemId: String, standAlone: Boolean = true /* Can be batch: see above */
    ) {
        if (standAlone) {
            downloadState = DownloadState.Loading
        }
        try {
            viewModelScope.launch {
                networkRepository.getHut(id = itemId, callback = { response ->
                    when (response.responseCode) {
                        200 -> response.hutSerial?.let { hutSerial ->
                            downloadState = DownloadState.Done
                            viewModelScope.launch {
                                if (
                                    updateHut(hut = hutSerial).await() > 0
                                ) {
                                    if (standAlone) {
                                        updateHutsList(itemId)
                                    }
                                }
                            }
                        }
                        else -> {
                            downloadState = when (response.responseCode) {
                                404  -> DownloadState.NotFound
                                else -> DownloadState.Error
                            }
                            updateHutWithResponse(
                                assetId = itemId,
                                responseCode = response.responseCode
                            )
                        }
                    }
                })
            }

        } catch (exception: IOException) {
            downloadState = DownloadState.Error
            Log.d(TAG,exception.toString())
        }
    }

    suspend fun getAlertById(id: String): AlertExtra =
        alertsOfflineRepo.getAlertExtraByAssetId(
            assetId = id, itemType = AssetType.Hut
        )

    /**
     * Call getHuts() on init so we can display status immediately,
     * gathering ALL info only IF items are filtered by region
     */
    init {
        getAllHuts(
            reset     = DEBUG_VIEW_MODELS_DOWNLOAD_ALL,
            allExtras = DEBUG_VIEW_MODELS_DOWNLOAD_ALL
        )
    }

    /**
     * Gets huts information from the Outdoors API Retrofit service
     */
    fun getAllHuts(reset: Boolean, allExtras: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                _hutsListState.update { currentState ->
                    currentState.copy(
                        hutState = HutsUiState.Downloading
                    )
                }

                val filterBy = settingsRepository.getSettingById(
                    id = PREF_HUTS_FILTER_BY
                )
                val regionId = filterBy.first()
                try {
                    // Get assets from DoC API and add to DB
                    networkRepository.getAllHuts( callback = { response ->
                        when (response.responseCode) {
                            200 -> upsertHuts(
                                allExtras = allExtras,
                                regionId = regionId,
                                hutsList = response.hutsList
                            )
                            else -> {
                                _hutsListState.update { currentState ->
                                    currentState.copy(
                                        hutsList = emptyList(),
                                        hutState = when (response.responseCode) {
                                            403  -> HutsUiState.Forbidden
                                            else -> HutsUiState.Error
                                        }
                                    )
                                }
                            }
                        }
                    })

                } catch (exception: IOException) {
                    _hutsListState.update { currentState ->
                        currentState.copy(
                            hutState = HutsUiState.Error
                        )
                    }
                    Log.d(TAG,exception.toString())
                }

            } else {
                offlineRepository.getHutNameCount().collect { count ->
                    if (count > 0) {

                        updateHutsUiState()

                    } else getAllHuts(reset = true)
                }
            }
        }
    }

    fun getHutAlerts(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                alertsOfflineRepo.deleteAllAlertExtras(
                    itemType = AssetType.Hut
                )
            }
            try {
                // Get alerts from DoC API and add to DB
                networkRepository.getHutAlerts { hutAlerts ->

                    if (hutAlerts.isNotEmpty()) {
                        viewModelScope.launch {

                            alertsOfflineRepo.insertCSOrHutAlertExtras(
                                alertsList = hutAlerts,
                                itemType = AssetType.Hut
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

    fun getHutIndex(itemId: String): Int {
        val huts = hutsListState.value.hutsList
        return huts.indexOf(
            huts.find { hut ->
                hut.assetId == itemId
            }
        )
    }

    fun setHutsHashMap(searchBy: SearchBy) {
        _hutsListState.update { currentState ->
            currentState.copy(
                searchBy = searchBy
            )
        }
    }

    fun getHutsHashMap(): HashMap<String, String> {
        return hutsListState.value.hutsList.associateBy(
            keySelector    = { hut -> hut.assetId },
            valueTransform = {
                hut -> when (hutsListState.value.searchBy) {
                    SearchBy.Name -> hut.name
                    SearchBy.Feat -> getSearchResultWithExtras(
                        hut.name, hut.facilities
                    )
                    SearchBy.Desc -> getSearchResultWithExtras(
                        hut.name, hut.introduction
                    )
                }
            }
        ) as HashMap<String, String>
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
                    itemType = AssetType.Hut.name
                )
                else -> favesRepository.insertFave(
                    assetId = assetId,
                    itemType = AssetType.Hut.name
                ).toInt()
            }
            if (result != 0) {
                updateHutsList(itemId = assetId)
            }
        }
    }

    private fun updateHut(hut: HutSerial): Deferred<Int> =
        viewModelScope.async {
            offlineRepository.updateHut(hut = hut)
        }

    private fun updateHutWithResponse(
        assetId: String,
        responseCode: Int
    ) {
        viewModelScope.launch {
            offlineRepository.updateHutWithResponse(
                assetId, responseCode
            )
        }
    }

    private suspend fun updateHutsList(itemId: String) {
        val updatedHut = offlineRepository.getHutKt(
            id = itemId
        )
        _hutsListState.update { currentState ->
            currentState.copy(hutsList =
                currentState.hutsList.map { hutKt ->
                    when (hutKt.assetId) {
                        itemId -> updatedHut.first()
                        else   -> hutKt
                    }
                }
            )
        }
    }

    private suspend fun updateHutsUiState() {
        offlineRepository.hutsKtFlow.collect { huts ->
            val latLngList = mutableListOf<LatLng>()
            huts.forEach { hut ->
                val isValidCoords = isValidCoords(
                    hut.lat,hut.lon
                )
                if (isValidCoords) {
                    latLngList.add(
                        LatLng(hut.lat,hut.lon)
                    )
                }
            }
            /* Possible empty list, thus empty LatLngBounds
               builder NPE, handled by getLatLngBounds() */
            val latLngBounds = getLatLngBounds(latLngList)
            _hutsListState.update { currentState ->
                currentState.copy(
                    boundingBox = latLngBounds,
                    containsValidCoords = huts.any { hut ->
                        hut.lat < 0.0 && hut.lon > 0.0
                    },
                    hutsList = huts,
                    hutState = HutsUiState.Success
                )
            }
        }
    }

    private fun upsertHuts(
        allExtras: Boolean,
        regionId: Int,
        hutsList: List<HutSerial>
    ) {
        if (hutsList.isNotEmpty()) {
            viewModelScope.launch {

                val result: List<Long> =
                    offlineRepository.upsertHuts(
                        hutsList
                    )
                if (result.isNotEmpty()) {

                    if (allExtras) { /* For debug only */
                        Log.d("HEY", hutsList.size.toString())
                        downloadAll(hutsList = hutsList)
                    } else if (
                        hutsAdvancedSearch.value != null
                        &&
                        hutsAdvancedSearch.value != 0
                    ) {
                        downloadAll(
                           hutsList.filter { hut ->
                                hut.region == hutsFilterByRegion.value
                           }
                        )
                    }

                    // Tidy up assets that may have been removed
                    val hutsListIds = hutsList.map { hut ->
                        hut.assetId.toString()
                    }
                    val hutIds = when (regionId) {
                        0 -> offlineRepository.getHutIds()
                        else -> {
                            offlineRepository.getHutIdsByRegionCode()
                        }
                    }
                    hutIds.forEach { id ->
                        if (
                            !hutsListIds.contains(id)
                        ) {
                            offlineRepository.deleteHut(id)
                            Log.d(TAG,"$id deleted")
                        }
                    }

                    async { getHutAlerts() }.await()

                    // Read back list of assets list from DB
                    updateHutsUiState()
                }
            }

        } else {
            Log.d(TAG,"1.SocketTimeoutException|UnknownHost")
            _hutsListState.update { currentState ->
                currentState.copy(
                    hutState = HutsUiState.Error
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
                val networkRepository = application.container.hutsNetworkRepo
                val offlineRepository = application.container.hutsOfflineRepo
                val settingsRepository = application.container.settingsRepo
                HutsViewModel(
                    alertsOfflineRepo, favouritesRepository, networkRepository,
                    offlineRepository, settingsRepository
                )
            }
        }
        const val TAG = "HutsViewModel"
    }
}