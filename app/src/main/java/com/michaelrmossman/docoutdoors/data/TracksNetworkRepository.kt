package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.interfaces.TracksListResponse
import com.michaelrmossman.docoutdoors.interfaces.TrackSingleResponse
import com.michaelrmossman.docoutdoors.model.TrackExtraSerial
import com.michaelrmossman.docoutdoors.model.TrackSerial
import com.michaelrmossman.docoutdoors.network.OutdoorsApiService
import com.michaelrmossman.docoutdoors.utils.DOC_DATA_TYPE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Tracks network implementation of TracksNetworkRepoBase
 */
class TracksNetworkRepository(
    private val docApiKey: String,
    private val outdoorsApiService: OutdoorsApiService
) : TracksNetworkRepoBase {

    override suspend fun getAllTracks(
        callback: (TracksListResponse) -> Unit, id: String?
    ) {
        val call = when (id) {
            null -> outdoorsApiService.getAllTracks(
                docApiKey = docApiKey,
                docTypeKey = DOC_DATA_TYPE
            )
            else -> outdoorsApiService.getTracksByRegionId(
                docApiKey = docApiKey,
                docTypeKey = DOC_DATA_TYPE,
                docRegionId = id
            )
        }
        call.enqueue(object : Callback<List<TrackSerial>> {
            override fun onResponse(
                call:     Call<List<TrackSerial>>,
                response: Response<List<TrackSerial>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { tracksList ->
                        callback(
                            TracksListResponse(
                                responseCode = response.code(),
                                tracksList = tracksList
                            )
                        )
                    }

                } else {
                    callback(
                        TracksListResponse(
                            responseCode = response.code()
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<List<TrackSerial>>, throwable: Throwable
            ) {
                callback(
                    TracksListResponse(/* TODO */)
                )
                println(throwable)
            }
        })
    }

    /** Fetches list of [TrackExtraSerial]s from outdoorsApi*/
    override suspend fun getTrackAlerts(
        callback: (List<TrackExtraSerial>) -> Unit
    ) {
        outdoorsApiService.getTrackAlerts(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE
        ).enqueue(object : Callback<List<TrackExtraSerial>> {
            override fun onResponse(
                call:     Call<List<TrackExtraSerial>>,
                response: Response<List<TrackExtraSerial>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { alertsList ->
                        // android.util.Log.d("HEY", alertsList.size.toString())
                        callback(alertsList)
                    }
                }
            }

            override fun onFailure(
                call: Call<List<TrackExtraSerial>>, throwable: Throwable
            ) {
                callback(emptyList())
                println(throwable)
            }
        })
    }

    /** Fetches single [TrackSerial?] from outdoorsApi*/
    override suspend fun getTrack(
        callback: (TrackSingleResponse) -> Unit, id: String
    ) {
        val call = outdoorsApiService.getTrack(
            docApiKey = docApiKey,
            docTypeKey = DOC_DATA_TYPE,
            docTrackId = id
        )
        call.enqueue(object : Callback<TrackSerial?> {
            override fun onResponse(
                call:     Call<TrackSerial?>,
                response: Response<TrackSerial?>
            ) {
                if (response.errorBody() != null) {
                    callback(
                        TrackSingleResponse(
                            responseCode = 404
                        )
                    )

                } else if (response.isSuccessful) {
                    response.body()?.let { track ->
                        callback(
                            TrackSingleResponse(
                                responseCode = response.code(),
                                trackSerial = track
                            )
                        )
                    }

                } else {
                    callback(
                        TrackSingleResponse(
                            responseCode = response.code()
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<TrackSerial?>, throwable: Throwable
            ) {
                callback(TrackSingleResponse())
                println(throwable)
            }
        })
    }
}