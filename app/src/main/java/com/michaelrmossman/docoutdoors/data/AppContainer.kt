package com.michaelrmossman.docoutdoors.data

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.michaelrmossman.docoutdoors.database.OutdoorsDatabase
import com.michaelrmossman.docoutdoors.network.OutdoorsApiService
import com.michaelrmossman.docoutdoors.utils.DEBUG_SHOW_ADDITIONAL_MESSAGES
import com.michaelrmossman.docoutdoors.utils.DOC_API_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

interface AppContainer {

    val alertsNetworkRepo: AlertsNetworkRepoBase
    val alertsOfflineRepo: AlertsOfflineRepoBase

    val campsitesNetworkRepo: CampsitesNetworkRepoBase
    val campsitesOfflineRepo: CampsitesOfflineRepoBase

    val database: OutdoorsDatabase

    val favesRepo: FavouritesRepoBase

    val hutsNetworkRepo: HutsNetworkRepoBase
    val hutsOfflineRepo: HutsOfflineRepoBase

    val mapsRepo: MapsRepoBase

    val regionsRepo: RegionsRepoBase

    val settingsRepo: SettingsRepoBase

    val tracksNetworkRepo: TracksNetworkRepoBase
    val tracksOfflineRepo: TracksOfflineRepoBase
}

class DefaultAppContainer(
    context: Context, private val docApiKey: String
) : AppContainer {

    private val interceptor = HttpLoggingInterceptor().apply {
        if (DEBUG_SHOW_ADDITIONAL_MESSAGES) {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
    /* The above solution shows logcat messages similar to the previous
       Retrofit method, using setLogLevel(RestAdapter.LogLevel.FULL) */
    private val client =
       OkHttpClient.Builder().addInterceptor(interceptor).build()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .baseUrl(DOC_API_URL)
        .client(client)
        .build()
    private val retrofitService: OutdoorsApiService by lazy {
        retrofit.create(OutdoorsApiService::class.java)
    }

    override val database = OutdoorsDatabase.getDatabase(context)

    override val alertsNetworkRepo: AlertsNetworkRepoBase by lazy {
        AlertsNetworkRepository(docApiKey, retrofitService)
    }
    override val alertsOfflineRepo: AlertsOfflineRepoBase by lazy {
        AlertsOfflineRepository(
            database.affectedDao(), database.alertsDao(),
            database.regionsDao(), database.settingsDao()
        )
    }

    override val campsitesNetworkRepo: CampsitesNetworkRepoBase by lazy {
        CampsitesNetworkRepository(docApiKey, retrofitService)
    }
    override val campsitesOfflineRepo: CampsitesOfflineRepoBase by lazy {
        CampsitesOfflineRepository(
            database.affectedDao(),
            database.campsitesDao(),
            database.favesDao(),
            database.regionsDao(),
            database.settingsDao()
        )
    }

    override val favesRepo: FavouritesRepoBase by lazy {
        FavouritesRepository(
            database.campsitesDao(),
            database.favesDao(),
            database.hutsDao(),
            database.settingsDao(),
            database.tracksDao()
        )
    }

    override val hutsNetworkRepo: HutsNetworkRepoBase by lazy {
        HutsNetworkRepository(docApiKey, retrofitService)
    }
    override val hutsOfflineRepo: HutsOfflineRepoBase by lazy {
        HutsOfflineRepository(
            database.affectedDao(),
            database.favesDao(),
            database.hutsDao(),
            database.regionsDao(),
            database.settingsDao()
        )
    }

    override val mapsRepo: MapsRepoBase by lazy {
        MapsRepository(database.settingsDao())
    }

    override val regionsRepo: RegionsRepoBase by lazy {
        RegionsRepository(database.regionsDao())
    }

    override val settingsRepo: SettingsRepoBase by lazy {
        SettingsRepository(database.regionsDao(), database.settingsDao())
    }

    override val tracksNetworkRepo: TracksNetworkRepoBase by lazy {
        TracksNetworkRepository(docApiKey, retrofitService)
    }
    override val tracksOfflineRepo: TracksOfflineRepoBase by lazy {
        TracksOfflineRepository(
            database.affectedDao(),
            database.coordsDao(),
            database.favesDao(),
            database.regionsDao(),
            database.settingsDao(),
            database.tracksDao()
        )
    }
}