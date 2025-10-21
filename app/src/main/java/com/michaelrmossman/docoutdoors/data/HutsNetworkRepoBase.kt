package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.interfaces.HutsListResponse
import com.michaelrmossman.docoutdoors.interfaces.HutSingleResponse
import com.michaelrmossman.docoutdoors.model.AlertExtraSerial
import com.michaelrmossman.docoutdoors.model.HutSerial

/**
 * Base network repository that fetches various lists using retrofit
 */
interface HutsNetworkRepoBase {

    suspend fun getAllHuts(callback: (HutsListResponse) -> Unit)

    suspend fun getHutAlerts(callback: (List<AlertExtraSerial>) -> Unit)

    suspend fun getHut(callback: (HutSingleResponse) -> Unit, id: String)
}