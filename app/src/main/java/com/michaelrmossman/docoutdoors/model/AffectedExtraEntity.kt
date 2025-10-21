package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_AFFECTED_EXTRA
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ITEM_ID
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ASSET_ID
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ITEM_TYPE
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.utils.replaceCRLF

@Entity(tableName = TABLE_NAME_AFFECTED_EXTRA) // ~ 90-350
data class AffectedExtraEntity(

    /* Labels in same order as original JSON response ...
     * Used by both AlertExtraSerial and TrackExtraSerial */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_NAME_COMMON_ITEM_ID)
    val id: Int = 0, // Added

    @ColumnInfo      // Json
    val displayDate: String,

    @ColumnInfo      // Json
    val heading: String,

    @ColumnInfo      // Json
    val detail: String,

    // Added as ref. for parent AlertAssetEntity or TrackExtraSerial id
    @ColumnInfo(name = COLUMN_NAME_COMMON_ASSET_ID)
    val assetId: String,

    // Added to distinguish between Campsite, Hut, or Track asset types
    @ColumnInfo(name = COLUMN_NAME_COMMON_ITEM_TYPE)
    val itemType: String
) {
    companion object {
        fun from(
            alert: AffectedExtraSerial,
            assetId: String,
            itemType: AssetType
        ) : AffectedExtraEntity {
            return AffectedExtraEntity(
                displayDate = alert.displayDate,
                heading = alert.heading,
                detail = alert.detail.replaceCRLF(),
                assetId = assetId,
                itemType = itemType.name
            )
        }
    }
}