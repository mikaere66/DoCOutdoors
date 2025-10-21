package com.michaelrmossman.docoutdoors.data

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.michaelrmossman.docoutdoors.OutdoorsApplication.Companion.instance
import com.michaelrmossman.docoutdoors.database.RegionsDao
import com.michaelrmossman.docoutdoors.database.SettingsDao
import com.michaelrmossman.docoutdoors.model.RegionEntity
import com.michaelrmossman.docoutdoors.model.SettingEntity
import com.michaelrmossman.docoutdoors.utils.WORK_MANAGER_UNIQUE_NAME
import com.michaelrmossman.docoutdoors.workers.AutoUpdateWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

/*
 * Concrete class implementation to access database
 */
class SettingsRepository(
    private val regionsDao: RegionsDao,
    private val settingsDao: SettingsDao
) : SettingsRepoBase {

    override suspend fun getRegionsList(): List<RegionEntity> =
        regionsDao.getRegionsList()

    override fun getSettingsAll(): List<SettingEntity> =
        SettingEntity.getSettings(
            allowRandom = true
        )

    override fun getSettingById(id: String): Flow<Int> =
        settingsDao.getSettingById(id)

    override suspend fun resetAllSettings() {
        val settings = SettingEntity.getSettings(
            allowRandom = true
        )
        settingsDao.updateSettings(settings)
    }

    override suspend fun saveSetting(
        settingId: String, setting: Int
    ) : Int {
        val settingEntity = SettingEntity(
            settingId = settingId,
            setting = setting
        )
        return settingsDao.updateSetting(settingEntity)
    }

    override fun setAutoUpdateWorker(
        interval: Int,
        network : Int,
        update  : Boolean,
        waitChrg: Int
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                when (network) {
                    0    -> NetworkType.UNMETERED // Wait for Wi-Fi
                    else -> NetworkType.CONNECTED // Mobile data Ok
                }
            )
            .setRequiresCharging(waitChrg == 1)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<AutoUpdateWorker>(
            repeatInterval = when (interval) {
                1    -> 1 // Daily
                else -> 7 // Weekly
            },
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
        .setConstraints(constraints)
        .build()

        WorkManager.getInstance(
            instance
        ).enqueueUniquePeriodicWork(
            WORK_MANAGER_UNIQUE_NAME,
            when (update) {
                true -> ExistingPeriodicWorkPolicy.KEEP
                else -> ExistingPeriodicWorkPolicy.REPLACE
            },
            periodicWorkRequest
        )

        // UUID, e.g. 47a39259-0160-4e01-8f13-5d234f2161dd
        // android.util.Log.d("HEY",periodicWorkRequest.id.toString())
    }

    override fun unsetAutoUpdateWorker() {
        WorkManager.getInstance(instance).cancelAllWork()
    }
}