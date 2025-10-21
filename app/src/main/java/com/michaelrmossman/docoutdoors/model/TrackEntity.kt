package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ASSET_NAME
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_DOGS_ALLOWED
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_LOCAT_STRING
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_RESP_CODE
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_REGION_MULTI
import com.michaelrmossman.docoutdoors.database.DbHelpers.getUnknownRegionOrCodeByRegionOrAssetId
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_TRACKS
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.utils.ITEM_SEPARATOR
import com.michaelrmossman.docoutdoors.utils.JsonUtils.parseJsonArrayAsStrings
import com.michaelrmossman.docoutdoors.utils.replaceCRLF
import java.util.UUID

@Entity(tableName = TABLE_NAME_TRACKS) // ~ 1400
data class TrackEntity(

    // Labels in same order as original JSON response:
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    val assetId: String,

    @ColumnInfo(name = COLUMN_NAME_COMMON_ASSET_NAME)
    val name: String,

    @ColumnInfo
    val regions: String, // Array

    @ColumnInfo
    val lon: Double,

    @ColumnInfo
    val lat: Double,

    /* Extended info (requires a
       separate d/load per item).
       Initialise all strings as
       empty Strings rather than
       null to avoid any NPEs */

    // (assetId already above)
    // (name already above)

    @ColumnInfo
    val introduction: String = String(),

    @ColumnInfo
    val introductionThumbnail: String = String(),

    @ColumnInfo
    val permittedActivities: String = String(), // Array

    @ColumnInfo
    val distance: String = String(),

    @ColumnInfo
    val walkDuration: String = String(),

    @ColumnInfo
    val walkDurationCategory: String = String(), // Array

    @ColumnInfo
    val walkTrackCategory: String = String(), // Array

    @ColumnInfo
    val wheelchairsAndBuggies: String = String(),

    @ColumnInfo
    val mtbDuration: String = String(),

    @ColumnInfo
    val mtbDurationCategory: String = String(), // Array

    @ColumnInfo
    val mtbTrackCategory: String = String(), // Array

    @ColumnInfo
    val kayakingDuration: String = String(),

    @ColumnInfo(name = COLUMN_NAME_COMMON_DOGS_ALLOWED)
    val dogsAllowed: String = String(), // Prepackaged

    @ColumnInfo(name = COLUMN_NAME_COMMON_LOCAT_STRING)
    val locationString: String = String(),

    @ColumnInfo
    val locationArray: String = String(), // Array

    @ColumnInfo
    val staticLink: String = String(),

    // (region already above)
    // (lat already above)
    // (lon already above)

    @ColumnInfo
    val lineCount: Int = 0,

    @ColumnInfo(name = COLUMN_NAME_REGION_MULTI)
    val regionCodes: String, // Array

    /* Added to monitor bad HTTP requests */
    @ColumnInfo(name = COLUMN_NAME_COMMON_RESP_CODE)
    val responseCode: Int
) {
    companion object {
        fun empty(): TrackEntity {
            return TrackEntity(
                assetId = UUID.randomUUID().toString(),
                name = "Some track out in the wops",
                regions = "Canterbury",
                lat = 0.0, lon = 0.0,
                regionCodes = "NZ-CAN",
                responseCode = 200
            )
        }

        fun from(
            lineCount: Int,
            responseCode: Int,
            track: TrackSerial
        ) : TrackEntity {
            /* replaceApos() handled by extension function */
            val regions = parseJsonArrayAsStrings(track.region)
            /* Not "simplified", to maintain "readability" */
            val regionsWithCodes = regions.split(ITEM_SEPARATOR).map { region ->
                getUnknownRegionOrCodeByRegionOrAssetId(
                    assetId = track.assetId,
                    assetType = AssetType.Track,
                    region = region
                ).first
            }
            return TrackEntity(
                track.assetId,
                track.name,
                regions,
                track.lon ?: 0.0,
                track.lat ?: 0.0,
                track.introduction?.replaceCRLF() ?: String(),
                track.introductionThumbnail ?: String(),
                track.permittedActivities?.joinToString(
                    ITEM_SEPARATOR // Array
                ) ?: String(),
                track.distance ?: String(),
                track.walkDuration ?: String(),
                track.walkDurationCategory?.joinToString(
                    ITEM_SEPARATOR // Array
                ) ?: String(),
                track.walkTrackCategory?.joinToString(
                    ITEM_SEPARATOR // Array
                ) ?: String(),
                track.wheelchairsAndBuggies ?: String(),
                track.mtbDuration ?: String(),
                track.mtbDurationCategory?.joinToString(
                    ITEM_SEPARATOR // Array
                ) ?: String(),
                track.mtbTrackCategory?.joinToString(
                    ITEM_SEPARATOR // Array
                ) ?: String(),
                track.kayakingDuration ?: String(),
                track.dogsAllowed ?: String(),
                track.locationString ?: String(),
                track.locationArray?.joinToString(
                    ITEM_SEPARATOR // Array
                ) ?: String(),
                track.staticLink ?: String(),
                lineCount,
                regionsWithCodes.joinToString(
                    ITEM_SEPARATOR // Array
                ),
                responseCode
            )
        }
    }

    fun toTrackKt(affectedCount: Int, isFavourite: Boolean): TrackKt {
        return TrackKt(
            assetId,
            name,
            regions,
            lat,
            lon,
            introduction,
            introductionThumbnail,
            permittedActivities,
            distance,
            walkDuration,
            walkDurationCategory,
            walkTrackCategory,
            wheelchairsAndBuggies,
            mtbDuration,
            mtbDurationCategory,
            mtbTrackCategory,
            kayakingDuration,
            dogsAllowed,
            locationString,
            locationArray,
            staticLink,
            lineCount,
            regionCodes,
            affectedCount,
            isFavourite
        )
    }
}