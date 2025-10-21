package com.michaelrmossman.docoutdoors.interfaces

sealed interface HutsUiState {
    data object Downloading: HutsUiState
    data object Error      : HutsUiState
    data object Forbidden  : HutsUiState
    data object Loading    : HutsUiState
    data object Success    : HutsUiState
}