package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.michaelrmossman.docoutdoors.model.FaveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavesDao {

    @Query("DELETE FROM $TABLE_NAME_FAVES")
    suspend fun deleteAllFavourites(): Int

    @Delete
    suspend fun deleteFave(fave: FaveEntity): Int

    @Query("""
        DELETE FROM $TABLE_NAME_FAVES
        WHERE assetId = :assetId
        AND itemType = :itemType
    """)
    suspend fun deleteFaveByIdAndType(
        assetId: String, itemType: String
    ) : Int

    @Query("""
        SELECT * FROM $TABLE_NAME_FAVES
        WHERE id = :id
    """)
    suspend fun getFaveById(id: String): FaveEntity

    @Query("""
        SELECT COUNT(*)
        FROM $TABLE_NAME_FAVES
    """)
    fun getFaveCount(): Flow<Int>

//    @Query("""
//        SELECT id FROM $TABLE_NAME_FAVES
//        WHERE itemType = :itemType
//    """)
//    suspend fun getFavouriteIdsByItemType(
//        itemType: String
//    ) : List<String> // Note returns [id]s only

    @Query("SELECT * FROM $TABLE_NAME_FAVES")
    fun getFavesFlow(): Flow<List<FaveEntity>>

    @Insert
    suspend fun insertFave(fave: FaveEntity): Long

    @Query("""
        SELECT EXISTS
        (
            SELECT assetId,itemType
            FROM $TABLE_NAME_FAVES
            WHERE assetId = :assetId
            AND itemType = :itemType
            LIMIT 1
        )
    """)
    suspend fun isFavourite(
        assetId: String, itemType: String
    ) : Boolean

    @Update
    suspend fun updateFave(fave: FaveEntity)
}