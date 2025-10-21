package com.michaelrmossman.docoutdoors.workers

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.michaelrmossman.docoutdoors.OutdoorsApplication.Companion.instance
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.data.AlertsNetworkRepoBase
import com.michaelrmossman.docoutdoors.data.AlertsOfflineRepoBase
import com.michaelrmossman.docoutdoors.model.AlertSerial
import com.michaelrmossman.docoutdoors.utils.PREF_ALERTS_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.parseStringDateToMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AutoUpdateWorker(
    context: Context, params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "AutoUpdateWorker"
    }

    override suspend fun doWork(): Result {

        val alertsNetworkRepo = instance.container.alertsNetworkRepo
        val alertsOfflineRepo = instance.container.alertsOfflineRepo
        val regionsRepo       = instance.container.regionsRepo
        val settingsRepo      = instance.container.settingsRepo

        makeStatusNotification(
            context = applicationContext,
            message = applicationContext.resources.getString(
                R.string.alerts_work_man_notif
            ),
            numNewAlerts = -1
        )

        return withContext(Dispatchers.IO) {

            try {
                val previousAlertIds = alertsOfflineRepo.getAlertIds()
                val regionPref = settingsRepo.getSettingById(
                    id = PREF_ALERTS_FILTER_BY
                )
                val regionCode = regionsRepo.getRegionCodeByActualId(
                    id = regionPref.first()
                )
                val deletion = alertsOfflineRepo.deleteAlertsByRegionCode(
                    regionCode = regionCode
                )
                Log.d(TAG,"$regionCode alerts deletion: $deletion")

                alertsNetworkRepo.getAlertsByRegionId(
                    id = regionCode, callback = { response ->

                        if (response.responseCode == 200) {

                            if (response.alertsList.isNotEmpty()) {

                                val res = applicationContext.resources

                                val downloaded = String.format(
                                    res.getString(
                                        R.string.alerts_work_man_log
                                    ),
                                    regionCode,
                                    response.alertsList.size
                                )
                                /* e.g. NZ-CAN alerts download: 35 */
                                Log.d(TAG, downloaded)

                                runBlocking {
                                    saveAlertsToDatabase(
                                        response.alertsList, alertsNetworkRepo
                                    )

                                    val alertsListIds =
                                        response.alertsList.map { alert ->
                                            alert.id
                                        }
                                    val newAlertIds = alertsListIds.filter { id ->
                                        !previousAlertIds.contains(id) // Note not
                                    }
                                    val newAlertCount = newAlertIds.size
                                    val message = String.format(
                                        res.getQuantityString(
                                            R.plurals.alerts_work_man_notif,
                                            newAlertCount, // qty for plural
                                            newAlertCount,
                                            regionCode
                                        )
                                    )

                                    makeStatusNotification(
                                        context = applicationContext,
                                        message = message,
                                        numNewAlerts = newAlertCount
                                    )
                                }
                            }
                        }
                    }
                )

                Result.success()

            } catch (throwable: Throwable) {
                Log.e(TAG,throwable.toString())
                Result.failure()
            }
        }
    }

    @WorkerThread
    private suspend fun saveAlertsToDatabase(
        alertsList: List<AlertSerial>,
        alertsNetworkRepo: AlertsNetworkRepoBase
    ) {
        val alertsOfflineRepo = instance.container.alertsOfflineRepo
        val result: List<Long> =
            alertsOfflineRepo.insertAlerts(alertsList)

        if (result.isNotEmpty()) {

            alertsList.forEach { alertSerial ->

                alertsNetworkRepo.getAlert(
                    id = alertSerial.id, callback = { response ->
                        runBlocking {
                            when (response.responseCode) {
                                200 -> {
                                    response.alertSerial?.let { alert ->
                                        saveAlertToDatabase(
                                            alert,
                                            alertsOfflineRepo,
                                            response.responseCode
                                        )
                                    }
                                }
                                else -> updateAlertWithResponse(
                                    alertsOfflineRepo,
                                    alertSerial.id,
                                    response.responseCode
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    @WorkerThread
    private suspend fun saveAlertToDatabase(
        alertSerial: AlertSerial,
        alertsOfflineRepo: AlertsOfflineRepoBase,
        responseCode: Int
    ) {
        alertsOfflineRepo.updateAlert(
            alertSerial,
            responseCode,
            alertSerial.lastUpdated.parseStringDateToMillis()
        )
    }

    @WorkerThread
    private suspend fun updateAlertWithResponse(
        alertsOfflineRepo: AlertsOfflineRepoBase,
        id: String,
        responseCode: Int
    ) {
        alertsOfflineRepo.updateAlertWithResponse(
            id,
            responseCode
        )
    }
}