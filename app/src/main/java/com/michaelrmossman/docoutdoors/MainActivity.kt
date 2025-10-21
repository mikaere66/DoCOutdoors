package com.michaelrmossman.docoutdoors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import com.michaelrmossman.docoutdoors.ui.OutdoorsApp
import com.michaelrmossman.docoutdoors.ui.theme.DoCOutdoorsTheme

class MainActivity: ComponentActivity() {

    @OptIn(
        ExperimentalMaterial3WindowSizeClassApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            DoCOutdoorsTheme {

                val windowSize = calculateWindowSizeClass(
                    this@MainActivity
                )
                OutdoorsApp(windowSize = windowSize)
            }
        }
    }
}