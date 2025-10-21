package com.michaelrmossman.docoutdoors.ui.favourites

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
import com.michaelrmossman.docoutdoors.data.AlertsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.CampsitesNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.CampsitesOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.FavouritesRepoBase
import com.michaelrmossman.docoutdoors.data.HutsNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.HutsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.TracksNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.TracksOfflineRepoBase
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.SortFavesBy
import com.michaelrmossman.docoutdoors.interfaces.DownloadState
import com.michaelrmossman.docoutdoors.model.AlertExtra
import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.model.Favourite
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.model.TrackKt
import kotlinx.coroutines.launch
import java.io.IOException

class FavesViewModel(
    private val alertsOfflineRepo: AlertsOfflineRepoBase,
    private val campsitesNetworkRepository: CampsitesNetworkRepoBase,
    private val campsitesOfflineRepo: CampsitesOfflineRepoBase,
    private val favesRepository: FavouritesRepoBase,
    private val hutsNetworkRepository: HutsNetworkRepoBase,
    private val hutsOfflineRepo: HutsOfflineRepoBase,
    private val tracksNetworkRepository: TracksNetworkRepoBase,
    private val tracksOfflineRepo: TracksOfflineRepoBase
) : ViewModel() {

    fun deleteAllFavourites() {
        viewModelScope.launch {
            favesRepository.deleteAllFavourites()
        }
    }

    var downloadState: DownloadState by mutableStateOf(DownloadState.None)
        private set

    // Get campsite extras individually
    fun downloadCampsiteExtras(itemId: String) {
        downloadState = DownloadState.Loading
        try {
            viewModelScope.launch {
                campsitesNetworkRepository.getCampsite(
                    id = itemId, callback = { response ->
                        when (response.responseCode) {
                            200 -> response.campsiteSerial?.let { campsiteSerial ->
                                downloadState = DownloadState.Done
                                viewModelScope.launch {
                                    campsitesOfflineRepo.updateCampsite(
                                        campsite = campsiteSerial
                                    )
                                }
                            }
                            else -> {
                                downloadState = when (response.responseCode) {
                                    404  -> DownloadState.NotFound
                                    else -> DownloadState.Error
                                }
                                updateAssetWithResponse(
                                    assetId = itemId,
                                    itemType = AssetType.Campsite,
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

    // Get hut extras individually
    fun downloadHutExtras(itemId: String) {
        downloadState = DownloadState.Loading
        try {
            viewModelScope.launch {
                hutsNetworkRepository.getHut(
                    id = itemId, callback = { response ->
                        when (response.responseCode) {
                            200 -> response.hutSerial?.let { hutSerial ->
                                downloadState = DownloadState.Done
                                viewModelScope.launch {
                                    hutsOfflineRepo.updateHut(
                                        hut = hutSerial
                                    )
                                }
                            }
                            else -> {
                                downloadState = when (response.responseCode) {
                                    404  -> DownloadState.NotFound
                                    else -> DownloadState.Error
                                }
                                updateAssetWithResponse(
                                    assetId = itemId,
                                    itemType = AssetType.Hut,
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

    // Get track extras individually
    fun downloadTrackExtras(itemId: String) {
        downloadState = DownloadState.Loading
        try {
            viewModelScope.launch {
                tracksNetworkRepository.getTrack(
                    id = itemId, callback = { response ->
                        when (response.responseCode) {
                            200 -> response.trackSerial?.let { trackSerial ->
                                downloadState = DownloadState.Done
                                viewModelScope.launch {
                                    tracksOfflineRepo.updateTrack(
                                        track = trackSerial
                                    )
                                }
                            }
                            else -> {
                                downloadState = when (response.responseCode) {
                                    404  -> DownloadState.NotFound
                                    else -> DownloadState.Error
                                }
                                updateAssetWithResponse(
                                    assetId = itemId,
                                    itemType = AssetType.Track,
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

    val favesSortedBy: LiveData<Int> =
        favesRepository.favesSortedBy.asLiveData()

    val favourites: LiveData<List<Favourite>> =
        favesRepository.getAllFavourites().asLiveData()

    suspend fun getAlertById(
        id: String, itemType: AssetType
    ) : AlertExtra = alertsOfflineRepo.getAlertExtraByAssetId(
        assetId = id, itemType = itemType
    )

    fun getCampsiteKt(id: String): LiveData<CampsiteKt> =
        campsitesOfflineRepo.getCampsiteKt(id).asLiveData()

    fun getHutKt(id: String): LiveData<HutKt> =
        hutsOfflineRepo.getHutKt(id).asLiveData()

    fun getTrackKt(id: String): LiveData<TrackKt> =
        tracksOfflineRepo.getTrackKt(id).asLiveData()

    fun resetDownloadState() {
        downloadState = DownloadState.None
    }

    fun setFavesSortedBy(sortBy: SortFavesBy) {
        viewModelScope.launch {
            favesRepository.setFavesSortedBy(sortBy)
        }
    }

    /* isFavourite refers to PREVIOUS state */
    fun toggleFavourite(
        assetId: String,
        isFavourite: Boolean,
        itemType: AssetType
    ) {
        viewModelScope.launch {
            when (isFavourite) {
                true -> favesRepository.deleteFaveByIdAndType(
                    assetId = assetId,
                    itemType = itemType.name
                )
                else -> favesRepository.insertFave(
                    assetId = assetId,
                    itemType = itemType.name
                )
            }
        }
    }

    private fun updateAssetWithResponse(
        assetId: String,
        itemType: AssetType,
        responseCode: Int
    ) {
        viewModelScope.launch {
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
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as OutdoorsApplication)
                val alertsOfflineRepo = application.container.alertsOfflineRepo
                val campsitesNetworkRepo = application.container.campsitesNetworkRepo
                val campsitesOfflineRepo = application.container.campsitesOfflineRepo
                val favouritesRepository = application.container.favesRepo
                val hutsNetworkRepository = application.container.hutsNetworkRepo
                val hutsOfflineRepo = application.container.hutsOfflineRepo
                val tracksNetworkRepository = application.container.tracksNetworkRepo
                val tracksOfflineRepo = application.container.tracksOfflineRepo
                FavesViewModel(
                    alertsOfflineRepo, campsitesNetworkRepo,
                    campsitesOfflineRepo, favouritesRepository,
                    hutsNetworkRepository, hutsOfflineRepo,
                    tracksNetworkRepository, tracksOfflineRepo
                )
            }
        }
        const val TAG = "FavesViewModel"
    }
}