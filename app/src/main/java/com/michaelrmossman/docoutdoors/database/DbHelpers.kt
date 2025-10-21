package com.michaelrmossman.docoutdoors.database

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.docoutdoors.OutdoorsApplication.Companion.instance
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.model.Alert
import com.michaelrmossman.docoutdoors.model.AlertEntity
import com.michaelrmossman.docoutdoors.model.RegionSerial
import com.michaelrmossman.docoutdoors.utils.ITEM_SEPARATOR

object DbHelpers {

    suspend fun AlertEntity.toAlertWithExtras(
        affectedDao: AffectedDao,
        regionsDao: RegionsDao
    ) : Alert {
        val affectedAssets = affectedDao.getAffectedByAlertId(
            this.id
        )

        val regionsList = mutableListOf<RegionSerial>()
        this.regionCodes.split(ITEM_SEPARATOR).forEach { regionCode ->
            if (regionCode.isNotBlank()) {
                val regionName = regionsDao.getRegionNameByRegionCode(
                    code = regionCode.trim() // Just for safety
                )
                regionsList.add(RegionSerial(
                    regionCode = regionCode,
                    regionName = regionName
                ))
            }
        }

        return this.toAlert(
            affectedAssets = affectedAssets,
            regionsList = regionsList,
            /* Don't update "updateMillis" value for
               new alert, as this causes the list to
               jump all over the place while viewing */
            updateMillis = this.updateMillis
        )
    }

    fun getAlertsByRegionIdQuery(regionId: Int): SimpleSQLiteQuery {
        val args: ArrayList<Any> = arrayListOf(regionId)

        val sb = StringBuilder()
        sb.append("SELECT")
        sb.append(" ")
        sb.append("*")
        sb.append(" ")
        sb.append("FROM $TABLE_NAME_ALERTS") // alerts_table
        sb.append(" ")
        sb.append("WHERE $COLUMN_NAME_REGION_MULTI") // regionCodes
        sb.append(" ")
        sb.append("LIKE")
        sb.append(" ")
        sb.append("(SELECT '%' ||")
        sb.append(" ")
        sb.append(COLUMN_NAME_REGION_SINGLE) // regionCode
        sb.append(" ")
        sb.append("|| '%'")
        sb.append(" ")
        sb.append("FROM $TABLE_NAME_REGIONS") // regions_table
        sb.append(" ")
        sb.append("WHERE $COLUMN_NAME_COMMON_ITEM_ID") // id
        sb.append(" ")
        sb.append("= ?)")

        // android.util.Log.d("HEY", sb.toString())
        /*
        SELECT * FROM alerts_table WHERE regionCodes LIKE (SELECT
        '%' || regionCode || '%' FROM regions_table WHERE id = ?)
        */
        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }

    fun getBookableOrDogsQueryWithExtras(
        /* Only for Campsites and Huts */
        bookable: Int = 0,
        /* Only for Campsites and Tracks */
        dogAccess: Int = 0,
        itemType: AssetType,
        regionId: Int
    ) : SimpleSQLiteQuery {
        val args: ArrayList<Any> = arrayListOf()
        val columnRegion = when (itemType) {
            AssetType.Track -> COLUMN_NAME_REGION_MULTI // regionCodes
             /* Campsites and Huts have SINGLE code */
            else            -> COLUMN_NAME_REGION_SINGLE // regionCode
        }
        val sb = StringBuilder()
        val tableName = when (itemType) {
            AssetType.Campsite -> TABLE_NAME_CAMPSITES // campsites_table
            AssetType.Hut      -> TABLE_NAME_HUTS      // huts_table
            AssetType.Track    -> TABLE_NAME_TRACKS    // tracks_table
        }
        args.add(itemType.name) // affectedCount
        args.add(itemType.name) // isFavourite

        sb.append("SELECT")
        sb.append(" ")

        sb.append("(SELECT COUNT(*)")
        sb.append(" ")
        sb.append("FROM $TABLE_NAME_AFFECTED_EXTRA AS A") // affected_extra_table
        sb.append(" ")
        sb.append("WHERE A.$COLUMN_NAME_COMMON_ASSET_ID") // assetId
        sb.append(" ")
        sb.append("= T.$COLUMN_NAME_COMMON_ASSET_ID") // assetId
        sb.append(" ")
        sb.append("AND A.$COLUMN_NAME_COMMON_ITEM_TYPE") // itemType
        sb.append(" ")
        sb.append("= ?)")
        sb.append(" ")
        sb.append("AS affectedCount,")
        sb.append(" ")

        sb.append("(SELECT EXISTS")
        sb.append(" ")
            sb.append("(SELECT *")
            sb.append(" ")
            sb.append("FROM $TABLE_NAME_FAVES AS F") // faves_table
            sb.append(" ")
            sb.append("WHERE F.$COLUMN_NAME_COMMON_ASSET_ID") // assetId
            sb.append(" ")
            sb.append("= T.$COLUMN_NAME_COMMON_ASSET_ID") // assetId
            sb.append(" ")
            sb.append("AND F.$COLUMN_NAME_COMMON_ITEM_TYPE") // itemType
            sb.append(" ")
            sb.append("= ?")
            sb.append(" ")
            sb.append("LIMIT 1)")
        sb.append(") ")
        sb.append("AS isFavourite,")

        sb.append(" ")
        sb.append("T.*")
        sb.append(" ")
        sb.append("FROM $tableName AS T")
        sb.append(" ")

        if (regionId > 0) {
            args.add(regionId)

            sb.append("INNER JOIN $TABLE_NAME_REGIONS AS R") // regions_table
            sb.append(" ")
            sb.append("ON T.$columnRegion")
            sb.append(" ")
            when (itemType) {
                AssetType.Track -> {
                    sb.append("LIKE")
                    sb.append(" ")
                    sb.append("'%' ||")
                    sb.append(" ")
                    sb.append("(")
                    sb.append("SELECT R.$COLUMN_NAME_REGION_SINGLE") // regionCode
                    sb.append(" ")
                    sb.append("WHERE R.$COLUMN_NAME_COMMON_ITEM_ID = ?") // id
                    sb.append(")")
                    sb.append(" ")
                    sb.append("|| '%'")
                }
                else -> {
                    sb.append("=")
                    sb.append(" ")
                    sb.append("(")
                    sb.append("SELECT R.$COLUMN_NAME_REGION_SINGLE") // regionCode
                    sb.append(" ")
                    sb.append("FROM $TABLE_NAME_REGIONS") // regions_table
                    sb.append(" ")
                    sb.append("WHERE R.$COLUMN_NAME_COMMON_ITEM_ID = ?") // id
                    sb.append(")")
                }
            }
            sb.append(" ")
        }

        sb.append("WHERE LENGTH(T.$columnRegion) > 0")
        sb.append(" ")

        if (bookable > 0 || dogAccess > 0) {
            sb.append("AND") // was WHERE
            sb.append(" ")
        }

        if (bookable > 0) {
            // Note 0 for testing ...
            args.add(0)

            // ... with > for testing
            sb.append("T.$COLUMN_NAME_COMMON_BOOKABLE > ?") // bookable
            sb.append(" ")
        }

        if (dogAccess > 0) {
            args.add(getDogAccessArg(dogAccess))

            if (bookable > 0) {
                sb.append("AND")
                sb.append(" ")
            }

            sb.append("T.$COLUMN_NAME_COMMON_DOGS_ALLOWED LIKE ?") // dogsAllowed
            sb.append(" ")
        }

        sb.append("ORDER BY T.$COLUMN_NAME_COMMON_ASSET_NAME") // name
        sb.append(" ")
        /* Allow for Māori special chars when sorting */
        sb.append("COLLATE UNICODE")

        // android.util.Log.d("HEY", sb.toString())
        /*
        SELECT (SELECT COUNT(*) FROM affected_extra_table AS A
        WHERE A.assetId = T.assetId AND A.itemType = ?) AS affectedCount,
        (SELECT EXISTS (SELECT * FROM faves_table AS F
        WHERE F.assetId = T.assetId AND F.itemType = ? LIMIT 1)) AS isFavourite,
        T.* FROM campsites_table AS T INNER JOIN regions_table AS R
        ON T.regionCode = (SELECT R.regionCode FROM regions_table WHERE R.id = ?)
        WHERE LENGTH(T.regionCode) > 0 ORDER BY T.name COLLATE UNICODE
        */
        return when (args.isEmpty()) { // Just for safety
            true -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
        }
    }

    /* Used by four main repos */
    fun getCountByRegionCodeWithExtrasQuery(
        filterType: FilterType,
        regionId: Int,
        /* Only for Campsites and Huts */
        bookable: Int = 0,
        /* Only for Campsites and Tracks */
        dogAccess: Int = 0
    ) : SimpleSQLiteQuery {
        val args: ArrayList<Any> = arrayListOf()
        val columnNameOrSummary = when (filterType) {
            FilterType.Alerts -> COLUMN_NAME_ALERT_SUMMARY  // summary
            else              -> COLUMN_NAME_COMMON_ASSET_NAME // name
        }
        val columnRegion = when (filterType) {
            FilterType.Alerts -> COLUMN_NAME_REGION_MULTI // regionCodes
            FilterType.Tracks -> COLUMN_NAME_REGION_MULTI
             /* Campsites and Huts have SINGLE code */
            else              -> COLUMN_NAME_REGION_SINGLE // regionCode
        }
        val sb = StringBuilder()
        val tableName = when (filterType) {
            FilterType.Alerts    -> TABLE_NAME_ALERTS    // alerts_table
            FilterType.Campsites -> TABLE_NAME_CAMPSITES // campsites_table
            FilterType.Huts      -> TABLE_NAME_HUTS      // huts_table
            FilterType.Tracks    -> TABLE_NAME_TRACKS    // tracks_table
        }

        sb.append("SELECT")
        sb.append(" ")
        sb.append("COUNT($columnNameOrSummary)")
        sb.append(" ")
        sb.append("FROM $tableName AS T")
        sb.append(" ")

        /* Only if filtered by region */
        if (regionId > 0) {
            args.add(regionId)

            sb.append("INNER JOIN $TABLE_NAME_REGIONS AS R") // regions_table
            sb.append(" ")
            sb.append("ON T.$columnRegion")
            sb.append(" ")
            sb.append("LIKE '%' || R.$COLUMN_NAME_REGION_SINGLE || '%'") // regionCode
            sb.append(" ")
            sb.append("WHERE R.$COLUMN_NAME_COMMON_ITEM_ID = ?") // id
            sb.append(" ")
        }

        if (
            (filterType == FilterType.Campsites
            ||
            filterType == FilterType.Huts)
            &&
            bookable > 0
        ) {
            when (args.size) {
                0 -> sb.append("WHERE")
                else -> sb.append("AND")
            }

            args.add(1) // Allow for random debug

            sb.append(" ")
            sb.append("T.$COLUMN_NAME_COMMON_BOOKABLE = ?") // bookable
            sb.append(" ")
        }

        if (
            (filterType == FilterType.Campsites
            ||
            filterType == FilterType.Tracks)
            &&
            dogAccess > 0
        ) {
            when (args.size) {
                0 -> sb.append("WHERE")
                else -> sb.append("AND")
            }

            args.add(getDogAccessArg(dogAccess))

            sb.append(" ")
            sb.append("T.$COLUMN_NAME_COMMON_DOGS_ALLOWED LIKE ?") // dogsAllowed
            sb.append(" ")
        }

        sb.append("AND")
        sb.append(" ") // Don't count if NOT downloaded
        sb.append("LENGTH(T.$columnNameOrSummary) > 0")

        // android.util.Log.d("HEY", sb.toString())
        /*
        SELECT COUNT(summary) FROM alerts_table AS T INNER JOIN regions_table
        AS R ON T.regionCodes LIKE '%' || R.regionCode || '%' WHERE R.id = ?
        AND LENGTH(T.summary) > 0

        SELECT COUNT(name) FROM campsites_table AS T INNER JOIN regions_table
        AS R ON T.regionCode LIKE '%' || R.regionCode || '%' WHERE R.id = ?
        AND T.bookable = ? AND T.dogsAllowed LIKE ? AND LENGTH(T.name) > 0
        */
        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }

    private fun getDogAccessArg(listFilterDogs: Int): String {
        val stringArray = instance.resources.getStringArray(
            R.array.filter_dogs_by
        )
        // Note wildcard, just at end of the string
        return stringArray[listFilterDogs].plus("%")
    }

    fun getFavouriteItemKtQuery(
        itemId: String, itemType: AssetType
    ) : SimpleSQLiteQuery {
        val args: ArrayList<Any> = arrayListOf()
        args.add(itemId)        // affectedCount
        args.add(itemType.name) // affectedCount

        args.add(itemId)        // isFavourite
        args.add(itemType.name) // isFavourite

        args.add(itemId)        // (main table)

        val sb = StringBuilder()
        val tableName = when (itemType) {
            AssetType.Campsite -> TABLE_NAME_CAMPSITES
            AssetType.Hut      -> TABLE_NAME_HUTS
            AssetType.Track    -> TABLE_NAME_TRACKS
        }
        sb.append("SELECT")
        sb.append(" ")

        sb.append("(SELECT COUNT(*)")
        sb.append(" ")
        sb.append("FROM $TABLE_NAME_AFFECTED_EXTRA") // affected_extra_table
        sb.append(" ")
        sb.append("WHERE $COLUMN_NAME_COMMON_ASSET_ID") // assetId
        sb.append(" ")
        sb.append("= ?")
        sb.append(" ")
        sb.append("AND $COLUMN_NAME_COMMON_ITEM_TYPE") // itemType
        sb.append(" ")
        sb.append("= ?)")
        sb.append(" ")
        sb.append("AS affectedCount,")
        sb.append(" ")

        sb.append("(SELECT EXISTS")
        sb.append(" ")
            sb.append("(SELECT *")
            sb.append(" ")
            sb.append("FROM $TABLE_NAME_FAVES") // faves_table
            sb.append(" ")
            sb.append("WHERE $COLUMN_NAME_COMMON_ASSET_ID") // assetId
            sb.append(" ")
            sb.append("= ?")
            sb.append(" ")
            sb.append("AND $COLUMN_NAME_COMMON_ITEM_TYPE") // itemType
            sb.append(" ")
            sb.append("= ?")
            sb.append(" ")
            sb.append("LIMIT 1)")
        sb.append(") ")
        sb.append("AS isFavourite,")
        sb.append(" ")

        sb.append(" ")
        sb.append("*")
        sb.append(" ")
        sb.append("FROM $tableName")
        sb.append(" ")
        sb.append("WHERE assetId = ?")

        // android.util.Log.d("HEY", sb.toString())
        /*
        SELECT (SELECT COUNT(*) FROM affected_extra_table
        WHERE assetId = ? AND itemType = ?) AS affectedCount,
        (SELECT EXISTS (SELECT * FROM faves_table
        WHERE assetId = ? AND itemType = ? LIMIT 1)) AS isFavourite,
        * FROM campsites_table WHERE assetId = ?
        */
        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }

    fun getItemIdsNotDownloadedQuery(
        regionId: Int,
        itemType: AssetType
    ) : SimpleSQLiteQuery {
        val args: ArrayList<Any> = arrayListOf(regionId)
        val tableName = when (itemType) {
            AssetType.Campsite -> TABLE_NAME_CAMPSITES // campsites_table
            AssetType.Hut      -> TABLE_NAME_HUTS      // huts_table
            AssetType.Track    -> TABLE_NAME_TRACKS    // tracks_table
        }
        val columnRegion = when (itemType) {
            AssetType.Track -> COLUMN_NAME_REGION_MULTI // regionCodes
             /* Campsites and Huts have SINGLE code */
            else            -> COLUMN_NAME_REGION_SINGLE // regionCode
        }

        val sb = StringBuilder()
        sb.append("SELECT $COLUMN_NAME_COMMON_ASSET_ID") // assetId
        sb.append(" ")
        sb.append("FROM $tableName")

        sb.append(" ")
        sb.append("WHERE $columnRegion") // regionCode[s]
        sb.append(" ")
        sb.append("LIKE")
        sb.append(" ")
        sb.append("(SELECT '%' ||")
        sb.append(" ")
        sb.append(COLUMN_NAME_REGION_SINGLE) // regionCode
        sb.append(" ")
        sb.append("|| '%'")
        sb.append(" ")
        sb.append("FROM $TABLE_NAME_REGIONS") // regions_table
        sb.append(" ")
        sb.append("WHERE $COLUMN_NAME_COMMON_ITEM_ID") // id
        sb.append(" ")
        sb.append("= ?)")

        sb.append(" ")
        sb.append("AND LENGTH")
        sb.append(" ")
        sb.append("($COLUMN_NAME_COMMON_LOCAT_STRING)") // locationString
        sb.append(" ")
        sb.append("= 0")

        // android.util.Log.d("HEY", sb.toString())
        /*
        SELECT assetId FROM campsites_table WHERE regionCode LIKE
        (SELECT '%' || regionCode || '%' FROM regions_table WHERE id = ?)
        AND LENGTH (locationString) = 0
        */
        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }

    suspend fun getRegionNameByRegionIdAndCode(
        regionId: Int,
        regionsDao: RegionsDao
    ) : String {
        val regionCode = regionsDao.getRegionCodeByActualId(
            id = regionId
        )

        return regionsDao.getRegionNameByRegionCode(
            code = regionCode
        )
    }

    fun getUnknownRegionOrCodeByRegionOrAssetId(
        assetId: String,
        assetType: AssetType,
        region: String?
    ) : Pair<String, String> {
        val regionCode = when (region) {
            "Northland"            -> "NZ-NTL"
            "Auckland"             -> "NZ-AUK"
            "Waikato"              -> "NZ-WKO"
            "Coromandel"           -> "DOC-COR"
            "Bay of Plenty"        -> "NZ-BOP"
            "East Coast"           -> "NZ-GIS"
            "Taranaki"             -> "NZ-TKI"
            "Manawatu/Whanganui"   -> "NZ-MWT"
            "Central North Island" -> "DOC-CNI"
            "Hawke's Bay"          -> "NZ-HKB"
            "Wellington/Kapiti"    -> "NZ-WGN"
            "Wairarapa"            -> "DOC-WPA"
            "Chatham Islands"      -> "NZ-CIT"
            "Nelson/Tasman"        -> "NZ-NSN"
            "Marlborough"          -> "NZ-MBH"
            "West Coast"           -> "NZ-WTC"
            "Canterbury"           -> "NZ-CAN"
            "Otago"                -> "NZ-OTA"
            "Southland"            -> "NZ-STL"
            "Fiordland"            -> "DOC-FIL"
            else -> when (assetType) {
                AssetType.Campsite -> {
                    getUnknownCampsiteRegionCodeByAssetId(assetId)
                }
                AssetType.Hut -> {
                    getUnknownHutRegionCodeByAssetId(assetId)
                }
                /* To date, no tracks have been
                   found without region code */
                AssetType.Track -> String()
            }
        }
        /* While this may seem circular in logic, it's
           mainly for the assets below, most of which
           have neither region name nor region code */
        return regionCode to when (regionCode) {
            "NZ-NTL"  -> "Northland"
            "NZ-AUK"  -> "Auckland"
            "NZ-WKO"  -> "Waikato"
            "DOC-COR" -> "Coromandel"
            "NZ-BOP"  -> "Bay of Plenty"
            "NZ-GIS"  -> "East Coast"
            "NZ-TKI"  -> "Taranaki"
            "NZ-MWT"  -> "Manawatu/Whanganui"
            "DOC-CNI" -> "Central North Island"
            "NZ-HKB"  -> "Hawke's Bay"
            "NZ-WGN"  -> "Wellington/Kapiti"
            "DOC-WPA" -> "Wairarapa"
            "NZ-CIT"  -> "Chatham Islands"
            "NZ-NSN"  -> "Nelson/Tasman"
            "NZ-MBH"  -> "Marlborough"
            "NZ-WTC"  -> "West Coast"
            "NZ-CAN"  -> "Canterbury"
            "NZ-OTA"  -> "Otago"
            "NZ-STL"  -> "Southland"
            "DOC-FIL" -> "Fiordland"
            else -> String()
        }
    }

    fun getUnknownCampsiteRegionCodeByAssetId(assetId: String): String { // 7
        /* Campsites located nearby other assets, and/or found on the map */
        return when (assetId) {
            "100093318" -> "NZ-CAN"  // Okiwi Bay Campsite
            "100044151" -> "DOC-FIL" // Mackay Creek Campsite
            "10000115"  -> "NZ-CAN"  // Whakarukumoana Nohoanga Site
            "100095182" -> "NZ-WGN"  // Blackgate Campground
            "100043023" -> "NZ-GIS"  // Tapuaenui Campsite
            "100039405" -> "NZ-OTA"  // Geordie Hill Campsite
            "100082372" -> "NZ-BOP"  // Humphrey's Bay Campsite New 08
            else -> String()
        }
    }

    fun getUnknownHutRegionCodeByAssetId(assetId: String): String { // 24
        /* Huts located nearby other assets, and/or located on the map */
        return when (assetId) {
            "100098292" -> "NZ-WKO" // Kopuatai Duck Hut 12
            "100098286" -> "NZ-WKO" // Kopuatai Duck Hut 5
            "100098288" -> "NZ-WKO" // Kopuatai Duck Hut 7
            "100098293" -> "NZ-WKO" // Kopuatai Duck Hut 16
            "100055258" -> "NZ-STL" // Aparima - Tony's Hut
            "100098285" -> "NZ-WKO" // Kopuatai Duck Hut 3
            "100098296" -> "NZ-WKO" // Whangamarino Duck Hut C
            "10000019"  -> "NZ-OTA" // Kahikatea Lodge Hut
            "100098289" -> "NZ-WKO" // Kopuatai Duck Hut 8
            "100098284" -> "NZ-WKO" // Kopuatai Duck Hut 2
            "100085933" -> "NZ-CAN" // Waimak Gorge - Walker Hut
            "100034558" -> "NZ-WKO" // Pahautea Hut (Old)
            "100098287" -> "NZ-WKO" // Kopuatai Duck Hut 6
            "100038182" -> "NZ-WGN" // Kiwi Hut
            "100098294" -> "NZ-WKO" // Kopuatai Duck Hut 17
            "100098295" -> "NZ-WKO" // Whangamarino Duck Hut B
            "100059146" -> "NZ-OTA" // Top Forks Old Hut
            "100098290" -> "NZ-WKO" // Kopuatai Duck Hut 9
            "100039503" -> "NZ-BOP" // Crow Hut
            "100098291" -> "NZ-WKO" // Kopuatai Duck Hut 10
            "100098297" -> "NZ-WKO" // Whangamarino Duck Hut E
            "100098283" -> "NZ-WKO" // Kopuatai Duck Hut 1
            "10000116"  -> "NZ-OTA" // Mae West Biv
            "100085934" -> "NZ-CAN" // Waimak Gorge - Hamilton Hut
            else -> String()
        }
    }
}