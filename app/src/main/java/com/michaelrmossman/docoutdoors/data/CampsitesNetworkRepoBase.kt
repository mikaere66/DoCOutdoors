package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.interfaces.CampsitesListResponse
import com.michaelrmossman.docoutdoors.interfaces.CampsiteSingleResponse
import com.michaelrmossman.docoutdoors.model.AlertExtraSerial
import com.michaelrmossman.docoutdoors.model.CampsiteSerial

/**
 * Base network repository that fetches various lists using retrofit
 */
interface CampsitesNetworkRepoBase {

    suspend fun getAllCampsites(callback: (CampsitesListResponse) -> Unit)

    suspend fun getCampsiteAlerts(callback: (List<AlertExtraSerial>) -> Unit)

    suspend fun getCampsite(callback: (CampsiteSingleResponse) -> Unit, id: String)
}