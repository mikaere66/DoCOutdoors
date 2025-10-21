package com.michaelrmossman.docoutdoors.utils

const val DEBUG_SETTINGS_GENERATE_RANDOM = false
const val DEBUG_SHOW_ADDITIONAL_MESSAGES = false
const val DEBUG_VIEW_MODELS_DOWNLOAD_ALL = false

const val DOC_ALERT_URL = "v2/alerts/{id}?coordinates=wgs84"
const val DOC_ALERTS_BY_REGION_URL = "v2/alerts/region/{regionId}"
const val DOC_ALERTS_URL = "v2/alerts"
const val DOC_API_KEY = "x-api-key"
const val DOC_API_URL = "https://api.doc.govt.nz"
const val DOC_CAMPSITES_URL = "v2/campsites?coordinates=wgs84"
const val DOC_CAMPSITE_ALERTS_URL = "v2/campsites/alerts"
const val DOC_CAMPSITE_URL = "v2/campsites/{id}/detail?coordinates=wgs84"
const val DOC_DATA_TYPE = "application/json"
const val DOC_HUTS_URL = "v2/huts?coordinates=wgs84"
const val DOC_HUT_ALERTS_URL = "v2/huts/alerts"
const val DOC_HUT_URL = "v2/huts/{id}/detail?coordinates=wgs84"
const val DOC_TRACKS_BY_REGION_URL = "v1/tracks/region/{regionId}?coordinates=wgs84"
const val DOC_TRACKS_URL = "v1/tracks?coordinates=wgs84"
const val DOC_TRACK_ALERTS_URL = "v1/tracks/alerts"
const val DOC_TRACK_URL = "v1/tracks/{id}/detail?coordinates=wgs84"
//const val DOC_TYPE_COORDS = "wgs84" // TODO
const val DOC_TYPE_KEY = "accept"

const val BATCH_DOWNLOAD_DELAY = 100L
const val FILTERED_ASTERISK = "* "
const val ITEM_SEPARATOR = ", "
const val KIWI_UPDATE_FORMAT = "EEE, dd MMM yyyy h:mma"
const val LAST_UPDATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z"
const val MAP_MARKER_BACKGROUND_ALPHA = 0.6F
const val MAP_MIDDLE_NZ_LAT = -41.3
const val MAP_MIDDLE_NZ_LON = 173.9
const val MAP_POLYLINE_WIDTH = 8F
const val MAP_ZOOM_ALL_DELAY    = 333L
const val MAP_ZOOM_ALL_DURATION = 1_000
const val MAP_ZOOM_ALL_PADDING_SMALL  = 80
const val MAP_ZOOM_ALL_PADDING_MEDIUM = 96
/* A higher number gives a "closer" zoom */
const val MAP_ZOOM_ITEMS_ALL   = 5.5F
const val MAP_ZOOM_ITEM_SINGLE = 12F

val NOTIFICATION_CHANNEL_NAME_UPD: CharSequence =
    "Alerts auto update"
const val NOTIFICATION_CHANNEL_DESC_UPD =
    "Shows notification when new alerts found"
val NOTIFICATION_NEW_TITLE: CharSequence = "Auto update complete"
val NOTIFICATION_UPD_TITLE: CharSequence = "Auto update starting"
const val NOTIFICATION_CHANNEL_UPD_ID = "ALERTS_NOTIFICATION_UPD"
const val NOTIFICATION_ID = 602593
const val PENDING_INTENT_REQUEST_CODE = 395206
//const val NOTIFICATION_TIMEOUT = 5000L
const val ON_SETTINGS_SAVED_DELAY = 2_500L
const val WORK_MANAGER_UNIQUE_NAME = "ALERTS_AUTO_UPDATE"

const val PREF_ALERTS_BY_LATEST = "alerts_by_latest"
const val PREF_ALERTS_BY_LATEST_DEFAULT = 1
const val PREF_ALERTS_FILTER_BY = "alerts_filter_by"
const val PREF_ALERTS_FILTER_BY_DEFAULT = 0
/* Default for all "one time message" settings is 0 */
const val PREF_ALERTS_UPD_AUTOMATIC = "alerts_upd_automatically"
const val PREF_ALERTS_UPD_LTE_OKAY = "alerts_upd_mobile_data"
const val PREF_ALERTS_UPD_LTE_OKAY_DEFAULT = 0 // WiFi Only
const val PREF_ALERTS_UPD_SHOW_NOTIF = "alerts_upd_show_notif"
const val PREF_ALERTS_UPD_SHOW_NOTIF_DEFAULT = 0
const val PREF_ALERTS_UPD_WAIT_CHRG = "alerts_upd_wait_charge"
const val PREF_ALERTS_UPD_WAIT_CHRG_DEFAULT = 0 // Any time
const val PREF_CAMPSITES_DLOAD_ALL = "campsites_dload_all"
const val PREF_CAMPSITES_FILTER_BY = "campsites_filter_by"
const val PREF_CAMPSITES_FILTER_BY_DEFAULT = 0
const val PREF_COMMON_FILTER_BOOKABLE = "filter_bookable"
const val PREF_COMMON_FILTER_BOOKABLE_DEFAULT = 0
const val PREF_COMMON_FILTER_DOGS_BY = "filter_dogs_by"
const val PREF_COMMON_FILTER_DOGS_BY_DEFAULT = 0
const val PREF_COMMON_SATELLITE_VIEW = "satellite_view"
const val PREF_COMMON_SATELLITE_VIEW_DEFAULT = 0
const val PREF_COMMON_SHOW_LOCATION = "show_location"
const val PREF_COMMON_SHOW_LOCATION_DEFAULT = 0
const val PREF_FAVES_SORTED_BY = "sort_faves_by"
const val PREF_FAVES_SORTED_BY_DEFAULT = 0
const val PREF_HUTS_DLOAD_ALL = "huts_dload_all"
const val PREF_HUTS_FILTER_BY = "huts_filter_by"
const val PREF_HUTS_FILTER_BY_DEFAULT = 0
const val PREF_TRACKS_DLOAD_ALL = "tracks_dload_all"
const val PREF_TRACKS_FILTER_BY = "tracks_filter_by"
const val PREF_TRACKS_FILTER_BY_DEFAULT = 0
const val PREF_TRACKS_ZOOM_ON_DLOAD = "zoom_on_download"
const val PREF_TRACKS_ZOOM_ON_DLOAD_DEFAULT = 1
