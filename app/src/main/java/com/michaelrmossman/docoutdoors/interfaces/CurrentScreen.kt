package com.michaelrmossman.docoutdoors.interfaces

import androidx.navigation3.runtime.NavKey
import com.michaelrmossman.docoutdoors.enums.AssetType
import com.michaelrmossman.docoutdoors.model.Alert
import kotlinx.serialization.Serializable

sealed interface CurrentScreen: NavKey {

    @Serializable
    data object HomeScreen: CurrentScreen

    @Serializable
    data object AlertsList: CurrentScreen
//    data class AlertsList(val itemId: String) : CurrentScreen {
//        companion object {
//            val deepLink = navDeepLink("app://example.com/detail/{itemId}")
//        }
//    }
    @Serializable
    data class AlertDetails(
        val index: Int
    ) : CurrentScreen
    @Serializable
    data class AffectedList(
        val alert: Alert,
        val index: Int
    ) : CurrentScreen
    @Serializable
    data class AffectedMap(
        val affectedIndex: Int,
        val alertIndex: Int
    ) : CurrentScreen

    @Serializable
    data object CampsitesList: CurrentScreen
    @Serializable
    data class CampsiteDetails(
        val index: Int
    ) : CurrentScreen
    @Serializable
    data class CampsiteSingle(
        val itemId: String
    ) : CurrentScreen
    @Serializable
    data class CampsitesMap(
        val index: Int
    ) : CurrentScreen

    @Serializable
    data object FavesScreen: CurrentScreen

    @Serializable
    data object HelpScreen: CurrentScreen

    @Serializable
    data object HutsList: CurrentScreen
    @Serializable
    data class HutDetails(
        val index: Int
    ) : CurrentScreen
    @Serializable
    data class HutSingle(
        val itemId: String
    ) : CurrentScreen
    @Serializable
    data class HutsMap(
        val index: Int
    ) : CurrentScreen

    @Serializable
    data object MultiMap: CurrentScreen
    @Serializable
    data class SingleMap(
        val assetId : String,
        val itemType: AssetType
    ) : CurrentScreen

    @Serializable
    data object TracksList: CurrentScreen
    @Serializable
    data class TrackDetails(
        val index: Int
    ) : CurrentScreen
    @Serializable
    data class TrackSingle(
        val itemId: String
    ) : CurrentScreen
    @Serializable
    data class TracksMap(
        val index: Int
    ) : CurrentScreen
}