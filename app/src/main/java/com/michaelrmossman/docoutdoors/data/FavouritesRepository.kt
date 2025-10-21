package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.database.CampsitesDao
import com.michaelrmossman.docoutdoors.database.FavesDao
import com.michaelrmossman.docoutdoors.database.HutsDao
import com.michaelrmossman.docoutdoors.database.SettingsDao
import com.michaelrmossman.docoutdoors.database.TracksDao
import com.michaelrmossman.docoutdoors.enums.SortFavesBy
import com.michaelrmossman.docoutdoors.model.FaveEntity
import com.michaelrmossman.docoutdoors.model.FaveEntity.Companion.from
import com.michaelrmossman.docoutdoors.model.Favourite
import com.michaelrmossman.docoutdoors.model.SettingEntity
import com.michaelrmossman.docoutdoors.utils.PREF_FAVES_SORTED_BY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/*
 * Concrete class implementation to access database
 */
class FavouritesRepository(
    private val campsitesDao: CampsitesDao,
    private val favesDao: FavesDao,
    private val hutsDao: HutsDao,
    private val settingsDao: SettingsDao,
    private val tracksDao: TracksDao
) : FavouritesRepoBase {

    private val _favesSortedBy = MutableStateFlow(
        settingsDao.getSettingById(
            settingId = PREF_FAVES_SORTED_BY
        )
    )
    override val favesSortedBy: Flow<Int>
        get() = _favesSortedBy.value

    override suspend fun deleteAllFavourites(): Int =
        favesDao.deleteAllFavourites()

    override suspend fun deleteFave(fave: Favourite): Int =
        favesDao.deleteFave(fave = from(fave))

    override suspend fun deleteFaveByIdAndType(
        assetId: String, itemType: String
    ) : Int = favesDao.deleteFaveByIdAndType(
        assetId = assetId, itemType = itemType
    )

    override val faveCount: Flow<Int> = favesDao.getFaveCount()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllFavourites(): Flow<List<Favourite>> =
        _favesSortedBy.flatMapLatest { sortedBy ->
            val sortFavesBy = SortFavesBy.entries[sortedBy.first()]

            favesDao.getFavesFlow().map { faves ->

                faves.map { fave ->
                    fave.toFavourite(campsitesDao, hutsDao, tracksDao)
                }

            }.map { faves ->

                faves.sortedBy { fave: Favourite ->
                    when (sortFavesBy) {
                        SortFavesBy.Date -> fave.added.toString()
                        SortFavesBy.Name -> fave.name
                        SortFavesBy.Type -> fave.itemType.name
                    }
                }
            }
        }

    override suspend fun insertFave(
        assetId: String,
        itemType: String
    ) : Long {
        val faveEntity = FaveEntity(
            id = 0,
            added = System.currentTimeMillis(),
            assetId = assetId,
            itemType = itemType
        )
        return favesDao.insertFave(faveEntity)
    }

    override suspend fun setFavesSortedBy(sortBy: SortFavesBy) {
        val settingEntity = SettingEntity(
            settingId = PREF_FAVES_SORTED_BY,
            setting = sortBy.ordinal
        )
        if (settingsDao.updateSetting(settingEntity) > 0) {
            _favesSortedBy.value = flowOf(sortBy.ordinal)
        }
    }
}