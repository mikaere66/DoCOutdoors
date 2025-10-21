package com.michaelrmossman.docoutdoors.data

import com.michaelrmossman.docoutdoors.interfaces.TracksListResponse
import com.michaelrmossman.docoutdoors.interfaces.TrackSingleResponse
import com.michaelrmossman.docoutdoors.model.TrackExtraSerial
import com.michaelrmossman.docoutdoors.model.TrackSerial

/**
 * Base network repository that fetches various lists using retrofit
 */
interface TracksNetworkRepoBase {

    suspend fun getAllTracks(callback: (TracksListResponse) -> Unit, id: String?)

    suspend fun getTrackAlerts(callback: (List<TrackExtraSerial>) -> Unit)

    suspend fun getTrack(callback: (TrackSingleResponse) -> Unit, id: String)
}