package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.docoutdoors.model.RegionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionsDao {

    @Query("""
        SELECT COUNT(*) FROM $TABLE_NAME_REGIONS
        WHERE $COLUMN_NAME_REGION_ALERTS_DLOAD
        = 1
    """) // Refer note RegionsRepository
    fun getDloadAlertsCount(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM $TABLE_NAME_REGIONS
        WHERE $COLUMN_NAME_REGION_TRACKS_DLOAD
        = 1
    """) // Refer note RegionsRepository
    fun getDloadTracksCount(): Flow<Int>

    @Query("""
        SELECT $COLUMN_NAME_REGION_SINGLE
        FROM $TABLE_NAME_REGIONS
        WHERE $COLUMN_NAME_COMMON_ITEM_ID
        = :id
    """) // e.g. 17 to NZ-CAN
    suspend fun getRegionCodeByActualId(id: Int): String

    @RawQuery
    fun getRegionNameByRegionId(query: SimpleSQLiteQuery): String

    @Query("""
        SELECT $COLUMN_NAME_REGION_SINGLE
        FROM $TABLE_NAME_REGIONS
        WHERE $COLUMN_NAME_COMMON_ITEM_ID
        = :id
    """) // e.g. 17 to NZ-CAN
    fun getRegionCodeByActualIdFlow(id: Int): Flow<String>

    @Query("""
        SELECT $COLUMN_NAME_REGION_NAME
        FROM $TABLE_NAME_REGIONS
        WHERE $COLUMN_NAME_REGION_SINGLE
        = :code
    """) // e.g. NZ-CAN to Canterbury
    suspend fun getRegionNameByRegionCode(code: String): String

    @Query("SELECT * FROM $TABLE_NAME_REGIONS")
    suspend fun getRegionsList(): List<RegionEntity>

    @Query("SELECT COUNT(*) FROM $TABLE_NAME_REGIONS")
    fun getRegionTotalCount(): Flow<Int>

    @Insert
    suspend fun insertRegions(region: List<RegionEntity>)

    @Query("""
        UPDATE $TABLE_NAME_REGIONS
        SET   $COLUMN_NAME_REGION_ALERTS_DLOAD
        = 0
        WHERE $COLUMN_NAME_REGION_ALERTS_DLOAD
        = 1
    """)
    suspend fun resetAlertsDload()

    @Query("""
        UPDATE $TABLE_NAME_REGIONS
        SET   $COLUMN_NAME_REGION_TRACKS_DLOAD
        = 0
        WHERE $COLUMN_NAME_REGION_TRACKS_DLOAD
        = 1
    """)
    suspend fun resetTracksDload()

    @Query("""
        UPDATE $TABLE_NAME_REGIONS
        SET $COLUMN_NAME_REGION_ALERTS_DLOAD
        = 1
        WHERE $COLUMN_NAME_COMMON_ITEM_ID
        = :id
    """)
    suspend fun setAlertsDload(id: Int)

    @Query("""
        UPDATE $TABLE_NAME_REGIONS
        SET $COLUMN_NAME_REGION_TRACKS_DLOAD
        = 1
        WHERE $COLUMN_NAME_COMMON_ITEM_ID
        = :id
    """)
    suspend fun setTracksDload(id: Int)
}