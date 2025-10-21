package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.model.CoordsEntity
import com.michaelrmossman.docoutdoors.model.TrackKt
import com.michaelrmossman.docoutdoors.model.TrackSerial
import kotlinx.coroutines.flow.Flow

/**
 * Base offline repository that fetches various lists from database
 */
interface TracksOfflineRepoBase {

    val commonFilterByDogAccess: Flow<Int>

    suspend fun deleteTrack(id: String)

    suspend fun doesTrackExist(id: String): Boolean

    suspend fun getCoordsByTrackId(
        id: String, lineCount: Int
    ) : List<List<CoordsEntity>>

    suspend fun getTrackById(id: String): TrackKt

    suspend fun getTrackIds(): List<String>

    suspend fun getTrackIdsByRegionCode(): List<String>

    suspend fun getTrackIdsNotDownloaded(filterBy: Int): List<String>

    fun getTrackKt(id: String): Flow<TrackKt>

    fun getTracksDloadCount(): Flow<Int>

    fun setCommonFilterDogsBy(filterDogsBy: Int)

    fun setFaveTrack(track: TrackKt)

    fun setTracksAdvancedSearch(advSearch: Int)

    fun setTracksRegionCode(region: Int)

    fun setTracksZoomOnDload(zoomOnDload: Int)

    val trackCount: Flow<Int>

     /* lateinit var in repository */
    var trackKt: TrackKt

    val tracksAdvancedSearch: Flow<Int>

    val tracksFilterById: Flow<Int>

    val tracksFilterByRegion: Flow<String>

    val tracksKtFlow: Flow<List<TrackKt>>

    val tracksListIncomplete: Flow<Boolean>

    val tracksZoomOnDload: Flow<Int>

    suspend fun updateTrack(track: TrackSerial): Int

    suspend fun updateTrackWithResponse(
        assetId: String,
        responseCode: Int
    )

    suspend fun upsertTracks(tracksList: List<TrackSerial>): List<Long>
}