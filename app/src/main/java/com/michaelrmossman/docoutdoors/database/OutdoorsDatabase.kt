package com.michaelrmossman.docoutdoors.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.michaelrmossman.docoutdoors.model.AffectedEntity
import com.michaelrmossman.docoutdoors.model.AffectedExtraEntity
import com.michaelrmossman.docoutdoors.model.AlertEntity
import com.michaelrmossman.docoutdoors.model.AlertExtraEntity
import com.michaelrmossman.docoutdoors.model.CampsiteEntity
import com.michaelrmossman.docoutdoors.model.CoordsEntity
import com.michaelrmossman.docoutdoors.model.FaveEntity
import com.michaelrmossman.docoutdoors.model.HutEntity
import com.michaelrmossman.docoutdoors.model.RegionEntity
import com.michaelrmossman.docoutdoors.model.SettingEntity
import com.michaelrmossman.docoutdoors.model.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AffectedEntity::class,
        AffectedExtraEntity::class,
        AlertEntity::class,
        AlertExtraEntity::class,
        CampsiteEntity::class,
        CoordsEntity::class,
        FaveEntity::class,
        HutEntity::class,
        RegionEntity::class,
        SettingEntity::class,
        TrackEntity::class
    ],
    exportSchema = EXPORT_SCHEMA,
    version = DATABASE_VERSION
)
//@TypeConverters(value = [Converters::class])
abstract class OutdoorsDatabase: RoomDatabase() {

    abstract fun affectedDao(): AffectedDao
    abstract fun alertsDao(): AlertsDao
    abstract fun campsitesDao(): CampsitesDao
    abstract fun coordsDao(): CoordsDao
    abstract fun hutsDao(): HutsDao
    abstract fun favesDao(): FavesDao
    abstract fun regionsDao(): RegionsDao
    abstract fun settingsDao(): SettingsDao
    abstract fun tracksDao(): TracksDao

    companion object {
        @Volatile
        private var instance: OutdoorsDatabase? = null

        fun getDatabase(context: Context): OutdoorsDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    OutdoorsDatabase::class.java,
                    "outdoors_database"
                )
                .createFromAsset("databases/outdoors_database.db")
//                .fallbackToDestructiveMigration() // TODO
                .addCallback(object: Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insert the data on the IO thread
                        CoroutineScope(Dispatchers.IO).launch {
                            instance?.settingsDao()?.insertSettings(
                                settings = SettingEntity.getSettings(
                                    allowRandom = false
                                )
                            )
                        }
                    }
                })
                .build()
                .also { instance = it}
            }
        }
    }
}