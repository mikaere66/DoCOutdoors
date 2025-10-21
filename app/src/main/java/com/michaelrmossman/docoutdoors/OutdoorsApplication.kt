package com.michaelrmossman.docoutdoors

import android.app.Application
import com.michaelrmossman.docoutdoors.data.AppContainer
import com.michaelrmossman.docoutdoors.data.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class OutdoorsApplication: Application() {

    /* DoC API key string, from file "secrets.properties" */
    private val docApiKey = BuildConfig.DOC_API_KEY

    /* Continue coroutines after viewModelScope lifecycle */
    val applicationScope = CoroutineScope(SupervisorJob())

    /* AppContainer instance, used to obtain dependencies */
    lateinit var container: AppContainer

    companion object {

        lateinit var instance: OutdoorsApplication
    }

    override fun onCreate() {
        super.onCreate()

        container = DefaultAppContainer(
            applicationContext, docApiKey
        )

        instance = this
    }
}