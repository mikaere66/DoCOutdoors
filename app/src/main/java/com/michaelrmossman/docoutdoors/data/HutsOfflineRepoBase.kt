package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.model.HutSerial
import kotlinx.coroutines.flow.Flow

/**
 * Base offline repository that fetches various lists from database
 */
interface HutsOfflineRepoBase {

    val commonFilterByBookable: Flow<Int>

    suspend fun deleteAllHuts()

    suspend fun deleteHut(id: String)

    suspend fun doesHutExist(id: String): Boolean

    suspend fun getHutById(id: String): HutKt

    suspend fun getHutIds(): List<String>

    suspend fun getHutIdsByRegionCode(): List<String>

    suspend fun getHutIdsNotDownloaded(filterBy: Int): List<String>

    fun getHutKt(id: String): Flow<HutKt>

    fun getHutNameCount(): Flow<Int>

    val hutCount: Flow<Int>

    var hutKt: HutKt

    val hutsAdvancedSearch: Flow<Int>

    val hutsFilterById: Flow<Int>

    val hutsFilterByRegion: Flow<String>

    val hutsKtFlow: Flow<List<HutKt>>

    fun setCommonFilterBookable(filterBookable: Int)

    fun setFaveHut(hut: HutKt)

    fun setHutsAdvancedSearch(advSearch: Int)

    fun setHutsRegionCode(region: Int)

    suspend fun updateHut(hut: HutSerial): Int

    suspend fun updateHutWithResponse(
        assetId: String,
        responseCode: Int
    )

    suspend fun upsertHuts(hutsList: List<HutSerial>): List<Long>
}