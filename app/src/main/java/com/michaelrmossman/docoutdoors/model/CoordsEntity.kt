package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_COORDS

@Entity(tableName = TABLE_NAME_COORDS) // 20
data class CoordsEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Int = 0,

    @ColumnInfo
    val assetId: String,

    @ColumnInfo
    val listIndex: Int,

    @ColumnInfo
    val lat: Double,

    @ColumnInfo
    val lon: Double
)