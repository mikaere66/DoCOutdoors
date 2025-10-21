package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.interfaces.AlertsListResponse
import com.michaelrmossman.docoutdoors.interfaces.AlertSingleResponse
import com.michaelrmossman.docoutdoors.model.AlertSerial
import com.michaelrmossman.docoutdoors.network.OutdoorsApiService
import com.michaelrmossman.docoutdoors.utils.DEBUG_SHOW_ADDITIONAL_MESSAGES
import com.michaelrmossman.docoutdoors.utils.DOC_DATA_TYPE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Alerts network implementation of AlertsNetworkRepoBase
 */
class AlertsNetworkRepository(
    private val docApiKey: String,
    private val outdoorsApiService: OutdoorsApiService
) : AlertsNetworkRepoBase {

    /** Fetches entire list of [AlertSerial]s from outdoorsApi,
        unless regionId is specified, then just alertsByRegion */
    override suspend fun getAllAlerts(
        callback: (AlertsListResponse) -> Unit, id: String?
    ) {
        val call = when (id) {
            null -> outdoorsApiService.getAllAlerts(
                docApiKey = docApiKey,
                docTypeKey = DOC_DATA_TYPE
            )
            else -> outdoorsApiService.getAlertsByRegionId(
                docApiKey = docApiKey,
                docTypeKey = DOC_DATA_TYPE,
                docRegionId = id
            )
        }
        call.enqueue(object : Callback<List<AlertSerial>> {
            override fun onResponse(
                call:     Call<List<AlertSerial>>,
                response: Response<List<AlertSerial>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { alertsList ->
                        callback(
                            AlertsListResponse(
                                alertsList = alertsList,
                                responseCode = response.code()
                            )
                        )
                    }

                } else {
                    callback(
                        AlertsListResponse(
                            responseCode = response.code()
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<List<AlertSerial>>, throwable: Throwable
            ) {
                callback(
                    AlertsListResponse(/* TODO */)
                )
                println(throwable)
            }
        })
    }

    /** Fetches sub-list of [AlertSerial]s based on regionCode */
    override suspend fun getAlertsByRegionId(
        callback: (AlertsListResponse) -> Unit, id: String
    ) {
        val call = outdoorsApiService.getAlertsByRegionId(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE,
            docRegionId = id
        )
        call.enqueue(object : Callback<List<AlertSerial>> {
            override fun onResponse(
                call:     Call<List<AlertSerial>>,
                response: Response<List<AlertSerial>>
            ) {
                if (response.errorBody() != null) {
                    callback(
                        AlertsListResponse(
                            /* https://restfulapi.net/http-status-codes/ */
                            responseCode = 418
                        )
                    )

                } else if (response.isSuccessful) {
                    response.body()?.let { alertsList ->
                        // android.util.Log.d("HEY",alertsList.size.toString())
                        callback(
                            AlertsListResponse(
                                alertsList = alertsList,
                                responseCode = response.code()
                            )
                        )
                    }

                } else {
                    callback(
                        AlertsListResponse(
                            responseCode = response.code()
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<List<AlertSerial>>, throwable: Throwable
            ) {
                callback(
                    AlertsListResponse()
                )
                println(throwable)
            }
        })
    }

    /** Fetches an [AlertSerial] from outdoorsApi, based on id */
    override suspend fun getAlert(
        callback: (AlertSingleResponse) -> Unit, id: String
    ) {
        val call = outdoorsApiService.getAlert(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE,
            docAlertId = id
        )
        call.enqueue(object : Callback<AlertSerial?> {
            override fun onResponse(
                call:     Call<AlertSerial?>,
                response: Response<AlertSerial?>
            ) {
                if (response.errorBody() != null) {
                    callback(
                        AlertSingleResponse(
                            responseCode = 404
                        )
                    )

                } else if (response.isSuccessful) {
                    response.body()?.let { alert ->
                        if (DEBUG_SHOW_ADDITIONAL_MESSAGES) {
                            alert.affectedAssets.forEach { asset ->
                                android.util.Log.d("HEY",asset.name)
                            }
                        }

                        callback(
                            AlertSingleResponse(
                                alertSerial = alert,
                                responseCode = response.code()
                            )
                        )
                    }

                } else {
                    callback(
                        AlertSingleResponse(
                            responseCode = response.code()
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<AlertSerial?>, throwable: Throwable
            ) {
                callback(AlertSingleResponse())
                println(throwable)
            }
        })
    }
}