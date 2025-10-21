package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.model.RegionEntity
import com.michaelrmossman.docoutdoors.model.SettingEntity
import kotlinx.coroutines.flow.Flow

interface SettingsRepoBase {

    suspend fun getRegionsList(): List<RegionEntity>

    fun getSettingsAll(): List<SettingEntity>

    fun getSettingById(id: String): Flow<Int>

    suspend fun resetAllSettings()

    suspend fun saveSetting(settingId: String, setting: Int): Int

    fun setAutoUpdateWorker(
        interval: Int,
        network : Int,
        update  : Boolean,
        waitChrg: Int
    )

    fun unsetAutoUpdateWorker()
}