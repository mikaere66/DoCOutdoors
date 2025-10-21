package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ASSET_NAME
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_BOOKABLE
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_LOCAT_STRING
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_RESP_CODE
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_REGION_SINGLE
import com.michaelrmossman.docoutdoors.database.DbHelpers.getUnknownRegionOrCodeByRegionOrAssetId
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_HUTS
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.utils.ITEM_SEPARATOR
import com.michaelrmossman.docoutdoors.utils.replaceApos
import com.michaelrmossman.docoutdoors.utils.replaceCRLF
import java.util.UUID

@Entity(tableName = TABLE_NAME_HUTS) // ~ 950
data class HutEntity(

    // Labels in same order as original JSON response:
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    /* Starts out as an integer ... see note below */
    val assetId: String,

    @ColumnInfo(name = COLUMN_NAME_COMMON_ASSET_NAME)
    val name: String,

    @ColumnInfo
    val status: String,

    @ColumnInfo
    val region: String?,

    @ColumnInfo
    val lat: Double,

    @ColumnInfo
    val lon: Double,

    /* Extended info (requires a
       separate d/load per item).
       Initialise all strings as
       empty Strings rather than
       null to avoid any NPEs */

    // (assetId already above)
    // (name already above)

    @ColumnInfo(name = COLUMN_NAME_COMMON_LOCAT_STRING)
    val locationString: String = String(),

    @ColumnInfo
    val numberOfBunks: Int? = null,

    @ColumnInfo
    val facilities: String = String(), // Array

    @ColumnInfo
    val hutCategory: String = String(),

    @ColumnInfo
    val proximityToRoadEnd: String = String(),

    @ColumnInfo(name = COLUMN_NAME_COMMON_BOOKABLE)
    val bookable: Boolean? = null, // Prepackaged

    @ColumnInfo
    val introduction: String = String(),

    @ColumnInfo
    val introductionThumbnail: String = String(),

    @ColumnInfo
    val staticLink: String = String(),

    // (region already above)

    @ColumnInfo
    val place: String = String(),

    // (status already above)
    // (lon already above)
    // (lat already above)

    /* Added for filtering */
    @ColumnInfo(name = COLUMN_NAME_REGION_SINGLE)
    val regionCode: String,

    /* Added to monitor bad HTTP requests */
    @ColumnInfo(name = COLUMN_NAME_COMMON_RESP_CODE)
    val responseCode: Int
) {
    companion object {
        fun empty(): HutEntity {
            return HutEntity(
                assetId = UUID.randomUUID().toString(),
                name = "Some hut out in the wops",
                status = "Closed",
                region = "Canterbury",
                lat = 0.0, lon = 0.0,
                regionCode = "NZ-CAN",
                responseCode = 0
            )
        }

        fun from(
            hut: HutSerial,
            responseCode: Int
        ) : HutEntity {
            val region: String? = hut.region?.replaceApos()
            val regionWithCode = getUnknownRegionOrCodeByRegionOrAssetId(
                assetId = hut.assetId.toString(),
                assetType = AssetType.Hut,
                region = region
            )
            return HutEntity(
                /* These have a numeric id, so for compatibility
                   & simplicity, convert/store it as a string */
                hut.assetId.toString(),
                hut.name,
                hut.status ?: String(),
                region ?: regionWithCode.second,
                hut.lat ?: 0.0,
                hut.lon ?: 0.0,
                hut.locationString ?: String(),
                hut.numberOfBunks,
                hut.facilities?.joinToString(
                    ITEM_SEPARATOR // Array
                ) ?: String(),
                hut.hutCategory ?: String(),
                hut.proximityToRoadEnd ?: String(),
                hut.bookable,      // Nullable
                hut.introduction?.replaceCRLF() ?: String(),
                hut.introductionThumbnail ?: String(),
                hut.staticLink ?: String(),
                hut.place ?: String(),
                regionWithCode.first,
                responseCode
            )
        }
    }

    fun toHutKt(affectedCount: Int, isFavourite: Boolean): HutKt {
        return HutKt(
            assetId,
            name,
            status,
            region,
            lat,
            lon,
            locationString,
            numberOfBunks,
            facilities,
            hutCategory,
            proximityToRoadEnd,
            bookable,
            introduction,
            introductionThumbnail,
            staticLink,
            place,
            regionCode,
            affectedCount,
            isFavourite
        )
    }
}