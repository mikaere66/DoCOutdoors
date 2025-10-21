package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.docoutdoors.model.AffectedExtraEntity
import com.michaelrmossman.docoutdoors.model.CampsiteEntity
import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.model.FaveEntity
import com.michaelrmossman.docoutdoors.model.RegionEntity
import com.michaelrmossman.docoutdoors.model.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampsitesDao {

    @Query("DELETE FROM $TABLE_NAME_CAMPSITES")
    suspend fun deleteAllCampsites()

    @Query("""
        DELETE FROM $TABLE_NAME_CAMPSITES
        WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
    """)
    suspend fun deleteCampsite(id: String)

    @Query("""
        SELECT EXISTS(
            SELECT * FROM $TABLE_NAME_CAMPSITES
            WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
        )
    """)
    suspend fun doesCampsiteExist(id: String): Boolean

    @Query("""
        SELECT * FROM $TABLE_NAME_CAMPSITES
        WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
    """)
    suspend fun getCampsiteById(id: String): CampsiteEntity

    @RawQuery(
        observedEntities = [
            CampsiteEntity::class,
            SettingEntity::class
        ]
    )
    fun getCampsiteCountByRegionCode(
        query: SimpleSQLiteQuery
    ) : Flow<Int>

    @Query("""
        SELECT $COLUMN_NAME_COMMON_ASSET_ID
        FROM $TABLE_NAME_CAMPSITES
    """)
    suspend fun getCampsiteIds(): List<String>

    @Query("""
        SELECT $COLUMN_NAME_COMMON_ASSET_ID
        FROM $TABLE_NAME_CAMPSITES
        WHERE $COLUMN_NAME_REGION_SINGLE
        = :regionCode
    """)
    suspend fun getCampsiteIdsByRegionCode(
        regionCode: String
    ) : List<String>

    @RawQuery
    suspend fun getCampsiteIdsNotDownloaded(
        query: SimpleSQLiteQuery
    ) : List<String>

    @RawQuery(
        observedEntities = [
            AffectedExtraEntity::class,
            CampsiteEntity::class,
            FaveEntity::class
        ]
    )
    fun getCampsiteKt(
        query: SimpleSQLiteQuery
    ) : Flow<CampsiteKt>

    @RawQuery(
        observedEntities = [
            AffectedExtraEntity::class,
            CampsiteEntity::class,
            RegionEntity::class
        ]
    )
    fun getCampsitesKtFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<CampsiteKt>>

    @Query("""
        SELECT COUNT($COLUMN_NAME_COMMON_ASSET_NAME)
        FROM $TABLE_NAME_CAMPSITES
        WHERE LENGTH($COLUMN_NAME_COMMON_ASSET_NAME) > 0
    """)
    fun getCampsiteNameCount(): Flow<Int>

    @Query("""
        SELECT * FROM $TABLE_NAME_CAMPSITES
        ORDER BY $COLUMN_NAME_COMMON_ASSET_NAME
        COLLATE UNICODE
    """)
    suspend fun getCampsitesList(): List<CampsiteEntity>

    @Update
    suspend fun updateCampsite(campsite: CampsiteEntity): Int

    @Query("""
        UPDATE $TABLE_NAME_CAMPSITES
        SET   $COLUMN_NAME_COMMON_RESP_CODE = :responseCode
        WHERE $COLUMN_NAME_COMMON_ASSET_ID  = :assetId
    """)
    suspend fun updateCampsiteWithResponseCode(
        assetId: String, responseCode: Int
    )

    @Upsert
    suspend fun upsertCampsites(campsites: List<CampsiteEntity>): List<Long>
}