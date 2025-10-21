package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.docoutdoors.model.AffectedEntity
import com.michaelrmossman.docoutdoors.model.AffectedExtraEntity

@Dao
interface AffectedDao {

    @Query("""
        DELETE FROM $TABLE_NAME_AFFECTED
        WHERE affectedId = :id
    """)
    suspend fun deleteAffectedByAlertId(
        id: String
    )

    @Query("DELETE FROM $TABLE_NAME_AFFECTED")
    suspend fun deleteAllAffected()

    @Query("""
        DELETE FROM $TABLE_NAME_AFFECTED_EXTRA
        WHERE $COLUMN_NAME_COMMON_ITEM_TYPE = :itemType
    """)
    suspend fun deleteAllAffectedExtras(itemType: String): Int

    @Query("""
        DELETE FROM $TABLE_NAME_AFFECTED_EXTRA
        WHERE $COLUMN_NAME_COMMON_ITEM_TYPE = :itemType
        AND   $COLUMN_NAME_COMMON_ASSET_ID  = :assetId
    """)
    suspend fun deleteAffectedExtraByAssetId(
        assetId: String, itemType: String
    )

    @Query("""
        SELECT * FROM $TABLE_NAME_AFFECTED
        WHERE affectedId = :id
    """)
    suspend fun getAffectedByAlertId(
        id: String
    ) : List<AffectedEntity>

    @Query("""
        SELECT * FROM $TABLE_NAME_AFFECTED
        WHERE affectId = :affectId
        AND type = :itemType
    """)
    suspend fun getAffectedByItemId(
        affectId: String,
        itemType: String
    ) : AffectedEntity

    @Query("""
        SELECT COUNT(*) FROM $TABLE_NAME_AFFECTED
        WHERE affectId = :affectId
        AND type = :itemType
    """)
    suspend fun getAffectedCountByItemId(
        affectId: String,
        itemType: String
    ) : Int

    @Query("""
        SELECT * FROM $TABLE_NAME_AFFECTED_EXTRA
        WHERE $COLUMN_NAME_COMMON_ASSET_ID = :assetId
        AND $COLUMN_NAME_COMMON_ITEM_TYPE = :itemType
    """)
    suspend fun getAffectedExtrasByItemId(
        assetId: String,
        itemType: String
    ) : List<AffectedExtraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAffected(
        affectedList: List<AffectedEntity>
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAffectedExtras(
        affectedList: List<AffectedExtraEntity>
    )
}