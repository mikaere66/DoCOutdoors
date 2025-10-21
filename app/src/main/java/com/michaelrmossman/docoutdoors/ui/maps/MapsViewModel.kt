package com.michaelrmossman.docoutdoors.ui.maps

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.michaelrmossman.docoutdoors.OutdoorsApplication
import com.michaelrmossman.docoutdoors.data.CampsitesOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.HutsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.MapsRepoBase
import com.michaelrmossman.docoutdoors.data.TracksNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.TracksOfflineRepoBase
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.model.Favourite
import com.michaelrmossman.docoutdoors.ui.favourites.SingleMapState
import com.michaelrmossman.docoutdoors.utils.MapUtils.getLatLngBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

/* Common to ALL five map screens, including favourites */
class MapsViewModel(
    private val campsitesOfflineRepo: CampsitesOfflineRepoBase,
    private val hutsOfflineRepo: HutsOfflineRepoBase,
    mapsRepository: MapsRepoBase,
    private val tracksNetworkRepo: TracksNetworkRepoBase,
    private val tracksOfflineRepo: TracksOfflineRepoBase
) : ViewModel() {

    val commonSatelliteView =
        mapsRepository.commonSatelliteView.asLiveData()

    val commonShowLocation =
        mapsRepository.commonShowLocation.asLiveData()

    // Get track extras (coords) individually
    fun downloadTrackExtras(itemId: String) {
        try {
            viewModelScope.launch {
                tracksNetworkRepo.getTrack(
                    id = itemId, callback = { response ->
                        when (response.responseCode) {
                            200 -> response.trackSerial?.let { track ->
                                viewModelScope.launch {
                                    val result = tracksOfflineRepo.updateTrack(
                                        track = track
                                    )
                                    if (result > 0) {
                                        /* Update SingleMapState */
                                        getTrackById(itemId)
                                    }
                                }
                            }
                            else -> updateTrackWithResponse(
                                assetId = itemId,
                                responseCode = response.responseCode
                            )
                        }
                    }
                )
            }

        } catch (exception: IOException) {
            Log.d(TAG,exception.toString())
        }
    }

    private fun updateTrackWithResponse(
        assetId: String,
        responseCode: Int
    ) {
        viewModelScope.launch {
            tracksOfflineRepo.updateTrackWithResponse(
                assetId, responseCode
            )
        }
    }

    fun getCampsiteById(id: String) {
        viewModelScope.launch {
            val campsite = campsitesOfflineRepo.getCampsiteKt(id).first()
            _singleMapState.update { currentState ->
                currentState.copy(
                    campsiteKt = campsite,
                    /* Remove previous trackKt to
                       avoid track-related UI */
                    trackKt = null
                )
            }
        }
    }

    fun getHutById(id: String) {
        viewModelScope.launch {
            val hut = hutsOfflineRepo.getHutKt(id).first()
            _singleMapState.update { currentState ->
                currentState.copy(
                    hutKt = hut,
                    /* Remove previous trackKt to
                       avoid track-related UI */
                    trackKt = null
                )
            }
        }
    }

    fun getTrackById(id: String) {
        viewModelScope.launch {
            val track = tracksOfflineRepo.getTrackKt(id).first()
            _singleMapState.update { currentState ->
                currentState.copy(
                    trackKt = track
                )
            }
        }
    }

    /* For single fave track: SingleMapScreen */
    fun setCoordsByTrackId(id: String, lineCount: Int) {
        viewModelScope.launch {
            val coordsLists = tracksOfflineRepo.getCoordsByTrackId(
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
            /* Possible empty list, thus empty LatLngBounds
               builder NPE, handled by getLatLngBounds() */
            val latLngBounds = getLatLngBounds(latLngList) // TODO
            _singleMapState.update { currentState ->
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

    /* For all favourites: MultiMapScreen */
    fun setFavesBoundingBox(favourites: List<Favourite>) {
        val latLngList = mutableListOf<LatLng>()
        favourites.forEach { fave ->
            when (fave.itemType) {
                AssetType.Campsite -> {
                    fave.campsiteKt?.let { campsite ->
                        latLngList.add(
                            LatLng(
                                campsite.lat,
                                campsite.lon
                            )
                        )
                    }
                }
                AssetType.Hut -> {
                    fave.hutKt?.let { hut ->
                        latLngList.add(
                            LatLng(
                                hut.lat,
                                hut.lon
                            )
                        )
                    }
                }
                AssetType.Track -> {
                    fave.trackKt?.let { track ->
                        latLngList.add(
                            LatLng(
                                track.lat,
                                track.lon
                            )
                        )
                    }
                }
            }
        }
        val latLngBounds = getLatLngBounds(latLngList)
        _singleMapState.update { currentState ->
            currentState.copy(
                boundingBox = latLngBounds
            )
        }
    }

    private val _singleMapState by lazy { MutableStateFlow(SingleMapState()) }
    val singleMapState: StateFlow<SingleMapState> = _singleMapState

    val tracksZoomOnDload = tracksOfflineRepo.tracksZoomOnDload.asLiveData()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as OutdoorsApplication)
                val campsitesOfflineRepo = application.container.campsitesOfflineRepo
                val hutsOfflineRepo = application.container.hutsOfflineRepo
                val mapsRepository = application.container.mapsRepo
                val tracksNetworkRepo = application.container.tracksNetworkRepo
                val tracksOfflineRepo = application.container.tracksOfflineRepo
                MapsViewModel(
                    campsitesOfflineRepo, hutsOfflineRepo,
                    mapsRepository, tracksNetworkRepo, tracksOfflineRepo
                )
            }
        }
        const val TAG = "MapsViewModel"
    }
}