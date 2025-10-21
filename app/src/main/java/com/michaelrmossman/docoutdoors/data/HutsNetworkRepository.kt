package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.interfaces.HutsListResponse
import com.michaelrmossman.docoutdoors.interfaces.HutSingleResponse
import com.michaelrmossman.docoutdoors.model.AlertExtraSerial
import com.michaelrmossman.docoutdoors.model.HutSerial
import com.michaelrmossman.docoutdoors.network.OutdoorsApiService
import com.michaelrmossman.docoutdoors.utils.DOC_DATA_TYPE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Huts network implementation of HutsNetworkRepoBase
 */
class HutsNetworkRepository(
    private val docApiKey: String,
    private val outdoorsApiService: OutdoorsApiService
) : HutsNetworkRepoBase {

    /** Fetches list of [HutSerial]s from outdoorsApi*/
    override suspend fun getAllHuts(callback: (HutsListResponse) -> Unit) {
        val call = outdoorsApiService.getAllHuts(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE
        )
        call.enqueue(object : Callback<List<HutSerial>> {
            override fun onResponse(
                call:     Call<List<HutSerial>>,
                response: Response<List<HutSerial>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { hutsList ->
                        // android.util.Log.d("HEY", hutsList.size.toString())
                        callback(
                            HutsListResponse(
                                hutsList = hutsList,
                                responseCode = response.code()
                            )
                        )
                    }

                } else {
                    callback(
                        HutsListResponse(
                            responseCode = response.code()
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<List<HutSerial>>, throwable: Throwable
            ) {
                callback(
                    HutsListResponse(/* TODO */)
                )
                println(throwable)
            }
        })
    }

    /** Fetches list of [AlertExtraSerial]s from outdoorsApi*/
    override suspend fun getHutAlerts(callback: (List<AlertExtraSerial>) -> Unit) {
        outdoorsApiService.getHutAlerts(
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

    /** Fetches single [HutSerial] from outdoorsApi*/
    override suspend fun getHut(callback: (HutSingleResponse) -> Unit, id: String) {
        val call = outdoorsApiService.getHut(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE,
            docHutId = id
        )
        call.enqueue(object : Callback<HutSerial?> {
            override fun onResponse(
                call:     Call<HutSerial?>,
                response: Response<HutSerial?>
            ) {
                if (response.errorBody() != null) {
                    callback(
                        HutSingleResponse(
                            responseCode = 404
                        )
                    )

                } else if (response.isSuccessful) {
                    response.body()?.let { hut ->
                        callback(
                            HutSingleResponse(
                                hutSerial = hut,
                                responseCode = response.code()
                            )
                        )
                    }

                } else {
                    HutSingleResponse(
                        responseCode = response.code()
                    )
                }
            }

            override fun onFailure(
                call: Call<HutSerial?>, throwable: Throwable
            ) {
                callback(HutSingleResponse())
                println(throwable)
            }
        })
    }
}