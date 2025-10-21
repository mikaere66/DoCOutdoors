package com.michaelrmossman.docoutdoors.data

import kotlinx.coroutines.flow.Flow

/**
 * Base offline repository that fetches various settings from database
 */
interface MapsRepoBase {

    val commonSatelliteView: Flow<Int>

    val commonShowLocation: Flow<Int>

    fun setCommonSatelliteView(satelliteView: Int)

    fun setCommonShowLocation(showLocation: Int)
}