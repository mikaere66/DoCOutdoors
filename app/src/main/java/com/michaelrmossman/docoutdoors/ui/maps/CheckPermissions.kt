package com.michaelrmossman.docoutdoors.ui.maps

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.michaelrmossman.docoutdoors.utils.MapUtils.locationPermissionGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermissions(
    onPermissionGranted: (Boolean) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { resultMap ->
            // Handle the result(s) of the permission request(s)
            onPermissionGranted(resultMap.any { result -> true })
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                when (locationPermissionGranted()) {
                    true -> onPermissionGranted(true)
                    else -> requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}