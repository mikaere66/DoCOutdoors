package com.michaelrmossman.docoutdoors.utils

import android.content.pm.PackageManager
import android.Manifest
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.michaelrmossman.docoutdoors.enums.AffectType
import com.michaelrmossman.docoutdoors.OutdoorsApplication.Companion.instance
import com.michaelrmossman.docoutdoors.R
import com.michaelrmossman.docoutdoors.model.AffectedEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object MapUtils {

    val EMPTY_LAT_LNG = LatLng(0.0,0.0)

    val EMPTY_LAT_LNG_BOUNDS = listOf(EMPTY_LAT_LNG).toLatLngBounds()

    fun getAffectedDrawableId(itemType: String): Int {
        return when (itemType) {
            AffectType.Campsite.type -> R.drawable.icons_lib_campsite_doc_blue_24
            AffectType.Hunting.type  -> R.drawable.icons_lib_hunting_doc_blue_24
            AffectType.Hut.type      -> R.drawable.baseline_house_doc_blue_24
            AffectType.Lodge.type    -> R.drawable.icons_lib_lodge_doc_blue_24
            AffectType.MtbTrack.type -> R.drawable.icons_lib_mtb_doc_blue_24
            AffectType.Place.type    -> R.drawable.icons_lib_place_doc_blue_24
            else /* WalkTrack */     -> R.drawable.baseline_hiking_doc_blue_24
        }
    }

    fun getLatLngBounds(latLng: LatLng): LatLngBounds {
        val boundsBuilder = LatLngBounds.Builder()
        boundsBuilder.include(latLng)
        return boundsBuilder.build()
    }

    fun getLatLngBounds(latLngList: List<LatLng>) = when (latLngList.size) {
        0 -> EMPTY_LAT_LNG_BOUNDS
        else -> latLngList.toLatLngBounds()
    }

    fun getMappableAssets(
        affectedAssets :List<AffectedEntity>
    ) : List<AffectedEntity> {
        return affectedAssets.filter { affected ->
            isValidCoords(affected.lat, affected.lon)
        }
    }

    fun isValidCoords(
        lat: Double?,
        lon: Double?
    ) : Boolean {
        return (
            lat != null && lat < 0.0
            &&
            lon != null && lon > 0.0
        )
    }

    fun isValidLatLng(latLng: LatLng): Boolean {
        return (
            latLng.latitude != 0.0
            &&
            latLng.longitude != 0.0
        )
    }

    fun locationPermissionGranted(): Boolean {
        return (
            ContextCompat.checkSelfPermission(
                instance,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                instance,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    fun zoomAll(
        boundingBox: LatLngBounds,
        coroutineScope: CoroutineScope,
        cameraPositionState: CameraPositionState,
        padding: Int = MAP_ZOOM_ALL_PADDING_SMALL
    ) {
        coroutineScope.launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(
                    boundingBox, padding
                ),
                durationMs = MAP_ZOOM_ALL_DURATION
            )
        }
    }
}