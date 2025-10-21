package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.database.AffectedDao
import com.michaelrmossman.docoutdoors.database.AlertsDao
import com.michaelrmossman.docoutdoors.database.DbHelpers.getAlertsByRegionIdQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getCountByRegionCodeWithExtrasQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getRegionNameByRegionIdAndCode
import com.michaelrmossman.docoutdoors.database.DbHelpers.toAlertWithExtras
import com.michaelrmossman.docoutdoors.database.RegionsDao
import com.michaelrmossman.docoutdoors.database.SettingsDao
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.model.AffectedEntity
import com.michaelrmossman.docoutdoors.model.AffectedExtraEntity
import com.michaelrmossman.docoutdoors.model.Alert
import com.michaelrmossman.docoutdoors.model.AlertEntity
import com.michaelrmossman.docoutdoors.model.AlertExtra
import com.michaelrmossman.docoutdoors.model.AlertExtraEntity
import com.michaelrmossman.docoutdoors.model.AlertExtraSerial
import com.michaelrmossman.docoutdoors.model.AlertSerial
import com.michaelrmossman.docoutdoors.model.TrackExtraSerial
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_BY_LATEST
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.parseStringDateToMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Alerts offline implementation of AlertsOfflineRepoBase
 */
class AlertsOfflineRepository(
    private val affectedDao: AffectedDao,
    private val alertsDao: AlertsDao,
    private val regionsDao: RegionsDao,
    private val settingsDao: SettingsDao
) : AlertsOfflineRepoBase {

    private val _alertsByLatest = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_ALERTS_BY_LATEST
        )
    )
    override fun setAlertsByLatest(byLatest: Int) {
        _alertsByLatest.value = flowOf(byLatest)
    }

    private val _alertsFilterById = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_ALERTS_FILTER_BY
        )
    )
    override val alertsFilterById: Flow<Int>
        get() = _alertsFilterById.value
    override fun setAlertsRegionCode(region: Int) {
        _alertsFilterById.value = flowOf(region)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val alertCount: Flow<Int> =
        _alertsFilterById.flatMapLatest { filterBy ->
            when (filterBy.first()) {
                0 -> alertsDao.getAlertCountAllRegions()
                else -> {
                    alertsDao.getAlertCountByRegionCode(
                        query = getCountByRegionCodeWithExtrasQuery(
                            filterType = FilterType.Alerts,
                            regionId = filterBy.first(),
                        )
                    )
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val alertsFilterByRegion: Flow<String> =
        _alertsFilterById.flatMapLatest { filterBy ->
            val region = when (filterBy.first()) {
                0 -> String()
                else -> getRegionNameByRegionIdAndCode(
                    regionId = filterBy.first(),
                    regionsDao = regionsDao
                )
            }
            flowOf(region)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val alertsFlow: Flow<List<Alert>> =
        _alertsByLatest.flatMapLatest { sortByLatest ->
            _alertsFilterById.flatMapLatest { filterBy ->
                val alerts = when (filterBy.first()) {
                    0    -> alertsDao.getAlertsList()
                    else -> alertsDao.getAlertsByRegionId(
                        getAlertsByRegionIdQuery(
                            regionId = filterBy.first()
                        )
                    )
                }
                val alertsWithExtras = alerts.map { alert ->
                    alert.toAlertWithExtras(
                        affectedDao = affectedDao,
                        regionsDao = regionsDao
                    )
                }
                val alertsSorted = when (sortByLatest.first()) {
                    1 -> alertsWithExtras.sortedByDescending { alert ->
                        alert.updateMillis
                    }
                    else -> alertsWithExtras.sortedBy { alert ->
                        alert.summary
                    }
                }
                flowOf(alertsSorted)
            }
        }

    private val regionTotalCount = regionsDao.getRegionTotalCount()
    private val alertsDloadCount = regionsDao.getDloadAlertsCount()
    @OptIn(ExperimentalCoroutinesApi::class)
    override val alertsListIncomplete: Flow<Boolean> =
        _alertsFilterById.flatMapLatest { filterBy ->
            regionTotalCount.combine(
                alertsDloadCount
            ) { regions, alerts ->
                alerts != 0
                && // i.e. between 1 & 19 inclusive
                alerts != regions
            } // Refer to note in RegionsRepository
        }

    override suspend fun deleteAlertsByRegionCode(
        regionCode: String
    ) : Int = alertsDao.deleteAlertsByRegionCode(regionCode)

    override suspend fun deleteAllAlerts() {
        val filterBy = settingsDao.getSettingById(
            settingId = PREF_ALERTS_FILTER_BY
        )
        when (filterBy.first()) {
            0 -> {
                affectedDao.deleteAllAffected()
                alertsDao.deleteAllAlerts()
            }
            else -> {
                val regionCode = regionsDao.getRegionCodeByActualId(
                    id = filterBy.first()
                )
                alertsDao.deleteAlertsByRegionCode(regionCode)
                /* It's not as simple as deleting by regionCode,
                   so we need to delete by relevant alertIds */
                alertsDao.getAlertIdsByRegionCode(
                    regionCode = regionCode
                ).forEach { assetId ->
                    affectedDao.deleteAffectedByAlertId(
                        id = assetId
                    )
                }
            }
        }
    }

    override suspend fun deleteAllAlertExtras(
        itemType: AssetType
    ) : Int {
        return alertsDao.deleteAllAlertExtras(
            itemType = itemType.name
        ).plus(
            affectedDao.deleteAllAffectedExtras(
                itemType = itemType.name
            )
        )
    }

    override suspend fun getAffectedByAlertId(
        id: String
    ) : List<AffectedEntity> = affectedDao.getAffectedByAlertId(id)

    override suspend fun getAlertById(id: String): Alert {
        val alert = alertsDao.getAlertById(id)
        return alert.toAlertWithExtras(
            affectedDao = affectedDao,
            regionsDao = regionsDao
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAlertCountAll(): Flow<Int> =
        _alertsFilterById.flatMapLatest { filterBy ->
            when (filterBy.first()) {
                0 -> alertsDao.getAlertCountAllRegions()
                else -> {
                    alertsDao.getAlertCountByRegionCode(
                        query = getCountByRegionCodeWithExtrasQuery(
                            filterType = FilterType.Alerts,
                            regionId = filterBy.first(),
                        )
                    )
                }
            }
        }

    override suspend fun getAlertExtraByAssetId(
        assetId: String, itemType: AssetType
    ) : AlertExtra {
        val alertExtra = alertsDao.getAlertExtraByItemId(
            assetId = assetId, itemType = itemType.name
        )
        return alertExtra.toAlertExtra(
            affectedExtras = affectedDao.getAffectedExtrasByItemId(
                assetId = assetId, itemType = itemType.name
            )
        )
    }

    override suspend fun getAlertIds(): List<String> =
        alertsDao.getAlertIds()

    override suspend fun insertAlerts(
        alertsList: List<AlertSerial>
    ) : List<Long> = alertsDao.insertAlerts(
        alertsList.filter { alertSerial ->
            alertSerial.summary.isNotBlank()
        }.map { alertSerial ->
            AlertEntity.from(
                alertSerial,
                responseCode = 200,
                alertSerial.lastUpdated.parseStringDateToMillis()
            )
        }
    )

    /* Campsites | Huts */
    override suspend fun insertCSOrHutAlertExtras(
        alertsList: List<AlertExtraSerial>,
        itemType: AssetType
    ) {
        val validAlertExtras = alertsList.filter { alertSerial ->
            alertSerial.assetId != null
            &&
            alertSerial.name.isNotBlank()
        }

        alertsDao.insertAlertExtras(
            validAlertExtras.map { alertSerial ->
                AlertExtraEntity.from(
                    alertSerial,
                    alertSerial.alerts.size,
                    itemType
                )
            }
        )

        validAlertExtras.forEach { alertSerial ->
            affectedDao.insertAffectedExtras(
                alertSerial.alerts.map { affected ->
                    AffectedExtraEntity.from(
                        affected,
                        alertSerial.assetId.toString(),
                        itemType
                    )
                }
            )
        }
    }

    /* Tracks Only */
    override suspend fun insertTrackAlertExtras(
        alertsList: List<TrackExtraSerial>
    ) {
        val validAlertExtras = alertsList.filter { alertSerial ->
            alertSerial.assetId.isNotBlank()
            &&
            alertSerial.name.isNotBlank()
        }

        alertsDao.insertAlertExtras(
            validAlertExtras.map { alertSerial ->
                AlertExtraEntity.from(
                    alertSerial,
                    alertSerial.alerts.size
                )
            }
        )

        validAlertExtras.forEach { alertSerial ->
            affectedDao.insertAffectedExtras(
                alertSerial.alerts.map { affected ->
                    AffectedExtraEntity.from(
                        affected,
                        alertSerial.assetId,
                        AssetType.Track
                    )
                }
            )
        }
    }

    /* All calls to this method are from within a 200 block */
    override suspend fun updateAlert(
        alert: AlertSerial,
        responseCode: Int,
        updateMillis: Long
    ) : Int {
        affectedDao.insertAffected(
            alert.affectedAssets.map { affected ->
                AffectedEntity.from(
                    affected, alert.id
                )
            }
        )

        return alertsDao.updateAlert(
            AlertEntity.from(
                alert, responseCode, updateMillis
            )
        )
    }

    override suspend fun updateAlertWithResponse(
        id: String,
        responseCode: Int
    ) {
        alertsDao.updateAlertWithResponseCode(
            id, responseCode
        )
    }
}