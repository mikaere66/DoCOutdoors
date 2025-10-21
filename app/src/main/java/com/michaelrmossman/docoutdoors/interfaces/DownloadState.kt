package com.michaelrmossman.docoutdoors.interfaces

sealed interface DownloadState {
    data object Done    : DownloadState
    data object Error   : DownloadState
    data object Loading : DownloadState
    data object None    : DownloadState
    data object NotFound: DownloadState
}