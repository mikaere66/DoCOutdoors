package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_ALERTS_EXTRA
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_ALERT_EXTRA_COUNT
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ITEM_ID
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ITEM_TYPE
import com.michaelrmossman.docoutdoors.enums.AssetType

@Entity(tableName = TABLE_NAME_ALERTS_EXTRA) // ~ 80-300
data class AlertExtraEntity(

    /* Labels in same order as original JSON response. Just
     * for Campsites and Huts (with Int as assetId) ... see
     * also TrackExtraSerial which has String as assetId */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_NAME_COMMON_ITEM_ID)
    val id: Int = 0, // Added

    @ColumnInfo      // Json
    val assetId: String,

    @ColumnInfo      // Json
    val name: String,

    // Added to keep track of how many actual AffectedExtraEntity items
    @ColumnInfo(name = COLUMN_NAME_ALERT_EXTRA_COUNT)
    val itemCount: Int,

    // Added to distinguish between Campsite, Hut, or Track asset types
    @ColumnInfo(name = COLUMN_NAME_COMMON_ITEM_TYPE)
    val itemType: String
) {
    companion object {
        fun from(
            alert: AlertExtraSerial,
            itemCount: Int,
            itemType: AssetType
        ) : AlertExtraEntity {
            return AlertExtraEntity(
                assetId = alert.assetId.toString(),
                name = alert.name,
                itemCount = itemCount,
                itemType = itemType.name
            )
        }

        fun from(
            alert: TrackExtraSerial,
            itemCount: Int
        ) : AlertExtraEntity {
            return AlertExtraEntity(
                assetId = alert.assetId,
                name = alert.name,
                itemCount = itemCount,
                itemType = AssetType.Track.name
            )
        }
    }

    fun toAlertExtra(
        affectedExtras: List<AffectedExtraEntity>
    ) : AlertExtra {
        return AlertExtra(
            id,
            assetId,
            name,
            itemCount,
            itemType,
            affectedExtras
        )
    }
}