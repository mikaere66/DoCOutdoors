package com.michaelrmossman.docoutdoors.interfaces

sealed interface HomeScreenState {
    data object Loading: HomeScreenState
    data object Ready  : HomeScreenState
}