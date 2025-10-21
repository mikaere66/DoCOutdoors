package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.docoutdoors.model.AffectedExtraEntity
import com.michaelrmossman.docoutdoors.model.FaveEntity
import com.michaelrmossman.docoutdoors.model.RegionEntity
import com.michaelrmossman.docoutdoors.model.SettingEntity
import com.michaelrmossman.docoutdoors.model.TrackEntity
import com.michaelrmossman.docoutdoors.model.TrackKt
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {

    @Query("DELETE FROM $TABLE_NAME_TRACKS")
    suspend fun deleteAllTracks()

    @Query("""
        DELETE FROM $TABLE_NAME_TRACKS
        WHERE $COLUMN_NAME_REGION_MULTI
        LIKE '%' || :regionCode || '%'
    """)
    suspend fun deleteAllTracksByRegionCode(
        regionCode: String
    )

    @Query("""
        DELETE FROM $TABLE_NAME_TRACKS
        WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
    """)
    suspend fun deleteTrack(id: String)

    @Query("""
        SELECT EXISTS(
            SELECT * FROM $TABLE_NAME_TRACKS
            WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
        )
    """)
    suspend fun doesTrackExist(id: String): Boolean

    @Query("""
        SELECT * FROM $TABLE_NAME_TRACKS
        WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
    """)
    suspend fun getTrackById(id: String): TrackEntity

    @RawQuery(
        observedEntities = [
            TrackEntity::class,
            SettingEntity::class
        ]
    )
    fun getTrackCountByRegionCode(
        query: SimpleSQLiteQuery
    ) : Flow<Int>

    @Query("""
        SELECT $COLUMN_NAME_COMMON_ASSET_ID
        FROM $TABLE_NAME_TRACKS
    """)
    suspend fun getTrackIds(): List<String>

    @Query("""
        SELECT $COLUMN_NAME_COMMON_ASSET_ID
        FROM $TABLE_NAME_TRACKS
        WHERE $COLUMN_NAME_REGION_MULTI
        LIKE '%' || :regionCode || '%'
    """)
    suspend fun getTrackIdsByRegionCode(
        regionCode: String
    ) : List<String>

    @RawQuery
    suspend fun getTrackIdsNotDownloaded(
        query: SimpleSQLiteQuery
    ) : List<String>

    @RawQuery(
        observedEntities = [
            AffectedExtraEntity::class,
            TrackEntity::class,
            FaveEntity::class
        ]
    )
    fun getTrackKt(
        query: SimpleSQLiteQuery
    ) : Flow<TrackKt>

    @RawQuery(
        observedEntities = [
            AffectedExtraEntity::class,
            TrackEntity::class,
            RegionEntity::class
        ]
    )
    fun getTracksKtFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<TrackKt>>

    @Query("""
        SELECT COUNT($COLUMN_NAME_REGION_MULTI)
        FROM $TABLE_NAME_TRACKS
        WHERE LENGTH($COLUMN_NAME_REGION_MULTI) > 0
    """)
    fun getTracksDloadCount(): Flow<Int>

    @Update
    suspend fun updateTrack(track: TrackEntity): Int

    @Query("""
        UPDATE $TABLE_NAME_TRACKS
        SET   $COLUMN_NAME_COMMON_RESP_CODE = :responseCode
        WHERE $COLUMN_NAME_COMMON_ASSET_ID  = :assetId
    """)
    suspend fun updateTrackWithResponseCode(
        assetId: String, responseCode: Int
    )

    @Upsert
    suspend fun upsertTracks(tracks: List<TrackEntity>): List<Long>
}