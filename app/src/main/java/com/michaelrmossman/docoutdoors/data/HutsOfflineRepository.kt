package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.database.AffectedDao
import com.michaelrmossman.docoutdoors.database.DbHelpers.getBookableOrDogsQueryWithExtras
import com.michaelrmossman.docoutdoors.database.DbHelpers.getCountByRegionCodeWithExtrasQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getFavouriteItemKtQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getItemIdsNotDownloadedQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getRegionNameByRegionIdAndCode
import com.michaelrmossman.docoutdoors.database.FavesDao
import com.michaelrmossman.docoutdoors.database.HutsDao
import com.michaelrmossman.docoutdoors.database.RegionsDao
import com.michaelrmossman.docoutdoors.database.SettingsDao
import com.michaelrmossman.docoutdoors.enums.AffectType
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.model.HutKt
import com.michaelrmossman.docoutdoors.model.HutEntity
import com.michaelrmossman.docoutdoors.model.HutSerial
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_BOOKABLE
import com.michaelrmossman.docoutdoors.utils.PREF_HUTS_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_HUTS_FILTER_BY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Huts offline implementation of HutsOfflineRepoBase
 */
class HutsOfflineRepository(
    private val affectedDao: AffectedDao,
    private val favesDao: FavesDao,
    private val hutsDao: HutsDao,
    private val regionsDao: RegionsDao,
    private val settingsDao: SettingsDao
) : HutsOfflineRepoBase {

    private val _commonFilterByBookable = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_COMMON_FILTER_BOOKABLE
        )
    )
    override val commonFilterByBookable: Flow<Int>
        get() = _commonFilterByBookable.value
    override fun setCommonFilterBookable(filterBookable: Int) {
        _commonFilterByBookable.value = flowOf(filterBookable)
    }

    override suspend fun deleteAllHuts() {
        hutsDao.deleteAllHuts()
        affectedDao.deleteAllAffectedExtras(
            itemType = AssetType.Hut.name
        )
    }

    override suspend fun deleteHut(id: String) =
        hutsDao.deleteHut(id = id)

    override suspend fun doesHutExist(id: String): Boolean =
        hutsDao.doesHutExist(id)

    override suspend fun getHutById(id: String): HutKt {
        val affected = affectedDao.getAffectedCountByItemId(
            affectId = id,
            itemType = AffectType.Hut.type
        )
        val favourite = favesDao.isFavourite(
            assetId = id, itemType = AssetType.Hut.name
        )
        return hutsDao.getHutById(id).toHutKt(
            affectedCount = affected,
            isFavourite = favourite
        )
    }

    override suspend fun getHutIds(): List<String> =
        hutsDao.getHutIds()

    override suspend fun getHutIdsByRegionCode(): List<String> {
        val filterBy = settingsDao.getSettingById(
            settingId = PREF_HUTS_FILTER_BY
        )
        val regionCode = regionsDao.getRegionCodeByActualId(
            id = filterBy.first()
        )
        return hutsDao.getHutIdsByRegionCode(regionCode)
    }

    override suspend fun getHutIdsNotDownloaded(
        filterBy: Int
    ) : List<String> {
        return hutsDao.getHutIdsNotDownloaded(
            query = getItemIdsNotDownloadedQuery(
                regionId = filterBy,
                itemType = AssetType.Hut
            )
        )
    }

    override fun getHutKt(id: String): Flow<HutKt> =
        hutsDao.getHutKt(
            getFavouriteItemKtQuery(
                itemId = id, itemType = AssetType.Hut
            )
        )

    override fun getHutNameCount(): Flow<Int> =
        hutsDao.getHutNameCount()

    override lateinit var hutKt: HutKt
    override fun setFaveHut(hut: HutKt) {
        hutKt = hut
    }

    private val _hutsAdvancedSearch = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_HUTS_DLOAD_ALL
        )
    )
    override val hutsAdvancedSearch: Flow<Int>
        get() = _hutsAdvancedSearch.value
    override fun setHutsAdvancedSearch(advSearch: Int) {
        _hutsAdvancedSearch.value = flowOf(advSearch)
    }

    private val _hutsFilterById = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_HUTS_FILTER_BY
        )
    )
    override val hutsFilterById: Flow<Int>
        get() = _hutsFilterById.value
    override fun setHutsRegionCode(region: Int) {
        _hutsFilterById.value = flowOf(region)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val hutCount: Flow<Int> =
        _commonFilterByBookable.flatMapLatest { bookable ->
            _hutsFilterById.flatMapLatest { filterBy ->
                if (bookable.first().plus(filterBy.first()) == 0) {
                    hutsDao.getHutNameCount()

                } else hutsDao.getHutCountByRegionCode(
                    query = getCountByRegionCodeWithExtrasQuery(
                        bookable = bookable.first(),
                        filterType = FilterType.Huts,
                        regionId = filterBy.first(),
                    )
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val hutsFilterByRegion: Flow<String> =
        _hutsFilterById.flatMapLatest { filterBy ->
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
    override val hutsKtFlow: Flow<List<HutKt>> =
        _hutsFilterById.flatMapLatest { filterBy ->
            _commonFilterByBookable.flatMapLatest { bookable ->
                hutsDao.getHutsKtFlow(
                    getBookableOrDogsQueryWithExtras(
                        bookable = bookable.first(),
                        regionId = filterBy.first(),
                        itemType = AssetType.Hut
                    )
                )
            }
        }

    /* All calls to this method are from within a 200 block */
    override suspend fun updateHut(hut: HutSerial): Int {
        return hutsDao.updateHut(
            HutEntity.from(hut, responseCode = 200)
        )
    }

    override suspend fun updateHutWithResponse(
        assetId: String,
        responseCode: Int
    ) {
        hutsDao.updateHutWithResponseCode(
            assetId, responseCode
        )
    }

    override suspend fun upsertHuts(
        hutsList: List<HutSerial>
    ) : List<Long> = hutsDao.upsertHuts(
        hutsList.filter { hutSerial ->
            hutSerial.name.isNotBlank()
        }.map { hutSerial ->
            when (
                doesHutExist(hutSerial.assetId.toString())
            ) {
                false -> HutEntity.from(
                    hutSerial, responseCode = 200
                )
                /* For upsert, do NOT update dog access string,
                   as this will be null for getAll*Assets() */
                else -> {
                    val hutEntity = hutsDao.getHutById(
                        hutSerial.assetId.toString()
                    )
                    HutEntity.from(
                        hut = hutSerial.copy(
                            bookable = hutEntity.bookable
                        ),
                        responseCode = 200
                    )
                }
            }
        }
    )
}