package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.enums.ByRegionType

/**
 * Base offline repository for regions
 */
interface RegionsRepoBase {

    suspend fun getRegionCodeByActualId(
        id: Int, byRegionType: ByRegionType? = null
    ) : String

    suspend fun resetAlertsDload()

    suspend fun resetTracksDload()
}