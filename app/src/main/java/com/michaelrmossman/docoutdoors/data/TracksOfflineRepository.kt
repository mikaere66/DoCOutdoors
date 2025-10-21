package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.database.AffectedDao
import com.michaelrmossman.docoutdoors.database.CoordsDao
import com.michaelrmossman.docoutdoors.database.DbHelpers.getBookableOrDogsQueryWithExtras
import com.michaelrmossman.docoutdoors.database.DbHelpers.getCountByRegionCodeWithExtrasQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getFavouriteItemKtQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getItemIdsNotDownloadedQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getRegionNameByRegionIdAndCode
import com.michaelrmossman.docoutdoors.database.FavesDao
import com.michaelrmossman.docoutdoors.database.RegionsDao
import com.michaelrmossman.docoutdoors.database.SettingsDao
import com.michaelrmossman.docoutdoors.database.TracksDao
import com.michaelrmossman.docoutdoors.enums.AffectType
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.model.CoordsEntity
import com.michaelrmossman.docoutdoors.model.TrackEntity
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.model.TrackSerial
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_DOGS_BY
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_ZOOM_ON_DLOAD
import com.michaelrmossman.docoutdoors.utils.isNegative
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Tracks offline implementation of TracksOfflineRepoBase
 */
class TracksOfflineRepository(
    private val affectedDao: AffectedDao,
    private val coordsDao: CoordsDao,
    private val favesDao: FavesDao,
    private val regionsDao: RegionsDao,
    private val settingsDao: SettingsDao,
    private val tracksDao: TracksDao
) : TracksOfflineRepoBase {

    private val _commonFilterByDogAccess = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_COMMON_FILTER_DOGS_BY
        )
    )
    override val commonFilterByDogAccess: Flow<Int>
        get() = _commonFilterByDogAccess.value
    override fun setCommonFilterDogsBy(filterDogsBy: Int) {
        _commonFilterByDogAccess.value = flowOf(filterDogsBy)
    }

    override suspend fun deleteTrack(id: String) =
        tracksDao.deleteTrack(id = id)

    override suspend fun doesTrackExist(id: String): Boolean =
        tracksDao.doesTrackExist(id)

    override suspend fun getCoordsByTrackId(
        id: String, lineCount: Int
    ) : List<List<CoordsEntity>> {
        val coordsLists = mutableListOf<List<CoordsEntity>>()
        for (i in 0 until lineCount) {
            coordsLists.add(
                coordsDao.getCoordsByTrackId(id, i)
            )
        }
        return coordsLists
    }

    override suspend fun getTrackById(id: String): TrackKt {
        val affected = affectedDao.getAffectedCountByItemId(
            affectId = id,
            itemType = AffectType.WalkTrack.type
        )
        val favourite = favesDao.isFavourite(
            assetId = id, itemType = AssetType.Track.name
        )
        return tracksDao.getTrackById(id).toTrackKt(
            affectedCount = affected,
            isFavourite = favourite
        )
    }

    override suspend fun getTrackIds(): List<String> =
        tracksDao.getTrackIds()

    override suspend fun getTrackIdsByRegionCode(): List<String> {
        val filterBy = settingsDao.getSettingById(
            settingId = PREF_TRACKS_FILTER_BY
        )
        val regionCode = regionsDao.getRegionCodeByActualId(
            id = filterBy.first()
        )
        return tracksDao.getTrackIdsByRegionCode(regionCode)
    }

    override suspend fun getTrackIdsNotDownloaded(
        filterBy: Int
    ) : List<String> {
        return tracksDao.getTrackIdsNotDownloaded(
            query = getItemIdsNotDownloadedQuery(
                regionId = filterBy,
                itemType = AssetType.Track
            )
        )
    }

    override fun getTrackKt(id: String): Flow<TrackKt> =
        tracksDao.getTrackKt(
            getFavouriteItemKtQuery(
                itemId = id, itemType = AssetType.Track
            )
        )

    override fun getTracksDloadCount(): Flow<Int> =
        tracksDao.getTracksDloadCount()

    override lateinit var trackKt: TrackKt
    override fun setFaveTrack(track: TrackKt) {
        trackKt = track
    }

    private val _tracksAdvancedSearch = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_TRACKS_DLOAD_ALL
        )
    )
    override val tracksAdvancedSearch: Flow<Int>
        get() = _tracksAdvancedSearch.value
    override fun setTracksAdvancedSearch(advSearch: Int) {
        _tracksAdvancedSearch.value = flowOf(advSearch)
    }

    private val _tracksFilterById = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_TRACKS_FILTER_BY
        )
    )
    override val tracksFilterById: Flow<Int>
        get() = _tracksFilterById.value
    override fun setTracksRegionCode(region: Int) {
        _tracksFilterById.value = flowOf(region)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val tracksFilterByRegion: Flow<String> =
        _tracksFilterById.flatMapLatest { filterBy ->
            val region = when (filterBy.first()) {
                0 -> String()
                else -> getRegionNameByRegionIdAndCode(
                    regionId = filterBy.first(),
                    regionsDao = regionsDao
                )
            }
            flowOf(region)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val trackCount: Flow<Int> =
        _commonFilterByDogAccess.flatMapLatest { dogAccess ->
            _tracksFilterById.flatMapLatest { filterBy ->
                if (dogAccess.first().plus(filterBy.first()) == 0) {
                    tracksDao.getTracksDloadCount()

                } else tracksDao.getTrackCountByRegionCode(
                    query = getCountByRegionCodeWithExtrasQuery(
                        dogAccess = dogAccess.first(),
                        filterType = FilterType.Tracks,
                        regionId = filterBy.first(),
                    )
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val tracksKtFlow: Flow<List<TrackKt>> =
        _tracksFilterById.flatMapLatest { filterBy ->
            _commonFilterByDogAccess.flatMapLatest { dogAccess ->
                tracksDao.getTracksKtFlow(
                    getBookableOrDogsQueryWithExtras(
                        dogAccess = dogAccess.first(),
                        regionId = filterBy.first(),
                        itemType = AssetType.Track
                    )
                )
            }
        }

    private val regionTotalCount = regionsDao.getRegionTotalCount()
    private val tracksDloadCount = regionsDao.getDloadTracksCount()
    @OptIn(ExperimentalCoroutinesApi::class)
    override val tracksListIncomplete: Flow<Boolean> =
        _tracksFilterById.flatMapLatest { filterBy ->
            regionTotalCount.combine(
                tracksDloadCount
            ) { regions, tracks ->
                tracks != 0
                && // i.e. between 1 & 19 inclusive
                tracks != regions
            } // Refer to note in RegionsRepository
        }

    private val _tracksZoomOnDload = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_TRACKS_ZOOM_ON_DLOAD
        )
    )
    override val tracksZoomOnDload: Flow<Int>
        get() = _tracksZoomOnDload.value
    override fun setTracksZoomOnDload(zoomOnDload: Int) {
        _tracksZoomOnDload.value = flowOf(zoomOnDload)
    }

    /* All calls to this method are from within a 200 block */
    override suspend fun updateTrack(track: TrackSerial): Int {
        val coordsList = mutableListOf<CoordsEntity>()
        for (i in 0 until track.line.size) {
            val latLngList = track.line[i]
            latLngList.forEach { latLng ->
                coordsList.add(
                    CoordsEntity(
                        assetId = track.assetId,
                        listIndex = i,
                        /* Note : these two are reversed in json */
                        lat = when (latLng.first().isNegative()) {
                            true -> latLng.first()
                            else -> latLng.last()
                        },
                        lon = when (latLng.last().isNegative()) {
                            true -> latLng.first()
                            else -> latLng.last()
                        }
                    )
                )
            }
        }
        coordsDao.insertCoords(coordsList)

        return tracksDao.updateTrack(
            TrackEntity.from(
                lineCount = track.line.size,
                responseCode = 200,
                track = track
            )
        )
    }

    override suspend fun updateTrackWithResponse(
        assetId: String,
        responseCode: Int
    ) {
        tracksDao.updateTrackWithResponseCode(
            assetId, responseCode
        )
    }

    override suspend fun upsertTracks(
        tracksList: List<TrackSerial>
    ) : List<Long> = tracksDao.upsertTracks(
        tracksList.filter { trackSerial ->
            trackSerial.name.isNotBlank()
        }.map { trackSerial ->
            when (
                doesTrackExist(trackSerial.assetId)
            ) {
                false -> TrackEntity.from(
                    lineCount = 0,
                    responseCode = 200,
                    track = trackSerial
                )
                else -> {
                    val lineCount = coordsDao.getLineCountByTrackId(
                        id = trackSerial.assetId
                    )
                    val trackEntity = tracksDao.getTrackById(
                        trackSerial.assetId
                    )
                    TrackEntity.from(
                        /* Only allow positive line count when
                           coords have already been downloaded */
                        lineCount = when (lineCount) {
                            0 -> 0
                            else -> trackSerial.line.size
                        },
                        responseCode = 200,
                        track = trackSerial.copy(
                            /* For upsert, do NOT update dog access string,
                               as this will be null for getAll*Assets() */
                            dogsAllowed = trackEntity.dogsAllowed,
                        )
                    )
                }
            }
        }
    )
}