package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.interfaces.AlertSingleResponse
import com.michaelrmossman.docoutdoors.interfaces.AlertsListResponse

/**
 * Base network repository that fetches various lists using retrofit
 */
interface AlertsNetworkRepoBase {

    suspend fun getAllAlerts(
        callback: (AlertsListResponse) -> Unit, id: String?
    )

    suspend fun getAlertsByRegionId(
        callback: (AlertsListResponse) -> Unit, id: String
    )

    suspend fun getAlert(
        callback: (AlertSingleResponse) -> Unit, id: String
    )
}