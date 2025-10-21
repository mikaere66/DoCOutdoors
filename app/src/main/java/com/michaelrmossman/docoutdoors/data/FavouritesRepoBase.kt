package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.model.Favourite
import com.michaelrmossman.docoutdoors.enums.SortFavesBy
import kotlinx.coroutines.flow.Flow

interface FavouritesRepoBase {

    suspend fun deleteAllFavourites(): Int

    suspend fun deleteFave(fave: Favourite): Int

    suspend fun deleteFaveByIdAndType(
        assetId: String, itemType: String
    ) : Int

    val faveCount: Flow<Int>

    val favesSortedBy: Flow<Int>

    fun getAllFavourites(): Flow<List<Favourite>>

    suspend fun insertFave(
        assetId: String, itemType: String
    ) : Long

    suspend fun setFavesSortedBy(sortBy: SortFavesBy)
}