package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.model.CampsiteSerial
import kotlinx.coroutines.flow.Flow

/**
 * Base offline repository that fetches various lists from database
 */
interface CampsitesOfflineRepoBase {

    val campsiteCount: Flow<Int>

    var campsiteKt: CampsiteKt

    val campsitesAdvancedSearch: Flow<Int>

    val campsitesFilterById: Flow<Int>

    val campsitesFilterByRegion: Flow<String>

    val campsitesKtFlow: Flow<List<CampsiteKt>>

    val commonFilterByBookable: Flow<Int>

    val commonFilterByDogAccess: Flow<Int>

    suspend fun deleteAllCampsites()

    suspend fun deleteCampsite(id: String)

    suspend fun doesCampsiteExist(id: String): Boolean

    suspend fun getCampsiteById(id: String): CampsiteKt

    suspend fun getCampsiteIds(): List<String>

    suspend fun getCampsiteIdsByRegionCode(): List<String>

    fun getCampsiteKt(id: String): Flow<CampsiteKt>

    suspend fun getCampsiteIdsNotDownloaded(filterBy: Int): List<String>

    fun getCampsiteNameCount(): Flow<Int>

    fun setCampsitesAdvancedSearch(advSearch: Int)

    fun setCampsitesRegionCode(region: Int)

    fun setCommonFilterBookable(filterBookable: Int)

    fun setCommonFilterDogsBy(filterDogsBy: Int)

    fun setFaveCampsite(campsite: CampsiteKt)

    suspend fun updateCampsite(campsite: CampsiteSerial): Int

    suspend fun updateCampsiteWithResponse(
        assetId: String,
        responseCode: Int
    )

    suspend fun upsertCampsites(campsitesList: List<CampsiteSerial>): List<Long>
}