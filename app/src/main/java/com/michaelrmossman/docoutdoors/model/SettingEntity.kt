package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_SETTING
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_SETTING_ID
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_SETTINGS
import com.michaelrmossman.docoutdoors.utils.DEBUG_SETTINGS_GENERATE_RANDOM
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_BY_LATEST
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_BY_LATEST_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_FILTER_BY_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_LTE_OKAY
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_LTE_OKAY_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_AUTOMATIC
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_SHOW_NOTIF
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_SHOW_NOTIF_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_WAIT_CHRG
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_UPD_WAIT_CHRG_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_CAMPSITES_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_CAMPSITES_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_CAMPSITES_FILTER_BY_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_BOOKABLE
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_BOOKABLE_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_DOGS_BY
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_DOGS_BY_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_SATELLITE_VIEW
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_SATELLITE_VIEW_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_SHOW_LOCATION
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_SHOW_LOCATION_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_FAVES_SORTED_BY
import com.michaelrmossman.docoutdoors.utils.PREF_FAVES_SORTED_BY_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_HUTS_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_HUTS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_HUTS_FILTER_BY_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_FILTER_BY_DEFAULT
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_ZOOM_ON_DLOAD
import com.michaelrmossman.docoutdoors.utils.PREF_TRACKS_ZOOM_ON_DLOAD_DEFAULT

@Entity(tableName = TABLE_NAME_SETTINGS) // 18
data class SettingEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COLUMN_NAME_SETTING_ID)
    val settingId: String,

    @ColumnInfo(name = COLUMN_NAME_SETTING)
    val setting: Int
) {

    companion object {
        fun getSettings(allowRandom: Boolean): List<SettingEntity> {
            return listOf(
                SettingEntity(
                    settingId = PREF_ALERTS_BY_LATEST,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_ALERTS_BY_LATEST_DEFAULT
                    )
                ),
                SettingEntity(
                    settingId = PREF_ALERTS_FILTER_BY,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_ALERTS_FILTER_BY_DEFAULT
                    )
                ),
                SettingEntity(
                    settingId = PREF_ALERTS_UPD_AUTOMATIC,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = 0
                    )
                ),
                SettingEntity(
                    settingId = PREF_ALERTS_UPD_LTE_OKAY,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_ALERTS_UPD_LTE_OKAY_DEFAULT
                    )
                ),
                SettingEntity(
                    settingId = PREF_ALERTS_UPD_SHOW_NOTIF,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_ALERTS_UPD_SHOW_NOTIF_DEFAULT
                    )
                ),
                SettingEntity(
                    settingId = PREF_ALERTS_UPD_WAIT_CHRG,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_ALERTS_UPD_WAIT_CHRG_DEFAULT
                    )
                ),

                SettingEntity(
                    settingId = PREF_CAMPSITES_DLOAD_ALL,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = 0
                    )
                ),
                SettingEntity(
                    settingId = PREF_CAMPSITES_FILTER_BY,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_CAMPSITES_FILTER_BY_DEFAULT
                    )
                ),

                SettingEntity(
                    settingId = PREF_COMMON_FILTER_BOOKABLE,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_COMMON_FILTER_BOOKABLE_DEFAULT
                    )
                ),
                SettingEntity(
                    settingId = PREF_COMMON_FILTER_DOGS_BY,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_COMMON_FILTER_DOGS_BY_DEFAULT
                    )
                ),

                SettingEntity(
                    settingId = PREF_HUTS_DLOAD_ALL,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = 0
                    )
                ),
                SettingEntity(
                    settingId = PREF_HUTS_FILTER_BY,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_HUTS_FILTER_BY_DEFAULT
                    )
                ),

                SettingEntity(
                    settingId = PREF_COMMON_SATELLITE_VIEW,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_COMMON_SATELLITE_VIEW_DEFAULT
                    )
                ),
                SettingEntity(
                    settingId = PREF_COMMON_SHOW_LOCATION,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_COMMON_SHOW_LOCATION_DEFAULT
                    )
                ),
                SettingEntity(
                    settingId = PREF_FAVES_SORTED_BY,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_FAVES_SORTED_BY_DEFAULT
                    )
                ),

                SettingEntity(
                    settingId = PREF_TRACKS_DLOAD_ALL,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = 0
                    )
                ),
                SettingEntity(
                    settingId = PREF_TRACKS_FILTER_BY,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_TRACKS_FILTER_BY_DEFAULT
                    )
                ),
                SettingEntity(
                    settingId = PREF_TRACKS_ZOOM_ON_DLOAD,
                    setting = getSetting(
                        allowRandom = allowRandom,
                        preference = PREF_TRACKS_ZOOM_ON_DLOAD_DEFAULT
                    )
                )
            )
        }

        @Suppress("KotlinConstantConditions", "SimplifyBooleanWithConstants")
        private fun getSetting(
            allowRandom: Boolean, preference: Int
        ) = when (
            allowRandom
            &&
            DEBUG_SETTINGS_GENERATE_RANDOM
        ) {
            true -> (0..2).random()
            else -> preference
        }
    }
}