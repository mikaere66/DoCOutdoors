package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.interfaces.CampsitesListResponse
import com.michaelrmossman.docoutdoors.interfaces.CampsiteSingleResponse
import com.michaelrmossman.docoutdoors.model.AlertExtraSerial
import com.michaelrmossman.docoutdoors.model.CampsiteSerial
import com.michaelrmossman.docoutdoors.network.OutdoorsApiService
import com.michaelrmossman.docoutdoors.utils.DOC_DATA_TYPE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Campsites network implementation of CampsitesNetworkRepoBase
 */
class CampsitesNetworkRepository(
    private val docApiKey: String,
    private val outdoorsApiService: OutdoorsApiService
) : CampsitesNetworkRepoBase {

    /** Fetches list of [CampsiteSerial]s from outdoorsApi*/
    override suspend fun getAllCampsites(callback: (CampsitesListResponse) -> Unit) {
        outdoorsApiService.getAllCampsites(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE
        ).enqueue(object : Callback<List<CampsiteSerial>> {
            override fun onResponse(
                call:     Call<List<CampsiteSerial>>,
                response: Response<List<CampsiteSerial>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { campsitesList ->
                        // android.util.Log.d("HEY", campsitesList.size.toString())
                        callback(
                            CampsitesListResponse(
                                campsitesList = campsitesList,
                                responseCode = response.code()
                            )
                        )
                    }

                } else {
                    callback(
                        CampsitesListResponse(
                            responseCode = response.code()
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<List<CampsiteSerial>>, throwable: Throwable
            ) {
                callback(
                    CampsitesListResponse(/* TODO */)
                )
                println(throwable)
            }
        })
    }

    /** Fetches list of [AlertExtraSerial]s from outdoorsApi*/
    override suspend fun getCampsiteAlerts(callback: (List<AlertExtraSerial>) -> Unit) {
        outdoorsApiService.getCampsiteAlerts(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE
        ).enqueue(object : Callback<List<AlertExtraSerial>> {
            override fun onResponse(
                call:     Call<List<AlertExtraSerial>>,
                response: Response<List<AlertExtraSerial>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { alertsList ->
                        // android.util.Log.d("HEY", alertsList.size.toString())
                        callback(alertsList)
                    }
                }
            }

            override fun onFailure(
                call: Call<List<AlertExtraSerial>>, throwable: Throwable
            ) {
                callback(emptyList())
                println(throwable)
            }
        })
    }

    /** Fetches single [CampsiteSerial] from outdoorsApi*/
    override suspend fun getCampsite(
        callback: (CampsiteSingleResponse) -> Unit, id: String
    ) {
        val call = outdoorsApiService.getCampsite(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE,
            docCampsiteId = id
        )
        call.enqueue(object : Callback<CampsiteSerial?> {
            override fun onResponse(
                call:     Call<CampsiteSerial?>,
                response: Response<CampsiteSerial?>
            ) {
                if (response.errorBody() != null) {
                    callback(
                        CampsiteSingleResponse(
                            responseCode = 404
                        )
                    )

                } else if (response.isSuccessful) {
                    response.body()?.let { campsite ->
                        callback(
                            CampsiteSingleResponse(
                                campsiteSerial = campsite,
                                responseCode = response.code()
                            )
                        )
                    }

                } else {
                    callback(
                        CampsiteSingleResponse(
                            responseCode = response.code()
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<CampsiteSerial?>, throwable: Throwable
            ) {
                callback(CampsiteSingleResponse())
                println(throwable)
            }
        })
    }
}