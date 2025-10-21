package com.michaelrmossman.docoutdoors.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.docoutdoors.model.CoordsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordsDao {

//    @Query("DELETE FROM $TABLE_NAME_COORDS")
//    suspend fun deleteAllCoords()

    @Query("""
        SELECT * FROM $TABLE_NAME_COORDS
        WHERE assetId = :id
        AND listIndex = :listIndex
    """)
    suspend fun getCoordsByTrackId(
        id: String, listIndex: Int
    ) : List<CoordsEntity>

    /* Each track's polyline is made up of one to many individual lines, so to
       see if coordinates for a particular track have already been downloaded,
       get a count of distinct indices in listIndex column for that assetId */
    @Query("""
        SELECT COUNT(DISTINCT listIndex)
        FROM $TABLE_NAME_COORDS
        WHERE assetId = :id
    """)
    suspend fun getLineCountByTrackId(
        id: String
    ) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoords(
        coordsList: List<CoordsEntity>
    )
}