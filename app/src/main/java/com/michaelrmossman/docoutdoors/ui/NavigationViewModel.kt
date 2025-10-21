package com.michaelrmossman.docoutdoors.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.michaelrmossman.docoutdoors.interfaces.CurrentScreen

class NavigationViewModel: ViewModel() {

    val backStack = mutableStateListOf<CurrentScreen>(
        CurrentScreen.HomeScreen
    )

    fun home() {
        backStack.forEach { screen ->
            backStack.removeIf { screen ->
                screen != CurrentScreen.HomeScreen
            }
        }
    }

    fun pop() {
        backStack.removeLastOrNull()
    }

    fun put(currentScreen: CurrentScreen) {
        backStack.add(currentScreen)
    }
}