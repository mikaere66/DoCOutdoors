package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.database.AffectedDao
import com.michaelrmossman.docoutdoors.database.CampsitesDao
import com.michaelrmossman.docoutdoors.database.DbHelpers.getBookableOrDogsQueryWithExtras
import com.michaelrmossman.docoutdoors.database.DbHelpers.getCountByRegionCodeWithExtrasQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getFavouriteItemKtQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getItemIdsNotDownloadedQuery
import com.michaelrmossman.docoutdoors.database.DbHelpers.getRegionNameByRegionIdAndCode
import com.michaelrmossman.docoutdoors.database.FavesDao
import com.michaelrmossman.docoutdoors.database.RegionsDao
import com.michaelrmossman.docoutdoors.database.SettingsDao
import com.michaelrmossman.docoutdoors.enums.AffectType
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.enums.FilterType
import com.michaelrmossman.docoutdoors.model.CampsiteEntity
import com.michaelrmossman.docoutdoors.model.CampsiteKt
import com.michaelrmossman.docoutdoors.model.CampsiteSerial
import com.michaelrmossman.docoutdoors.utils.PREF_CAMPSITES_DLOAD_ALL
import com.michaelrmossman.docoutdoors.utils.PREF_CAMPSITES_FILTER_BY
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_BOOKABLE
import com.michaelrmossman.docoutdoors.utils.PREF_COMMON_FILTER_DOGS_BY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Campsites offline implementation of CampsitesOfflineRepoBase
 */
class CampsitesOfflineRepository(
    private val affectedDao: AffectedDao,
    private val campsitesDao: CampsitesDao,
    private val favesDao: FavesDao,
    private val regionsDao: RegionsDao,
    private val settingsDao: SettingsDao
) : CampsitesOfflineRepoBase {

    override lateinit var campsiteKt: CampsiteKt
    override fun setFaveCampsite(campsite: CampsiteKt) {
        campsiteKt = campsite
    }

    private val _campsitesAdvancedSearch = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_CAMPSITES_DLOAD_ALL
        )
    )
    override val campsitesAdvancedSearch: Flow<Int>
        get() = _campsitesAdvancedSearch.value
    override fun setCampsitesAdvancedSearch(advSearch: Int) {
        _campsitesAdvancedSearch.value = flowOf(advSearch)
    }

    private val _campsitesFilterById = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_CAMPSITES_FILTER_BY
        )
    )
    override val campsitesFilterById: Flow<Int>
        get() = _campsitesFilterById.value
    override fun setCampsitesRegionCode(region: Int) {
        _campsitesFilterById.value = flowOf(region)
    }

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

    private val _commonFilterByDogAccess = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_COMMON_FILTER_DOGS_BY
        )
    )
    override val commonFilterByDogAccess: Flow<Int>
        get() = _commonFilterByDogAccess.value
    override fun setCommonFilterDogsBy(filterDogsBy: Int) {
        _commonFilterByDogAccess.value = flowOf(filterDogsBy)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val campsiteCount: Flow<Int> =
        _commonFilterByBookable.flatMapLatest { bookable ->
            _commonFilterByDogAccess.flatMapLatest { dogAccess ->
                _campsitesFilterById.flatMapLatest { filterBy ->
                    if (bookable.first().plus(
                        dogAccess.first().plus(
                        filterBy.first()
                    )) == 0) {
                        campsitesDao.getCampsiteNameCount()

                    } else campsitesDao.getCampsiteCountByRegionCode(
                        query = getCountByRegionCodeWithExtrasQuery(
                            bookable = bookable.first(),
                            dogAccess = dogAccess.first(),
                            filterType = FilterType.Campsites,
                            regionId = filterBy.first(),
                        )
                    )
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val campsitesFilterByRegion: Flow<String> =
        _campsitesFilterById.flatMapLatest { filterBy ->
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
    override val campsitesKtFlow: Flow<List<CampsiteKt>> =
        _campsitesFilterById.flatMapLatest { filterBy ->
            _commonFilterByBookable.flatMapLatest { bookable ->
                _commonFilterByDogAccess.flatMapLatest { dogAccess ->
                    campsitesDao.getCampsitesKtFlow(
                        getBookableOrDogsQueryWithExtras(
                            bookable = bookable.first(),
                            dogAccess = dogAccess.first(),
                            regionId = filterBy.first(),
                            itemType = AssetType.Campsite
                        )
                    )
                }
            }
        }

    override suspend fun deleteAllCampsites() {
        campsitesDao.deleteAllCampsites()
        affectedDao.deleteAllAffectedExtras(
            itemType = AssetType.Campsite.name
        )
    }

    override suspend fun deleteCampsite(id: String) =
        campsitesDao.deleteCampsite(id = id)

    override suspend fun doesCampsiteExist(id: String): Boolean =
        campsitesDao.doesCampsiteExist(id)

    override suspend fun getCampsiteById(id: String): CampsiteKt {
        val affected = affectedDao.getAffectedCountByItemId(
            affectId = id,
            itemType = AffectType.Campsite.type
        )
        val favourite = favesDao.isFavourite(
            assetId = id, itemType = AssetType.Campsite.name
        )
        return campsitesDao.getCampsiteById(id).toCampsiteKt(
            affectedCount = affected,
            isFavourite = favourite
        )
    }

    override suspend fun getCampsiteIds(): List<String> =
        campsitesDao.getCampsiteIds()

    override suspend fun getCampsiteIdsByRegionCode(): List<String> {
        val filterBy = settingsDao.getSettingById(
            settingId = PREF_CAMPSITES_FILTER_BY
        )
        val regionCode = regionsDao.getRegionCodeByActualId(
            id = filterBy.first()
        )
        return campsitesDao.getCampsiteIdsByRegionCode(regionCode)
    }

    override fun getCampsiteKt(id: String): Flow<CampsiteKt> =
        campsitesDao.getCampsiteKt(
            getFavouriteItemKtQuery(
                itemId = id, itemType = AssetType.Campsite
            )
        )

    override suspend fun getCampsiteIdsNotDownloaded(
        filterBy: Int
    ) : List<String> {
        return campsitesDao.getCampsiteIdsNotDownloaded(
            query = getItemIdsNotDownloadedQuery(
                regionId = filterBy,
                itemType = AssetType.Campsite
            )
        )
    }

    override fun getCampsiteNameCount(): Flow<Int> =
        campsitesDao.getCampsiteNameCount()

    /* All calls to this method are from within a 200 block */
    override suspend fun updateCampsite(campsite: CampsiteSerial): Int {
        return campsitesDao.updateCampsite(
            CampsiteEntity.from(campsite = campsite, responseCode = 200)
        )
    }

    override suspend fun upsertCampsites(
        campsitesList: List<CampsiteSerial>
    ) : List<Long> = campsitesDao.upsertCampsites(
        campsitesList.filter { campsiteSerial ->
            campsiteSerial.name.isNotBlank()
        }.map { campsiteSerial ->
            when (
                doesCampsiteExist(campsiteSerial.assetId.toString())
            ) {
                false -> CampsiteEntity.from(
                    campsite = campsiteSerial, responseCode = 200
                )
                /* For upsert, do NOT update bookable / dogs,
                   as these will be null for getAll*Assets() */
                else -> {
                    val campsiteEntity = campsitesDao.getCampsiteById(
                        campsiteSerial.assetId.toString()
                    )
                    CampsiteEntity.from(
                        campsite = campsiteSerial.copy(
                            bookable = campsiteEntity.bookable,
                            dogsAllowed = campsiteEntity.dogsAllowed
                        ),
                        responseCode = 200
                    )
                }
            }
        }
    )

    override suspend fun updateCampsiteWithResponse(
        assetId: String,
        responseCode: Int
    ) {
        campsitesDao.updateCampsiteWithResponseCode(
            assetId, responseCode
        )
    }
}