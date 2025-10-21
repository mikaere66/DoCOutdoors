package com.michaelrmossman.docoutdoors.interfaces

sealed interface AlertsUiState {
    data object Downloading: AlertsUiState
    data object Empty      : AlertsUiState
    data object Error      : AlertsUiState
    data object Forbidden  : AlertsUiState
    data object Loading    : AlertsUiState
    data object Success    : AlertsUiState
}