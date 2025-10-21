package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.model.AffectedEntity
import com.michaelrmossman.docoutdoors.model.Alert
import com.michaelrmossman.docoutdoors.model.AlertExtraSerial
import com.michaelrmossman.docoutdoors.model.AlertExtra
import com.michaelrmossman.docoutdoors.model.AlertSerial
import com.michaelrmossman.docoutdoors.model.TrackExtraSerial
import kotlinx.coroutines.flow.Flow

/**
 * Base offline repository that fetches various lists from database
 */
interface AlertsOfflineRepoBase {

    val alertCount: Flow<Int>

    val alertsListIncomplete: Flow<Boolean>

    val alertsFilterById: Flow<Int>

    val alertsFilterByRegion: Flow<String>

    val alertsFlow: Flow<List<Alert>>

    suspend fun deleteAlertsByRegionCode(regionCode: String): Int

    suspend fun deleteAllAlerts()

    suspend fun deleteAllAlertExtras(itemType: AssetType): Int

    suspend fun getAffectedByAlertId(id: String): List<AffectedEntity>

    suspend fun getAlertById(id: String): Alert

    fun getAlertCountAll(): Flow<Int>

    suspend fun getAlertIds(): List<String>

    suspend fun getAlertExtraByAssetId(
        assetId: String, itemType: AssetType
    ) : AlertExtra

    suspend fun insertAlerts(alertsList: List<AlertSerial>): List<Long>

    suspend fun insertCSOrHutAlertExtras(
        alertsList: List<AlertExtraSerial>,
        itemType: AssetType
    )

    suspend fun insertTrackAlertExtras(
        alertsList: List<TrackExtraSerial>
    )

    fun setAlertsByLatest(byLatest: Int)

    fun setAlertsRegionCode(region: Int)

    suspend fun updateAlert(
        alert: AlertSerial,
        responseCode: Int,
        updateMillis: Long
    ) : Int

    suspend fun updateAlertWithResponse(
        id: String,
        responseCode: Int
    )
}