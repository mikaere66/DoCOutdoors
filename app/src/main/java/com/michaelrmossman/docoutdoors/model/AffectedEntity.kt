package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_AFFECT_ID
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_AFFECTED
import com.michaelrmossman.docoutdoors.utils.MapUtils.EMPTY_LAT_LNG
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = TABLE_NAME_AFFECTED) // ?
data class AffectedEntity(

    val affectedId: String, // the actual/original affected

    // Labels in same order as original JSON response
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COLUMN_NAME_COMMON_AFFECT_ID)
    val affectId: String, // the "affectedAsset" id

    @ColumnInfo
    val assetId: String, // the "asset affected" id(s)

    @ColumnInfo
    val name: String, // the "asset affected" name

    @ColumnInfo
    val type: String, // CAMPSITE | HUNTING AREA | HUT | LODGE | MOUNTAIN BIKE TRACK | PLACE | WALK TRACK

    @ColumnInfo
    val docUrl: String,

    @ColumnInfo
    val lon: Double,

    @ColumnInfo
    val lat: Double
) {
    companion object {
        fun from(affected: AffectedSerial, alertId: String): AffectedEntity {
            return AffectedEntity(
                affectedId = alertId,
                affected.id ?: String(),
                affected.assetId ?: String(),
                affected.name,
                affected.type,
                affected.docUrl,
                affected.lon ?: 0.0,
                affected.lat ?: 0.0
            )
        }
    }

    fun toAffected(affectedCount: Int): Affected {
        return Affected(
            affectedId,
            affectId,
            assetId,
            name,
            type,
            docUrl,
            LatLng(lat,lon),
            affectedCount
        )
    }
}