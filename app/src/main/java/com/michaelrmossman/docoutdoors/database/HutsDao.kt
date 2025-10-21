package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.docoutdoors.model.AffectedExtraEntity
import com.michaelrmossman.docoutdoors.model.FaveEntity
import com.michaelrmossman.docoutdoors.model.HutEntity
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.model.RegionEntity
import com.michaelrmossman.docoutdoors.model.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HutsDao {

    @Query("DELETE FROM $TABLE_NAME_HUTS")
    suspend fun deleteAllHuts()

    @Query("""
        DELETE FROM $TABLE_NAME_HUTS
        WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
    """)
    suspend fun deleteHut(id: String)

    @Query("""
        SELECT EXISTS(
            SELECT * FROM $TABLE_NAME_HUTS
            WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
        )
    """)
    suspend fun doesHutExist(id: String): Boolean

    @Query("""
        SELECT * FROM $TABLE_NAME_HUTS
        WHERE $COLUMN_NAME_COMMON_ASSET_ID = :id
    """)
    suspend fun getHutById(id: String): HutEntity

    @RawQuery(
        observedEntities = [
            HutEntity::class,
            SettingEntity::class
        ]
    )
    fun getHutCountByRegionCode(
        query: SimpleSQLiteQuery
    ) : Flow<Int>

    @Query("""
        SELECT $COLUMN_NAME_COMMON_ASSET_ID
        FROM $TABLE_NAME_HUTS
    """)
    suspend fun getHutIds(): List<String>

    @Query("""
        SELECT $COLUMN_NAME_COMMON_ASSET_ID
        FROM $TABLE_NAME_HUTS
        WHERE $COLUMN_NAME_REGION_SINGLE
        = :regionCode
    """)
    suspend fun getHutIdsByRegionCode(
        regionCode: String
    ) : List<String>

    @RawQuery
    suspend fun getHutIdsNotDownloaded(
        query: SimpleSQLiteQuery
    ) : List<String>

    @RawQuery(
        observedEntities = [
            AffectedExtraEntity::class,
            HutEntity::class,
            FaveEntity::class
        ]
    )
    fun getHutKt(
        query: SimpleSQLiteQuery
    ) : Flow<HutKt>

    @RawQuery(
        observedEntities = [
            AffectedExtraEntity::class,
            HutEntity::class,
            RegionEntity::class
        ]
    )
    fun getHutsKtFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<HutKt>>

    @Query("""
        SELECT COUNT($COLUMN_NAME_COMMON_ASSET_NAME)
        FROM $TABLE_NAME_HUTS
        WHERE LENGTH($COLUMN_NAME_COMMON_ASSET_NAME) > 0
    """)
    fun getHutNameCount(): Flow<Int>

    @Update
    suspend fun updateHut(hut: HutEntity): Int

    @Query("""
        UPDATE $TABLE_NAME_HUTS
        SET   $COLUMN_NAME_COMMON_RESP_CODE = :responseCode
        WHERE $COLUMN_NAME_COMMON_ASSET_ID  = :assetId
    """)
    suspend fun updateHutWithResponseCode(
        assetId: String, responseCode: Int
    )

    @Upsert
    suspend fun upsertHuts(huts: List<HutEntity>): List<Long>
}