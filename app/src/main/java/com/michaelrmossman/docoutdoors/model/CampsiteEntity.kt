package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_CAMPSITES
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ASSET_ID
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ASSET_NAME
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_BOOKABLE
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_DOGS_ALLOWED
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_LOCAT_STRING
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_RESP_CODE
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_REGION_SINGLE
import com.michaelrmossman.docoutdoors.database.DbHelpers.getUnknownRegionOrCodeByRegionOrAssetId
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.utils.ITEM_SEPARATOR
import com.michaelrmossman.docoutdoors.utils.replaceApos
import com.michaelrmossman.docoutdoors.utils.replaceCRLF
import java.util.UUID

@Entity(tableName = TABLE_NAME_CAMPSITES) // ~ 330
data class CampsiteEntity(

    // Labels in same order as original JSON responses:
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COLUMN_NAME_COMMON_ASSET_ID)
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
    val introduction: String = String(),

    @ColumnInfo
    val introductionThumbnail: String = String(),

    @ColumnInfo
    val landscape: String = String(), // Array

    @ColumnInfo
    val campsiteCategory: String = String(),

    @ColumnInfo
    val access: String = String(), // Array

    @ColumnInfo
    val facilities: String = String(), // Array

    @ColumnInfo
    val activities: String = String(), // Array

    @ColumnInfo(name = COLUMN_NAME_COMMON_DOGS_ALLOWED)
    val dogsAllowed: String = String(), // Prepackaged

    @ColumnInfo
    val numberOfPoweredSites: Int? = null,

    @ColumnInfo
    val numberOfUnpoweredSites: Int? = null,

    @ColumnInfo(name = COLUMN_NAME_COMMON_BOOKABLE)
    val bookable: Boolean? = null, // Prepackaged

    @ColumnInfo
    val staticLink: String = String(),

    // (region already above)

    @ColumnInfo
    val place: String = String(),

    // (status already above)
    // (lat already above)
    // (lon already above)

    /* Added for filtering */
    @ColumnInfo(name = COLUMN_NAME_REGION_SINGLE)
    val regionCode: String,

    /* Added to monitor bad HTTP requests */
    @ColumnInfo(name = COLUMN_NAME_COMMON_RESP_CODE)
    val responseCode: Int
) {
    companion object {
        fun empty(): CampsiteEntity {
            return CampsiteEntity(
                assetId = UUID.randomUUID().toString(),
                name = "Some campsite out in the wops",
                status = "Closed",
                region = "Canterbury",
                lat = 0.0, lon = 0.0,
                regionCode = "NZ-CAN",
                responseCode = 200
            )
        }

        fun from(
            campsite: CampsiteSerial,
            responseCode: Int
        ) : CampsiteEntity {
            val region: String? = campsite.region?.replaceApos()
            val regionWithCode = getUnknownRegionOrCodeByRegionOrAssetId(
                assetId = campsite.assetId.toString(),
                assetType = AssetType.Campsite,
                region = region
            )
            return CampsiteEntity(
                /* These have a numeric id, so for compatibility
                   & simplicity, convert/store it as a string */
                campsite.assetId.toString(),
                campsite.name,
                campsite.status,
                campsite.region ?: regionWithCode.second,
                campsite.lat ?: 0.0,
                campsite.lon ?: 0.0,
                campsite.locationString ?: String(),
                campsite.introduction?.replaceCRLF() ?: String(),
                campsite.introductionThumbnail ?: String(),
                campsite.landscape?.joinToString(
                    ITEM_SEPARATOR               // Array
                ) ?: String(),
                campsite.campsiteCategory ?: String(),
                campsite.access?.joinToString(
                    ITEM_SEPARATOR               // Array
                ) ?: String(),
                campsite.facilities?.joinToString(
                    ITEM_SEPARATOR               // Array
                ) ?: String(),
                campsite.activities?.joinToString(
                    ITEM_SEPARATOR               // Array
                ) ?: String(),
                campsite.dogsAllowed ?: String(),
                campsite.numberOfPoweredSites,   // Nullable
                campsite.numberOfUnpoweredSites, // Nullable
                campsite.bookable,               // Nullable
                campsite.staticLink ?: String(),
                campsite.place ?: String(),
                regionWithCode.first,
                responseCode
            )
        }
    }

    fun toCampsiteKt(affectedCount: Int, isFavourite: Boolean): CampsiteKt {
        return CampsiteKt(
            assetId,
            name,
            status,
            region,
            lat,
            lon,
            locationString,
            introduction,
            introductionThumbnail,
            landscape,
            campsiteCategory,
            access,
            facilities,
            activities,
            dogsAllowed,
            numberOfPoweredSites,
            numberOfUnpoweredSites,
            bookable,
            staticLink,
            place,
            regionCode,
            affectedCount,
            isFavourite
        )
    }
}