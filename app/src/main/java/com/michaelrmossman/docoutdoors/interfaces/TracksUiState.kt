package com.michaelrmossman.docoutdoors.interfaces

sealed interface TracksUiState {
    data object Downloading: TracksUiState
    data object Empty      : TracksUiState
    data object Error      : TracksUiState
    data object Forbidden  : TracksUiState
    data object Loading    : TracksUiState
    data object Success    : TracksUiState
}