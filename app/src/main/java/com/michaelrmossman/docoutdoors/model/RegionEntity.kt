package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ITEM_ID
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_REGION_ALERTS_DLOAD
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_REGION_NAME
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_REGION_SINGLE
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_REGION_TRACKS_DLOAD
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_REGIONS

@Entity(tableName = TABLE_NAME_REGIONS) // 20
data class RegionEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COLUMN_NAME_COMMON_ITEM_ID)
    val id: Int,           // Get spinner pos

    /* DoC sometimes refers to this as "regionId"
     * e.g. in region-specific search requests */
    @ColumnInfo(name = COLUMN_NAME_REGION_SINGLE)
    val regionCode: String,    // e.g. NZ-STL

    @ColumnInfo(name = COLUMN_NAME_REGION_NAME)
    val regionName: String, // e.g. Southland

    /* Keep "track" of alerts that have been downloaded
       for specific regions when using filteredBy, then
       if user goes back to ALL alerts, show message */
    @ColumnInfo(name = COLUMN_NAME_REGION_ALERTS_DLOAD)
    val alertsDload: Boolean,

    /* Keep "track" of tracks that have been downloaded
       for specific regions when using filteredBy, then
       if user goes back to ALL tracks, show message */
    @ColumnInfo(name = COLUMN_NAME_REGION_TRACKS_DLOAD)
    val tracksDload: Boolean
)