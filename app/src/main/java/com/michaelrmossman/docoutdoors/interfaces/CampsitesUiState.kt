package com.michaelrmossman.docoutdoors.interfaces

sealed interface CampsitesUiState {
    data object Downloading: CampsitesUiState
    data object Error      : CampsitesUiState
    data object Forbidden  : CampsitesUiState
    data object Loading    : CampsitesUiState
    data object Success    : CampsitesUiState
}