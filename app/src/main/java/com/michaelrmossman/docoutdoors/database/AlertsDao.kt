package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.docoutdoors.model.AffectedEntity
import com.michaelrmossman.docoutdoors.model.AlertEntity
import com.michaelrmossman.docoutdoors.model.AlertExtraEntity
import com.michaelrmossman.docoutdoors.model.FaveEntity
import com.michaelrmossman.docoutdoors.model.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {

    @Query("""
        DELETE FROM $TABLE_NAME_ALERTS
        WHERE $COLUMN_NAME_REGION_MULTI
        LIKE '%' || :regionCode || '%'
    """)
    suspend fun deleteAlertsByRegionCode(regionCode: String): Int

    @Query("DELETE FROM $TABLE_NAME_ALERTS")
    suspend fun deleteAllAlerts()

    @Query("""
        DELETE FROM $TABLE_NAME_ALERTS_EXTRA
        WHERE $COLUMN_NAME_COMMON_ITEM_TYPE = :itemType
    """)
    suspend fun deleteAllAlertExtras(itemType: String): Int

    @Query("""
        SELECT * FROM $TABLE_NAME_ALERTS
        WHERE $COLUMN_NAME_COMMON_ITEM_ID = :id
    """)
    suspend fun getAlertById(id: String): AlertEntity

    @Query("SELECT COUNT(*) FROM $TABLE_NAME_ALERTS")
    fun getAlertCountAllRegions(): Flow<Int>

    @RawQuery(
        observedEntities = [
            AlertEntity::class,
            SettingEntity::class
        ]
    )
    fun getAlertCountByRegionCode(
        query: SimpleSQLiteQuery
    ) : Flow<Int>

    @Query("""
        SELECT * FROM $TABLE_NAME_ALERTS_EXTRA
        WHERE $COLUMN_NAME_COMMON_ASSET_ID = :assetId
        AND $COLUMN_NAME_COMMON_ITEM_TYPE = :itemType
    """)
    suspend fun getAlertExtraByItemId(
        assetId: String, itemType: String
    ) : AlertExtraEntity

    @Query("""
        SELECT $COLUMN_NAME_COMMON_ITEM_ID
        FROM $TABLE_NAME_ALERTS
    """)
    suspend fun getAlertIds(): List<String>

    @Query("""
        SELECT $COLUMN_NAME_COMMON_ITEM_ID
        FROM $TABLE_NAME_ALERTS
        WHERE $COLUMN_NAME_REGION_MULTI
        LIKE '%' || :regionCode || '%'
    """)
    suspend fun getAlertIdsByRegionCode(
        regionCode: String
    ) : List<String>

    @RawQuery
    suspend fun getAlertsByRegionId(
        query: SimpleSQLiteQuery
    ) : List<AlertEntity>

    @RawQuery(
        observedEntities = [
            AlertEntity::class,
            AffectedEntity::class,
            FaveEntity::class
        ]
    )
    fun getAlertsFlow(
        query: SimpleSQLiteQuery
    ) : Flow<List<AlertEntity>>

    @Query("SELECT * FROM $TABLE_NAME_ALERTS")
    suspend fun getAlertsList(): List<AlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlertExtras(alerts: List<AlertExtraEntity>)

    @Update
    suspend fun updateAlert(alert: AlertEntity): Int

    @Query("""
        UPDATE $TABLE_NAME_ALERTS
        SET $COLUMN_NAME_COMMON_RESP_CODE = :responseCode
        WHERE $COLUMN_NAME_COMMON_ITEM_ID = :id
    """)
    suspend fun updateAlertWithResponseCode(
        id: String, responseCode: Int
    )
}