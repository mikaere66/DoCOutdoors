package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.CampsitesDao
import com.michaelrmossman.docoutdoors.database.DbHelpers.getFavouriteItemKtQuery
import com.michaelrmossman.docoutdoors.database.HutsDao
import com.michaelrmossman.docoutdoors.database.TracksDao
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_FAVES
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.utils.parseMillisToKiwiDate
import com.michaelrmossman.docoutdoors.utils.parseKiwiDateToMillis
import kotlinx.coroutines.flow.first

@Entity(tableName = TABLE_NAME_FAVES) // ∞
data class FaveEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Int,

    @ColumnInfo
    val added: Long,

    @ColumnInfo
    val assetId: String,

    @ColumnInfo
    val itemType: String
) {
    companion object {
        fun from(fave: Favourite): FaveEntity {
            return FaveEntity(
                fave.id,
                fave.added, // .parseKiwiDateToMillis(),
                fave.assetId,
                fave.itemType.name
            )
        }
    }

    suspend fun toFavourite(
        campsitesDao: CampsitesDao,
        hutsDao: HutsDao,
        tracksDao: TracksDao
    ) : Favourite {
        val fave = Favourite(
            id,
            added, // .parseMillisToKiwiDate(),
            assetId,
            AssetType.valueOf(itemType)
        )
        val query = getFavouriteItemKtQuery(
            itemId = fave.assetId,
            itemType = fave.itemType
        )
        return when (fave.itemType) {
            AssetType.Campsite -> {
                val campsiteKt =
                    campsitesDao.getCampsiteKt(query).first()
                fave.copy(
                    campsiteKt = campsiteKt,
                    name = campsiteKt.name
                )
            }
            AssetType.Hut -> {
                val hutKt = hutsDao.getHutKt(query).first()
                fave.copy(
                    hutKt = hutKt,
                    name = hutKt.name
                )
            }
            AssetType.Track -> {
                val trackKt = tracksDao.getTrackKt(query).first()
                fave.copy(
                    trackKt = trackKt,
                    name = trackKt.name
                )
            }
        }
    }
}