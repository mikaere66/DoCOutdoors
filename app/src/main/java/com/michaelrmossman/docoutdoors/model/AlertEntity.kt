package com.michaelrmossman.docoutdoors.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.docoutdoors.database.TABLE_NAME_ALERTS
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_ITEM_ID
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_COMMON_RESP_CODE
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_ALERT_SUMMARY
import com.michaelrmossman.docoutdoors.database.COLUMN_NAME_REGION_MULTI
import com.michaelrmossman.docoutdoors.utils.replaceCRLF
import com.michaelrmossman.docoutdoors.utils.JsonUtils.parseJsonArrayAsObjects

@Entity(tableName = TABLE_NAME_ALERTS) // ~ 200-300
data class AlertEntity(

    // Labels in same order as original JSON response:
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COLUMN_NAME_COMMON_ITEM_ID)
    val id: String,

    @ColumnInfo(name = COLUMN_NAME_ALERT_SUMMARY)
    val summary: String,

    @ColumnInfo
    val description: String,

    @ColumnInfo
    val descriptionHtml: String,

    @ColumnInfo
    val startDate: String,

    @ColumnInfo
    val endDate: String,

    @ColumnInfo
    val lastUpdated: String,

    @ColumnInfo(name = COLUMN_NAME_REGION_MULTI)
    val regionCodes: String, // Array

    /* Although alerts table can be cleared upon reset,
     * we still need a way to trigger recomposition */
//    @ColumnInfo
//    val isFavourite: Boolean,

    /* Added to monitor bad HTTP requests */
    @ColumnInfo(name = COLUMN_NAME_COMMON_RESP_CODE)
    val responseCode: Int,

    /* Added for sort by latest ... only
    added upon insert() NOT update(), to
    keep sortedBy latest order intact */
    @ColumnInfo
    val updateMillis: Long
) {
    companion object {
        fun from(
            alert: AlertSerial,
            responseCode: Int,
            updateMillis: Long
        ) : AlertEntity {
            return AlertEntity(
                alert.id,
                alert.summary,
                alert.description,
                alert.descriptionHtml.replaceCRLF(),
                alert.startDate,
                alert.endDate,
                alert.lastUpdated,
                parseJsonArrayAsObjects(alert.regions),
                // isFavourite = false,
                responseCode,
                updateMillis
            )
        }
    }

    fun toAlert(
        affectedAssets: List<AffectedEntity>,
        // isFavourite: Boolean,
        regionsList: List<RegionSerial>,
        updateMillis: Long
    ) : Alert {
        return Alert(
            id,
            summary,
            description,
            descriptionHtml,
            startDate,
            endDate,
            lastUpdated,
            regionsList,
            affectedAssets,
            // isFavourite,
            updateMillis
        )
    }
}