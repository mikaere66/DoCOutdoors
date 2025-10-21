package com.michaelrmossman.docoutdoors.database

const val DATABASE_VERSION = 1
const val EXPORT_SCHEMA = false

/* Not all column names are shown here, only columns
   that are referred to OUTSIDE the entity itself */
const val COLUMN_NAME_ALERT_EXTRA_COUNT   = "itemCount"
const val COLUMN_NAME_ALERT_SUMMARY       = "summary"
const val COLUMN_NAME_COMMON_AFFECT_ID    = "affectId"
const val COLUMN_NAME_COMMON_AFFECT_TYPE  = "type"
const val COLUMN_NAME_COMMON_ASSET_ID     = "assetId"
const val COLUMN_NAME_COMMON_ASSET_NAME   = "name"
const val COLUMN_NAME_COMMON_BOOKABLE     = "bookable"
const val COLUMN_NAME_COMMON_DOGS_ALLOWED = "dogsAllowed"
const val COLUMN_NAME_COMMON_ITEM_ID      = "id"
const val COLUMN_NAME_COMMON_ITEM_TYPE    = "itemType"
const val COLUMN_NAME_COMMON_LATITUDE     = "lat"
const val COLUMN_NAME_COMMON_LOCAT_STRING = "locationString"
const val COLUMN_NAME_COMMON_LONGITUDE    = "lon"
const val COLUMN_NAME_COMMON_RESP_CODE    = "responseCode"
const val COLUMN_NAME_REGION_ALERTS_DLOAD = "alertsDload"
const val COLUMN_NAME_REGION_MULTI        = "regionCodes"
const val COLUMN_NAME_REGION_NAME         = "regionName"
const val COLUMN_NAME_REGION_SINGLE       = "regionCode"
const val COLUMN_NAME_REGION_TRACKS_DLOAD = "tracksDload"
const val COLUMN_NAME_SETTING_ID          = "settingId"
const val COLUMN_NAME_SETTING             = "setting"

const val TABLE_NAME_AFFECTED       = "affected_table"
const val TABLE_NAME_AFFECTED_EXTRA = "affected_extra_table"
const val TABLE_NAME_ALERTS         = "alerts_table"
const val TABLE_NAME_ALERTS_EXTRA   = "alerts_extra_table"
const val TABLE_NAME_CAMPSITES      = "campsites_table"
const val TABLE_NAME_COORDS         = "coords_table"
const val TABLE_NAME_HUTS           = "huts_table"
const val TABLE_NAME_FAVES          = "faves_table"
const val TABLE_NAME_REGIONS        = "regions_table"
const val TABLE_NAME_SETTINGS       = "settings_table"
const val TABLE_NAME_TRACKS         = "tracks_table"