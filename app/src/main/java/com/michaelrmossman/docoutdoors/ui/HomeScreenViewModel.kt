package com.michaelrmossman.docoutdoors.ui

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
import com.michaelrmossman.docoutdoors.data.CampsitesOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.FavouritesRepoBase
import com.michaelrmossman.docoutdoors.data.HutsOfflineRepoBase
import com.michaelrmossman.docoutdoors.data.TracksOfflineRepoBase
import com.michaelrmossman.docoutdoors.interfaces.HomeScreenState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeScreenViewModel(
    alertsOfflineRepo: AlertsOfflineRepoBase,
    campsitesOfflineRepo: CampsitesOfflineRepoBase,
    favesRepository: FavouritesRepoBase,
    hutsOfflineRepo: HutsOfflineRepoBase,
    tracksOfflineRepo: TracksOfflineRepoBase
) : ViewModel() {

    val alertCount: StateFlow<Int> =
        alertsOfflineRepo.alertCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = -1
        )

    /* An initial value of -1 is used to hide "* Filtered" msg at startup */
    val alertsFilterById: StateFlow<Int> =
        alertsOfflineRepo.alertsFilterById.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = 0
        )

    val alertsFilterByRegion: LiveData<String> =
        alertsOfflineRepo.alertsFilterByRegion.asLiveData()

    val campsiteCount: StateFlow<Int> =
        campsitesOfflineRepo.campsiteCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = -1
        )

    val campsitesFilterById: StateFlow<Int> =
        campsitesOfflineRepo.campsitesFilterById.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = 0
        )

    val campsitesFilterBookable: LiveData<Int> =
        campsitesOfflineRepo.commonFilterByBookable.asLiveData()

    val campsitesFilterByRegion: LiveData<String> =
        campsitesOfflineRepo.campsitesFilterByRegion.asLiveData()

    val campsitesFilterDogAccess: LiveData<Int> =
        campsitesOfflineRepo.commonFilterByDogAccess.asLiveData()

    val faveCount: StateFlow<Int> =
        favesRepository.faveCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = 0
        )

    val hutCount: StateFlow<Int> =
        hutsOfflineRepo.hutCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = -1
        )

    val hutsFilterBookable: LiveData<Int> =
        hutsOfflineRepo.commonFilterByBookable.asLiveData()

    val hutsFilterById: StateFlow<Int> =
        hutsOfflineRepo.hutsFilterById.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = 0
        )

    val hutsFilterByRegion: LiveData<String> =
        hutsOfflineRepo.hutsFilterByRegion.asLiveData()

    val trackCount: StateFlow<Int> =
        tracksOfflineRepo.trackCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = -1
        )

    val tracksFilterDogAccess: LiveData<Int> =
        tracksOfflineRepo.commonFilterByDogAccess.asLiveData()

    val tracksFilterById: StateFlow<Int> =
        tracksOfflineRepo.tracksFilterById.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = 0
        )

    val tracksFilterByRegion: LiveData<String> =
        tracksOfflineRepo.tracksFilterByRegion.asLiveData()

    var uiState: HomeScreenState by mutableStateOf(HomeScreenState.Loading)
        private set

    fun setHomeScreenState(homeScreenState: HomeScreenState) {
        uiState = homeScreenState
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as OutdoorsApplication)
                val alertsOfflineRepo = application.container.alertsOfflineRepo
                val campsitesOfflineRepo = application.container.campsitesOfflineRepo
                val favouritesRepository = application.container.favesRepo
                val hutsOfflineRepo = application.container.hutsOfflineRepo
//                val settingsRepository = application.container.settingsRepo
                val tracksOfflineRepo = application.container.tracksOfflineRepo
                HomeScreenViewModel(
                    alertsOfflineRepo, campsitesOfflineRepo,
                    favouritesRepository,
                    hutsOfflineRepo, tracksOfflineRepo
                )
            }
        }
    }
}