package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.michaelrmossman.docoutdoors.R

@Composable
fun SnackbarMessage(
    onRefreshClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val doneMsg = stringResource(R.string.common_snackbar_refresh)
    val userMsg = stringResource(R.string.incomplete_snackbar_message)

    LaunchedEffect(key1 = Unit) {
        val result = snackbarHostState.showSnackbar(
            actionLabel = doneMsg,
            duration = SnackbarDuration.Indefinite,
            message = userMsg
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                // Handle click action, e.g. refresh list
                onRefreshClick()
            }
            SnackbarResult.Dismissed -> {
                // Handle snackbar dismiss without action
                println("Snackbar dismissed")
            }
        }
    }
}