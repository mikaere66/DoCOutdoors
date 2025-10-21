package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.michaelrmossman.docoutdoors.model.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("""
        SELECT setting FROM $TABLE_NAME_SETTINGS
        WHERE settingId = :settingId
    """)
    fun getSettingById(settingId: String): Flow<Int>

    @Insert
    suspend fun insertSettings(settings: List<SettingEntity>)

    @Update // For "restore app defaults"
    suspend fun updateSettings(settings: List<SettingEntity>)

    @Update // For single setting update
    suspend fun updateSetting(setting: SettingEntity): Int
}