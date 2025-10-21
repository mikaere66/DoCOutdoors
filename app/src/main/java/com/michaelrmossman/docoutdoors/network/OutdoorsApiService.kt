package com.michaelrmossman.docoutdoors.network

import com.michaelrmossman.docoutdoors.model.AlertExtraSerial
import com.michaelrmossman.docoutdoors.model.AlertSerial
import com.michaelrmossman.docoutdoors.model.CampsiteSerial
import com.michaelrmossman.docoutdoors.model.HutSerial
import com.michaelrmossman.docoutdoors.model.TrackExtraSerial
import com.michaelrmossman.docoutdoors.model.TrackSerial
import com.michaelrmossman.docoutdoors.utils.DOC_ALERTS_URL
import com.michaelrmossman.docoutdoors.utils.DOC_ALERTS_BY_REGION_URL
import com.michaelrmossman.docoutdoors.utils.DOC_ALERT_URL
import com.michaelrmossman.docoutdoors.utils.DOC_API_KEY
import com.michaelrmossman.docoutdoors.utils.DOC_CAMPSITES_URL
import com.michaelrmossman.docoutdoors.utils.DOC_CAMPSITE_ALERTS_URL
import com.michaelrmossman.docoutdoors.utils.DOC_CAMPSITE_URL
import com.michaelrmossman.docoutdoors.utils.DOC_HUTS_URL
import com.michaelrmossman.docoutdoors.utils.DOC_HUT_ALERTS_URL
import com.michaelrmossman.docoutdoors.utils.DOC_HUT_URL
import com.michaelrmossman.docoutdoors.utils.DOC_TRACKS_BY_REGION_URL
import com.michaelrmossman.docoutdoors.utils.DOC_TRACKS_URL
import com.michaelrmossman.docoutdoors.utils.DOC_TRACK_ALERTS_URL
import com.michaelrmossman.docoutdoors.utils.DOC_TRACK_URL
import com.michaelrmossman.docoutdoors.utils.DOC_TYPE_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface OutdoorsApiService {

    /* v2/alerts/region/{regionId} */
    @GET(DOC_ALERTS_BY_REGION_URL)
    fun getAlertsByRegionId(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String,
        @Path("regionId") docRegionId: String
    ) : Call<List<AlertSerial>>

    /* v2/alerts */
    @GET(DOC_ALERTS_URL)
    fun getAllAlerts(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String
    ) : Call<List<AlertSerial>>

    /* v2/alerts/{id}?coordinates=wgs84 */
    @GET(DOC_ALERT_URL)
    fun getAlert(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String,
        @Path("id") docAlertId: String
    ) : Call<AlertSerial>

    /* v2/campsites?coordinates=wgs84 */
    @GET(DOC_CAMPSITES_URL)
    fun getAllCampsites(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String
    ) : Call<List<CampsiteSerial>>

    /* v2/campsites/alerts */
    @GET(DOC_CAMPSITE_ALERTS_URL)
    fun getCampsiteAlerts(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String
    ) : Call<List<AlertExtraSerial>>

    /* v2/campsites/{id}/detail?coordinates=wgs84 */
    @GET(DOC_CAMPSITE_URL)
    fun getCampsite(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String,
        @Path("id") docCampsiteId: String
    ) : Call<CampsiteSerial>

    /* v2/huts?coordinates=wgs84 */
    @GET(DOC_HUTS_URL)
    fun getAllHuts(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String
    ) : Call<List<HutSerial>>

    /* v2/huts/alerts */
    @GET(DOC_HUT_ALERTS_URL)
    fun getHutAlerts(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String
    ) : Call<List<AlertExtraSerial>>

    /* v2/huts/{id}/detail?coordinates=wgs84 */
    @GET(DOC_HUT_URL)
    fun getHut(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String,
        @Path("id") docHutId: String
    ) : Call<HutSerial>

    /* v1/tracks?coordinates=wgs84 */
    @GET(DOC_TRACKS_URL)
    fun getAllTracks(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String
    ) : Call<List<TrackSerial>>

    /* v1/tracks/alerts */
    @GET(DOC_TRACK_ALERTS_URL)
    fun getTrackAlerts(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String
    ) : Call<List<TrackExtraSerial>>

    /* v1/tracks/region/{regionId}?coordinates=wgs84 */
    @GET(DOC_TRACKS_BY_REGION_URL)
    fun getTracksByRegionId(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String,
        @Path("regionId") docRegionId: String
    ) : Call<List<TrackSerial>>

    /* v1/tracks/{id}/detail?coordinates=wgs84 */
    @GET(DOC_TRACK_URL)
    fun getTrack(
        @Header(DOC_API_KEY) docApiKey: String,
        @Header(DOC_TYPE_KEY) docTypeKey: String,
        @Path("id") docTrackId: String
    ) : Call<TrackSerial>
}