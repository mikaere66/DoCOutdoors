package com.michaelrmossman.docoutdoors.ui.tracks

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
import com.michaelrmossman.docoutdoors.data.RegionsRepoBase
import com.michaelrmossman.docoutdoors.data.SettingsRepoBase
import com.michaelrmossman.docoutdoors.data.TracksNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.TracksOfflineRepoBase
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.ByRegionType
import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.interfaces.DownloadState
import com.michaelrmossman.docoutdoors.interfaces.TracksUiState
import com.michaelrmossman.docoutdoors.model.AlertExtra
import com.michaelrmossman.docoutdoors.model.TrackSerial
import com.michaelrmossman.docoutdoors.utils.BATCH_DOWNLOAD_DELAY
import com.michaelrmossman.docoutdoors.utils.DEBUG_VIEW_MODELS_DOWNLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.MapUtils.getLatLngBounds
import com.michaelrmossman.docoutdoors.utils.MapUtils.isValidCoords
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_FILTER_BY
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

class TracksViewModel(
    private val alertsOfflineRepo: AlertsOfflineRepoBase,
    private val favesRepository: FavouritesRepoBase,
    private val networkRepository: TracksNetworkRepoBase,
    private val offlineRepository: TracksOfflineRepoBase,
    private val regionsRepository: RegionsRepoBase,
    private val settingsRepository: SettingsRepoBase
) : ViewModel() {

    val commonFilterByDogAccess =
        offlineRepository.commonFilterByDogAccess.asLiveData()

    var downloadState: DownloadState by mutableStateOf(DownloadState.None)
        private set

    val tracksAdvancedSearch: LiveData<Int> =
        offlineRepository.tracksAdvancedSearch.asLiveData()

    val tracksFilterByRegion: LiveData<String> =
        offlineRepository.tracksFilterByRegion.asLiveData()

    val tracksListIncomplete: LiveData<Boolean> =
        offlineRepository.tracksListIncomplete.asLiveData()

    val tracksZoomOnDload =
        offlineRepository.tracksZoomOnDload.asLiveData()

    /** The mutable State that stores the status of the most recent request */
    private val _tracksListState by lazy { MutableStateFlow(TracksListState()) }
    val tracksListState: StateFlow<TracksListState> = _tracksListState

    // Get extras for all tracks [debug only]
    private suspend fun downloadAll(tracksList: List<TrackSerial>) {
        try {
            tracksList.forEachIndexed { index, track ->
                downloadExtras(
                    itemId = track.assetId,
                    standAlone = false
                )
                /* DoC requests limited to 100 per
                   second and/or 200 in a burst */
                delay(BATCH_DOWNLOAD_DELAY) // 100ms
                Log.d(TAG,"$index: ${ track.assetId }")
            }

        } catch (exception: IOException) {
            Log.d(TAG,exception.toString())
        }
    }

    // Get track extras individually
    fun downloadExtras(
        itemId: String, standAlone: Boolean = true /* Can be batch: see above */
    ) {
        if (standAlone) {
            downloadState = DownloadState.Loading
        }
        try {
            viewModelScope.launch {
                networkRepository.getTrack(id = itemId, callback = { response ->
                    when (response.responseCode) {
                        200 -> response.trackSerial?.let { trackSerial ->
                            viewModelScope.launch {
                                downloadState = DownloadState.Done
                                if (
                                    updateTrack(track = trackSerial).await() > 0
                                ) {
                                    if (standAlone) {
                                        updateTracksList(itemId)
                                    }
                                }
                            }
                        }
                        else -> {
                            downloadState = when (response.responseCode) {
                                404  -> DownloadState.NotFound
                                else -> DownloadState.Error
                            }
                            updateTrackWithResponse(
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
            assetId = id, itemType = AssetType.Track
        )

    /**
     * Call getTracks() on init so we can display status immediately
     */
    init {
        getAllTracks(
            reset     = DEBUG_VIEW_MODELS_DOWNLOAD_ALL,
            allExtras = DEBUG_VIEW_MODELS_DOWNLOAD_ALL
        )
    }

    /**
     * Gets tracks information from the Outdoors API Retrofit service
     */
    fun getAllTracks(reset: Boolean, allExtras: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                _tracksListState.update { currentState ->
                    currentState.copy(
                        trackState = TracksUiState.Downloading
                    )
                }

                val filterBy = settingsRepository.getSettingById(
                    id = PREF_TRACKS_FILTER_BY
                )
                val regionId = filterBy.first()
                try {
                    // Get assets from DoC API and add to DB
                    networkRepository.getAllTracks(id = when (regionId) {
                        0 -> null
                        else -> regionsRepository.getRegionCodeByActualId(
                            id = regionId,
                            // Refer note in RegionsRepository
                            byRegionType = ByRegionType.Tracks
                        )
                    },
                    callback = { response ->
                        when (response.responseCode) {
                            200 -> upsertTracks(
                                allExtras = allExtras,
                                regionId = regionId,
                                tracksList = response.tracksList
                            )
                            else -> _tracksListState.update { currentState ->
                                currentState.copy(
                                    tracksList = emptyList(),
                                    trackState = when (response.responseCode) {
                                        400  -> {
                                            /* e.g. Hawke's Bay */
                                            TracksUiState.Empty
                                        }
                                        403  -> TracksUiState.Forbidden
                                        else -> TracksUiState.Error
                                    }
                                )
                            }
                        }
                    })

                } catch (exception: IOException) {
                    _tracksListState.update { currentState ->
                        currentState.copy(
                            trackState = TracksUiState.Error
                        )
                    }
                    Log.d(TAG,exception.toString())
                }

            } else {
                offlineRepository.getTracksDloadCount().collect { count ->
                    if (count > 0) {

                        updateTracksUiState()

                    } else getAllTracks(reset = true)
                }
            }
        }
    }

    fun getTrackAlerts(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                alertsOfflineRepo.deleteAllAlertExtras(
                    itemType = AssetType.Track
                )
            }
            try {
                // Get alerts from DoC API and add to DB
                networkRepository.getTrackAlerts { trackAlerts ->

                    if (trackAlerts.isNotEmpty()) {
                        viewModelScope.launch {

                            alertsOfflineRepo.insertTrackAlertExtras(
                                alertsList = trackAlerts
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

    fun getTrackIndex(itemId: String): Int {
        val tracks = tracksListState.value.tracksList
        return tracks.indexOf(
            tracks.find { track ->
                track.assetId == itemId
            }
        )
    }

    fun getTracksHashMap(): HashMap<String, String> {
        return tracksListState.value.tracksList.associateBy(
            keySelector    = { track -> track.assetId },
            valueTransform = { track ->
                when (tracksListState.value.searchBy) {
                    SearchBy.Name -> track.name
                    /* Facilities does not apply to Tracks */
                    else -> getSearchResultWithExtras(
                        track.name,
                        track.introduction
                    )
                }
            }
        ) as HashMap<String, String>
    }

    fun resetBoundingBox() {
        viewModelScope.launch {
            val latLngList = mutableListOf<LatLng>()
            tracksListState.value.tracksList.forEach { track ->
                val isValidCoords = isValidCoords(
                    track.lat,track.lon
                )
                if (isValidCoords) {
                    latLngList.add(
                        LatLng(
                            track.lat,
                            track.lon
                        )
                    )
                }
            }
            val latLngBounds = getLatLngBounds(latLngList)
            _tracksListState.update { currentState ->
                currentState.copy(
                    boundingBox = latLngBounds
                )
            }
        }
    }

    /* Update download state, to reset. Called
       from either ListScreen or DetailsScreen
       depending on WindowWidthSizeClass */
    fun resetDownloadState() {
        downloadState = DownloadState.None
    }

    fun setCoordsByTrackId(id: String, lineCount: Int) {
        viewModelScope.launch {
            val coordsLists = offlineRepository.getCoordsByTrackId(
                id, lineCount
            )
            val latLngList = mutableListOf<LatLng>()
            coordsLists.forEach { list ->
                list.forEach { listItem ->
                    latLngList.add(
                        LatLng(
                            listItem.lat,
                            listItem.lon
                        )
                    )
                }
            }
            val latLngBounds = getLatLngBounds(latLngList) // TODO
            _tracksListState.update { currentState ->
                currentState.copy(
                    boundingBox = latLngBounds,            // TODO
                    trackCoords = coordsLists.map { list ->
                        list.map { listItem ->
                            LatLng(
                                listItem.lat,
                                listItem.lon
                            )
                        }
                    }
                )
            }
        }
    }

    fun setTracksHashMap(searchBy: SearchBy) {
        _tracksListState.update { currentState ->
            currentState.copy(
                searchBy = searchBy
            )
        }
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
                    itemType = AssetType.Track.name
                )
                else -> favesRepository.insertFave(
                    assetId = assetId,
                    itemType = AssetType.Track.name
                ).toInt()
            }
            if (result != 0) {
                updateTracksList(itemId = assetId)
            }
        }
    }

    private fun updateTrack(track: TrackSerial): Deferred<Int> =
        viewModelScope.async {
            offlineRepository.updateTrack(track = track)
        }

    private fun updateTrackWithResponse(
        assetId: String,
        responseCode: Int
    ) {
        viewModelScope.launch {
            offlineRepository.updateTrackWithResponse(
                assetId, responseCode
            )
        }
    }

    private suspend fun updateTracksList(itemId: String) {
        val updatedTrack = offlineRepository.getTrackKt(
            id = itemId
        )
        _tracksListState.update { currentState ->
            currentState.copy(tracksList =
                currentState.tracksList.map { trackKt ->
                    when (trackKt.assetId) {
                        itemId -> updatedTrack.first()
                        else   -> trackKt
                    }
                }
            )
        }
    }

    private suspend fun updateTracksUiState() {
        offlineRepository.tracksKtFlow.collect { tracks ->
            val latLngList = mutableListOf<LatLng>()
            tracks.forEach { track ->
                val isValidCoords = isValidCoords(
                    track.lat,track.lon
                )
                if (isValidCoords) {
                    latLngList.add(
                        LatLng(track.lat,track.lon)
                    )
                }
            }
            /* Possible empty list, thus empty LatLngBounds
               builder NPE, handled by getLatLngBounds() */
            val latLngBounds = getLatLngBounds(latLngList)
            _tracksListState.update { currentState ->
                currentState.copy(
                    boundingBox = latLngBounds,
                    containsValidCoords = tracks.any { track ->
                        track.lat < 0.0 && track.lon > 0.0
                    },
                    tracksList = tracks,
                    trackState = TracksUiState.Success
                )
            }
        }
    }

    private fun upsertTracks(
        allExtras: Boolean,
        regionId: Int,
        tracksList: List<TrackSerial>
    ) {
        if (tracksList.isNotEmpty()) {
            viewModelScope.launch {

                val result: List<Long> =
                    offlineRepository.upsertTracks(
                        tracksList
                    )
                if (result.isNotEmpty()) {

                    /* Reset all tracksDload values to zero.
                       Refer to note in RegionsRepository */
                    if (regionId == 0) {
                        regionsRepository.resetTracksDload()
                    }

                    if (allExtras) { /* For debug only */
                        Log.d("HEY", tracksList.size.toString())
                        downloadAll(tracksList = tracksList)
                    } else if (
                        tracksAdvancedSearch.value != null
                        &&
                        tracksAdvancedSearch.value != 0
                    ) {
                        downloadAll(tracksList = tracksList)
                    }

                    // Tidy up assets that may have been removed
                    val tracksListIds = tracksList.map { track ->
                        track.assetId
                    }
                    val trackIds = when (regionId) {
                        0 -> offlineRepository.getTrackIds()
                        else -> {
                            offlineRepository.getTrackIdsByRegionCode()
                        }
                    }
                    trackIds.forEach { id ->
                        if (
                            !tracksListIds.contains(id)
                        ) {
                            offlineRepository.deleteTrack(id)
                            Log.d(TAG,"$id deleted")
                        }
                    }

                    async { getTrackAlerts() }.await()

                    // Read back list of assets from DB
                    updateTracksUiState()
                }
            }

        } else {
            Log.d(TAG,"1.SocketTimeoutException|UnknownHost")
            _tracksListState.update { currentState ->
                currentState.copy(
                    trackState = TracksUiState.Error
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
                val networkRepository = application.container.tracksNetworkRepo
                val offlineRepository = application.container.tracksOfflineRepo
                val regionsRepository = application.container.regionsRepo
                val settingsRepository = application.container.settingsRepo
                TracksViewModel(
                    alertsOfflineRepo, favouritesRepository, networkRepository,
                    offlineRepository, regionsRepository, settingsRepository
                )
            }
        }
        const val TAG = "TracksViewModel"
    }
}