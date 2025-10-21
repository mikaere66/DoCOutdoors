package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.database.SettingsDao
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_SATELLITE_VIEW
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_SHOW_LOCATION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Maps offline implementation of MapsRepoBase
 */
class MapsRepository(settingsDao: SettingsDao): MapsRepoBase {

    private val _commonSatelliteView = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_COMMON_SATELLITE_VIEW
        )
    )
    override val commonSatelliteView: Flow<Int>
        get() = _commonSatelliteView.value
    override fun setCommonSatelliteView(satelliteView: Int) {
        _commonSatelliteView.value = flowOf(satelliteView)
    }

    private val _commonShowLocation = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_COMMON_SHOW_LOCATION
        )
    )
    override val commonShowLocation: Flow<Int>
        get() = _commonShowLocation.value
    override fun setCommonShowLocation(showLocation: Int) {
        // android.util.Log.d("HEY1",showLocation.toString())
        _commonShowLocation.value = flowOf(showLocation)
    }
}